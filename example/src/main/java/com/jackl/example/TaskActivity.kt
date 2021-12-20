package com.jackl.exampleimport android.Manifestimport android.content.Intentimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.content.pm.ActivityInfoimport android.widget.Buttonimport androidx.recyclerview.widget.LinearLayoutManagerimport androidx.recyclerview.widget.RecyclerViewimport com.jackl.agilebatchtask.AgileBatchimport com.jackl.agilebatchtask.model.IRequestTaskimport com.jackl.agilebatchtask.model.IResponseTaskimport com.jackl.example.adapter.LogAdapterimport com.jackl.example.adapter.PreviewAdapterimport com.jackl.example.model.Dataimport com.jackl.example.model.MyRequsetModelimport com.jackl.example.model.MyResponseModelimport com.jackl.example.utils.FileUtilsimport com.jackl.example.utils.RetroiftUtilsimport com.jackl.finalpermission.annotation.RequestPermissionimport com.zhihu.matisse.MimeTypeimport com.zhihu.matisse.Matisseimport com.zhihu.matisse.internal.entity.CaptureStrategyimport top.zibin.luban.Lubanimport java.text.SimpleDateFormatimport com.jackl.agilebatchtask.interfaces.FunctionTask1import com.jackl.agilebatchtask.interfaces.CallBackclass TaskActivity : AppCompatActivity() {    private val tv_select: Button by lazy { findViewById(R.id.select) }    private val rv_log: RecyclerView by lazy { findViewById(R.id.rv_log) }    private val rv_preview: RecyclerView by lazy { findViewById(R.id.rv_preview) }    private val REQUEST_CODE_CHOOSE = 10000    private var logAdapter: LogAdapter? = null    private var previewAdapter: PreviewAdapter? = null    private var sdf: SimpleDateFormat = SimpleDateFormat("HH:mm:ss:sss")    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_task)        tv_select.setOnClickListener {            openSelecter()        }        initLogRecycleView()        initRreviewRecycleView()    }    private fun initLogRecycleView() {        logAdapter = LogAdapter(rv_log,this)        rv_log.adapter = logAdapter        rv_log.layoutManager = LinearLayoutManager(this)    }    private fun initRreviewRecycleView() {        previewAdapter = PreviewAdapter(rv_preview)        rv_preview.adapter = previewAdapter        rv_preview.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)    }    @RequestPermission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)    private fun openSelecter() {        Matisse.from(this@TaskActivity)            .choose(MimeType.ofAll())            .countable(true)            .capture(true)            .captureStrategy(CaptureStrategy(true, "${packageName}fileProvider"))            .maxSelectable(1)            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)            .thumbnailScale(0.85f)            .imageEngine(GlideEngine())            .forResult(REQUEST_CODE_CHOOSE)    }    fun startUpload(data: MyRequsetModel) {        previewAdapter?.datas?.clear()        previewAdapter?.notifyDataSetChanged()        logAdapter?.logs?.clear()        logAdapter?.notifyDataSetChanged()        AgileBatch().Builder()            .addTask(data)            .doBeforeTsak(object : FunctionTask1<IRequestTask, MyRequsetModel> {                override fun invoke(p1: IRequestTask?): MyRequsetModel? {                    val millis = System.currentTimeMillis()                    if (p1 != null) {                        p1 as MyRequsetModel                        logAdapter?.addLog("Task${p1.index}= Before\nDescription= 对图片进行压缩处理\nTheradName= ${Thread.currentThread().name}\ntime= ${sdf.format(millis)}")                        p1.file = Luban.with(this@TaskActivity).load(p1.path).get()[0]                        return p1                    }                    return null                }            })            .doBeginTsak(object : FunctionTask1<IRequestTask, MyResponseModel> {                override fun invoke(p1: IRequestTask?): MyResponseModel? {                    val millis = System.currentTimeMillis()                    if (p1 != null) {                        p1 as MyRequsetModel                        logAdapter?.addLog("Task${p1.index}= Begin\nDescription= 开始上传到服务器\nTheradName= ${Thread.currentThread().name}\ntime= ${sdf.format(millis)}")                        val myResponseModel = RetroiftUtils.upload(p1.file!!)                        myResponseModel?.data?.index = p1.index                        return myResponseModel                    }                    return null                }            })            .doAfterTsak(object : FunctionTask1<IResponseTask, MyResponseModel> {                override fun invoke(p1: IResponseTask?): MyResponseModel? {                    val millis = System.currentTimeMillis()                    if (p1 != null) {                        p1 as MyResponseModel                        logAdapter?.addLog("Task${p1.data.index}= After(DoSomething)\ndescription= 上传成功后可做一些善后事宜\nTheradName= ${Thread.currentThread().name}\nTime= ${sdf.format(millis)}")                        return p1                    }                    return null                }            })            .setCallBack(object : CallBack<IResponseTask> {                override fun onBegin() {                    val millis = System.currentTimeMillis()                    logAdapter?.addLog("TasksBeginning......\nDescription= 开始执行任务\nTime= ${sdf.format(millis)}")                }                override fun onFinish(t: IResponseTask) {                    t as MyResponseModel                    val millis = System.currentTimeMillis()                    logAdapter?.addLog("TasksFinish\nDescription= 所有任务结束\nTheradName= ${Thread.currentThread().name}\nTime= ${sdf.format(millis)}")                    previewAdapter?.addData(t.data)                }                override fun onError(e: Throwable) {                    val millis = System.currentTimeMillis()                    logAdapter?.addLog("TasksError\nDescription= 任务出现异常\nTheradId= ${Thread.currentThread().name}\nTime= ${sdf.format(millis)}")                }            })            .execute()    }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        super.onActivityResult(requestCode, resultCode, data)        when (requestCode) {            REQUEST_CODE_CHOOSE -> {                if (data != null) {                    val obtainResult = Matisse.obtainResult(data)                    val myRequsetModel = MyRequsetModel(0, FileUtils.getPathFromUri(this@TaskActivity, obtainResult[0])!!)                    startUpload(myRequsetModel)                }            }        }    }}