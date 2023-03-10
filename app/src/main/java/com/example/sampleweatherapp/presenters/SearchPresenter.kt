package com.example.sampleweatherapp.presenters

import android.annotation.SuppressLint
import com.example.sampleweatherapp.model.api.models.geocod.GeoCodeItem
import com.example.sampleweatherapp.model.repositories.SAVED
import com.example.sampleweatherapp.model.repositories.SearchRepository
import com.example.sampleweatherapp.views.SearchView
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class SearchPresenter @Inject constructor(
    private val repository: SearchRepository
) : BasePresenter<SearchView>() {

    @SuppressLint("CheckResult")
    override fun enable() {
        repository.dataEmitter.subscribe { responce ->
            viewState.setLoading(false)
            if (responce.purpose == SAVED) {
                viewState.fillFavoriteList(responce.data)
            } else {
                viewState.fillSearchCityList(responce.data)
            }

        }

    }

    fun getGeoByCityName(name: String) {
        repository.getSearchCityListWith(name)
    }

    fun deleteCity(city: GeoCodeItem) {
        repository.delete(city)
    }

    fun savedCity(city: GeoCodeItem) {
        repository.add(city)
    }

    fun getFavoriteList() {
        repository.getData()
    }

}
