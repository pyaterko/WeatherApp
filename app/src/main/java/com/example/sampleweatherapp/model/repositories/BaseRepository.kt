package com.example.sampleweatherapp.model.repositories

import com.example.sampleweatherapp.model.api.ApiProvider
import io.reactivex.rxjava3.subjects.BehaviorSubject

abstract  class BaseRepository<T>(val api:ApiProvider) {
     val dataEmitter: BehaviorSubject<T> = BehaviorSubject.create()
}