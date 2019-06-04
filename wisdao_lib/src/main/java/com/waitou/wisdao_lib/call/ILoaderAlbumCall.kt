package com.waitou.wisdao_lib.call

import com.waitou.wisdao_lib.bean.Album

/**
 * auth aboom
 * date 2019-05-26
 */
interface ILoaderAlbumCall {
    fun albumResult(albums: List<Album>)
}

