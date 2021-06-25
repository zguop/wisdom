package com.waitou.wisdom_lib.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.interfaces.LoaderMedia
import com.waitou.wisdom_lib.loader.MediaCollection

/**
 * auth aboom
 * date 2019-06-06
 */
abstract class WisPreViewActivity : AppCompatActivity(),
    LoaderMedia {

    companion object {
        //预览模式，可以编辑勾选
        internal const val WIS_PREVIEW_MODULE_TYPE_EDIT = 0x01

        //预览模式，不可以编辑
        internal const val WIS_PREVIEW_MODULE_TYPE_VISIT = 0x02

        //进入到预览页面所要带的数据
        internal const val EXTRA_PREVIEW_SELECT_MEDIA = "extra_preview_select_media"

        //浏览的起始位置
        internal const val EXTRA_PREVIEW_CURRENT_POSITION = "extra_preview_current_position"

        //预览相册的当前id
        internal const val EXTRA_PREVIEW_ALBUM_ID = "extra_preview_albumId"

        // 预览模式 {WIS_PREVIEW_MODULE_TYPE_EDIT or WIS_PREVIEW_MODULE_TYPE_VISIT}
        internal const val EXTRA_PREVIEW_MODULE_TYPE = "extra_preview_module_type"

        //返回上一页或者退出相册
        internal const val EXTRA_PREVIEW_RESULT_EXIT = "extra_preview_exit"

        //显示原图按钮
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
            return Intent(context, clazz).apply {
                putParcelableArrayListExtra(EXTRA_PREVIEW_SELECT_MEDIA, ArrayList(selectMedias))
                putExtra(EXTRA_PREVIEW_CURRENT_POSITION, currentPosition)
                putExtra(EXTRA_PREVIEW_ALBUM_ID, albumId)
                putExtra(EXTRA_PREVIEW_MODULE_TYPE, moduleType)
                putExtra(EXTRA_FULL_IMAGE, fullImage)
            }
        }
    }

    private val mediaCollection by lazy { MediaCollection(activity = this, loaderMedia = this) }

    private lateinit var albumId: String
    lateinit var selectMedias: ArrayList<Media>

    private var previewModule: Int = WIS_PREVIEW_MODULE_TYPE_VISIT

    protected var currentPosition: Int = 0
    protected var fullImage: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewModule = intent.getIntExtra(EXTRA_PREVIEW_MODULE_TYPE, previewModule)
        selectMedias = intent.getParcelableArrayListExtra(EXTRA_PREVIEW_SELECT_MEDIA) ?: arrayListOf()
        currentPosition = intent.getIntExtra(EXTRA_PREVIEW_CURRENT_POSITION, currentPosition)
        albumId = intent.getStringExtra(EXTRA_PREVIEW_ALBUM_ID).orEmpty()
        fullImage = intent.getBooleanExtra(EXTRA_FULL_IMAGE, false)
    }

    override fun onStart() {
        super.onStart()
        startLoading()
    }

    open fun startLoading() {
        if (albumId.isNotEmpty()) {
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
     * @param exit true 退出选择 false 关闭选择页面
     */
    fun onResultFinish(exit: Boolean) {
        val i = Intent()
        i.putParcelableArrayListExtra(EXTRA_PREVIEW_SELECT_MEDIA, selectMedias)
        i.putExtra(EXTRA_PREVIEW_RESULT_EXIT, exit)
        i.putExtra(EXTRA_FULL_IMAGE, fullImage)
        setResult(Activity.RESULT_OK, i)
        finish()
    }
}
