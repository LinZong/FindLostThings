package misaka.nemesiss.com.findlostthings.Activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferState;
import com.yalantis.ucrop.UCrop;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import misaka.nemesiss.com.findlostthings.Adapter.*;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.*;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.StorageBucket.BucketFileOperation;
import misaka.nemesiss.com.findlostthings.Services.Thing.ThingServices;
import misaka.nemesiss.com.findlostthings.Tasks.*;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import misaka.nemesiss.com.findlostthings.Utils.EventProxy;
import misaka.nemesiss.com.findlostthings.Utils.ImageHelper;
import misaka.nemesiss.com.findlostthings.Utils.UUIDGenerator;
import misaka.nemesiss.com.findlostthings.View.PercentageProgressBar;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PickupImageActivity extends FindLostThingsActivity
{
    //绑定UI控件。
    @BindView(R.id.previewImageRecyclerView)
    RecyclerView recyclerView;


    @BindView(R.id.AdditionalDescLayout)
    LinearLayout AdditionalDescLayout;
    @BindView(R.id.AdditionalDescCollapsing)
    TextView AdditionalDescCollapsing;

    @BindView(R.id.LostThingAdditionalDescEditText)
    EditText LostThingAdditionalDescEditText;
    @BindView(R.id.FindLostThingLocationDescEditText)
    EditText FindLostThingLocationDescEditText;
    @BindView(R.id.PublishLostThingToolbar)
    Toolbar PublishLostThingToolbar;
    @BindView(R.id.PublishLostThingsTitle)
    EditText PublishLostThingsTitle;
    @BindView(R.id.ThingCategorySpinner)
    MaterialSpinner ThingCategorySpinner;//下拉选择
    @BindView(R.id.ThingDetailedSpinner)
    MaterialSpinner ThingDetailedSpinner;
    @BindView(R.id.SchoolAreaSpinner)
    MaterialSpinner SchoolAreaSpinner;
    @BindView(R.id.SchoolBuildingSpinner)
    MaterialSpinner SchoolBuildingSpinner;
    @BindView(R.id.PickDateLayout)
    ConstraintLayout PickDateLayout;
    @BindView(R.id.PickTimeLayout)
    ConstraintLayout PickTimeLayout;
    @BindView(R.id.TimeTextView)
    TextView TimeTextView;
    @BindView(R.id.DateTextView)
    TextView DateTextView;


    //展示选项用的对象

    private List<Uri> PickupImagesList = new LinkedList<>();
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    private SparseArray<List<LostThingDetail>> CacheThingsDetail = new SparseArray<>();
    private SparseArray<List<MySchoolBuildings>> CacheSchoolBuildingsList = new SparseArray<>();

    private List<LostThingsCategory> thingsCategories = new ArrayList<>();
    private List<LostThingDetail> thingsDetails = new ArrayList<>();
    private List<SchoolInfo> allSupportedSchool = new ArrayList<>();
    private List<MySchoolBuildings> currentSchoolBuildings = new ArrayList<>();

    //配套上面这些List的Adapter
    private LostThingCategoryAdapter thingsCategoryAdapter = new LostThingCategoryAdapter(PickupImageActivity.this, thingsCategories);
    private LostThingDetailAdapter thingsDetailedAdapter = new LostThingDetailAdapter(PickupImageActivity.this, thingsDetails);
    private SchoolInfoCategoryAdapter supportedSchoolListAdapter = new SchoolInfoCategoryAdapter(PickupImageActivity.this, allSupportedSchool);
    private SchoolBuildingsCategoryAdapter currentSchoolBuildingsAdapter = new SchoolBuildingsCategoryAdapter(PickupImageActivity.this, currentSchoolBuildings);


    //处理失物图片的布局
    private GridLayoutManager gridLayoutManager;
    private PublishLostThingPreviewImageAdapter imageAdapter;
    private Uri TempImageSavedUri;


    //本次提交相关的对象
    private String CurrentPublishUUID = UUIDGenerator.Generate();
    private Calendar CurrentPickDateAndTime;
    private List<String> FinishUploadImageUrl = new ArrayList<>();

    //处理当前正在上传的图片的数据结构
    private Queue<Pair<String, String>> CurrentUploadImageQueue;
    private int CurrentUploadImageIndex = -1;
    private PercentageProgressBar CurrentUploadImageProgress;
    private boolean IsUploadingImages = false;

    //处理活动之间来回跳转
    public static final int PREVIEW_ACTIVITY = 1998;


    private void ListenNetworkChange()
    {
        ThingServices ts = FindLostThingsApplication.getThingServices();
        ts.getOriginalSchoolsObservable().subscribe(
                NewSchools -> {
                    allSupportedSchool.clear();
                    allSupportedSchool.addAll(NewSchools);
                    supportedSchoolListAdapter.notifyDataSetChanged();
                });
        ts.getThingCategoryObservable().subscribe(
                NewLTC -> {
                    thingsCategories.clear();
                    thingsCategories.addAll(NewLTC);
                    thingsCategoryAdapter.notifyDataSetChanged();
                });
    }


    public RxBusResultDisposable<ImageMultipleResultEvent> getMultiImageSelectHandler()
    {
        return new RxBusResultDisposable<ImageMultipleResultEvent>()
        {
            @Override
            protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception
            {
                List<MediaBean> mb = imageMultipleResultEvent.getResult();
                for (MediaBean mediaBean : mb)
                {
                    Uri currUri = Uri.fromFile(new File(mediaBean.getOriginalPath()));
                    AppendImage(currUri, false);
                }
                if (imageAdapter != null) imageAdapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_image);
        ButterKnife.bind(this);
        CurrentPickDateAndTime = Calendar.getInstance(Locale.CHINA);
        AppUtils.ToolbarShowReturnButton(PickupImageActivity.this, PublishLostThingToolbar);
        InitComponents();
        LoadSpinnerItems();
        ListenNetworkChange();
    }

    private void LoadSpinnerItems()
    {
        ThingServices ts = FindLostThingsApplication.getThingServices();

        thingsCategories.addAll(ts.getOriginalThingCategory());
        thingsCategoryAdapter.notifyDataSetChanged();

        allSupportedSchool.addAll(ts.getOriginalSchools());
        supportedSchoolListAdapter.notifyDataSetChanged();

    }

    private void InitComponents()
    {
        PickupImagesList.add(AppUtils.ParseResourceIdToUri(R.drawable.add_photo));

        if (gridLayoutManager == null)
        {
            gridLayoutManager = new GridLayoutManager(PickupImageActivity.this, 3);
            imageAdapter = new PublishLostThingPreviewImageAdapter(PickupImagesList, PickupImageActivity.this);
        }

        gridLayoutManager.setAutoMeasureEnabled(true);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(imageAdapter);

        // 动态绑定UI 元素。

        AdditionalDescArrow = AdditionalDescCollapsing.getCompoundDrawables()[2];

        AdditionalDescLayoutHeight = Dp2Px(AdditionalDescLayoutExpandedDp);

        Calendar ca = Calendar.getInstance(Locale.CHINA);
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH);
        int mDay = ca.get(Calendar.DAY_OF_MONTH);
        int mHour = ca.get(Calendar.HOUR_OF_DAY);
        int mMinus = ca.get(Calendar.MINUTE);

        //绑定日期和时间的点击事件，用以启动对应的Picker
        datePickerDialog = new DatePickerDialog(PickupImageActivity.this, this::HandleDatePick, mYear, mMonth, mDay);
        timePickerDialog = new TimePickerDialog(PickupImageActivity.this, this::HandleTimePick, mHour, mMinus, true);

        PickDateLayout.setOnClickListener(v -> datePickerDialog.show());
        PickTimeLayout.setOnClickListener(v -> timePickerDialog.show());


        ThingCategorySpinner.setOnItemSelectedListener(this::HandleUpdateThingsDetailed);
        SchoolAreaSpinner.setOnItemSelectedListener(this::HandleUpdateSchoolBuildings);

        //绑定Adapters
        ThingCategorySpinner.setAdapter(thingsCategoryAdapter);
        ThingDetailedSpinner.setAdapter(thingsDetailedAdapter);
        SchoolAreaSpinner.setAdapter(supportedSchoolListAdapter);
        SchoolBuildingSpinner.setAdapter(currentSchoolBuildingsAdapter);
    }

    private void HandleUpdateSchoolBuildings(MaterialSpinner materialSpinner, int position, long id, Object o)
    {

        int SelectedSchoolID = thingsCategories.get(position).getId();
        List<MySchoolBuildings> cached = CacheSchoolBuildingsList.get(SelectedSchoolID, null);
        if (cached == null)
        {
            new GetSchoolBuildingsTask((result) -> {
                CacheSchoolBuildingsList.append(SelectedSchoolID, result.getSchoolBuildings());
                currentSchoolBuildings.clear();
                currentSchoolBuildings.addAll(result.getSchoolBuildings());
                SchoolBuildingSpinner.setSelectedIndex(0);
                SchoolBuildingSpinner.setText(result.getSchoolBuildings().get(0).getBuildingName());
            }).execute(SelectedSchoolID);
        } else
        {
            currentSchoolBuildings.clear();
            currentSchoolBuildings.addAll(cached);
            currentSchoolBuildingsAdapter.notifyDataSetChanged();
            SchoolBuildingSpinner.setSelectedIndex(0);
            SchoolBuildingSpinner.setText(cached.get(0).getBuildingName());
        }
    }

    private void HandleUpdateThingsDetailed(MaterialSpinner materialSpinner, int position, long id, Object item)
    {
        int SelectedThingCategoryID = thingsCategories.get(position).getId();
        ThingServices ts = FindLostThingsApplication.getThingServices();
        SparseArray<LostThingDetail> dt = ts.getThingDetails().get(SelectedThingCategoryID);

        int len = dt.size();

        thingsDetails.clear();
        for (int i = 0; i < len; i++)
        {
            thingsDetails.add(dt.valueAt(i));
        }
        thingsDetailedAdapter.notifyDataSetChanged();
        ThingDetailedSpinner.setSelectedIndex(0);
        ThingDetailedSpinner.setText(thingsDetails.get(0).getName());
    }

    private void HandleTimePick(TimePicker timePicker, int HourOfDay, int Minus)
    {
        CurrentPickDateAndTime.set(Calendar.HOUR_OF_DAY, HourOfDay);
        CurrentPickDateAndTime.set(Calendar.MINUTE, Minus);


        TimeTextView.setText(String.format("%d:%02d", HourOfDay, Minus));
    }

    private void HandleDatePick(DatePicker datePicker, int year, int month, int dayOfMonth)
    {
        CurrentPickDateAndTime.set(year, month, dayOfMonth);

        DateTextView.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
    }

    public void SetTempImageSavedUri(Uri uri)
    {
        TempImageSavedUri = uri;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (TempImageSavedUri != null)
        {
            outState.putString("TempImageSavedUriString", TempImageSavedUri.getPath());
        }
        ArrayList<String> PickupImagesStringList = new ArrayList<>();
        int PickLength = PickupImagesList.size() - 1;
        for (int i = 0; i < PickLength; i++)
        {
            PickupImagesStringList.add(PickupImagesList.get(i).getPath());
        }
        outState.putStringArrayList("PickupImagesStringList", PickupImagesStringList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        String savedTempFilePath = savedInstanceState.getString("TempImageSavedUriString", null);
        if (savedTempFilePath != null)
        {
            TempImageSavedUri = Uri.fromFile(new File(savedTempFilePath));
        }
        ArrayList<String> PickupImagesStringList = savedInstanceState.getStringArrayList("PickupImagesStringList");
        if (PickupImagesStringList != null)
        {
            for (String s : PickupImagesStringList)
            {
                AppendImage(Uri.fromFile(new File(s)), false);
            }
            imageAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.publish_send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.PublishTickBtn:
            {
                if (IsUploadingImages)
                {
                    Toast.makeText(PickupImageActivity.this, "正在上传图片, 完成后会自动发布，请勿重复点击", Toast.LENGTH_SHORT).show();
                } else
                {
                    if (PickupImagesList.size() == 1)
                    {
                        AppUtils.ShowAlertDialog(PickupImageActivity.this, false, "未选择任何图片", "上传失物的图片有助于失主辨认丢失物，确定不上传任何照片吗?")
                                .setPositiveButton("确定", (d, i) -> ValidateAllFields())
                                .setNegativeButton("取消", (d, i) -> {
                                }).show();
                    } else ValidateAllFields();
                }
                break;
            }
            case android.R.id.home:
            {
                finish();
                break;
            }
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case Activity.RESULT_CANCELED:
            case Activity.RESULT_OK:
            {
                switch (requestCode)
                {
                    case AppUtils.TYPE_CAMERA:
                    {
                        if (resultCode != Activity.RESULT_CANCELED)
                        {
                            NotifyImageToCamera(PickupImageActivity.this, TempImageSavedUri);
                            UCrop of = AppUtils.OpenUCrop(TempImageSavedUri);
                            of.start(PickupImageActivity.this);
                        }
                        break;
                    }
                    case UCrop.REQUEST_CROP:
                    {
                        if (data != null)
                        {
                            Uri FinishedCropped = UCrop.getOutput(data);
                            AppendImage(FinishedCropped, true);
                        } else Log.e("PickupImageActivity", "UCrop回传的data为null!");
                        break;
                    }
                    case PREVIEW_ACTIVITY:
                    {
                        if (data != null)
                        {
                            boolean shouldDelete = data.getBooleanExtra("ShouldDeleteImage", false);
                            Uri previewImageUri = data.getParcelableExtra("PreviewImageUri");
                            int previewIndex = data.getIntExtra("PreviewImageIndex", -1);
                            if (shouldDelete)
                            {
                                RemoveImage(previewIndex, true);
                            } else
                            {
                                Uri OldUri = PickupImagesList.get(previewIndex);
                                if (OldUri.equals(previewImageUri))
                                {
                                    return;
                                } else
                                {
                                    PickupImagesList.set(previewIndex, previewImageUri);
                                    imageAdapter.notifyDataSetChanged();
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }

    }

    public void AppendImage(Uri imageUri, boolean ShouldNotifyDataSetChanged)
    {
        if (PickupImagesList.size() != 10)
        {
            PickupImagesList.add(PickupImagesList.size() - 1, imageUri);
            if (ShouldNotifyDataSetChanged)
            {
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    public void RemoveImage(int index, boolean ShouldNotifyDataSetChanged)
    {
        PickupImagesList.remove(index);
        recyclerView.removeViewAt(index);
        if (ShouldNotifyDataSetChanged)
        {
            imageAdapter.notifyDataSetChanged();
        }

    }

    private void NotifyImageToCamera(Context context, Uri uri)
    {
        try
        {
            File file = new File(uri.getPath());
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }


    private COSXMLUploadTask UploadSingleImage(String OriginalFilePath, String CosPath)
    {
        COSXMLUploadTask uploadTask = BucketFileOperation.UploadFile(OriginalFilePath, CosPath);
        uploadTask.setCosXmlProgressListener(this::UploadProgressHandler);
        uploadTask.setCosXmlResultListener(new CosXmlResultListener()
        {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result)
            {
                FinishUploadImageUrl.add(result.accessUrl);
                Log.d("PickupImageActivity", result.accessUrl);
                CurrentUploadImageQueue.remove();
                runOnUiThread(() -> CurrentUploadImageProgress.setVisibility(View.GONE));
                UploadNextImage();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException)
            {
                IsUploadingImages = false;
                Log.d("PickupImageActivity", "Failed: " + (exception == null ? serviceException.getMessage() : exception.toString()));
            }
        });
        uploadTask.setTransferStateListener(this::UploadStatusHandler);
        return uploadTask;
    }

    private void UploadStatusHandler(TransferState transferState)
    {

    }

    private void UploadProgressHandler(long complete, long target)
    {
        runOnUiThread(() -> {
            int progress = (int) (complete / target * 100);
            CurrentUploadImageProgress.setPercentage(progress);
        });
    }

    //上传任务到腾讯云存储桶相关。
    //启动上传任务。
    private void BeginUploadImage()
    {
        IsUploadingImages = true;
        CurrentUploadImageIndex = -1;
        //初始化上传队列
        CurrentUploadImageQueue = new LinkedList<>();
        //重置放置存已经传好的图片的存储桶地址
        FinishUploadImageUrl.clear();
        //本次需要上传的所有图片的本地文件路径


        if (PickupImagesList.size() == 1)
        {
            AllImageUploadFinished();
            return;
        }

        List<Uri> uploadList = new ArrayList<>(PickupImagesList);
        uploadList.remove(uploadList.size() - 1);

        ImageHelper.CompressAllImage(uploadList, new EventProxy.EventResult<Uri>()
        {
            @Override
            public void handle(ConcurrentHashMap<Uri, Object> evs, ConcurrentHashMap<Uri, EventProxy.EventStatus> evStatus)
            {
                Log.d("PickupImageActivity", "图片压缩完成...");
                List<Uri> compressed = new ArrayList<>();
//                for (Object val : evs.values()) {
//                    if(val != null) {
//                        compressed.add(Uri.fromFile((File) val));
//                    }
//                }
                for (Map.Entry<Uri, EventProxy.EventStatus> statEntry : evStatus.entrySet())
                {
                    Uri key = statEntry.getKey();
                    EventProxy.EventStatus status = statEntry.getValue();
                    if (status == EventProxy.EventStatus.Finish)
                    {
                        File f = (File) evs.get(key);
                        compressed.add(Uri.fromFile(f));
                    }
                }


                // 压缩完成后文件的路径
                List<String> AllImagePath = AppUtils.GetAllUploadObjectOriginalFilePath(compressed);
                // UserID
                long userid = FindLostThingsApplication.getUserService().GetUserID();
                // 应该存储在存储桶的object key
                List<String> AllObjectKeys = AppUtils.GetAllUploadObjectKeys(compressed, userid, CurrentPublishUUID);
                // 求大小
                int len = AllImagePath.size();
                //把本地文件路径和存储桶路径匹配
                for (int i = 0; i < len; i++)
                {
                    CurrentUploadImageQueue.add(new Pair<>(AllImagePath.get(i), AllObjectKeys.get(i)));
                }
                //触发上传
                UploadNextImage();
            }
        });

    }

    private void UploadNextImage()
    {
        if (CurrentUploadImageQueue.isEmpty())
        {
            //没什么可以上传的了，退出。
            runOnUiThread(this::AllImageUploadFinished);
        } else
        {
            //一个接一个上传
            CurrentUploadImageIndex++;
            runOnUiThread(() -> {
                CurrentUploadImageProgress = gridLayoutManager.getChildAt(CurrentUploadImageIndex).findViewById(R.id.UploadProgressHint);
                CurrentUploadImageProgress.setVisibility(View.VISIBLE);
            });
            Pair<String, String> CurrentUploadItem = CurrentUploadImageQueue.peek();
            UploadSingleImage(CurrentUploadItem.first, CurrentUploadItem.second);
        }
    }

    private void AllImageUploadFinished()
    {
        IsUploadingImages = false;
        BuildupPublishThingRequest();
    }

    private void BuildupPublishThingRequest()
    {
        String title = PublishLostThingsTitle.getText().toString();
        int CategorySelectedIndex = ThingCategorySpinner.getSelectedIndex();
        int DetailedSelectedIndex = ThingDetailedSpinner.getSelectedIndex();
        int SchoolInfoSelectedIndex = SchoolAreaSpinner.getSelectedIndex();
        int SchoolBuildingSelectedIndex = SchoolBuildingSpinner.getSelectedIndex();

        int CategoryID = thingsCategories.get(CategorySelectedIndex).getId();
        int DetailedID = thingsDetails.get(DetailedSelectedIndex).getId();
        int SchoolID = allSupportedSchool.get(SchoolInfoSelectedIndex).getId();
        int SchoolBuildingID = currentSchoolBuildings.get(SchoolBuildingSelectedIndex).getId();

        String LostThingAddiDesc = LostThingAdditionalDescEditText.getVisibility() == View.VISIBLE ? LostThingAdditionalDescEditText.getText().toString() : "";
        String GetLostThingLocationAddiDesc = FindLostThingLocationDescEditText.getVisibility() == View.VISIBLE ? FindLostThingLocationDescEditText.getText().toString() : "";


        long FoundLostThingTimeStamp = AppUtils.Date2UnixStamp(CurrentPickDateAndTime.getTime());
        long PublishLostThingTimeStamp = AppUtils.Date2UnixStamp(new Date());
        Gson gson = new Gson();
        String UploadedImageUrl = gson.toJson(FinishUploadImageUrl, new TypeToken<List<String>>()
        {
        }.getType());


        LostThingsInfo info = new LostThingsInfo();
        info.setTitle(title);
        info.setFoundTime(FoundLostThingTimeStamp);
        info.setPublishTime(PublishLostThingTimeStamp);
        info.setFoundAddress(SchoolID + "-" + SchoolBuildingID);
        info.setId(CurrentPublishUUID);
        info.setThingCatId(CategoryID);
        info.setThingDetailId(DetailedID);
        info.setPublisher(FindLostThingsApplication.getUserService().GetUserID());
        info.setThingPhotoUrls(UploadedImageUrl);
        info.setFoundAddrDescription(GetLostThingLocationAddiDesc);
        info.setThingAddiDescription(LostThingAddiDesc);

        new PublishLostThingsInfoTask((result) -> {
            if (AppUtils.CommonResponseOK(result))
            {
                if (result.getStatusCode() == 0)
                {
                    Toast.makeText(PickupImageActivity.this, "发布成功!", Toast.LENGTH_SHORT).show();
                    Log.d("PickupImageActivity", String.valueOf(result));
                    finish();
                } else
                {
                    Toast.makeText(PickupImageActivity.this, "发布失败!" + result, Toast.LENGTH_SHORT).show();
                    Log.d("PickupImageActivity", String.valueOf(result));
                }
            } else
            {
                Toast.makeText(PickupImageActivity.this, "发布失败!请检查网络连接。", Toast.LENGTH_SHORT).show();
            }
        }).execute(info);
    }

    private void ValidateAllFields()
    {
        boolean refuse = false;
        ClearErrorFlags();
        if (TextUtils.isEmpty(PublishLostThingsTitle.getText().toString()))
        {
            PublishLostThingsTitle.setError("必须填写发布失物的标题");
            refuse = true;
        }
        if (!ConfirmSpinnerAllSelected(ThingCategorySpinner, ThingDetailedSpinner, SchoolAreaSpinner, SchoolBuildingSpinner))
        {
            refuse = true;
        }
        if (TextUtils.isEmpty(TimeTextView.getText().toString()))
        {
            TimeTextView.setError("必须设置捡到失物的时间");
            refuse = true;
        }
        if (TextUtils.isEmpty(DateTextView.getText().toString()))
        {
            DateTextView.setError("必须设置捡到失物的日期");
            refuse = true;
        }
        if (!refuse)
        {
            BeginUploadImage();
        }
    }

    private void ClearErrorFlags()
    {
        PublishLostThingsTitle.setError(null);

        ThingCategorySpinner.setError(null);
        ThingDetailedSpinner.setError(null);
        SchoolAreaSpinner.setError(null);
        SchoolBuildingSpinner.setError(null);

        TimeTextView.setError(null);
        DateTextView.setError(null);
    }

    private boolean ConfirmSpinnerAllSelected(MaterialSpinner... spinners)
    {
        boolean all = true;
        for (int i = 0; i < spinners.length; i++)
        {
            String t1 = spinners[i].getText().toString();
            String t2 = spinners[i].getHint().toString();
            if (TextUtils.isEmpty(t1) || t1.equals(t2))
            {
                spinners[i].setError("必须选择一项");
                all = false;
            }
        }
        return all;
    }


    /*
     * 展开/关闭补充说明框动画控制相关
     * */

    private boolean IsAdditionalDescCollapsed = true;
    private Drawable AdditionalDescArrow;
    private int AnimationDuration = 300;
    private int AdditionalDescLayoutHeight;
    private int AdditionalDescLayoutExpandedDp = 163;

    @OnClick({R.id.AdditionalDescCollapsing})
    public void SwitchAdditionalDescCollapsing(View v)
    {
        int ArrowBegin = IsAdditionalDescCollapsed ? 0 : 10000;
        int ArrowEnd = IsAdditionalDescCollapsed ? 10000 : 0;
        int LayoutBegin = IsAdditionalDescCollapsed ? 1 : AdditionalDescLayoutHeight;
        int LayoutEnd = IsAdditionalDescCollapsed ? AdditionalDescLayoutHeight : 1;


        if (IsAdditionalDescCollapsed)
        {
            AdditionalDescLayout.setVisibility(View.VISIBLE);
        }

        ObjectAnimator arrowAnimator = ObjectAnimator.ofInt(AdditionalDescArrow, "level", ArrowBegin, ArrowEnd);
        ValueAnimator layoutAnimator = ValueAnimator.ofInt(LayoutBegin, LayoutEnd);

        arrowAnimator.addListener(ArrowAnimatorListener);
        layoutAnimator.addUpdateListener(AdditionalDescLayoutAnimatorListener);

        arrowAnimator.setDuration(AnimationDuration);
        layoutAnimator.setDuration(AnimationDuration);
        arrowAnimator.start();
        layoutAnimator.start();

        IsAdditionalDescCollapsed = !IsAdditionalDescCollapsed;
    }

    private ValueAnimator.AnimatorUpdateListener AdditionalDescLayoutAnimatorListener = new ValueAnimator.AnimatorUpdateListener()
    {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator)
        {

            Log.d("PickupImageActivity", String.valueOf(AdditionalDescLayout.getHeight()));
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) AdditionalDescLayout.getLayoutParams();
            lp.height = (Integer) valueAnimator.getAnimatedValue();
            AdditionalDescLayout.setLayoutParams(lp);
        }
    };


    private ObjectAnimator.AnimatorListener ArrowAnimatorListener = new ObjectAnimator.AnimatorListener()
    {

        @Override
        public void onAnimationStart(Animator animator)
        {

        }

        @Override
        public void onAnimationEnd(Animator animator)
        {
            if (IsAdditionalDescCollapsed)
            {
                AdditionalDescLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationCancel(Animator animator)
        {

        }

        @Override
        public void onAnimationRepeat(Animator animator)
        {

        }
    };


    private int Dp2Px(int dp)
    {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
