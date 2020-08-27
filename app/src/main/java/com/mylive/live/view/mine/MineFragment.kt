package com.mylive.live.view.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mylive.live.arch.annotation.FieldMap
import com.mylive.live.arch.subscriber.Scheduler
import com.mylive.live.base.BaseFragment
import com.mylive.live.databinding.FragmentMineBinding
import com.mylive.live.model.beans.HttpResp
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
class MineFragment : BaseFragment() {
    @FieldMap("binding")
    var binding: FragmentMineBinding? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return FragmentMineBinding.inflate(
                inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        avatar_decor.background = null
    }

    override fun onSubscribe(scheduler: Scheduler) {
        super.onSubscribe(scheduler)
        scheduler.subscribe(HttpResp::class.java) { httpResp: HttpResp<*>? -> }
    }
}