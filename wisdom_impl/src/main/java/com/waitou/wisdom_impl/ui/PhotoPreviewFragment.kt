package com.waitou.wisdom_impl.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig
import kotlinx.android.synthetic.main.wis_fragment_preview.*

/**
 * auth aboom
 * date 2019-06-07
 */
class PhotoPreviewFragment : Fragment() {

    companion object {
        private const val EXTRA_DATA = "extra_data"
        fun newInstance(media: Media): PhotoPreviewFragment {
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_DATA, media)
            val fragment = PhotoPreviewFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var onPhotoTapListener: OnOutsidePhotoTapListener? = null
    private var media: Media? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnOutsidePhotoTapListener) {
            onPhotoTapListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        media = arguments?.getParcelable(EXTRA_DATA)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wis_fragment_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image.setOnClickListener { onPhotoTapListener?.onOutsidePhotoTap(null) }
        media?.let {
            //            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
//            val metrics = resources.displayMetrics
//            val point = Point(metrics.widthPixels, metrics.heightPixels)

//            Log.e("aa" ,point.toString())

//            Log.e("aa" , " bitmap w = " + bitmap.width + " H = "   + bitmap.height)
            //测试来看 这个比例目前清晰度没什么问题
            WisdomConfig.getInstance().iImageEngine?.displayImage(image, it.uri, 480, 800)
        }
    }
}