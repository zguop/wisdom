package com.waitou.wisdom_lib.call

import com.waitou.wisdom_lib.bean.ResultMedia

/**
 * auth aboom
 * date 2019-06-01
 */
interface OnResultMediaListener{
    fun onResultFinish(resultMedias: ArrayList<ResultMedia>)
}
