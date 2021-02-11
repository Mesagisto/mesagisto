package io.itsusinn.forward

import android.os.Bundle
import android.support.multidex.MultiDex
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)
      MultiDex.install(this)
   }
}
