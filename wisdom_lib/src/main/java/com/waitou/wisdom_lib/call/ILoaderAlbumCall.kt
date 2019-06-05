package com.waitou.wisdom_lib.call

import com.waitou.wisdom_lib.bean.Album

/**
 * auth aboom
 * date 2019-05-26
 */
interface ILoaderAlbumCall {
    fun albumResult(albums: List<Album>)
}

