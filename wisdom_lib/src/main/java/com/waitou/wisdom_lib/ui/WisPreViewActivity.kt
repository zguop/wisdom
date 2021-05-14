package com.waitou.wisdom_lib.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.call.LoaderMedia
import com.waitou.wisdom_lib.loader.MediaCollection

/**
 * auth aboom
 * date 2019-06-06
 */
abstract class WisPreViewActivity : AppCompatActivity(),
    LoaderMedia {

    companion object {

        /**
         * 预览模式，可以编辑勾选
         */
        internal const val WIS_PREVIEW_MODULE_TYPE_EDIT = 0x01

        /**
         * 预览模式，不可以编辑
         */
        internal const val WIS_PREVIEW_MODULE_TYPE_VISIT = 0x02

        /**
         * the this requestCode
         */
        internal const val WIS_PREVIEW_REQUEST_CODE = 0x12

        /**
         * 进入到预览页面所要带的数据
         */
        internal const val EXTRA_PREVIEW_SELECT_MEDIA = "extra_preview_select_media"

        /**
         * 浏览的起始位置
         */
        internal const val EXTRA_PREVIEW_CURRENT_POSITION = "extra_preview_current_position"

        /**
         * 预览相册的当前id
         */
        internal const val EXTRA_PREVIEW_ALBUM_ID = "extra_preview_albumId"

        /**
         * 预览模式 {WIS_PREVIEW_MODULE_TYPE_EDIT or WIS_PREVIEW_MODULE_TYPE_VISIT}
         */
        internal const val EXTRA_PREVIEW_MODULE_TYPE = "extra_preview_module_type"

        /**
         * 返回上一页或者退出相册
         */
        internal const val EXTRA_PREVIEW_RESULT_EXIT = "extra_preview_exit"

        /**
         * 显示原图按钮
         */
        internal const val EXTRA_FULL_IMAGE = "extra_full_image"

        @JvmStatic
        fun getIntent(
            context: Context,
            clazz: Class<out WisPreViewActivity>,
            selectMedias: List<Media>,
            currentPosition: Int,
            albumId: String?,
            fullImage: Boolean,
            moduleType: Int
        ): Intent {
            val i = Intent(context, clazz)
            i.putParcelableArrayListExtra(EXTRA_PREVIEW_SELECT_MEDIA, ArrayList(selectMedias))
            i.putExtra(EXTRA_PREVIEW_CURRENT_POSITION, currentPosition)
            i.putExtra(EXTRA_PREVIEW_ALBUM_ID, albumId)
            i.putExtra(EXTRA_PREVIEW_MODULE_TYPE, moduleType)
            i.putExtra(EXTRA_FULL_IMAGE, fullImage)
            return i
        }
    }


    private val mediaCollection by lazy { MediaCollection() }

    private lateinit var albumId: String
    private var previewModule: Int = WIS_PREVIEW_MODULE_TYPE_VISIT

    lateinit var selectMedias: ArrayList<Media>
    protected var currentPosition: Int = 0
    protected var fullImage: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewModule = intent.getIntExtra(EXTRA_PREVIEW_MODULE_TYPE, previewModule)
        selectMedias = intent.getParcelableArrayListExtra(EXTRA_PREVIEW_SELECT_MEDIA)
        currentPosition = intent.getIntExtra(EXTRA_PREVIEW_CURRENT_POSITION, currentPosition)
        albumId = intent.getStringExtra(EXTRA_PREVIEW_ALBUM_ID)
        fullImage = intent.getBooleanExtra(EXTRA_FULL_IMAGE, false)
    }

    override fun onStart() {
        super.onStart()
        startLoading()
    }

    open fun startLoading() {
        if (albumId.isNotEmpty()) {
            mediaCollection.onCreate(this, this)
            mediaCollection.loadMedia(albumId)
        } else {
            mediaResult(selectMedias.toList())
        }
    }

    override fun onBackPressed() {
        onResultFinish(false)
    }

    /**
     * true 可以操作checkBox的模式
     */
    fun isEditor(): Boolean {
        return previewModule == WIS_PREVIEW_MODULE_TYPE_EDIT
    }

    /**
     * 离开此页
     */
    fun onResultFinish(exit: Boolean) {
        val i = Intent()
        i.putParcelableArrayListExtra(EXTRA_PREVIEW_SELECT_MEDIA, selectMedias)
        i.putExtra(EXTRA_PREVIEW_RESULT_EXIT, exit)
        i.putExtra(EXTRA_FULL_IMAGE, fullImage)
        setResult(Activity.RESULT_OK, i)
        //如果推出了就直接finish，否则就当做back返回
        if (exit) finish() else super.onBackPressed()
    }
}
