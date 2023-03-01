package com.example.sampleweatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elveum.elementadapter.simpleAdapter
import com.example.sampleweatherapp.databinding.ActivityMainBinding
import com.example.sampleweatherapp.databinding.ItemHorizontalBinding
import com.example.sampleweatherapp.databinding.ItemVerticalBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}