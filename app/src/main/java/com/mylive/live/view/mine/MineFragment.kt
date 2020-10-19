package com.mylive.live.view.mine

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
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
        avatar_decor.setOnClickListener {
            val img1 = if (img_avatar.rotationX == 0f) img_avatar else img_avatar2
            val img2 = if (img_avatar.rotationX == 0f) img_avatar2 else img_avatar
            ObjectAnimator.ofFloat(img1, View.ROTATION_Y, 0f, 90f)
                    .apply {
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                ObjectAnimator.ofFloat(img2, View.ROTATION_Y, 90f, 0f)
                                        .apply {
                                            duration = 500
                                            start()
                                        }
                            }
                        })
                        duration = 500
                        start()
                    }
        }
    }

    override fun onSubscribe(scheduler: Scheduler) {
        super.onSubscribe(scheduler)
        scheduler.subscribe(HttpResp::class.java) { httpResp: HttpResp<*>? -> }
    }
}