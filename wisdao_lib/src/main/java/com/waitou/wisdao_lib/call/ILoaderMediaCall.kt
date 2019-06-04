package com.waitou.wisdao_lib.call

import com.waitou.wisdao_lib.bean.Media

/**
 * auth aboom
 * date 2019-05-26
 */
interface ILoaderMediaCall {
    fun mediaResult(medias: List<Media>)
}
