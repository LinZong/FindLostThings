package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;
import misaka.nemesiss.com.findlostthings.Adapter.SwipeLostThingImageAdapter;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Model.MySchoolBuildings;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;
import misaka.nemesiss.com.findlostthings.Model.SchoolInfo;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.Thing.ThingServices;
import misaka.nemesiss.com.findlostthings.Tasks.GetLostThingsCategoryPartitionTask;
import misaka.nemesiss.com.findlostthings.Tasks.GetSchoolBuildingsTask;
import misaka.nemesiss.com.findlostthings.Tasks.GetUserInformationTask;
import misaka.nemesiss.com.findlostthings.Tasks.UpdateLostThingsInfoTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import misaka.nemesiss.com.findlostthings.View.SwipeImageView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LostThingDetailActivity extends FindLostThingsActivity {

    private static final int REQUEST_CODE_SCAN = 1001;
    private static final int REQUEST_GIVEN_CONFIRM = 999;

    @BindView(R.id.HomeArrowBtnLostThingDetail)
    Button homeArrow;

    @BindView(R.id.LostThingTitle)
    TextView LostThingTitle;

    @BindView(R.id.TakeOrGivenThing)
    FloatingActionButton TakeOrGivenThing;
    @BindView(R.id.UserQrCodeContainer)
    RelativeLayout UserQrCodeContainer;
    @BindView(R.id.UserQrCode)
    ImageView UserQrCode;
    @BindView(R.id.LostThingImagesSwiper)
    SwipeImageView imageViewSwiper;

    @BindView(R.id.ThingCategory)
    TextView ThingCategory;
    @BindView(R.id.ThingDetailCategory)
    TextView ThingDetailCategory;
    @BindView(R.id.SchoolName)
    TextView SchoolName;
    @BindView(R.id.SchollBuildingName)
    TextView SchoolBuildingName;
    @BindView(R.id.PublishTime)
    TextView PublishTime;

    @BindView(R.id.QQDetailField)
    TextView QQ;
    @BindView(R.id.WxDetailField)
    TextView Wx;
    @BindView(R.id.MobileDetailField)
    TextView Mobile;
    @BindView(R.id.EmailDetailField)
    TextView Email;
    @BindView(R.id.LocationDesc)
    TextView LocationDesc;
    @BindView(R.id.ThingDesc)
    TextView ThingDesc;

    @BindView(R.id.GivenDateHint)
    TextView GivenDateHint;
    @BindView(R.id.GivenDateField)
    TextView GivenDateField;


    private LostThingsInfo CurrentLostThingInfo;
    private UserInformation CurrentLoginUser;
    private UserInformation CurrentTakingThingUser;

    private UserInformation PublisherContacts;
    private SwipeLostThingImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);


        setContentView(R.layout.activity_lost_thing_detail);
        ButterKnife.bind(this);
        CurrentLostThingInfo = (LostThingsInfo) getIntent().getSerializableExtra("LostThingsInfo");
        int arrowDrawRes = getIntent().getIntExtra("ArrowDrawableRes",-1);
        if(arrowDrawRes!=-1){
            homeArrow.setCompoundDrawablesWithIntrinsicBounds(arrowDrawRes,0,0,0);
        }
        CurrentLoginUser = FindLostThingsApplication.getUserService().getMyProfile();
        if (CurrentLoginUser.getId() == CurrentLostThingInfo.getPublisher()) {
            if(CurrentLostThingInfo.getIsgiven() == 1) {
                TakeOrGivenThing.setTitle("您已归还失物");
                TakeOrGivenThing.setEnabled(false);
            }
            else TakeOrGivenThing.setTitle("归还失物");
        } else {
            if(CurrentLostThingInfo.getIsgiven() == 1) {
                TakeOrGivenThing.setTitle("您已认领失物");
                TakeOrGivenThing.setEnabled(false);
            }
            TakeOrGivenThing.setTitle("认领失物");
        }
        LoadInformation();
    }


    public static int ComputeHomeArrowColor(ImageView iv)
    {
        //ImageView iv = imageViewSwiper.GetInnerImageViews().get(position);
        GlideBitmapDrawable glideDrawable = ((GlideBitmapDrawable) (iv.getDrawable()));
        if(glideDrawable == null) {
            return R.drawable.ic_keyboard_arrow_left_blue_grey_200_36dp;
        }
        Bitmap bitmap = glideDrawable.getBitmap();

        int size = 47;
        long totalR = 0, totalG = 0, totalB = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int color = bitmap.getPixel(i, j);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                totalR += r;
                totalG += g;
                totalB += b;
            }
        }
        totalR /= (size * size);
        totalG /= (size * size);
        totalB /= (size * size);

        long avg = (totalR + totalG + totalB) / 3;
        if (avg > 200) {
            return R.drawable.ic_keyboard_arrow_left_blue_grey_200_36dp;
        } else {
            return R.drawable.ic_keyboard_arrow_left_black_36dp;
        }
    }

    private void LoadInformation() {

        String str = CurrentLostThingInfo.getThingPhotoUrls();
        String[] urlArray = new Gson().fromJson(str, new TypeToken<String[]>() {
        }.getType());
        if (urlArray.length > 0) {
            // Convert To URI List
            List<Uri> uri = new ArrayList<>();
            for (String s : urlArray) {
                uri.add(Uri.parse(s));
            }
            adapter = new SwipeLostThingImageAdapter(uri,LostThingDetailActivity.this,true);
            imageViewSwiper.SetImageListAdapter(adapter);
        }

        // 实现返回按钮自动变色。

        imageViewSwiper.SetOnPageChangedListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageView iv = imageViewSwiper.GetInnerImageViews().get(position);
                int res = ComputeHomeArrowColor(iv);
                homeArrow.setCompoundDrawablesWithIntrinsicBounds(res,0,0,0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        LostThingTitle.setText(CurrentLostThingInfo.getTitle());
        PublishTime.setText(AppUtils.UnixStampToFmtString(CurrentLostThingInfo.getPublishTime()));

        String locDesc = CurrentLostThingInfo.getFoundAddrDescription();
        String thingDesc = CurrentLostThingInfo.getThingAddiDescription();

        if (!TextUtils.isEmpty(locDesc)) {
            LocationDesc.setVisibility(View.VISIBLE);
            LocationDesc.setText(locDesc);
        }
        if (!TextUtils.isEmpty(thingDesc)) {
            ThingDesc.setVisibility(View.VISIBLE);
            ThingDesc.setText(thingDesc);
        }

        if (CurrentLostThingInfo.getIsgiven() == 1) {
            GivenDateField.setVisibility(View.VISIBLE);
            GivenDateHint.setVisibility(View.VISIBLE);
            // 把认领的时间SET上去。
            GivenDateField.setText(AppUtils.UnixStampToFmtString(CurrentLostThingInfo.getGivenTime()));
        }


        // 解析失物类别和地点

        int catID = CurrentLostThingInfo.getThingCatId();
        int detailID = CurrentLostThingInfo.getThingDetailId();
        String[] foundAddress = CurrentLostThingInfo.getFoundAddress().split("-");
        int schID = Integer.parseInt(foundAddress[0]);
        int schBuildingID = Integer.parseInt(foundAddress[1]);

        ThingServices ts = FindLostThingsApplication.getThingServices();

        SchoolInfo si = ts.getSchools().get(schID);
        SchoolName.setText(si.getName());

        LostThingsCategory tc = ts.getThingCategory().get(catID);
        ThingCategory.setText(tc.getName());


        new GetSchoolBuildingsTask(TaskRet -> {
            if (AppUtils.CommonResponseOK(TaskRet)) {
                for (MySchoolBuildings sb : TaskRet.getSchoolBuildings()) {
                    if (sb.getId() == schBuildingID) {
                        SchoolBuildingName.setText(sb.getBuildingName());
                    }
                }
            }
        }).execute(schID);

        new GetLostThingsCategoryPartitionTask(TaskRet -> {
            if (AppUtils.CommonResponseOK(TaskRet)) {
                for (LostThingsCategory cd : TaskRet.getCategoryDetails()) {
                    if (cd.getId() == detailID) {
                        ThingDetailCategory.setText(cd.getName());
                    }
                }
            }
        }).execute(catID);

        // 解析发布者的联系方式。

        new GetUserInformationTask(TaskRet -> {
            if (TaskRet != null && TaskRet.getStatusCode() == 0) {
                PublisherContacts = TaskRet.getUserInfo();
                String qq = PublisherContacts.getQQ();
                String wx = PublisherContacts.getWxID();
                String email = PublisherContacts.getEmail();
                String mobile = PublisherContacts.getPhoneNumber();
                String[] value = {qq, wx, email, mobile};
                TextView[] fields = {QQ, Wx, Email, Mobile};
                for (int i = 0; i < value.length; i++) {
                    if (!TextUtils.isEmpty(value[i])) {
                        fields[i].setText(value[i]);
                    }
                }
            }
        }).execute(CurrentLostThingInfo.getPublisher());

    }

    @OnClick({R.id.TakeOrGivenThing})
    public void HandleTakeOrGivenThing(View v) {
        if (CurrentLoginUser.getId() == CurrentLostThingInfo.getPublisher()) {

            if (CurrentLoginUser.getRealPersonValid() != 1) {
                Toast.makeText(LostThingDetailActivity.this, "当前登陆用户尚未完成实名认证，暂时无法归还失物。", Toast.LENGTH_SHORT).show();
                return;
            }

            // 跳转到扫描二维码界面

            Intent intent = new Intent(LostThingDetailActivity.this, CaptureActivity.class);
            ZxingConfig config = new ZxingConfig();
            config.setPlayBeep(false);//是否播放扫描声音 默认为true
            config.setShake(true);//是否震动  默认为true
            config.setDecodeBarCode(true);//是否扫描条形码 默认为true
            config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
            config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
            config.setScanLineColor(android.R.color.transparent);//设置扫描线的颜色 默认白色
            config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
            config.setShowFlashLight(false);
            config.setShowAlbum(false);
            config.setShowbottomLayout(false);
            intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
            startActivityForResult(intent, REQUEST_CODE_SCAN);

        } else {

            if (CurrentLoginUser.getRealPersonValid() != 1) {
                Toast.makeText(LostThingDetailActivity.this, "当前登陆用户尚未完成实名认证，暂时无法认领失物。", Toast.LENGTH_SHORT).show();
                return;
            }

            // 当前登录的账号可以认领此失物。
            QrCodeInfo qrCodeInfo = new QrCodeInfo();
            qrCodeInfo.setThingID(CurrentLostThingInfo.getId());
            qrCodeInfo.setUser(CurrentLoginUser);

            String QrCodeJson = qrCodeInfo.ToJson();
            String Base64UserInfoJson;

            try {
                Base64UserInfoJson = Base64.encodeToString(QrCodeJson.getBytes("utf-8"), Base64.DEFAULT);
                Bitmap QrCode = CodeCreator.createQRCode(Base64UserInfoJson, 260, 260, null);
                UserQrCode.setImageBitmap(QrCode);
                ToggleUserQrCodeContainer(true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    @OnClick({R.id.HomeArrowBtnLostThingDetail})
    public void HomeArrowBtn(View v) {
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK: {
                switch (requestCode) {
                    case REQUEST_CODE_SCAN: {
                        String content = data.getStringExtra(Constant.CODED_CONTENT);
                        try {
                            String decodeContent = new String(Base64.decode(content, Base64.DEFAULT), "utf-8");
                            QrCodeInfo decodeInfo = new Gson().fromJson(decodeContent, QrCodeInfo.class);
                            if (!decodeInfo.getThingID().equals(CurrentLostThingInfo.getId())) {
                                Toast.makeText(LostThingDetailActivity.this, "当前二维码对应的失物信息与待归还失物不一致。", Toast.LENGTH_SHORT).show();
                            } else {
                                // 跳转到信息确认界面。
                                CurrentTakingThingUser = decodeInfo.getUser(); // 提取出失主信息。
                                Intent intent = new Intent(LostThingDetailActivity.this, ValidateGivenActivity.class);
                                intent.putExtra("UserInformation", CurrentTakingThingUser);
                                startActivityForResult(intent, REQUEST_GIVEN_CONFIRM);
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case REQUEST_GIVEN_CONFIRM: {
                        int Action = data.getIntExtra("Action", -1);
                        switch (Action) {
                            case ValidateGivenActivity.CONFIRM_GIVEN: {

                                long CurrentTime = AppUtils.Date2UnixStamp(new Date());
                                CurrentLostThingInfo.setGiven(CurrentTakingThingUser.getId());
                                CurrentLostThingInfo.setIsgiven(1);
                                CurrentLostThingInfo.setGivenTime(CurrentTime);
                                new UpdateLostThingsInfoTask((result) -> {
                                    if (result != null) {
                                        Toast.makeText(LostThingDetailActivity.this, "已归还失物!", Toast.LENGTH_SHORT).show();
                                    }
                                }).execute(CurrentLostThingInfo);

                                break;
                            }
                            case ValidateGivenActivity.REPORT_NOT_VALID: {
                                Toast.makeText(LostThingDetailActivity.this, "报告了认领人非法", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default: {
                                Toast.makeText(LostThingDetailActivity.this, "未知错误!", Toast.LENGTH_SHORT).show();
                                break;
                            }

                        }
                        break;
                    }
                }
                break;
            }

        }
    }

    @OnClick({R.id.UserQrCodeContainer})
    public void HideQrCodeContainer(View view) {
        ToggleUserQrCodeContainer(false);
    }

    private void ToggleUserQrCodeContainer(boolean type) {
        float BeginAlpha = type ? 0f : 1f;
        float EndAlpha = type ? 1f : 0f;
        AlphaAnimation alphaAnimation = new AlphaAnimation(BeginAlpha, EndAlpha);
        alphaAnimation.setDuration(300);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (type) {
                    UserQrCodeContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!type) {
                    UserQrCodeContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        UserQrCodeContainer.startAnimation(alphaAnimation);
    }


    class QrCodeInfo {
        private String ThingID;
        private UserInformation User;

        public String getThingID() {
            return ThingID;
        }

        public UserInformation getUser() {
            return User;
        }

        public void setThingID(String thingID) {
            ThingID = thingID;
        }

        public void setUser(UserInformation user) {
            User = user;
        }

        public String ToJson() {
            return new Gson().toJson(this, QrCodeInfo.class);
        }
    }

    private int Dp2Px(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
