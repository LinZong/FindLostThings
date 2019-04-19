package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.yalantis.ucrop.UCrop;
import misaka.nemesiss.com.findlostthings.Adapter.PublishLostThingPreviewImageAdapter;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import misaka.nemesiss.com.findlostthings.Utils.PermissionsHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class PickupImageActivity extends AppCompatActivity
{
    private List<Uri> PickupImagesList = new LinkedList<>();

    @BindView(R.id.previewImageRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ToggleFindLostThingLocationDesc)
    TextView ToggleFindLostThingLocationDesc;
    @BindView(R.id.ToggleLostThingAdditionalDesc)
    TextView ToggleLostThingAdditionalDesc;
    @BindView(R.id.LostThingAdditionalDescEditText)
    EditText LostThingAdditionalDescEditText;
    @BindView(R.id.FindLostThingLocationDescEditText)
    EditText FindLostThingLocationDescEditText;
    @BindView(R.id.PublishLostThingToolbar)
    Toolbar PublishLostThingToolbar;
    @BindView(R.id.PublishLostThingsTitle)
    TextView PublishLostThingsTitle;
    @BindView(R.id.ThingCategorySpinner)
    MaterialSpinner ThingCategorySpinner;
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

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    private GridLayoutManager gridLayoutManager;
    private PublishLostThingPreviewImageAdapter imageAdapter;
    private Uri TempImageSavedUri;

    private Calendar CurrentPickDateAndTime;



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
                    AppendImage(currUri,false);
                }
                if(imageAdapter!=null) imageAdapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_image);
        ButterKnife.bind(this);
        PermissionsHelper.RequestAllPermissions(PickupImageActivity.this,PickupImageActivity.this);
        CurrentPickDateAndTime = Calendar.getInstance(Locale.CHINA);
        AppUtils.ToolbarShowReturnButton(PickupImageActivity.this,PublishLostThingToolbar);
        InitComponents();
    }

    private void InitComponents()
    {
        PickupImagesList.add(AppUtils.ParseResourceIdToUri(R.drawable.add_photo));

        if(gridLayoutManager==null){
            gridLayoutManager = new GridLayoutManager(PickupImageActivity.this,3);
            imageAdapter = new PublishLostThingPreviewImageAdapter(PickupImagesList,PickupImageActivity.this);
        }

        gridLayoutManager.setAutoMeasureEnabled(true);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(imageAdapter);


        Calendar ca = Calendar.getInstance(Locale.CHINA);
        int  mYear = ca.get(Calendar.YEAR);
        int  mMonth = ca.get(Calendar.MONTH);
        int  mDay = ca.get(Calendar.DAY_OF_MONTH);
        int mHour = ca.get(Calendar.HOUR_OF_DAY);
        int mMinus = ca.get(Calendar.MINUTE);

        //绑定日期和时间的点击事件，用以启动对应的Picker
        datePickerDialog = new DatePickerDialog(PickupImageActivity.this,this::HandleDatePick,mYear, mMonth, mDay);
        timePickerDialog = new TimePickerDialog(PickupImageActivity.this,this::HandleTimePick,mHour,mMinus,true);

        PickDateLayout.setOnClickListener(v -> datePickerDialog.show());
        PickTimeLayout.setOnClickListener(v -> timePickerDialog.show());

        //绑定展开/关闭补充说明的点击事件
        ToggleLostThingAdditionalDesc
                .setOnClickListener(HandleAdditionalDescCollapsing(ToggleLostThingAdditionalDesc,LostThingAdditionalDescEditText));
        ToggleFindLostThingLocationDesc
                .setOnClickListener(HandleAdditionalDescCollapsing(ToggleFindLostThingLocationDesc,FindLostThingLocationDescEditText));
    }

    private void HandleTimePick(TimePicker timePicker, int HourOfDay, int Minus)
    {
        CurrentPickDateAndTime.set(Calendar.HOUR_OF_DAY,HourOfDay);
        CurrentPickDateAndTime.set(Calendar.MINUTE,Minus);


        TimeTextView.setText(String.format("%d:%02d",HourOfDay,Minus));
    }

    private void HandleDatePick(DatePicker datePicker, int year, int month, int dayOfMonth)
    {
        CurrentPickDateAndTime.set(year,month+1,dayOfMonth);

        DateTextView.setText(year+"-"+(month+1)+"-"+dayOfMonth);
    }

    private View.OnClickListener HandleAdditionalDescCollapsing(TextView toggle,EditText editor)
    {
        AtomicBoolean initStatus = new AtomicBoolean(false);
        return view -> {
            if(!initStatus.get()){
                toggle.setTextColor(getResources().getColor(R.color.OpenAdditionalInfoColor));
                editor.setVisibility(View.VISIBLE);
            }
            else {
                toggle.setTextColor(getResources().getColor(R.color.DefaultTextViewColor));
                editor.setVisibility(View.GONE);
            }
            initStatus.set(!initStatus.get());
        };
    }

    public void SetTempImageSavedUri(Uri uri)
    {
        TempImageSavedUri = uri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Activity.RESULT_OK:{
                switch (requestCode){
                    case AppUtils.TYPE_CAMERA:{
                        NotifyImageToCamera(PickupImageActivity.this,TempImageSavedUri);
                        UCrop of = AppUtils.OpenUCrop(TempImageSavedUri);
                        of.start(PickupImageActivity.this);
                        break;
                    }
                    case UCrop.REQUEST_CROP:{
                        Uri FinishedCropped = UCrop.getOutput(data);
                        AppendImage(FinishedCropped,true);
                        break;
                    }
                }
            }
        }

    }

    public void AppendImage(Uri imageUri,boolean ShouldNotifyDataSetChanged)
    {
        if (PickupImagesList.size() != 10)
        {
            PickupImagesList.add(PickupImagesList.size()-1,imageUri);
            if(ShouldNotifyDataSetChanged){
                imageAdapter.notifyDataSetChanged();
            }
        }
    }
    public void RemoveImage(int index,boolean ShouldNotifyDataSetChanged)
    {
        PickupImagesList.remove(index);
        recyclerView.removeViewAt(index);
        if(ShouldNotifyDataSetChanged)
        {
            imageAdapter.notifyDataSetChanged();
        }

    }
    private void NotifyImageToCamera(Context context, Uri uri) {
        try {
            File file = new File(uri.getPath());
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

}
