package com.tc.client.ui.steam

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simform.refresh.SSPullToRefreshLayout
import com.tc.client.R
import com.tc.client.render.FrameRenderActivity
import com.tc.client.Settings
import com.tc.client.databinding.FragmentSteamAppBinding
import com.tc.client.db.DBServer
import com.tc.client.events.OnRunningGames
import com.tc.client.events.OnServerAvailable
import com.tc.client.events.OnServerDeleted
import com.tc.client.events.OnServerEmpty
import com.tc.client.events.OnServerOffline
import com.tc.client.steam.SteamGame
import com.tc.client.ui.BaseFragment
import com.tc.client.ui.base.CustomAlertDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SteamAppFragment() : BaseFragment() {

    companion object {
        const val TAG = "Main";
    }

    private var binding: FragmentSteamAppBinding? = null
    private var _handler: Handler? = null;
    private val handler get() = _handler!!;
    private lateinit var steamAppAdapter: SteamAppAdapter
    private var steamGames = mutableListOf<SteamGame>();
    private var lastAvailableServer: DBServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = FragmentSteamAppBinding.inflate(inflater, container, false)
        _handler = Handler(Looper.getMainLooper());
        binding?.idEmptyIcon?.visibility = View.GONE
        homeViewModel.text.observe(viewLifecycleOwner) {

        }
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.refreshLayout.apply {
            setRepeatMode(SSPullToRefreshLayout.RepeatMode.REPEAT);
            setRepeatCount(SSPullToRefreshLayout.RepeatCount.INFINITE);
            setRefreshStyle(SSPullToRefreshLayout.RefreshStyle.NORMAL);
            setLottieAnimation("lottie_clock.json");
            setOnRefreshListener {
                handler.postDelayed({
                    setRefreshing(false);
                }, 2000)
            }
        }

        binding!!.bookList.apply {
            val itemCount: Int
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                itemCount = 2
                addItemDecoration(ItemDecoration());
            } else {
                itemCount = 5
                addItemDecoration(ItemDecorationHorizontal(itemCount))
            }
            layoutManager = GridLayoutManager(activity, itemCount)
            steamAppAdapter = SteamAppAdapter(context, steamGames);
            steamAppAdapter.itemClickListener = object : SteamAppAdapter.OnItemClickListener {
                override fun onItemClicked(game: SteamGame) {
                    processGameClicked(game);
                }
            }

            adapter = steamAppAdapter;
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val manager = recyclerView.layoutManager as GridLayoutManager;
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                        if (lastVisibleItem == (steamGames.size - 1)) {
                            Toast.makeText(activity, "Last...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        requestSteamGames()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onRefresh() {
        super.onRefresh()
        requestSteamGames()
    }

    private fun addPresetItems() {
        steamGames.add(SteamGame.create(1, "Desktop", SteamGame.TAG_PRESET));
        steamGames.add(SteamGame.create(2, "Steam Big Picture", SteamGame.TAG_PRESET));
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onServerAvailableEvent(event: OnServerAvailable) {
        if (lastAvailableServer == null || lastAvailableServer?.serverId != event.server.serverId) {
            if (lastAvailableServer != null)
                Log.i(TAG, "${lastAvailableServer!!.serverId} => ${event.server.serverId}")
            requestSteamGames();
        }
        lastAvailableServer = event.server
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onServerOfflineEvent(event: OnServerOffline) {
        if (lastAvailableServer != null && event.server.serverId == lastAvailableServer!!.serverId) {
            clearApp()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onServerEmptyEvent(event: OnServerEmpty) {
        clearApp()
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onServerDeletedEvent(event: OnServerDeleted) {
        appContext.postDelayTask({
            requestSteamGames()
        }, 100)
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onRunningGames(event: OnRunningGames) {
        appContext.postUITask {
            synchronized(SteamAppFragment::class.java) {
                val notifyIndices = mutableListOf<Int>()
                for (i in 0 until steamGames.size) {
                    val steamGame = steamGames[i]
                    var findInRunningGames = false;
                    event.runningGames.forEach { runningGame ->
                        if (steamGame.gameId == runningGame.gameId) {
                            if (steamGame.gameTag != SteamGame.TAG_RUNNING) {
                                steamGame.gameTag = SteamGame.TAG_RUNNING
                            }
                            findInRunningGames = true
                            notifyIndices.add(i)
                            return@forEach
                        }
                    }

                    if (!findInRunningGames) {
                        if (steamGame.gameTag == SteamGame.TAG_RUNNING) {
                            steamGame.gameTag = SteamGame.TAG_IDLE;
                            notifyIndices.add(i)
                        }
                    }
                }

                notifyIndices.forEach {
                    steamAppAdapter.notifyItemChanged(it, 0)
                    //Log.i(TAG, "Notify...$it")
                }
                //Log.i(TAG, "----------------------")
            }
        }
    }

    private fun clearApp() {
//        try {
//            Log.i(TAG, "ClearApp..." + Exception().stackTraceToString())
//        } catch (e: Exception){}
        appContext.postUITask {
            lastAvailableServer = null
            activity?.runOnUiThread {
                steamGames.clear()
                steamAppAdapter.notifyDataSetChanged()
                setEmptyVisibility(true)
            }
        }
    }

    private fun requestSteamGames() {
        appContext.postTask {
            val result = appContext.steamManager.requestSteamGames();
            if (!result.ok()) {
                Log.i(TAG, "requestSteamApps failed.");
                clearApp();
                return@postTask
            }
            if (steamGames.isEmpty()) {
                addPresetItems();
            }

            synchronized(SteamAppFragment::class.java) {
                steamGames.removeAll(result.value)
                steamGames.addAll(result.value)
                steamGames.sort()
            }

            appContext.postUITask{
                if (steamGames.isNotEmpty()) {
                    setEmptyVisibility(false)
                }
                steamAppAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun requestRunningGames() {
        appContext.postTask {
            val result = appContext.steamManager.requestRunningGames();
            if (!result.ok()) {
                Log.i(TAG, "request running games failed");
                return@postTask
            }

            result.value.forEach {
                Log.i(TAG, "running game: $it")
            }
        }
    }

    private fun setEmptyVisibility(visible: Boolean) {
        if (visible) {
            binding?.idEmptyIcon?.visibility = View.VISIBLE
            binding?.idEmptyTip?.visibility = View.VISIBLE
        } else {
            binding?.idEmptyIcon?.visibility = View.GONE
            binding?.idEmptyTip?.visibility = View.GONE
        }
    }

    fun processGameClicked(game: SteamGame) {
        val dialog = GameOpDialog(requireActivity())
        dialog.onStartGameClicked = View.OnClickListener {
            appContext.postTask {
                val gamePath = game.getGamePath()
                if (TextUtils.isEmpty(gamePath) && !game.isDesktop() && !game.isBigPictureMode()) {
                    AlertDialog.Builder(activity)
                        .setTitle("Error")
                        .setMessage("Game exe path is empty")
                        .show()
                    return@postTask;
                }
                appContext.steamManager.startGame(gamePath)

                // after some conditions...
                // startFrameRenderActivity
                startFrameRenderActivity();
            }
        }

        dialog.onStopGameClicked = View.OnClickListener {
            val confirmDialog = CustomAlertDialog.createDialog(requireActivity(),
                getString(R.string.stop_game),
                getString(R.string.do_you_want_to_stop_game));
            confirmDialog.onSureClicked = View.OnClickListener {
                appContext.postTask {
                    appContext.steamManager.stopGame(game.gameId.toString())
                }
            }
            confirmDialog.show()
        }
        dialog.show()
    }

    private fun startFrameRenderActivity() {
        val intent = Intent(context, FrameRenderActivity::class.java);
        val server = Settings.getInstance().currentServer
        if (!server.available || TextUtils.isEmpty(server.serverIp)) {
            Toast.makeText(context, "Server has not connected", Toast.LENGTH_SHORT).show()
            return;
        }
        intent.putExtra("ip", server.serverIp)
        intent.putExtra("port", server.streamWsPort)
        intent.putExtra("streamId", server.streamId)
        context?.startActivity(intent)
    }

}