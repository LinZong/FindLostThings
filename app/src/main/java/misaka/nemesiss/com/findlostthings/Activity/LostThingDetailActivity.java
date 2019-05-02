package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Tasks.UpdateLostThingsInfoTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class LostThingDetailActivity extends FindLostThingsActivity {

    private static final int REQUEST_CODE_SCAN = 1001;
    private static final int REQUEST_GIVEN_CONFIRM = 999;
    @BindView(R.id.TakeOrGivenThing)
    Button TakeOrGivenThing;
    @BindView(R.id.UserQrCodeContainer)
    RelativeLayout UserQrCodeContainer;
    @BindView(R.id.UserQrCode)
    ImageView UserQrCode;
    @BindView(R.id.LostThingDetailToolbar)
    Toolbar toolbar;

    private LostThingsInfo CurrentLostThingInfo;
    private UserInformation CurrentLoginUser;
    private UserInformation CurrentTakingThingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_thing_detail);
        ButterKnife.bind(this);
        AppUtils.ToolbarShowReturnButton(LostThingDetailActivity.this,toolbar);

        CurrentLostThingInfo = (LostThingsInfo) getIntent().getSerializableExtra("LostThingsInfo");
        CurrentLoginUser = FindLostThingsApplication.getUserService().getMyProfile();
        if (CurrentLoginUser.getId() == CurrentLostThingInfo.getPublisher()) {
            TakeOrGivenThing.setText("归还失物");
        } else {
            TakeOrGivenThing.setText("认领失物");
        }
    }

    @OnClick({R.id.TakeOrGivenThing})
    public void HandleTakeOrGivenThing(View v) {

        if (CurrentLoginUser.getId() == CurrentLostThingInfo.getPublisher()) {
            // 当前登陆的账号是该失物的发布者，可以实行归还操作

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