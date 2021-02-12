package com.araujotadeu.dot_indicator.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var a = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {

            if (!a) {
                testDefault.setCurrent(0)
                testExpanded.setCurrent(0)
                testWorm.setCurrent(0)
            } else {
                testDefault.setCurrent(2)
                testExpanded.setCurrent(2)
                testWorm.setCurrent(2)
            }

            a = !a

        }
    }
}