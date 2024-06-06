package com.tc.client.ui.machine

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simform.refresh.SSPullToRefreshLayout
import com.tc.client.MainActivity
import com.tc.client.NetworkChecker
import com.tc.client.R
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MachineFragment() : BaseFragment() {

    companion object {
        const val TAG = "Main"
    }

    private var _binding: FragmentMachineBinding? = null
    private val binding get() = _binding!!

    private lateinit var machineAdapter: MachineAdapter
    private var machines = mutableListOf<DBServer>();
    private var timerCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preset = DBServer.create("MOCKING");
        preset.available = true;
        machines.add(preset);
        Log.i(TAG, "MachineFragment onCreate, will loadServers")
        EventBus.getDefault().register(this);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentMachineBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dashboardViewModel.text.observe(viewLifecycleOwner) {

        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEmptyTipVisibility(true)

        binding.refreshLayout.apply {
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

        binding.machineList.apply {
            layoutManager = GridLayoutManager(activity, 2);
            machineAdapter = MachineAdapter(context, machines);
            adapter = machineAdapter;
            addItemDecoration(MachineItemDecoration(90));
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val manager = recyclerView.layoutManager as GridLayoutManager;
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                        if (lastVisibleItem == (machines.size - 1)) {
                            //Toast.makeText(activity, "Last...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        machineAdapter.setOnItemClickListener(object: OnListItemListener<DBServer> {
            override fun onItemClicked(pos: Int, srv: DBServer) {
                val dialog = MachineOpDialog(activity!!)
                dialog.onAllAppClicked = View.OnClickListener {
                    changeToGamesTab()
                }
                dialog.onDeleteAppClicked = View.OnClickListener {
                    activity?.runOnUiThread {
                        val delDialog = CustomAlertDialog.createDialog(activity!!,
                            getString(R.string.delete),
                            getString(R.string.do_you_want_to_delete_this_server))
                        delDialog.onSureClicked = View.OnClickListener {
                            deleteServer(srv)
                        }
                        delDialog.onCancelClicked = View.OnClickListener {

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
        _binding = null
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
        if (machines.contains(event.server)) {
            return;
        }
        machines.add(event.server)
        activity?.runOnUiThread {
            setEmptyTipVisibility(machines.isEmpty())
            machineAdapter.notifyDataSetChanged()
            checkServerInfo()
        }
    }

    private fun loadServers() {
        appContext.postTask{
            val servers = appContext.dbManager.queryServers()
            if (servers.isEmpty()) {
                EventBus.getDefault().post(OnServerEmpty())
            }

            machines.clear()
            machines.addAll(servers)
            appContext.postUITask{
                setEmptyTipVisibility(machines.isEmpty())
                machineAdapter.notifyDataSetChanged()
                Log.i(TAG, "Machine fragment checkServer info")
                checkServerInfo()
            }
        }
    }

    private fun setEmptyTipVisibility(visible: Boolean) {
        binding.idEmptyIcon.visibility = if (visible) View.VISIBLE else View.GONE
        binding.idEmptyTip.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun checkServerInfo() {
        val nc = NetworkChecker(appContext);
        machines.forEach {
            nc.checkDBServerAvailable(it, object: NetworkChecker.OnDBServerCheckAvailableCallback {
                override fun onCheck(s: DBServer, originAvailable: Boolean) {
                    if (s.available) {
                        val msg = OnServerAvailable()
                        msg.server = s
                        EventBus.getDefault().post(msg)

                        appContext.postUITask {
                            if (s.available != originAvailable) {
                                machineAdapter.notifyDataSetChanged()
                            }
                            if (s.serverIp != null) {
                                if (activity != null) {
                                    (activity as MainActivity).updateTitleMessage(s.serverId)
                                }
                            }
                        }
                    } else {
                        val originServerIp = Settings.getInstance().currentServer.serverIp;
                        val msg = OnServerOffline()
                        msg.server = s
                        EventBus.getDefault().post(msg)
                        appContext.postUITask {
                            if (s.available != originAvailable) {
                                machineAdapter.notifyDataSetChanged()
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

    private fun deleteServer(srv: DBServer) {
        appContext.postTask {
            appContext.dbManager.deleteServer(srv)
            appContext.postUITask {
                val msg = OnServerDeleted()
                msg.server = srv
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

}