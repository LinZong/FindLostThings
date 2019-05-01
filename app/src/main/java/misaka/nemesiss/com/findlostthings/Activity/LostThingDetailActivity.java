package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import misaka.nemesiss.com.findlostthings.Tasks.UpdateUserInformationAsyncTask;
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

    private LostThingsInfo CurrentLostThingInfo;
    private UserInformation CurrentLoginUser;
    private UserInformation CurrentTakingThingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_thing_detail);
        ButterKnife.bind(this);
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
            String UserInfoJson = CurrentLoginUser.ToJson();
            String Base64UserInfoJson = "";
            try {
                Base64UserInfoJson = Base64.encodeToString(UserInfoJson.getBytes("utf-8"), Base64.DEFAULT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();

            }
            Bitmap QrCode = CodeCreator.createQRCode(Base64UserInfoJson, 280, 280, null);
            UserQrCode.setImageBitmap(QrCode);
            ToggleUserQrCodeContainer(true);
        }

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
                            CurrentTakingThingUser = new Gson().fromJson(decodeContent,UserInformation.class);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    case REQUEST_GIVEN_CONFIRM: {
                        int Action = data.getIntExtra("Action", -1);
                        switch (Action) {
                            case ValidateGivenActivity.CONFIRM_GIVEN: {

                                long CurrentTime = AppUtils.Date2UnixStamp(new Date());
                                CurrentLostThingInfo.setGiven(CurrentTakingThingUser.getId());
                                CurrentLostThingInfo.setIsgiven(1);
                                CurrentLostThingInfo.setGivenTime(CurrentTime);
//
//                                new (TaskRet -> {
//
//                                }).execute(CurrentLostThingInfo)
                                break;
                            }
                            case ValidateGivenActivity.REPORT_NOT_VALID: {

                                break;
                            }
                            default: {
                                break;
                            }

                        }
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
}
