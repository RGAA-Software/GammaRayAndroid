package com.tc.client.ui.server

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simform.refresh.SSPullToRefreshLayout
import com.tc.client.MainActivity
import com.tc.client.NetworkChecker
import com.tc.client.R
import com.tc.client.ServerApi
import com.tc.client.Settings
import com.tc.client.databinding.FragmentMachineBinding
import com.tc.client.db.DBServer
import com.tc.client.events.OnServerAvailable
import com.tc.client.events.OnServerDeleted
import com.tc.client.events.OnServerEmpty
import com.tc.client.events.OnServerOffline
import com.tc.client.events.OnServerScanned
import com.tc.client.ui.BaseFragment
import com.tc.client.ui.base.CustomAlertDialog
import com.tc.client.ui.base.OnListItemListener
import com.tc.client.ui.processes.AllRunningProcessActivity
import com.tc.client.util.HttpUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


class ServerFragment() : BaseFragment() {

    companion object {
        const val TAG = "Main"
    }

    private var binding: FragmentMachineBinding? = null

    private lateinit var serverAdapter: ServerAdapter
    private var servers = mutableListOf<DBServer>();
    private var timerCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preset = DBServer.create("MOCKING");
        preset.available = true;
        servers.add(preset);
        Log.i(TAG, "MachineFragment onCreate, will loadServers")
        EventBus.getDefault().register(this);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMachineBinding.inflate(inflater, container, false)
        val root: View = binding?.root!!
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEmptyTipVisibility(true)

        binding!!.refreshLayout.apply {
            setRepeatMode(SSPullToRefreshLayout.RepeatMode.REPEAT);
            setRepeatCount(SSPullToRefreshLayout.RepeatCount.INFINITE);
            setRefreshStyle(SSPullToRefreshLayout.RefreshStyle.NORMAL);
            setLottieAnimation("lottie_clock.json");
            setOnRefreshListener {
                //requestSteamApps();
                handler.postDelayed({
                    setRefreshing(false);
                }, 2000)
            }
        }

