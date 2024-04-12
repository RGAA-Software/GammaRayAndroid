package com.tc.client.ui.steam

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simform.refresh.SSPullToRefreshLayout
import com.tc.client.databinding.FragmentSteamAppBinding
import com.tc.client.steam.SteamApp
import com.tc.client.ui.BaseFragment

class SteamAppFragment(private val hostActivity: Activity) : BaseFragment(hostActivity) {

    private var _binding: FragmentSteamAppBinding? = null
    private var _handler: Handler? = null;
    private val binding get() = _binding!!
    private val handler get() = _handler!!;
    private lateinit var steamAppAdapter: SteamAppAdapter
    private var steamApps = mutableListOf<SteamApp>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        steamApps.add(SteamApp.create(1, "Desktop"));
        steamApps.add(SteamApp.create(2, "Steam Big Picture"));
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentSteamAppBinding.inflate(inflater, container, false)
        _handler = Handler(Looper.getMainLooper());
        val root: View = binding.root

        homeViewModel.text.observe(viewLifecycleOwner) {

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
                requestSteamApps();
                handler.postDelayed({
                    setRefreshing(false);
                }, 2000)
            }
        }

        binding.bookList.apply {
            layoutManager = GridLayoutManager(activity, 2);
            steamAppAdapter = SteamAppAdapter(context, steamApps);
            adapter = steamAppAdapter;
            addItemDecoration(ItemDecoration(90));
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val manager = recyclerView.layoutManager as GridLayoutManager;
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                        if (lastVisibleItem == (steamApps.size - 1)) {
                            Toast.makeText(activity, "Last...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        //
        requestSteamApps()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestSteamApps() {
        if (appContext == null) {
            return;
        }
        appContext.postTask {
            val result = appContext.steamManager.requestSteamApps();
            if (!result.ok()) {
                appContext.postUITask {
                    Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
                }
                return@postTask
            }

            steamApps.removeAll(result.value)
            steamApps.addAll(result.value)
            appContext.postUITask{
                steamAppAdapter.notifyDataSetChanged()
            }
        }
    }
}