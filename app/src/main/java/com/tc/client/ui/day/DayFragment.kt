package com.tc.client.ui.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.tc.client.AppContext
import com.tc.client.databinding.FragmentDayBinding
import com.tc.client.ui.BaseFragment

class DayFragment(appContext: AppContext) : BaseFragment(appContext) {

    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!
    private val daySentence = DaySentenceManager(appContext);

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentDayBinding.inflate(inflater, container, false)
        val root: View = binding.root

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