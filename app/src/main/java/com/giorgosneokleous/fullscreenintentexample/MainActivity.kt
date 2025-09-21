/*
 * MIT License
 *
 * Copyright (c) 2020 Giorgos Neokleous
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.giorgosneokleous.fullscreenintentexample

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    enum class Options {
        SHOW_NOTIFICATION_FULL_SCREEN,
        SCHEDULE_NOTIFICATION_TRUE,
        SCHEDULE_NOTIFICATION_FALSE
    }

    var currentOptions: Options = Options.SHOW_NOTIFICATION_FULL_SCREEN
    val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when (currentOptions) {
                Options.SHOW_NOTIFICATION_FULL_SCREEN -> showNotificationWithFullScreenIntent()
                Options.SCHEDULE_NOTIFICATION_TRUE -> scheduleNotification(true)
                Options.SCHEDULE_NOTIFICATION_FALSE -> scheduleNotification(false)
            }
        }

    val multiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionMap ->
            if (permissionMap.all {
                    ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        it.key
                    ) == PackageManager.PERMISSION_GRANTED
                }) {
                when (currentOptions) {
                    Options.SHOW_NOTIFICATION_FULL_SCREEN -> showNotificationWithFullScreenIntent()
                    Options.SCHEDULE_NOTIFICATION_TRUE -> scheduleNotification(true)
                    Options.SCHEDULE_NOTIFICATION_FALSE -> scheduleNotification(false)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.showFullScreenIntentButton).setOnClickListener {
            currentOptions = Options.SHOW_NOTIFICATION_FULL_SCREEN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else
                showNotificationWithFullScreenIntent()
        }

        findViewById<Button>(R.id.showFullScreenIntentWithDelayButton).setOnClickListener {
            currentOptions = Options.SCHEDULE_NOTIFICATION_FALSE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                multiplePermissionsLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.POST_NOTIFICATIONS,
                        android.Manifest.permission.USE_EXACT_ALARM
                    )
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionLauncher.launch(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
            } else
                scheduleNotification(false)
        }

        findViewById<Button>(R.id.showFullScreenIntentLockScreenWithDelayButton).setOnClickListener {
            currentOptions = Options.SCHEDULE_NOTIFICATION_TRUE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                multiplePermissionsLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.POST_NOTIFICATIONS,
                        android.Manifest.permission.USE_EXACT_ALARM
                    )
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionLauncher.launch(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
            } else
                scheduleNotification(true)
        }
    }
}

