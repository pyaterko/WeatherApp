package com.example.sampleweatherapp.model.repositories

import android.annotation.SuppressLint
import android.util.Log
import com.example.sampleweatherapp.model.api.ApiProvider
import com.example.sampleweatherapp.model.api.models.geocod.GeoCodeItem
import com.example.sampleweatherapp.model.database.entities.GeoCodeDbModel.Companion.toDbModelItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

const val SAVED = 1
const val CURRENT = 0

class SearchRepository(val api: ApiProvider) : BaseRepository<SearchRepository.Content>() {

    private val dbAccess = database.geoCodeDao()


    @SuppressLint("CheckResult")
    fun getSearchCityListWith(name: String) {
        api.provideGeoCodingApi().detCityByName(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                Content(it, CURRENT)
            }
            .subscribe {
                dataEmitter.onNext(it)
            }
    }

    fun getData() {
        getFavoriteListWith()
    }

    fun add(item: GeoCodeItem) {
        getFavoriteListWith { dbAccess.add(item.toDbModelItem()) }
    }

    fun delete(item: GeoCodeItem) {
        getFavoriteListWith { dbAccess.delete(item.toDbModelItem()) }
    }

    private fun getFavoriteListWith(daoQuery: (() -> Unit)? = null) {
        roomTransaction {
            daoQuery?.let { it() }
            Content(dbAccess.getData().map { it.fromItem() }, SAVED)
        }
    }


    data class Content(val data: List<GeoCodeItem>, val purpose: Int)
}