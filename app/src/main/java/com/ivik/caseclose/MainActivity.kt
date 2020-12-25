package com.ivik.caseclose

import android.Manifest
import android.Manifest.permission.WRITE_SETTINGS
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.material.internal.ContextUtils.getActivity
import com.kishan.askpermission.AskPermission
import com.kishan.askpermission.ErrorCallback
import com.kishan.askpermission.PermissionCallback
import com.kishan.askpermission.PermissionInterface


class MainActivity : AppCompatActivity(), PermissionCallback {
    var service = OnCaseClosedPowerOffService()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<Button>(R.id.stop_btn).setOnClickListener {
            stopService()
        }

        findViewById<Button>(R.id.permission_btn).setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.setData(Uri.parse("package:$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        findViewById<Button>(R.id.start_btn).setOnClickListener {
            startService()
        }

        AskPermission.Builder(this)
            .setPermissions(Manifest.permission.FOREGROUND_SERVICE)
            .setCallback(this)
            .request(1)


    }

    private fun startService() {
        startForegroundService(Intent(baseContext, service::class.java))
    }

    private fun stopService() {
        stopService(Intent(baseContext, service::class.java))
    }

    override fun onPermissionsGranted(requestCode: Int) {
        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
        if (requestCode == 1) {
            AskPermission.Builder(this)
                .setPermissions(Manifest.permission.WRITE_SETTINGS)
                .setCallback(this)
                .request(2)
        }
    }

    override fun onPermissionsDenied(requestCode: Int) {
        Toast.makeText(this, "Permissions Denied:(", Toast.LENGTH_SHORT).show()
    }

}