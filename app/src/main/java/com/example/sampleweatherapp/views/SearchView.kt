package com.example.sampleweatherapp.views

import com.example.sampleweatherapp.model.api.models.geocod.GeoCodeItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface SearchView : MvpView {
    @AddToEndSingle
    fun fillSearchCityList(data: List<GeoCodeItem>)

    @AddToEndSingle
    fun fillFavoriteList(data: List<GeoCodeItem>)

    @AddToEndSingle
    fun setLoading(flag: Boolean)

}
