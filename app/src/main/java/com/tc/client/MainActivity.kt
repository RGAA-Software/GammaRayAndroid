package com.tc.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.king.camera.scan.CameraScan
import com.tc.client.databinding.ActivityMainBinding
import com.tc.client.steam.JavaWSClient
import com.tc.client.steam.UdpBroadcastReceiver
import com.tc.client.ui.MainTopRightMenu
import com.tc.client.ui.machine.MachineFragment
import com.tc.client.ui.steam.SteamAppFragment
import com.tc.client.ui.me.AboutMeFragment
import com.tc.client.ui.day.DayFragment
import com.tc.client.util.ScreenUtil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "Main";

        private const val ID_BOOK = 1
        private const val ID_MOVIE = 2
        private const val ID_DAY = 3
        private const val ID_ME = 4
    }

    private lateinit var steamAppFragment: SteamAppFragment
    private lateinit var machineFragment: MachineFragment
    private lateinit var dayFragment: DayFragment
    private lateinit var aboutMeFragment: AboutMeFragment
    private var currentFragment: Fragment? = null

    private lateinit var appContext: AppContext;

    private lateinit var wsClient: JavaWSClient;
    private lateinit var udpReceiver: UdpBroadcastReceiver;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenUtil.makeActivityFullScreen(this);

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appContext = (application as App).appContext;

        binding.idOption.setOnClickListener {
            val menu = MainTopRightMenu(this);
            menu.show(binding.idOption);
        }

        steamAppFragment = SteamAppFragment();
        steamAppFragment.appContext = appContext
        machineFragment = MachineFragment();
        machineFragment.appContext = appContext
        dayFragment = DayFragment();
        dayFragment.appContext = appContext
        aboutMeFragment = AboutMeFragment();
        aboutMeFragment.appContext = appContext

        val fragmentHost = binding.root.findViewById<RelativeLayout>(R.id.fragment_host);

        binding.bottomBar.apply {
            add(MeowBottomNavigation.Model(ID_MOVIE, R.drawable.ic_laptop))
            add(MeowBottomNavigation.Model(ID_BOOK, R.drawable.ic_controller))
            add(MeowBottomNavigation.Model(ID_DAY, R.drawable.ic_sun))
            add(MeowBottomNavigation.Model(ID_ME, R.drawable.ic_account))

            //setCount(ID_NOTIFICATION, "15")

            setOnShowListener {

            }

            setOnClickMenuListener {
                Log.i(TAG, "id: ${it.id}")
                when (it.id) {
                    ID_BOOK -> {
                        switchFragment(steamAppFragment);
                        setActionBarTitle("Games");
                    }
                    ID_MOVIE -> {
                        switchFragment(machineFragment);
                        setActionBarTitle("Machines");
                    }
                    ID_DAY -> {
                        switchFragment(dayFragment)
                        setActionBarTitle("Everyday");
                    }
                    ID_ME -> {
                        switchFragment(aboutMeFragment)
                        setActionBarTitle("About Me");
                    }
                }
            }

            setOnReselectListener {
                Toast.makeText(context, "item ${it.id} is reselected.", Toast.LENGTH_LONG).show()
            }

            show(ID_MOVIE)
            switchFragment(machineFragment);
            setActionBarTitle("Machines");
        }

        //switchFragment(bookFragment)
        //wsClient = JavaWSClient("192.168.31.5", 20369);
        wsClient = JavaWSClient("10.0.0.16", 20369);
        wsClient.start();
        appContext.register1STimer("ws") {
            wsClient.sendMessage("..xx...");
        };

        udpReceiver = UdpBroadcastReceiver();
        udpReceiver.start();
    }

    private fun switchFragment(to: Fragment) {
        val transaction = supportFragmentManager.beginTransaction();
        if (currentFragment == null) {
            if (!to.isAdded) {
                transaction.add(R.id.fragment_host, to).commit();
            } else {
                transaction.show(to).commit();
            }
            currentFragment = to;
        } else {
            if (currentFragment == to) {
                if (!to.isAdded) {
                    transaction.add(R.id.fragment_host, to).commit();
                } else {
                    transaction.show(to).commit();
                }
            } else {
                if (!to.isAdded) {
                    transaction.hide(currentFragment!!).add(R.id.fragment_host, to).commit();
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

    override fun onDestroy() {
        super.onDestroy()
        udpReceiver.stopReceiving()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val message = data?.getStringExtra(CameraScan.SCAN_RESULT)
            Toast.makeText(this, "msg: $message", Toast.LENGTH_SHORT).show();
        }
    }

}