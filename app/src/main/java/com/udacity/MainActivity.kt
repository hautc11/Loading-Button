package com.udacity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding
import com.udacity.ui.ButtonState
import com.udacity.util.Constants
import com.udacity.util.NotificationUtil
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_main.custom_button
import kotlinx.android.synthetic.main.content_main.view.radioGroup


class MainActivity : AppCompatActivity() {

    private lateinit var downloadManager: DownloadManager
    private var url: String = ""
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(toolbar)
        NotificationUtil().createChannel(applicationContext)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        custom_button.setOnClickListener {
            if (url.isEmpty()) {
                Toast.makeText(this, "Please select file to download!", Toast.LENGTH_SHORT).show()
            } else {
                custom_button.setNewStateForButton(ButtonState.Loading)
                download(url)
            }
        }

        handleRadioButtonSelect()
    }

    private fun handleRadioButtonSelect() {
        binding.contentLayout.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioGlide    -> url = Constants.URL_GLIDE
                R.id.radioRetrofit -> url = Constants.URL_RETROFIT
                R.id.radioUdacity  -> url = Constants.URL_UDACITY
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            when (getStatusDownload(id)) {
                DownloadManager.STATUS_FAILED     -> {
                    NotificationUtil().makeANotification(
                        applicationContext,
                        url,
                        Constants.STATUS_FAILED
                    )
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    NotificationUtil().makeANotification(
                        applicationContext,
                        url,
                        Constants.STATUS_SUCCESS
                    )
                }
            }
        }
    }

    private fun getStatusDownload(id: Long?): Int {
        id ?: return DownloadManager.STATUS_FAILED
        val query = DownloadManager.Query().setFilterById(id)
        return (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
            .query(query)
            .use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                } else {
                    DownloadManager.STATUS_FAILED
                }
            }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


        downloadManager.enqueue(request)
    }
}
