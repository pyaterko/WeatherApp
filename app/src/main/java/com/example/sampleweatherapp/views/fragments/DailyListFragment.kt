package com.example.sampleweatherapp.views.fragments

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.elveum.elementadapter.simpleAdapter
import com.example.sampleweatherapp.R
import com.example.sampleweatherapp.databinding.FragmentDailylistBinding
import com.example.sampleweatherapp.databinding.ItemVerticalBinding
import com.example.sampleweatherapp.model.api.models.Daily
import com.example.sampleweatherapp.untils.dateFormatter
import com.example.sampleweatherapp.untils.getDateFormat
import com.example.sampleweatherapp.untils.setIcon
import com.example.sampleweatherapp.untils.toDegree
import com.example.sampleweatherapp.views.viewBinding
import java.text.SimpleDateFormat
import java.util.*

@Suppress("ktPropBy")
class DailyListFragment : BaseFragment<List<Daily>>(R.layout.fragment_dailylist) {

    private val binding by viewBinding(FragmentDailylistBinding::bind)
    private val dailyAdapter = getDailyAdapter()

    override fun updateView() {
        dailyAdapter.submitList(data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvDailyList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDailyList.adapter = dailyAdapter
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun getDailyAdapter() =
        simpleAdapter<Daily, ItemVerticalBinding> {
            areItemsSame = { oldItem, newItem -> oldItem == newItem }
            bind {
                if (it.dt.getDateFormat(dateFormatter)== getCurrentDateFormat(dateFormatter)){
                   tvDay.setTextColor(ContextCompat.getColor(requireContext(),R.color.color_current_day))
                }
                tvDay.text = it.dt.getDateFormat(dateFormatter)
                tvMax.text = it.temp.max.toDegree() + getString(R.string.temp_)
                tvMin.text = it.temp.min.toDegree() + getString(R.string.temp_)
                tvAirHumidityValue.text = it.humidity.toString() + getString(R.string.humidity_)
                ivWeather.setIcon(it.weather[0].icon)
            }
            listeners {
                root.onClick {
                    launchDailyInfoFragment(it)
                }
            }
        }

    private fun launchDailyInfoFragment(day:Daily) {
        val fragment = DailyInfoFragment()
       fragment.setValueData(day)
        fragmentMgr.beginTransaction()
            .replace(R.id.fr_container, fragment)
            .addToBackStack(null)
            .commit()
    }
    @SuppressLint("SimpleDateFormat")
  private  fun getCurrentDateFormat(format: String): String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        calendar.timeInMillis = System.currentTimeMillis()
        return sdf.format(calendar.time)

    }
}