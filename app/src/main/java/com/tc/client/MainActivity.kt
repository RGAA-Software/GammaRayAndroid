package com.tc.client

import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.tc.client.databinding.ActivityMainBinding
import com.tc.client.ui.video.VideoFragment
import com.tc.client.ui.book.SteamAppFragment
import com.tc.client.ui.me.AboutMeFragment
import com.tc.client.ui.day.DayFragment
import com.tc.reading.util.ScreenUtil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val ID_BOOK = 1
        private const val ID_MOVIE = 2
        private const val ID_DAY = 3
        private const val ID_ME = 4
    }

    private lateinit var steamAppFragment: SteamAppFragment
    private lateinit var videoFragment: VideoFragment
    private lateinit var dayFragment: DayFragment
    private lateinit var aboutMeFragment: AboutMeFragment
    private var currentFragment: Fragment? = null

    private lateinit var appContext: AppContext;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenUtil.makeActivityFullScreen(this);

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appContext = AppContext(this);

        steamAppFragment = SteamAppFragment(appContext);
        videoFragment = VideoFragment(appContext);
        dayFragment = DayFragment(appContext);
        aboutMeFragment = AboutMeFragment(appContext);

        supportActionBar?.title = "Books";
        val fragmentHost = binding.root.findViewById<RelativeLayout>(R.id.fragment_host);


        binding.bottomBar.apply {
            add(MeowBottomNavigation.Model(ID_BOOK, R.drawable.ic_book))
            add(MeowBottomNavigation.Model(ID_MOVIE, R.drawable.ic_movie))
            add(MeowBottomNavigation.Model(ID_DAY, R.drawable.ic_sun))
            add(MeowBottomNavigation.Model(ID_ME, R.drawable.ic_account))

            //setCount(ID_NOTIFICATION, "15")

            setOnShowListener {
                when (it.id) {
                    ID_BOOK -> {
                        switchFragment(steamAppFragment);
                        setActionBarTitle("Books");
                    }
                    ID_MOVIE -> {
                        switchFragment(videoFragment);
                        setActionBarTitle("Videos");
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

            setOnClickMenuListener {

            }

            setOnReselectListener {
                Toast.makeText(context, "item ${it.id} is reselected.", Toast.LENGTH_LONG).show()
            }

            show(ID_BOOK)

        }

        //switchFragment(bookFragment)
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
        supportActionBar?.title = title
    }

}