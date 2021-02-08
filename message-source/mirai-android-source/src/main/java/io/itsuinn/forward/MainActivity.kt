package io.itsuinn.forward

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.multidex.MultiDex

class MainActivity : AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)
      MultiDex.install(this)
   }
}