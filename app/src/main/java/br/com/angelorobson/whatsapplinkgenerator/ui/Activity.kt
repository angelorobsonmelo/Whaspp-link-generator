package br.com.angelorobson.whatsapplinkgenerator.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.ActivityService
import kotlinx.android.synthetic.main.activity.*


class Activity : AppCompatActivity() {

    private lateinit var activityService: ActivityService
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
        setupToolbar()

        activityService = applicationContext.component.activityService()
        activityService.onCreate(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.navigationHostFragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onDestroy() {
        activityService.onDestroy(this)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || return super.onSupportNavigateUp()

    }

}
