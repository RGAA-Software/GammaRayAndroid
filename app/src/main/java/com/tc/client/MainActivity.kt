package com.tc.client

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.king.camera.scan.CameraScan
import com.tc.client.databinding.ActivityMainBinding
import com.tc.client.events.OnAddScanInfo
import com.tc.client.events.OnServerAvailable
import com.tc.client.events.OnServerEmpty
import com.tc.client.events.OnServerScanned
import com.tc.client.steam.JavaWSClient
import com.tc.client.steam.UdpBroadcastReceiver
import com.tc.client.ui.BaseFragment
import com.tc.client.ui.MainTopRightMenu
import com.tc.client.ui.machine.MachineFragment
import com.tc.client.ui.steam.SteamAppFragment
import com.tc.client.ui.me.AboutMeFragment
import com.tc.client.ui.effects.MusicEffectsFragment
import com.tc.client.util.ScreenUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "Main";

        const val ID_MACHINE = 1
        const val ID_GAMES = 2
        const val ID_MUSIC_EFFECTS = 3
        const val ID_ME = 4

        init {
            System.loadLibrary("client");
        }

    }

    private lateinit var steamAppFragment: SteamAppFragment
    private lateinit var machineFragment: MachineFragment
    private lateinit var musicEffectsFragment: MusicEffectsFragment
    private lateinit var aboutMeFragment: AboutMeFragment
    private var currentFragment: Fragment? = null
    private val fragments = mutableListOf<BaseFragment>()

    private lateinit var appContext: AppContext;

    private var wsClient: JavaWSClient? = null;
    //private lateinit var udpReceiver: UdpBroadcastReceiver;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenUtil.makeActivityFullScreen(this);

        EventBus.getDefault().register(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appContext = (application as App).appContext;

        binding.idOption.setOnClickListener {
            val menu = MainTopRightMenu(this);
            menu.show(binding.idOption);
        }

        binding.idRefresh.setOnClickListener {
            if (currentFragment != null && currentFragment is BaseFragment) {
                (currentFragment as BaseFragment).onRefresh()
            }
        }

        val mgr = this.assets;
        val tf = Typeface.createFromAsset(mgr, "fonts/matrix.ttf");
        binding.idTitleMsg.typeface = tf;

        Log.i(TAG, "MainActivity onCreate, will create fragments")

        machineFragment = getFragment(ID_MACHINE) as MachineFragment
        steamAppFragment = getFragment(ID_GAMES) as SteamAppFragment;
        musicEffectsFragment = getFragment(ID_MUSIC_EFFECTS) as MusicEffectsFragment;
        aboutMeFragment = getFragment(ID_ME) as AboutMeFragment;
        fragments.add(machineFragment)
        fragments.add(steamAppFragment)
        fragments.add(musicEffectsFragment)
        fragments.add(aboutMeFragment)

        val fragmentHost = binding.root.findViewById<RelativeLayout>(R.id.fragment_host);

        binding.bottomBar.apply {
            add(MeowBottomNavigation.Model(ID_MACHINE, R.drawable.ic_laptop))
            add(MeowBottomNavigation.Model(ID_GAMES, R.drawable.ic_controller))
            add(MeowBottomNavigation.Model(ID_MUSIC_EFFECTS, R.drawable.ic_sun))
            add(MeowBottomNavigation.Model(ID_ME, R.drawable.ic_account))

            //setCount(ID_NOTIFICATION, "15")

            setOnShowListener {

            }

            setOnClickMenuListener {
                Log.i(TAG, "id: ${it.id}")
                changeToTab(it.id)
            }

            setOnReselectListener {
                Toast.makeText(context, "item ${it.id} is reselected.", Toast.LENGTH_LONG).show()
            }

            changeToTab(ID_MACHINE)
        }

        appContext.register1STimer("ws") {
            //wsClient?.sendMessage("..xx...");
        };

        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)

        //udpReceiver = UdpBroadcastReceiver();
        //udpReceiver.start();

        // test
        val testIp = "10.0.0.16";
