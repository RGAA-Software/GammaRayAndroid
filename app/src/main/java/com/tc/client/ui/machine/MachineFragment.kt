package com.tc.client.ui.machine

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simform.refresh.SSPullToRefreshLayout
import com.tc.client.databinding.FragmentMachineBinding
import com.tc.client.db.DBServer
import com.tc.client.events.OnServerScanned
import com.tc.client.ui.BaseFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MachineFragment(private val hostActivity: Activity) : BaseFragment(hostActivity) {

    private var _binding: FragmentMachineBinding? = null
    private val binding get() = _binding!!

    private lateinit var machineAdapter: MachineAdapter
    private var machines = mutableListOf<DBServer>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //machines.add(DBServer.create("Searching..."));
        loadServers();
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
                            Toast.makeText(activity, "Last...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onMessageEvent(event: OnServerScanned) {
        if (machines.contains(event.server)) {
            return;
        }
        machines.add(event.server)
        activity?.runOnUiThread {
            machineAdapter.notifyDataSetChanged()
        }
    }

    private fun loadServers() {
        appContext.postTask{
            val servers = appContext.dbManager.queryServers()
            machines.removeAll(servers)
            machines.addAll(servers)
            appContext.postUITask{
                machineAdapter.notifyDataSetChanged()
            }
        }
    }
}