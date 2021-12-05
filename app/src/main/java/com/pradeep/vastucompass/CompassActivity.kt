package com.pradeep.vastucompass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider

class CompassActivity : AppCompatActivity() {
    private lateinit var viewModel: CompassViewModel
    private lateinit var imgView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(CompassViewModel::class.java)
        init()
        initObservable()
    }

    private fun init(){
       imgView = findViewById(R.id.imageViewCompass)
    }

    private fun initObservable(){
        viewModel.angleDegreeLiveData.observe(this, {
            val rotateAnimation = RotateAnimation(
                viewModel.currentDegree,
                -it,
                RELATIVE_TO_SELF, 0.5f,
                RELATIVE_TO_SELF, 0.5f)
            rotateAnimation.duration = 1000
            rotateAnimation.fillAfter = true

            rotateAnimation.apply {
                duration = 5000
                interpolator = LinearInterpolator()
            }
            imgView.startAnimation(rotateAnimation)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.registerSensorManager()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unRegisterSensorManager()
    }

}