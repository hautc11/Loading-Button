package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ContentDetailBinding
import com.udacity.util.Constants
import kotlinx.android.synthetic.main.activity_detail.toolbar
import kotlinx.android.synthetic.main.content_detail.btnOk

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ContentDetailBinding
    private lateinit var fileName: String
    private lateinit var status: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.content_detail)
        setSupportActionBar(toolbar)

        fileName = intent.getStringExtra(Constants.FILE_NAME) ?: Constants.EMPTY
        status = intent.getStringExtra(Constants.STATUS) ?: Constants.EMPTY

        if (status == Constants.STATUS_SUCCESS) {
            binding.tvStatus.setTextColor(getColor(R.color.green))
        } else {
            binding.tvStatus.setTextColor(getColor(R.color.red))
        }

        binding.tvFileName.text = fileName
        binding.tvStatus.text = status

        btnOk.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

}
