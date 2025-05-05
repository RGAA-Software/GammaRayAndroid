package com.tc.client.ui.effects

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tc.client.R
import com.tc.client.Settings
import com.tc.client.databinding.FragmentEffectBinding
import com.tc.client.effects.EffectActivity
import com.tc.client.effects.EffectDefinition
import com.tc.client.ui.BaseFragment
import com.tc.client.ui.base.CustomAlertDialog
import com.tc.client.ui.base.OnListItemListener

class EffectDisplayFragment() : BaseFragment() {

    private var _binding: FragmentEffectBinding? = null
    private val binding get() = _binding!!
    private lateinit var effectDisplayAdapter: EffectDisplayAdapter
    private val effects: MutableList<EffectDefinition.EffectInfo> = mutableListOf()
    private val effectDefinition = EffectDefinition()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        effectDefinition.init()
        effects.addAll(effectDefinition.effects)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEffectBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.effectList.apply {
            var itemCount = 0
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                itemCount = 2
                addItemDecoration(EffectDisplayItemDecoration());
            } else {
                itemCount = 4
                addItemDecoration(EffectDisplayItemDecorationHorizontal(4));
            }
            layoutManager = GridLayoutManager(activity, itemCount)
            effectDisplayAdapter = EffectDisplayAdapter(context, effects);
            adapter = effectDisplayAdapter;
            effectDisplayAdapter.setOnItemClickListener(object: OnListItemListener<EffectDefinition.EffectInfo> {
                override fun onItemClicked(pos: Int, value: EffectDefinition.EffectInfo) {
                    val dialog = CustomAlertDialog.createDialog(activity!!,
                        context.getString(R.string.open_spectrum_activity),
                        context.getString(R.string.do_you_want_to_open_spectrum_activity))
                    dialog.onSureClicked = View.OnClickListener {
                        startEffectActivity(value);
                    }
                    dialog.show()
                }
            })

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val manager = recyclerView.layoutManager as GridLayoutManager;
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                        if (lastVisibleItem == (effects.size - 1)) {
                            //Toast.makeText(activity, "Last...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private fun startEffectActivity(value: EffectDefinition.EffectInfo) {
        val intent = Intent(context, EffectActivity::class.java);
        val server = Settings.getInstance().currentServer
        if (!server.available || TextUtils.isEmpty(server.serverIp)) {
            Toast.makeText(context, "Server has not connected", Toast.LENGTH_SHORT).show()
            return;
        }
        intent.putExtra("ip", server.serverIp);
        intent.putExtra("port", server.streamWsPort);
        intent.putExtra("idx", value.idx)
        intent.putExtra("streamId", server.streamId)
        intent.putExtra("remoteDeviceId", if (server.serverId == null) {""} else {server.serverId})
        context?.startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}