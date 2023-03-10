package com.example.sampleweatherapp.model.repositories

import android.util.Log
import com.example.sampleweatherapp.App
import com.example.sampleweatherapp.model.api.ApiProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

abstract class BaseRepository<T : Any> {

    val dataEmitter: BehaviorSubject<T> = BehaviorSubject.create()

    protected fun roomTransaction(transaction: () -> T) =
        Observable.fromCallable { transaction() }
            .subscribeOn(Schedulers.io())
            .onErrorResumeNext {
                Log.d("onErrorResume", it.message.toString())
                Observable.fromCallable { transaction() }
                    .subscribeOn(Schedulers.io())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                dataEmitter.onNext(it)
            }
}