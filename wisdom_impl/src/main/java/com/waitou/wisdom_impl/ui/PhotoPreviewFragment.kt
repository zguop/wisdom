package com.waitou.wisdom_impl.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.github.chrisbanes.photoview.PhotoView
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.utils.tdp
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig

/**
 * auth aboom
 * date 2019-06-07
 */
class PhotoPreviewFragment : Fragment() {

    companion object {
        private const val EXTRA_DATA = "extra_data"
        fun newInstance(media: Media): PhotoPreviewFragment {
            return PhotoPreviewFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable(EXTRA_DATA, media)
                arguments = bundle
            }
        }
    }

    private val media by lazy { arguments?.getParcelable<Media>(EXTRA_DATA) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wis_fragment_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = view.findViewById<PhotoView>(R.id.image)
        image.setOnClickListener { (requireActivity() as PhotoPreviewActivity).onOutsidePhotoTap() }
        media?.let {
            if (it.isVideo()) {
                val videoPlay = View(activity)
                videoPlay.setBackgroundResource(R.drawable.wis_svg_ic_video_play)
                (view as ViewGroup).addView(videoPlay, FrameLayout.LayoutParams(60.tdp(requireContext()), 60.tdp(requireContext()), Gravity.CENTER))
                videoPlay.setOnClickListener { _ ->
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(it.uri, it.mineType)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            WisdomConfig.getInstance().imageEngine?.displayPreviewImage(image, it.uri, 480, 800, it.isGif())
        }
    }
}