package com.tc.client.ui.effects

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.tc.client.R
import com.tc.client.databinding.FragmentDayBinding
import com.tc.client.effects.box2d.Box2dActivity
import com.tc.client.effects.fireworks.FireWorksActivity
import com.tc.client.effects.spine.SpineActivity
import com.tc.client.ui.BaseFragment

class MusicEffectsFragment() : BaseFragment() {

    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!
    private lateinit var daySentence: DaySentenceManager;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        daySentence = DaySentenceManager(appContext);
        _binding = FragmentDayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        root.findViewById<CardView>(R.id.day_youdao).setOnClickListener {
            startActivity(Intent(activity, Box2dActivity::class.java));
        }
        root.findViewById<CardView>(R.id.day_shanbei).setOnClickListener {
            startActivity(Intent(activity, SpineActivity::class.java));
        }
        root.findViewById<TextView>(R.id.date_day).setOnClickListener {
            startActivity(Intent(activity, FireWorksActivity::class.java));
        }
        notificationsViewModel.text.observe(viewLifecycleOwner) {

        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        daySentence.requestTodaySentence{ sentence ->
            appContext.postUITask{
                if (sentence.type == "shanbay") {
                    binding.shanbayAuthor.text = "--" + sentence.author;
                    binding.shanbayContent.text = sentence.content;
                    binding.shanbayTranslation.text = sentence.translation;
                    Glide.with(this)
                        .load(sentence.imageUrl)
                        .centerCrop()
                        //.placeholder(R.drawable.test_cover)
                        .into(binding.shanbayCover);
                }
            };
        };
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}