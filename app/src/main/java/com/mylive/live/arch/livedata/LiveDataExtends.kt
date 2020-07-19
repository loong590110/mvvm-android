package com.mylive.live.arch.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class MutexLiveData<P, N> {
    private val positiveLiveData by lazy { MutableLiveData<P?>() }
    private val negativeLiveData by lazy { MutableLiveData<N?>() }

//    var positiveValue by object : ReadWriteProperty<MutexLiveData<P, N>, P?> {
//        override fun getValue(thisRef: MutexLiveData<P, N>, property: KProperty<*>): P? {
//            return positiveLiveData.value
//        }
//
//        override fun setValue(thisRef: MutexLiveData<P, N>, property: KProperty<*>, value: P?) {
//            positiveLiveData.value = value
//        }
//    }

    fun getPositiveValue() = positiveLiveData.value
    fun setPositiveValue(value: P?) {
        positiveLiveData.value = value
    }

    fun postPositiveValue(value: P?) {
        positiveLiveData.postValue(value)
    }

    fun getNegativeValue() = negativeLiveData.value
    fun setNegativeValue(value: N?) {
        negativeLiveData.value = value
    }

    fun postNegativeValue(value: N?) {
        negativeLiveData.postValue(value)
    }

//    fun observe(owner: LifecycleOwner, observer: (P) -> Unit): (observer: (N) -> Unit) -> Unit {
//        positiveLiveData.observe(owner, Observer(observer))
//        return {
//            negativeLiveData.observe(owner, Observer(it))
//        }
//    }

    fun observe(owner: LifecycleOwner, observer: (P?) -> Unit): NegativeObserver<N?> {
        positiveLiveData.observe(owner, Observer(observer))
        return NegativeObserver(owner, negativeLiveData)
    }

//    fun observe(owner: LifecycleOwner, pObserver: (P) -> Unit, nObserver: (N) -> Unit) {
//        positiveLiveData.observe(owner, Observer(pObserver))
//        negativeLiveData.observe(owner, Observer(nObserver))
//    }

    class NegativeObserver<N>(
            private val owner: LifecycleOwner,
            private val negativeLiveData: MutableLiveData<N?>
    ) {
        infix fun and(observer: (N?) -> Unit) {
            negativeLiveData.observe(owner, Observer(observer))
        }

        operator fun plus(observer: (N?) -> Unit) = and(observer)
    }
}