        binding!!.machineList.apply {
            val itemCount: Int
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                itemCount = 2
                addItemDecoration(ServerItemDecoration());
            } else {
                itemCount = 4
                addItemDecoration(ServerItemDecorationHorizontal(itemCount));
            }
            layoutManager = GridLayoutManager(activity, itemCount);
            serverAdapter = ServerAdapter(context, servers);
            adapter = serverAdapter;
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val manager = recyclerView.layoutManager as GridLayoutManager;
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                        if (lastVisibleItem == (servers.size - 1)) {
                            //Toast.makeText(activity, "Last...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        serverAdapter.setOnItemClickListener(object: OnListItemListener<DBServer> {
            override fun onItemClicked(pos: Int, server: DBServer) {

                val funShowOfflineIfNeeded: ()->Boolean = {
                    if (Settings.getInstance().currentServer.serverId != server.serverId) {
                        val confirmDialog = CustomAlertDialog.createDialog(requireActivity(),
                            getString(R.string.error),
                            getString(R.string.server_offline))
                        confirmDialog.show()
                        true
                    } else {
                        false
                    }
                }

                val dialog = ServerOpDialog(activity!!)
                dialog.onAllAppClicked = View.OnClickListener {
                    if (funShowOfflineIfNeeded()) {
                        return@OnClickListener
                    }
                    changeToGamesTab()
                }

                dialog.onRestartServerClicked = View.OnClickListener {
                    if (funShowOfflineIfNeeded()) {
                        return@OnClickListener
                    }

                    activity?.runOnUiThread {
                        val delDialog = CustomAlertDialog.createDialog(activity!!,
                            getString(R.string.restart_server),
                            getString(R.string.do_you_want_to_restart_server))
                        delDialog.onSureClicked = View.OnClickListener {
                            restartServer(pos, server)
                        }
                        delDialog.show()
                    }
                }

                dialog.onAllProcessClicked = View.OnClickListener {
                    if (funShowOfflineIfNeeded()) {
                        return@OnClickListener
                    }
                    startActivity(Intent(activity, AllRunningProcessActivity::class.java))
                }

                dialog.onDeleteAppClicked = View.OnClickListener {
                    activity?.runOnUiThread {
                        val delDialog = CustomAlertDialog.createDialog(activity!!,
                            getString(R.string.delete),
                            getString(R.string.do_you_want_to_delete_this_server))
                        delDialog.onSureClicked = View.OnClickListener {
                            deleteServer(server)
                        }
                        delDialog.show()
                    }
                }
                dialog.show()
            }
        })

        loadServers();
    }

    override fun onStart() {
        super.onStart()
        appContext.register1STimer("machine") {
            // todo: to use a Refresh button
            if (++timerCounter % 5 == 0) {
                checkServerInfo()
                Log.i(TAG, "check the server info: $timerCounter")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        appContext.remove1STimer("machine")
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this);
    }

    override fun onRefresh() {
        super.onRefresh()
        loadServers();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(event: OnServerScanned) {
        if (servers.contains(event.server)) {
            return;
        }
        servers.add(event.server)
        activity?.runOnUiThread {
            setEmptyTipVisibility(servers.isEmpty())
            serverAdapter.notifyDataSetChanged()
            checkServerInfo()
        }
    }

    private fun loadServers() {
        appContext.postTask{
            val servers = appContext.dbManager.queryServers()
            if (servers.isEmpty()) {
                EventBus.getDefault().post(OnServerEmpty())
            }

            this.servers.clear()
            this.servers.addAll(servers)
            appContext.postUITask{
                setEmptyTipVisibility(this.servers.isEmpty())
                serverAdapter.notifyDataSetChanged()
                Log.i(TAG, "Machine fragment checkServer info")
                checkServerInfo()
            }
        }
    }

    private fun setEmptyTipVisibility(visible: Boolean) {
        binding?.idEmptyIcon?.visibility = if (visible) View.VISIBLE else View.GONE
        binding?.idEmptyTip?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun checkServerInfo() {
        val nc = NetworkChecker(appContext);
        servers.forEach {
            nc.checkDBServerAvailable(it, object: NetworkChecker.OnDBServerCheckAvailableCallback {
                override fun onCheck(s: DBServer, originAvailable: Boolean) {
                    if (s.available) {
                        requestAvailableServerInfo(s)
                    } else {
                        val originServerIp = Settings.getInstance().currentServer.serverIp;
                        val msg = OnServerOffline()
                        msg.server = s
                        EventBus.getDefault().post(msg)
                        appContext.postUITask {
                            if (s.available != originAvailable) {
                                serverAdapter.notifyDataSetChanged()
                            }
                            // the offline server is current server
                            if (s.serverIp != null && s.serverIp == originServerIp && activity != null) {
                                (activity as MainActivity).updateTitleMessage("")
                            }
                        }
                    }
                }
            })
        }
    }

    private fun requestAvailableServerInfo(srv: DBServer) {
        appContext.postTask{
            val url = "http://" + srv.serverIp + ":" + srv.httpServerPort + ServerApi.API_SIMPLE_INFO
            Log.i(TAG, "request available server: $url")
            val resp = HttpUtil.reqUrl(url)
            if (TextUtils.isEmpty(resp)) {
                appContext.postUITask {
                    Toast.makeText(activity, "Failed to restart server", Toast.LENGTH_SHORT).show()
                }
                return@postTask
            }
            var info = ""
            try {
                info = JSONObject(resp!!).getJSONObject("data").toString()
            } catch (e: Exception) {
                Log.e(TAG, "Parse failed: " + resp!!)
                return@postTask;
            }
            val scanInfo = Settings.getInstance().parseScanInfo(info)
            srv.iconIndex = scanInfo.iconIndex
            srv.serverId = scanInfo.sysUniqueId
            Log.i(TAG, "online server, ip: ${srv.serverIp}, icon index: ${srv.iconIndex}")

            val msg = OnServerAvailable()
            msg.server = srv
            EventBus.getDefault().post(msg)

            appContext.postUITask {
                servers.forEach {
                    if (scanInfo.hasTargetIp(it.serverIp)) {
                        it.iconIndex = scanInfo.iconIndex
                        it.serverId = scanInfo.sysUniqueId
                    }
                }
                if (srv.serverIp != null) {
                    if (activity != null) {
                        (activity as MainActivity).updateTitleMessage(srv.serverId)
                    }
                }
                serverAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun deleteServer(server: DBServer) {
        appContext.postTask {
            appContext.dbManager.deleteServer(server)
            appContext.postUITask {
                val msg = OnServerDeleted()
                msg.server = server
                EventBus.getDefault().post(msg)
                if (activity != null) {
                    (activity as MainActivity).updateTitleMessage("")
                }
            }
            loadServers()
        }
    }

    private fun changeToGamesTab() {
        if (activity != null) {
            (activity as MainActivity).changeToTab(MainActivity.Companion.ID_GAMES)
        }
    }

    private fun restartServer(position: Int, server: DBServer) {
        appContext.postNetworkTask {
            val url = Settings.getInstance().getApiBaseUrl() + ServerApi.API_STOP_SERVER
            val resp = HttpUtil.reqUrl(url)
            if (TextUtils.isEmpty(resp)) {
                appContext.postUITask {
                    Toast.makeText(activity, "Failed to restart server", Toast.LENGTH_SHORT).show()
                }
                return@postNetworkTask
            }
            appContext.postUITask {
                server.available = false
                serverAdapter.notifyItemChanged(position)
                Toast.makeText(activity, "Request to restart server success, wait server starting...", Toast.LENGTH_SHORT).show()
            }
        }
    }

}