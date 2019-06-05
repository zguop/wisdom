package com.waitou.wisdaoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.waitou.wisdom_impl.ui.PhotoWallActivity
import com.waitou.wisdom_lib.Wisdom
import com.waitou.wisdom_lib.config.ofAll
import com.waitou.wisdom_lib.config.ofImage
import com.waitou.wisdom_lib.config.ofVideo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var isCamera = true
    private var ofType = ofAll()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        camera.setOnCheckedChangeListener { _, isChecked ->
            isCamera = isChecked
        }

        radio.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.all -> ofType = ofAll()
                R.id.image -> ofType = ofImage()
                R.id.video -> ofType = ofVideo()
            }
        }

        go.setOnClickListener {
            Wisdom.of(this@MainActivity)
                .config(ofType)
                .imageEngine(GlideEngine())
                .selectLimit(Integer.valueOf(num.text.toString()))
                .fileProvider("$packageName.utilcode.provider", "image")
                .isCamera(isCamera)
                .forResult(0x11, PhotoWallActivity::class.java)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK == resultCode) {
            if (requestCode == 0x11 && data != null) {
                val resultMedia = Wisdom.obtainResult(data)
                Log.e("aa", resultMedia.toString())

            }
        }
    }
}
