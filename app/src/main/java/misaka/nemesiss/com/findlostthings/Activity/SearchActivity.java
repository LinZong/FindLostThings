package misaka.nemesiss.com.findlostthings.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jaredrummler.materialspinner.MaterialSpinner;
import misaka.nemesiss.com.findlostthings.Adapter.LostThingCategoryAdapter;
import misaka.nemesiss.com.findlostthings.Adapter.SchoolBuildingsCategoryAdapter;
import misaka.nemesiss.com.findlostthings.Adapter.SchoolInfoCategoryAdapter;
import misaka.nemesiss.com.findlostthings.Model.*;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Tasks.*;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import misaka.nemesiss.com.findlostthings.Utils.UUIDGenerator;
import java.util.*;

public class SearchActivity extends FindLostThingsActivity
{
    @BindView(R.id.SearchLostThingToolbar)
    Toolbar toolbar;

    @BindView(R.id.ThingCategorySpinner)
    MaterialSpinner ThingCategorySpinner;//下拉选择
    @BindView(R.id.ThingDetailedSpinner)
    MaterialSpinner ThingDetailedSpinner;
    @BindView(R.id.SchoolAreaSpinner)
    MaterialSpinner SchoolAreaSpinner;
    @BindView(R.id.SchoolBuildingSpinner)
    MaterialSpinner SchoolBuildingSpinner;

    @BindView(R.id.PickStartDateLayout)
    ConstraintLayout pickStartDateLayout;
    @BindView(R.id.startDateTextView)
    TextView startDateTextView;

    @BindView(R.id.PickEndDateLayout)
    ConstraintLayout pickEndDateLayout;
    @BindView(R.id.endDateTextView)
    TextView endDateTextView;

    @BindView(R.id.PickStartTimeLayout)
    ConstraintLayout pickStartTimeLayout;
    @BindView(R.id.startTimeTextView)
    TextView startTimeTextView;

    @BindView(R.id.PickEndTimeLayout)
    ConstraintLayout pickEndTimeLayout;
    @BindView(R.id.endTimeTextView)
    TextView endTimeTextView;

    //展示选项用的对象

    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;
    TimePickerDialog startTimePickerDialog;
    TimePickerDialog endTimePickerDialog;

    private SparseArray<List<LostThingsCategory>> CacheThingsDetail = new SparseArray<>();
    private SparseArray<List<MySchoolBuildings>> CacheSchoolBuildingsList = new SparseArray<>();

    private List<LostThingsCategory> thingsCategories = new ArrayList<>();
    private List<LostThingsCategory> thingsDetails = new ArrayList<>();
    private List<SchoolInfo> allSupportedSchool = new ArrayList<>();
    private List<MySchoolBuildings> currentSchoolBuildings = new ArrayList<>();

    //配套上面这些List的Adapter
    private LostThingCategoryAdapter thingsCategoryAdapter = new LostThingCategoryAdapter(SearchActivity.this, thingsCategories);
    private LostThingCategoryAdapter thingsDetailedAdapter = new LostThingCategoryAdapter(SearchActivity.this, thingsDetails);
    private SchoolInfoCategoryAdapter supportedSchoolListAdapter = new SchoolInfoCategoryAdapter(SearchActivity.this, allSupportedSchool);
    private SchoolBuildingsCategoryAdapter currentSchoolBuildingsAdapter = new SchoolBuildingsCategoryAdapter(SearchActivity.this, currentSchoolBuildings);

