package com.vhenriquez.txwork.utils

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

class PermissionRequester(private val componentActivity: ComponentActivity,
                          private val permission : String,
                          private val onRationale : () -> Unit = {},
                          private val onDenied : () -> Unit = {}) {
    private var onGranted : () -> Unit = {}
    private val permissionLauncher =
        componentActivity.registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted->
            when{
                isGranted -> onGranted()
                componentActivity.shouldShowRequestPermissionRationale(permission)->
                    onRationale()
                else -> onDenied()
            }
        }

    fun runWithPermission(body : () -> Unit){
        onGranted = body
        permissionLauncher.launch(permission)
    }
}