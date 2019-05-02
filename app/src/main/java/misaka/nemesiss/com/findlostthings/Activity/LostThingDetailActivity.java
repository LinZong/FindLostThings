package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.AccountContacts;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;
import misaka.nemesiss.com.findlostthings.R;
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
    private AccountContacts PublisherContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_thing_detail);
        ButterKnife.bind(this);

        CurrentLostThingInfo = (LostThingsInfo) getIntent().getSerializableExtra("LostThingsInfo");
        CurrentLoginUser = FindLostThingsApplication.getUserService().getMyProfile();
        if (CurrentLoginUser.getId() == CurrentLostThingInfo.getPublisher()) {
            TakeOrGivenThing.setTitle("归还失物");
        } else {
            TakeOrGivenThing.setTitle("认领失物");
        }
    }


    private void LoadInformation() {
        String str = CurrentLostThingInfo.getThingPhotoUrls();
        String[] urlArray = new Gson().fromJson(str,new TypeToken<String[]>(){}.getType());
        if(urlArray.length > 0) {
            // Convert To URI List
            List<Uri> uri = new ArrayList<>();
            for (String s : urlArray) {
                uri.add(Uri.parse(s));
            }
            imageViewSwiper.SetImageList(uri, LostThingDetailActivity.this);
        }

        PublishTime.setText(AppUtils.UnixStampToFmtString(CurrentLostThingInfo.getPublishTime()));

        String locDesc = CurrentLostThingInfo.getFoundAddrDescription();
        String thingDesc = CurrentLostThingInfo.getThingAddiDescription();

        if(!TextUtils.isEmpty(locDesc)) {
            LocationDesc.setVisibility(View.VISIBLE);
            LocationDesc.setText(locDesc);
        }
        if(!TextUtils.isEmpty(thingDesc)) {
            ThingDesc.setVisibility(View.VISIBLE);
            ThingDesc.setText(thingDesc);
        }

        if(CurrentLostThingInfo.getIsgiven() == 1) {
            GivenDateField.setVisibility(View.VISIBLE);
            GivenDateHint.setVisibility(View.VISIBLE);
            // 把认领的时间SET上去。
            GivenDateField.setText(AppUtils.UnixStampToFmtString(CurrentLostThingInfo.getGivenTime()));
        }

        // 解析发布者的联系方式。

        String contactsStr = CurrentLostThingInfo.getPublisherContacts();
        if(!TextUtils.isEmpty(contactsStr)) {

            PublisherContacts = new Gson().fromJson(contactsStr,AccountContacts.class);
            String qq = PublisherContacts.getQQ();
            String wx = PublisherContacts.getWxID();
            String email = PublisherContacts.getEmail();
            String mobile = PublisherContacts.getPhoneNumber();

            String[] value = {qq,wx,email,mobile};
            TextView[] fields = {QQ,Wx,Email,Mobile};
            for (int i = 0; i < value.length; i++) {
                if(!TextUtils.isEmpty(value[i])) {
                    fields[i].setText(value[i]);
                }
            }
        }
    }

    @OnClick({R.id.TakeOrGivenThing})
    public void HandleTakeOrGivenThing(View v) {
        if (CurrentLoginUser.getId() == CurrentLostThingInfo.getPublisher()) {

            if(CurrentLoginUser.getRealPersonValid() != 1) {
                Toast.makeText(LostThingDetailActivity.this,"当前登陆用户尚未完成实名认证，暂时无法归还失物。", Toast.LENGTH_SHORT).show();
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

            if(CurrentLoginUser.getRealPersonValid() != 1) {
                Toast.makeText(LostThingDetailActivity.this,"当前登陆用户尚未完成实名认证，暂时无法认领失物。", Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : {
                finish();
                break;
            }
        }
        return true;
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
                            QrCodeInfo decodeInfo = new Gson().fromJson(decodeContent,QrCodeInfo.class);
                            if(!decodeInfo.getThingID().equals(CurrentLostThingInfo.getId())){
                                Toast.makeText(LostThingDetailActivity.this,"当前二维码对应的失物信息与待归还失物不一致。", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // 跳转到信息确认界面。
                                CurrentTakingThingUser = decodeInfo.getUser(); // 提取出失主信息。
                                Intent intent = new Intent(LostThingDetailActivity.this,ValidateGivenActivity.class);
                                intent.putExtra("UserInformation",CurrentTakingThingUser);
                                startActivityForResult(intent,REQUEST_GIVEN_CONFIRM);
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
                                    if(result!=null) {
                                        Toast.makeText(LostThingDetailActivity.this,"已归还失物!", Toast.LENGTH_SHORT).show();
                                    }
                                }).execute(CurrentLostThingInfo);

                                break;
                            }
                            case ValidateGivenActivity.REPORT_NOT_VALID: {
                                Toast.makeText(LostThingDetailActivity.this,"报告了认领人非法", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default: {
                                Toast.makeText(LostThingDetailActivity.this,"未知错误!", Toast.LENGTH_SHORT).show();
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
            return new Gson().toJson(this,QrCodeInfo.class);
        }
    }

}
