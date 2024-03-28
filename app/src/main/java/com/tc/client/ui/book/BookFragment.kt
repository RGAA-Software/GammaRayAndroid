package com.tc.client.ui.book

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
import com.tc.client.AppContext
import com.tc.client.databinding.FragmentBookBinding
import com.tc.client.ui.BaseFragment

class BookFragment(appContext: AppContext) : BaseFragment(appContext) {

    private var _binding: FragmentBookBinding? = null
    private var _handler: Handler? = null;
    private val binding get() = _binding!!
    private val handler get() = _handler!!;

    private var books = mutableListOf<BookInfo>();

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentBookBinding.inflate(inflater, container, false)
        _handler = Handler(Looper.getMainLooper());
        val root: View = binding.root

        homeViewModel.text.observe(viewLifecycleOwner) {

        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (i in 1..120) {
            books.add(BookInfo());
        }

        binding.refreshLayout.apply {
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

        binding.bookList.apply {
            layoutManager = GridLayoutManager(activity, 2);
            adapter = BookAdapter(context, books);
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
                        if (lastVisibleItem == (books.size - 1)) {
                            Toast.makeText(activity, "Last...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}