    //本次提交相关的对象
    private String CurrentSearchUUID = UUIDGenerator.Generate();
    private Calendar CurrentPickDateAndTime1;
    private Calendar CurrentPickDateAndTime2;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        CurrentPickDateAndTime1= Calendar.getInstance(Locale.CHINA);
        CurrentPickDateAndTime2= Calendar.getInstance(Locale.CHINA);
        AppUtils.ToolbarShowReturnButton(SearchActivity.this, toolbar);
        InitComponents();
        LoadSpinnerItems();
    }

    private void LoadSpinnerItems()
    {
        new GetLostThingsCategoryAsyncTask((result) -> {
            if(AppUtils.CommonResponseOK(result))
            {
                Log.d("PickupImageActivity", "物品种类加载完成");
                thingsCategories.clear();
                thingsCategories.addAll(result.getCategoryList());
                thingsCategoryAdapter.notifyDataSetChanged();
                Log.d("PickupImageActivity", String.valueOf(ThingCategorySpinner.getSelectedIndex()));
            }
        }).execute();
        new GetSupportSchoolsTask((result) -> {
            if(AppUtils.CommonResponseOK(result))
            {
                Log.d("PickupImageActivity", "支持的学校加载完成");
                allSupportedSchool.clear();
                allSupportedSchool.addAll(result.getSupportSchools());
                supportedSchoolListAdapter.notifyDataSetChanged();
            }
        }).execute();
    }

    private void InitComponents()
    {
        // 动态绑定UI 元素。
        Calendar ca = Calendar.getInstance(Locale.CHINA);
        int mYear = ca.get(Calendar.YEAR);
        int mMonth = ca.get(Calendar.MONTH);
        int mDay = ca.get(Calendar.DAY_OF_MONTH);
        int mHour = ca.get(Calendar.HOUR_OF_DAY);
        int mMinus = ca.get(Calendar.MINUTE);

        //绑定日期和时间的点击事件，用以启动对应的Picker
        startDatePickerDialog = new DatePickerDialog(SearchActivity.this, this::HandleStartDatePick, mYear, mMonth, mDay);
        endDatePickerDialog = new DatePickerDialog(SearchActivity.this, this::HandleEndDatePick, mYear, mMonth, mDay);
        startTimePickerDialog = new TimePickerDialog(SearchActivity.this, this::HandleStartTimePick, mHour, mMinus, true);
        endTimePickerDialog = new TimePickerDialog(SearchActivity.this, this::HandleEndTimePick, mHour, mMinus, true);

        pickStartDateLayout.setOnClickListener(v -> startDatePickerDialog.show());
        pickEndDateLayout.setOnClickListener(v -> endDatePickerDialog.show());
        pickStartTimeLayout.setOnClickListener(v -> startTimePickerDialog.show());
        pickEndTimeLayout.setOnClickListener(v -> endTimePickerDialog.show());

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

                MySchoolBuildings NotSelected = new MySchoolBuildings();
                NotSelected.setId(-1);
                NotSelected.setBuildingName("不选");
                currentSchoolBuildings.add(0,NotSelected);

                currentSchoolBuildings.addAll(result.getSchoolBuildings());
                SchoolBuildingSpinner.setSelectedIndex(0);
                SchoolBuildingSpinner.setText(currentSchoolBuildings.get(0).getBuildingName());
            }).execute(SelectedSchoolID);
        }
        else
        {
            currentSchoolBuildings.clear();

            MySchoolBuildings NotSelected = new MySchoolBuildings();
            NotSelected.setId(-1);
            NotSelected.setBuildingName("不选");
            currentSchoolBuildings.add(0,NotSelected);

            currentSchoolBuildings.addAll(cached);
            currentSchoolBuildingsAdapter.notifyDataSetChanged();
            SchoolBuildingSpinner.setSelectedIndex(0);
            SchoolBuildingSpinner.setText(currentSchoolBuildings.get(0).getBuildingName());
        }
    }

    private void HandleUpdateThingsDetailed(MaterialSpinner materialSpinner, int position, long id, Object item)
    {
        int SelectedThingCategoryID = thingsCategories.get(position).getId();
        List<LostThingsCategory> cached = CacheThingsDetail.get(SelectedThingCategoryID, null);

        if (cached == null)
        {
            new GetLostThingsCategoryPartitionTask((result) -> {
                CacheThingsDetail.append(SelectedThingCategoryID, result.getCategoryDetails());
                thingsDetails.clear();
                thingsDetails.addAll(result.getCategoryDetails());
                thingsDetailedAdapter.notifyDataSetChanged();
                ThingDetailedSpinner.setSelectedIndex(0);
                ThingDetailedSpinner.setText(result.getCategoryDetails().get(0).getName());
            }).execute(SelectedThingCategoryID);
        }
        else
        {
            thingsDetails.clear();
            thingsDetails.addAll(cached);
            thingsDetailedAdapter.notifyDataSetChanged();
            ThingDetailedSpinner.setSelectedIndex(0);
            ThingDetailedSpinner.setText(cached.get(0).getName());
        }
    }

    private void HandleStartTimePick(TimePicker timePicker, int HourOfDay, int Minus)
    {
        CurrentPickDateAndTime1.set(Calendar.HOUR_OF_DAY, HourOfDay);
        CurrentPickDateAndTime1.set(Calendar.MINUTE, Minus);
        startTimeTextView.setText(String.format("%d:%02d", HourOfDay, Minus));
    }

    private void HandleEndTimePick(TimePicker timePicker, int HourOfDay, int Minus)
    {
        CurrentPickDateAndTime2.set(Calendar.HOUR_OF_DAY, HourOfDay);
        CurrentPickDateAndTime2.set(Calendar.MINUTE, Minus);
        endTimeTextView.setText(String.format("%d:%02d", HourOfDay, Minus));
    }

    private void HandleStartDatePick(DatePicker datePicker, int year, int month, int dayOfMonth)
    {
        CurrentPickDateAndTime1.set(year, month, dayOfMonth);
        startDateTextView.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
    }

    private void HandleEndDatePick(DatePicker datePicker, int year, int month, int dayOfMonth)
    {
        CurrentPickDateAndTime2.set(year, month, dayOfMonth);
        endDateTextView.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
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
                if (!ValidateAllFields())
                {
                    BuildupSearchThingRequest();
                    Intent intent=new Intent(SearchActivity.this,SearchResultActivity.class);
                    startActivity(intent);
                }
               else
                {
                    Toast.makeText(SearchActivity.this,"必须设置好查询条件", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return true;
    }

    private void BuildupSearchThingRequest()
    {
        int CategorySelectedIndex = ThingCategorySpinner.getSelectedIndex();
        int DetailedSelectedIndex = ThingDetailedSpinner.getSelectedIndex();
        int SchoolInfoSelectedIndex = SchoolAreaSpinner.getSelectedIndex();
        int SchoolBuildingSelectedIndex = SchoolBuildingSpinner.getSelectedIndex();
        int CategoryID = thingsCategories.get(CategorySelectedIndex).getId();
        int DetailedID = thingsDetails.get(DetailedSelectedIndex).getId();
        int SchoolID = allSupportedSchool.get(SchoolInfoSelectedIndex).getId();
        // 判一下有没有选
        int SchoolBuildingID = currentSchoolBuildings.get(SchoolBuildingSelectedIndex).getId();
        long FoundDateBeginUnix = AppUtils.Date2UnixStamp(CurrentPickDateAndTime1.getTime());
        long FoundDateEndUnix = AppUtils.Date2UnixStamp(CurrentPickDateAndTime2.getTime());


        SearchLostThingsInfo searchLostThingsInfo = new SearchLostThingsInfo();
        searchLostThingsInfo.setFoundDateBeginUnix(FoundDateBeginUnix);
        searchLostThingsInfo.setFoundDateEndUnix(FoundDateEndUnix);
        searchLostThingsInfo.setSchoolId(SchoolID);
        searchLostThingsInfo.setThingCatId(CategoryID);
        searchLostThingsInfo.setThingDetailId(DetailedID);

        if(SchoolBuildingID != -1)
        {
            // 提取信息
            searchLostThingsInfo.setSchoolBuildingId(SchoolBuildingID);
        }

        new GetSearchLostThingsInfoTask((result) -> {
            Toast.makeText(SearchActivity.this, String.valueOf(result), Toast.LENGTH_SHORT).show();
            Log.d("SearchActivity", String.valueOf(result));
        }).execute(searchLostThingsInfo);
    }

    private boolean ValidateAllFields()
    {
        boolean refuse = false;
        ClearErrorFlags();
        if (!ConfirmSpinnerAllSelected(ThingCategorySpinner, ThingDetailedSpinner, SchoolAreaSpinner))
        {
            refuse = true;
        }
        if (TextUtils.isEmpty(startTimeTextView.getText().toString()))
        {
            startTimeTextView.setError("必须设置搜索的开始时间");
            refuse = true;
        }
        if (TextUtils.isEmpty(endTimeTextView.getText().toString()))
        {
            endTimeTextView.setError("必须设置搜索的截止时间");
            refuse = true;
        }
        if (TextUtils.isEmpty(startDateTextView.getText().toString()))
        {
            startDateTextView.setError("必须设置搜索的开始日期");
            refuse = true;
        }
        if (TextUtils.isEmpty(endDateTextView.getText().toString()))
        {
            endDateTextView.setError("必须设置搜索的截止日期");
            refuse = true;
        }
        return refuse;

    }

    private void ClearErrorFlags()
    {
        ThingCategorySpinner.setError(null);
        ThingDetailedSpinner.setError(null);
        //SchoolAreaSpinner.setError(null);
       // SchoolBuildingSpinner.setError(null);
        startTimeTextView.setError(null);
        endTimeTextView.setError(null);
        startDateTextView.setError(null);
        endDateTextView.setError(null);
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
}
