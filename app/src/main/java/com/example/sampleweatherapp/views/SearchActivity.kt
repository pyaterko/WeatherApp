package com.example.sampleweatherapp.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.elveum.elementadapter.simpleAdapter
import com.example.sampleweatherapp.R
import com.example.sampleweatherapp.databinding.ActivitySearchBinding
import com.example.sampleweatherapp.databinding.ItemFavoriteCityBinding
import com.example.sampleweatherapp.databinding.ItemSearchCityBinding
import com.example.sampleweatherapp.model.api.models.geocod.GeoCodeItem
import com.example.sampleweatherapp.presenters.SearchPresenter
import com.example.sampleweatherapp.untils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("ktPropBy")
class SearchActivity : MvpAppCompatActivity(), SearchView {

    private val binding by viewBinding(ActivitySearchBinding::inflate)
    private val citySearchAdapter = getCitySearchAdapter()
    private val favoriteCityAdapter = getFavoriteCityAdapter()
    private val presenter by moxyPresenter { SearchPresenter() }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        presenter.enable()
        presenter.getFavoriteList()
        initRV()
        initSearch()
        binding.etSearch.createObservable()
            .doOnNext { setLoading(true) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isNotEmpty()) {
                    presenter.getGeoByCityName(it)
                }
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    @Suppress("DEPRECATION")
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_back)
    }



    //------------------------init-------------------------

    @SuppressLint("CheckResult")
    private fun initSearch() {
        binding.etSearch.createObservable()
            .doOnNext { setLoading(true) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

            }
    }

    private fun initRV() = with(binding) {
        rvSearchCity.apply {
            adapter = citySearchAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
        rvFavorite.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = favoriteCityAdapter
        }
    }

    //--------------------------moxy-----------------------------------------------
    override fun fillSearchCityList(data: List<GeoCodeItem>) = with(binding) {
        val list = data.filter { it.local_names?.ru != null }
        containerRvSearchFavorite.isVisible = data.isNotEmpty()
        citySearchAdapter.submitList(list)
    }

    override fun fillFavoriteList(data: List<GeoCodeItem>) {
        favoriteCityAdapter.submitList(data)
    }

    override fun setLoading(flag: Boolean) = with(binding) {
        pbSearchFavorite.isActivated = flag
        pbSearchFavorite.visibility = if (flag) View.VISIBLE else View.GONE
    }


//------------------------adapters-------------------------


    private fun getCitySearchAdapter() =
        simpleAdapter<GeoCodeItem, ItemSearchCityBinding> {
            areItemsSame =
                { oldItem, newItem ->
                    oldItem.hashCode() == newItem.hashCode()
                }
            areContentsSame = { oldItem, newItem -> oldItem == newItem }
            bind { item ->

                if (item.local_names?.ru != null && item.local_names.ru.isNotEmpty()) {
                    ivFavorite.setBackgroundResource(item.isFavorite.getIconFavorite())
                    tvCityName.text = item.getCityName()
                    tvState.text = Locale("", item.country).displayName
                }


            }
            listeners {
                ivFavorite.onClick {
                    presenter.savedCity(it.copy(isFavorite = true))
                    presenter.getFavoriteList()
                }
            }
        }

    private fun getFavoriteCityAdapter() =
        simpleAdapter<GeoCodeItem, ItemFavoriteCityBinding> {
            areItemsSame = { oldItem, newItem -> oldItem == newItem }
            bind { item ->
                if (item.local_names?.ru != null) {
                    item.local_names.ru.let { tvCityName.text = it }
                    tvState.text = Locale("", item.country).displayName
                }
            }
            listeners {
                root.onClick { item ->
                    showWeatherIn(item)
                }
                ivFavorite.onClick {
                    presenter.deleteCity(it)
                }
            }
        }

    private fun showWeatherIn(item: GeoCodeItem) {
        Intent(this@SearchActivity, MainActivity::class.java).apply {
            val coordinates = Bundle().apply {
                putString(LAT, item.lat.toString())
                putString(LON, item.lon.toString())
            }
            putExtra(COORDINATES, coordinates)
            startActivity(this)
            overridePendingTransition(R.anim.fade_in, R.anim.slide_back)
            finish()
        }
    }
}



