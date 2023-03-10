package com.example.sampleweatherapp.model.repositories

import android.annotation.SuppressLint
import com.example.sampleweatherapp.model.api.ApiProvider
import com.example.sampleweatherapp.model.api.models.geocod.GeoCodeItem
import com.example.sampleweatherapp.model.database.GeoCodeDao
import com.example.sampleweatherapp.model.database.entities.GeoCodeDbModel.Companion.toDbModelItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

const val SAVED = 1
const val CURRENT = 0

class SearchRepository @Inject constructor(
    val api: ApiProvider,
    private val geoCodeDao: GeoCodeDao,
) : BaseRepository<SearchRepository.Content>() {


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
        getFavoriteListWith { geoCodeDao.add(item.toDbModelItem()) }
    }

    fun delete(item: GeoCodeItem) {
        getFavoriteListWith { geoCodeDao.delete(item.toDbModelItem()) }
    }

    private fun getFavoriteListWith(daoQuery: (() -> Unit)? = null) {
        roomTransaction {
            daoQuery?.let { it() }
            Content(geoCodeDao.getData().map { it.fromItem() }, SAVED)
        }
    }


    data class Content(val data: List<GeoCodeItem>, val purpose: Int)
}