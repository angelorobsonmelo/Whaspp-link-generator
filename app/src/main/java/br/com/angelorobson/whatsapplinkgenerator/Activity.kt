package br.com.angelorobson.whatsapplinkgenerator

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.angelorobson.whatsapplinkgenerator.utils.ActivityService
import br.com.angelorobson.whatsapplinkgenerator.utils.WHAT_APP_RESULT_CODE


class Activity : AppCompatActivity() {

    private lateinit var activityService: ActivityService
    var count = 0

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

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        count++
        if (requestCode == WHAT_APP_RESULT_CODE && count == 2) {
            val i = Intent(this, Activity::class.java)
            i.flags = FLAG_ACTIVITY_NO_HISTORY
            startActivity(i)
        }
        println(requestCode)
    }
}
