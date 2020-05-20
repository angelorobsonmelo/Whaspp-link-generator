package br.com.angelorobson.whatsapplinkgenerator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.angelorobson.whatsapplinkgenerator.utils.ActivityService

class Activity : AppCompatActivity() {

    private lateinit var activityService: ActivityService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
        activityService = applicationContext.component.activityService()
        activityService.onCreate(this)
    }

    override fun onDestroy() {
        activityService.onDestroy(this)
        super.onDestroy()
    }
}
