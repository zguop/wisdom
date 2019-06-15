package com.waitou.wisdaoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.theartofdev.edmodo.cropper.CropImage
import com.waitou.wisdaoapp.test.JaActivity
import com.waitou.wisdom_impl.ui.PhotoWallActivity
import com.waitou.wisdom_impl.view.GridSpacingItemDecoration
import com.waitou.wisdom_lib.Wisdom
import com.waitou.wisdom_lib.call.ImageEngine
import com.waitou.wisdom_lib.config.ofAll
import com.waitou.wisdom_lib.config.ofImage
import com.waitou.wisdom_lib.config.ofVideo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private var isCamera = true
    private var ofType = ofAll()
    private var imageEngine: ImageEngine = GlideEngine()
    private var selectLimit = 0
    private var isCrop = false
    private var cropType = R.id.ucrop


    private val cropEngine by lazy { UCropEngine() }
    private val cropperEngine by lazy { CropperEngine() }

    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.layoutManager = GridLayoutManager(this, 3)
        list.addItemDecoration(GridSpacingItemDecoration(3, 4, true))
        this.adapter = MainAdapter()
        list.adapter = this.adapter
        updateSelectLimit()
        updateCropTool()

        //数量输入
        num.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateSelectLimit()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //相机
        camera.setOnCheckedChangeListener { _, isChecked ->
            isCamera = isChecked
        }

        //选择类型
        radio.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.all -> ofType = ofAll()
                R.id.image -> ofType = ofImage()
                R.id.video -> ofType = ofVideo()
            }
        }

        //图片加载引擎
        radio2.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.glide -> imageEngine = GlideEngine()
                R.id.picasso -> imageEngine = PicassoEngine()
            }
        }

        //是否裁剪
        crop.setOnCheckedChangeListener { _, isChecked ->
            isCrop = isChecked
            updateCropTool()
        }

        //裁剪框架选择
        radio3.setOnCheckedChangeListener { _, checkedId ->
            cropType = checkedId
        }

        //配置代码
        go.setOnClickListener {
            Wisdom.of(this@MainActivity)
                .config(ofType) //选择类型 ofAll() ofImage() ofVideo()
                .imageEngine(imageEngine) //图片加载引擎
                .selectLimit(selectLimit) //选择的最大数量 数量1为单选模式
                .fileProvider("$packageName.utilcode.provider", "image") //兼容android7.0
                .isCamera(isCamera) //是否打开相机，
                .forResult(0x11, PhotoWallActivity::class.java) //requestCode，界面实现Activity，需要继承于核心库activity
        }


        action.setOnClickListener {
            startActivity(Intent(this, JaActivity::class.java))
        }
    }


    private fun updateCropTool(){
        radio3.visibility = if(isCrop) View.VISIBLE else View.GONE
        radio3.check(cropType)
    }

    private fun updateSelectLimit() {
        selectLimit = if (num.text.isNullOrEmpty()) {
            num.setText("1")
            1
        } else num.text.toString().toInt()
        crop.visibility = if (selectLimit == 1) View.VISIBLE else View.GONE
        radio3.visibility = crop.visibility
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK != resultCode) {
            return
        }
        if (data == null) {
            return
        }
        //相册回调
        if (requestCode == 0x11) {
            val resultMedia = Wisdom.obtainResult(data) //获取回调数据
            if (isCrop) {
                when (cropType) {
                    R.id.ucrop -> cropEngine.onStartCrop(this, resultMedia[0].uri, 0x12)
                    R.id.cropper -> cropperEngine.onStartCrop(this, resultMedia[0].uri)
                }
                return
            }
            this.adapter.addData(resultMedia.map { it.uri }.toList())
        }
        //裁剪回调
        if (requestCode == 0x12) {
            //file:///storage/emulated/0/Pictures/image/IMAGE_2019_06_15_00_52_29.jpg
            val onCropResult = cropEngine.onCropResult(data)
            onCropResult?.let {
                this.adapter.addData(listOf(it))
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val onCropResult = cropperEngine.onCropResult(data)
            this.adapter.addData(listOf(onCropResult))
        }
    }
}