//        val testIp = "192.168.31.5";
        val testPort = 20371;
        //val client = TestMediaClient(testIp, testPort);
        //client.start()

        // test
        val intent = Intent(this, FrameRenderActivity::class.java);
        intent.putExtra("ip", testIp);
        intent.putExtra("port", testPort);
        //this.startActivity(intent)
    }

    private fun switchFragment(to: Fragment, tab: String) {
        if (to is MachineFragment) {
            binding.idOption.visibility = View.VISIBLE
        } else {
            binding.idOption.visibility = View.GONE
        }
        val transaction = supportFragmentManager.beginTransaction();
        if (currentFragment == null) {
            fragments.forEach {
                if (it != to) {
                    transaction.hide(it)
                }
            }

            if (!to.isAdded) {
                transaction.add(R.id.fragment_host, to, tab).commit();
            } else {
                transaction.show(to).commit();
            }
            currentFragment = to;
        } else {
            if (currentFragment == to) {
                if (!to.isAdded) {
                    transaction.add(R.id.fragment_host, to, tab)
                    transaction.show(to).commit()
                } else {
                    transaction.show(to).commit();
                }
            } else {
                if (!to.isAdded) {
                    transaction.hide(currentFragment!!).add(R.id.fragment_host, to, tab)
                    transaction.show(to).commit()
                } else {
                    transaction.hide(currentFragment!!).show(to).commit();
                }
            }
            currentFragment = to;
        }
    }

    private fun setActionBarTitle(title: String) {
        binding.idTitleBarText.text = title;
    }

    override fun onPause() {
        super.onPause()
        fragments.forEach {
            it.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        //udpReceiver.stopReceiving()

        EventBus.getDefault().unregister(this)
        Log.i(TAG, "MainActivity onDestroy")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val message = data?.getStringExtra(CameraScan.SCAN_RESULT)
            if (message != null) {
                val scanInfo = Settings.getInstance().parseScanInfo(message)
                val msg = OnAddScanInfo()
                msg.scanInfo = scanInfo
                onAddScanInfoEvent(msg)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onAddScanInfoEvent(event: OnAddScanInfo) {
        if (!event.scanInfo.valid()) {
            return;
        }
        Log.i(TAG, "will check scan info : ${event.scanInfo}")
        NetworkChecker(appContext).checkScanInfoAvailable(event.scanInfo, object: NetworkChecker.OnScanInfoCheckAvailableCallback{
            override fun onCheck(scanInfo: ScanInfo) {
                Log.i(TAG, "target ip: ${scanInfo.targetIp}, can connect: ${scanInfo.canConnect()}")
                if (scanInfo.canConnect()) {
                    appContext.postTask {
                        val dbServer = scanInfo.asDBServer();
                        appContext.dbManager.insertOrUpdateServer(dbServer)

                        val msg = OnServerScanned()
                        msg.server = dbServer
                        EventBus.getDefault().post(msg)
                    }
                }
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onServerAvailableEvent(event: OnServerAvailable) {
        if (wsClient == null || !wsClient!!.isOpen) {
            val cs = Settings.getInstance().currentServer
            if (TextUtils.isEmpty(cs.serverIp)) {
                return;
            }
            wsClient = JavaWSClient(cs.serverIp, cs.wsServerPort);
            wsClient!!.start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onServerEmptyEvent(event: OnServerEmpty) {
        updateTitleMessage("")
    }

    fun updateTitleMessage(m: String) {
        runOnUiThread {
            binding.idTitleMsg.text = m
        }
    }

    fun changeToTab(tab: Int) {
        when (tab) {
            ID_MACHINE -> {
                binding.bottomBar.show(ID_MACHINE)
                switchFragment(machineFragment, tab.toString());
                setActionBarTitle("Machines");
            }
            ID_GAMES -> {
                binding.bottomBar.show(ID_GAMES)
                switchFragment(steamAppFragment, tab.toString());
                setActionBarTitle("Games");
            }
            ID_MUSIC_EFFECTS -> {
                binding.bottomBar.show(ID_MUSIC_EFFECTS)
                switchFragment(musicEffectsFragment, tab.toString())
                setActionBarTitle("Music Effects");
            }
            ID_ME -> {
                binding.bottomBar.show(ID_ME)
                switchFragment(aboutMeFragment, tab.toString())
                setActionBarTitle("About Me");
            }
        }
    }

    private fun getFragment(id: Int): BaseFragment {
        val fragment = supportFragmentManager.findFragmentByTag(id.toString());
        if (fragment != null) {
            return fragment as BaseFragment;
        }
        when (id) {
            ID_MACHINE -> {
                return MachineFragment()
            }
            ID_GAMES -> {
                return SteamAppFragment()
            }
            ID_MUSIC_EFFECTS -> {
                return MusicEffectsFragment()
            }
            ID_ME -> {
                return AboutMeFragment()
            }
        }
        return MachineFragment()
    }

}