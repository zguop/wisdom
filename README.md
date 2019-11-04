# Wisdom 多图片选择库 

### 项目介绍

kotlin版本的图片视频选择器，多图片视频选择，权限适配，项目支持UI界面完全自定义，包含 wisdom_lib 和 默认UI实现的wisdom_impl，两者完全隔离。如果不满意当前实现的UI，可以依赖于wisdom_lib，实现一套符合项目需要的图片选择库。

### 核心库 wisdom_lib
- 不依赖任何第三方库（轻量）
- 不具备任何UI处理(基类提供实现)
- 兼容不同版本，对6.0适配权限处理，7.0使用FileProvider获取图片路径(fileProvider自定义配置)
- 提供核心的图片查询结果封装返回
- 实现该库，只关心界面逻辑即可
- 支持图片，视频选择，多数量选择
- 支持打开拍照，录像
- 支持自定义图片加载框架
- 支持gif图片

### 默认UI实现库 wisdom_impl
- 提供一套默认的UI实现
- 可以基于这个实现基础上进行修改
- 也可以根据这个样板，自己实现一套UI操作
- 基于核心库，实现了核心库所有功能


### 依赖
```groovy
dependencies {
    //核心库版本
    compile 'com.waitou:wisdom_lib:1.0.9'
    //UI版本
    compile 'com.waitou:wisdom_impl:1.0.9'
}
```
### 使用
只配置需要的api即可
```groovy
Wisdom.of(this@MainActivity)
     .config(ofType) //选择类型 ofAll() ofImage() ofVideo()
     .imageEngine(GlideEngine()) //图片加载引擎
     .selectLimit(Integer.valueOf(num.text.toString())) //选择的最大数量 数量1为单选模式
     .fileProvider("$packageName.utilcode.provider", "image") //兼容android7.0
     .isCamera(isCamera) //是否打开相机，
     .forResult(0x11, PhotoWallActivity::class.java) //requestCode，界面实现Activity，需要继承于核心库activity
     
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (Activity.RESULT_OK == resultCode) {
        if (requestCode == 0x11 && data != null) {
            val resultMedia = Wisdom.obtainResult(data) //获取回调数据 类型Media 包含String path， Uri uri 路径
            Log.e("aa", resultMedia.toString())
        }
    }
}

//直接进入预览页面 预览图片

Wisdom.of(this@MainActivity)
     .preview()
     .imageEngine(imageEngine)
     .setMedias(List<Media>)
     .go(PhotoPreviewActivity::class.java)

Wisdom.of(this@MainActivity)
     .preview()
     .imageEngine(imageEngine)
     .setPaths(listOf("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=255586071,2019273368&fm=26&gp=0.jpg",
                    "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2028868596,3857587342&fm=26&gp=0.jpg",
                    "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3147757822,2248639000&fm=26&gp=0.jpg",
                    "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=248541496,3500754578&fm=26&gp=0.jpg"))
     .go(PhotoPreviewActivity::class.java,1)//第二个参数是预览的起始位置

```
        
### 项目截图

<div><img width="300" height="600" src="gif/auto5.gif"/></div>
<div><img width="300" height="600" src="gif/1560244261536.jpg"/></div>


### 更新
    时间：20191102（1.0.9）
    内容：默认勾选图片。扩展图片预览功能，可以直接进入图片预览界面浏览
    
    时间：20190627（1.0.8）
    内容：增强体验，在权限请求拒绝时，pop不可以再弹出

    时间：20190626（1.0.7）
    内容：修复了一个bug，进入预览页，由于相机的是否打开，疏忽了position的值，导致position值正确
    
    时间：20190624（1.0.6）
    内容：稳定相机兼容性

    时间：20190621（1.0.5）
    内容：低版本兼容api兼容，wisdom_impl导入发生资源冲突，则使用exclude group :'com.android.support' 移除support

    时间：20190617（1.0.4）
    内容：增加两种图片裁剪实例，sdk本身暂不提供裁剪接口，查看demo获取实现裁剪方式
    
    时间：20190613（1.0.3）
    内容：imageEngine增加到三个方法，分别加载(相册封面,缩略图，预览图) picasso加载适配

    时间：20190612（1.0.2）
    内容：一些功能优化，去除Media图片文件名字段，其实可以通过路径Path获取
    
    时间：20190611（1.0.0）
    内容：图片选择框架发布，支持图片or视频单选，多选，打开相机，图片预览

总结
-
xiexie ni de guāng gù ！ 喜欢的朋友轻轻右上角赏个star，您的鼓励会给我持续更新的动力。








