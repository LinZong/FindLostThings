package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import misaka.nemesiss.com.findlostthings.Adapter.SwipeLostThingImageAdapter;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.AppSettings;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.Common.AppService;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import misaka.nemesiss.com.findlostthings.View.SwipeImageView;

import java.util.ArrayList;
import java.util.List;

public class UserGuidanceActivity extends AppCompatActivity
{

    @BindView(R.id.UserGuideSwiper)
    SwipeImageView GuideSwiper;
    @BindView(R.id.FinishUserGuide)
    Button FinishUserGuide;

    private List<Uri> imgs = new ArrayList<>();

    private SwipeLostThingImageAdapter GuideImgAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guidance);
        ButterKnife.bind(this);

        imgs.add(AppUtils.ParseResourceIdToUri(R.drawable.guide_1));
        imgs.add(AppUtils.ParseResourceIdToUri(R.drawable.guide_2));
        imgs.add(AppUtils.ParseResourceIdToUri(R.drawable.guide_3));
        imgs.add(AppUtils.ParseResourceIdToUri(R.drawable.guide_4));
        imgs.add(AppUtils.ParseResourceIdToUri(R.drawable.guide_5));
        GuideImgAdapter = new SwipeLostThingImageAdapter(imgs,UserGuidanceActivity.this,false);


        GuideSwiper.SetImageListAdapter(GuideImgAdapter);
        GuideSwiper.SetOnPageChangedListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                if(position == imgs.size() - 1) {
                    FinishUserGuide.setVisibility(View.VISIBLE);
                }
                else FinishUserGuide.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    @OnClick({R.id.FinishUserGuide})
    public void HandleFinishUserGuide() {

        AppService appService = FindLostThingsApplication.getAppService();
        AppSettings appSettings = appService.GetAppSettings();
        appSettings.setHaveSeenUserGuide(true);
        appService.SaveAppSettings();

        startActivity(new Intent(UserGuidanceActivity.this,QQAuthLoginActivity.class));
        finish();
    }


}
