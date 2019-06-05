package com.waitou.basic_lib.photo.viewmodule

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.bean.Media

/**
 * auth aboom
 * date 2019-05-26
 */
class PhotoWallViewModule : ViewModel() {

    val albumLiveData = MutableLiveData<List<Album>>()

    val selectCountLiveData = MutableLiveData<List<Media>>()

}
