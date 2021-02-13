package com.araujotadeu.dot_indicator.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // TODO
    // Auto and manual modes

    var index = 0
    var max = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {

            testDefault.setCurrent(index)
            testExpanded.setCurrent(index)
            testWorm.setCurrent(index)

            index += 1
            if (index == max) {
                index = 0
            }
        }
    }
}