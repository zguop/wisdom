package com.waitou.wisdaoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.waitou.wisdaoapp.test.JaActivity
import com.waitou.wisdom_impl.ui.PhotoWallActivity
import com.waitou.wisdom_impl.view.GridSpacingItemDecoration
import com.waitou.wisdom_lib.Wisdom
import com.waitou.wisdom_lib.config.ofAll
import com.waitou.wisdom_lib.config.ofImage
import com.waitou.wisdom_lib.config.ofVideo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var isCamera = true
    private var ofType = ofAll()


    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.layoutManager = GridLayoutManager(this, 3)
        list.addItemDecoration(GridSpacingItemDecoration(3, 4, true))
        this.adapter = MainAdapter()
        list.adapter = this.adapter


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
                .config(ofType) //选择类型 ofAll() ofImage() ofVideo()
                .imageEngine(GlideEngine()) //图片加载引擎
                .selectLimit(Integer.valueOf(num.text.toString())) //选择的最大数量 数量1为单选模式
                .fileProvider("$packageName.utilcode.provider", "image") //兼容android7.0
                .isCamera(isCamera) //是否打开相机，
                .forResult(0x11, PhotoWallActivity::class.java) //requestCode，界面实现Activity，需要继承于核心库activity
        }


        action.setOnClickListener {
            startActivity(Intent(this, JaActivity::class.java))
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK == resultCode) {
            if (requestCode == 0x11 && data != null) {
                val resultMedia = Wisdom.obtainResult(data) //获取回调数据
                this.adapter.replaceMedias(resultMedia)
            }
        }
    }
}
