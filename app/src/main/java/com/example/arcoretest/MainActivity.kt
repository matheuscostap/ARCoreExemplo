package com.example.arcoretest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_basic_model.setOnClickListener {
            val intent = Intent(this, BasicModelActivity::class.java)
            startActivity(intent)
        }

        btn_augmented_image.setOnClickListener {
            val intent = Intent(this, AugmentedImageActivity::class.java)
            startActivity(intent)
        }
    }
}
