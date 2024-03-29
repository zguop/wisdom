package com.waitou.wisdaoapp

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.*
import com.waitou.wisdaoapp.engine.camera.CustomCameraEngine
import com.waitou.wisdaoapp.engine.compress.TinyCompressEngine
import com.waitou.wisdaoapp.engine.crop.CropperEngine
import com.waitou.wisdaoapp.engine.crop.UCropEngine
import com.waitou.wisdaoapp.engine.image.GlideEngine
import com.waitou.wisdaoapp.engine.image.PicassoEngine
import com.waitou.wisdom_impl.ui.PhotoPreviewActivity
import com.waitou.wisdom_impl.ui.PhotoWallActivity
import com.waitou.wisdom_impl.utils.formatSize
import com.waitou.wisdom_impl.view.GridSpacingItemDecoration
import com.waitou.wisdom_lib.Wisdom
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.interfaces.CropEngine
import com.waitou.wisdom_lib.interfaces.ImageEngine
import com.waitou.wisdom_lib.config.ofAll
import com.waitou.wisdom_lib.config.ofImage
import com.waitou.wisdom_lib.config.ofVideo
import com.waitou.wisdom_lib.interfaces.CameraEngine
import com.waitou.wisdom_lib.interfaces.CompressEngine
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {


    private var isCamera = true
    private var ofType = ofAll()
    private var imageEngine: ImageEngine = GlideEngine()
    private var compressEngine: CompressEngine? = null
    private var cropEngine: CropEngine? = null
    private var cameraEngine: CameraEngine? = null
    private var selectLimit = 2
    private var isCrop = false
    private var cropType = R.id.ucrop
    private var compressId = R.id.tiny
    private var resultMedia: List<Media>? = null
    private var imageFilterMaxFile: Long? = null
    private var videoFilterMaxFile: Long? = null
    private var mimeTypeSet: MutableSet<String>? = null

    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
//        UriUtils.uri2File()
        //配置代码
        go.setOnClickListener {
            //跳转到相册选择
            //有裁剪 压缩， 先裁剪，裁剪的图再压缩
            Wisdom.of(this@MainActivity)
                .config(ofType) //选择类型 ofAll() ofImage() ofVideo()
                .imageEngine(imageEngine) //图片加载引擎
                .compressEngine(compressEngine)
                .cropEngine(cropEngine)
                .selectLimit(selectLimit) //选择的最大数量 数量1为单选模式
                .fileProvider("$packageName.utilcode.provider", AppUtils.getAppName())
                .isCamera(isCamera) //是否打开相机，
                .cameraEngine(cameraEngine)
                .setMedias(resultMedia)
                .hasFullImage(true)
                .filterImageMaxFileSize(imageFilterMaxFile)
                .filterVideoMaxFileSize(videoFilterMaxFile)
                .mimeTypeSet(mimeTypeSet, false)
                .forResult(
                    0x11,
                    PhotoWallActivity::class.java
                ) //requestCode，界面实现Activity，需要继承于核心库WisdomWallActivity

        }

        action.setOnClickListener {
            resultMedia?.let {
                Wisdom.of(this@MainActivity)
                    .preview()
                    .imageEngine(imageEngine)
                    .setMedias(resultMedia!!)
                    .go(PhotoPreviewActivity::class.java)
            }
        }

        externalStorage.setOnClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            startActivity(intent)
        }
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
            val fullImage = Wisdom.isFullImage(data)
            resultMedia = Wisdom.obtainResult(data) //获取回调数据

            resultMedia?.also {
                it.forEach { media ->
                    Log.e("aa", " ===================================== ")
                    Log.e("aa", " fullImage=$fullImage")
                    Log.e("aa", " uri=" + media.uri)
                    Log.e("aa", " path=" + media.path)
                    Log.e("aa", " cropUri=" + media.cropUri)
                    Log.e("aa", " compressUri=" + media.compressUri)
                    Log.e("aa", " size=" + media.size)
                    Log.e("aa", " width=" + media.width)
                    Log.e("aa", " height=" + media.height)
                    Log.e("aa", " orientation=" + media.orientation)
                    Log.e("aa", " mimeType=" + media.mineType)
                    Log.e("aa", " duration=" + media.duration)
                    Log.e("aa", " fileName=" + media.displayName)
                    Log.e("aa", " md5=" + contentResolver.openInputStream(media.uri)?.encryptMD5InputStream())
                    Log.e("aa", " ===================================== ")
                }

                this.adapter.addData(it.map { media ->

                    val closeable = Utils.getApp().contentResolver.openFileDescriptor(media.compressOrCropNullToUri(), "r")
                    val parcelFileDescriptor = closeable as ParcelFileDescriptor
                    val l = parcelFileDescriptor.statSize
                    PathBean(
                        media.compressOrCropNullToUri(),
                        l.formatSize()
                    )
                })
            }
        }
    }

    fun InputStream?.encryptMD5InputStream(): String {
        return this?.use {
            var md = MessageDigest.getInstance("MD5")
            val digestInputStream = DigestInputStream(it, md)
            val buffer = ByteArray(256 * 1024)
            while (true) {
                if (digestInputStream.read(buffer) <= 0) break
            }
            md = digestInputStream.messageDigest
            ConvertUtils.bytes2HexString(md.digest())
        } ?: ""
    }


    private fun init() {
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

        filter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                imageFilterMaxFile = if (s.isNullOrEmpty()) null else s.toString().toLong() * 1024 * 1024
            }
        })

        videoFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                videoFilterMaxFile = if (s.isNullOrEmpty()) null else s.toString().toLong() * 1024 * 1024
            }
        })

        //相机
        camera.setOnCheckedChangeListener { _, isChecked ->
            isCamera = isChecked
            customCameraEngine.visibility = if (isCamera) View.VISIBLE else View.GONE
        }

        customCameraEngine.setOnCheckedChangeListener { _, isChecked ->
            cameraEngine = if (isChecked) {
                CustomCameraEngine()
            } else {
                null
            }
        }

        gif.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mimeTypeSet = mutableSetOf("image/gif")
            } else {
                mimeTypeSet = null
            }
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
                R.id.glide -> imageEngine =
                    GlideEngine()
                R.id.picasso -> imageEngine =
                    PicassoEngine()
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
            updateCropTool()
        }

        compress.setOnCheckedChangeListener { _, isChecked ->
            radio4.visibility = if (isChecked) View.VISIBLE else View.GONE
            updateCompress()
        }

        radio4.setOnCheckedChangeListener { _, checkedId ->
            compressId = checkedId
            updateCompress()
        }

    }

    private fun updateCompress() {
        if (radio4.visibility == View.GONE) {
            compressEngine = null
            return
        }
        compressEngine = if (R.id.tiny == compressId) {
            TinyCompressEngine()
        } else {
            null
        }
    }


    private fun updateCropTool() {
        radio3.visibility = if (isCrop) View.VISIBLE else View.GONE
        radio3.check(cropType)

        cropEngine = if (isCrop) {
            if (cropType == R.id.ucrop) {
                UCropEngine()
            } else {
                CropperEngine()
            }
        } else {
            null
        }
    }

    private fun updateSelectLimit() {
        selectLimit = if (num.text.isNullOrEmpty()) {
            9
        } else num.text.toString().toInt()
        crop.visibility = if (selectLimit == 1) View.VISIBLE else View.GONE
        radio3.visibility = crop.visibility
    }

    override fun onPause() {
        super.onPause()
        KeyboardUtils.hideSoftInput(this)
    }
}
