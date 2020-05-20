package br.com.angelorobson.whatsapplinkgenerator.utils

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController

class Navigator(private val viewId: Int, private val activityService: ActivityService) {

    private val navController: NavController
        get() = activityService.activity.findNavController(viewId)

    fun to(directions: NavDirections) {
        navController.navigate(directions)
    }

}