package com.waitou.wisdom_lib.interfaces

import com.waitou.wisdom_lib.bean.Album

/**
 * auth aboom
 * date 2019-05-26
 */
interface LoaderAlbum {
    fun albumResult(albums: List<Album>)
}

