package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import de.hdodenhof.circleimageview.CircleImageView;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.UserAccount;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Adapter.LostThingsInfoAdapter;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import misaka.nemesiss.com.findlostthings.Tasks.PostUserInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends FindLostThingsActivity {

    @BindView(R.id.ToolbarUserAvatar)
    CircleImageView ToolbarUserAvatar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    TextView NickNameTextView;
    CircleImageView NavigationHeaderBigAvatar;

    private DrawerLayout mDrawerLayout;
    private List<LostThingsInfo> LostThingsInfoList = new ArrayList<>();
    private LostThingsInfoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    //Drawer动画相关
    private Runnable ShouldHandleMenuClicked = null;
    private float CurrentSlideOffset = 0.0f;

    //再按一次退出相关
    private long LastPressedMillSeconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        NickNameTextView = navigationView.getHeaderView(0).findViewById(R.id.nick_name);
        NavigationHeaderBigAvatar = navigationView.getHeaderView(0).findViewById(R.id.head_photo);

        initLostThingsInfo();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LostThingsInfoAdapter(LostThingsInfoList);
        recyclerView.setAdapter(adapter);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this::refreshLostThingsInfo);
        ToolbarUserAvatar.setOnClickListener(this::ClickAvatarToOpenDrawers);

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener()
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {

                super.onDrawerSlide(drawerView, slideOffset);

                if(CurrentSlideOffset > slideOffset && slideOffset < 0.015f && ShouldHandleMenuClicked != null){
                    ShouldHandleMenuClicked.run();
                    ShouldHandleMenuClicked=null;
                    runOnUiThread(()->{
                        navigationView.setCheckedItem(R.id.menu_none);
                    });
                }
                CurrentSlideOffset = slideOffset;
            }
        });

        LoadNickNameAndAvatar();
    }


    private boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.my_find:
                //
                ShouldHandleMenuClicked = () -> {
                    Intent intent = new Intent(MainActivity.this, MyPublishActivity.class);
                    startActivity(intent);
                };
                break;
            case R.id.set_up:
                //mDrawerLayout.closeDrawers();
                ShouldHandleMenuClicked = () -> {
                    Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent1);
                };
                break;
            case R.id.user_nickNameAndImage:
                ShouldHandleMenuClicked = () -> {
                    Intent intent2 = new Intent(MainActivity.this, ShowOrChangeUserInfo.class);
                    startActivity(intent2);
                };
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawers();
        return true;
    }


    private void ClickAvatarToOpenDrawers(View view) {
        mDrawerLayout.openDrawer(Gravity.START);
    }

    public void refreshLostThingsInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    //TODO 在这里获取最新的瀑布流
//                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO 在这里把获取到的数据SET到View上。
//                        initLostThingsInfo();
//                        adapter.notifyDataSetChanged();
//                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.publish:
                startActivity(new Intent(MainActivity.this, PickupImageActivity.class));
                break;
            case R.id.search:
                break;
            default:
                break;

        }
        return true;
    }

    public void initLostThingsInfo() {
        LostThingsInfo lostThingsInfo1 = new LostThingsInfo();
        lostThingsInfo1.setTitle("zm");
        lostThingsInfo1.setPhotoUrl(R.mipmap.fake_avatar);

        LostThingsInfo lostThingsInfo2 = new LostThingsInfo();
        lostThingsInfo2.setTitle("zm");
        lostThingsInfo2.setPhotoUrl(R.drawable.add_photo);

        LostThingsInfo lostThingsInfo3 = new LostThingsInfo();
        lostThingsInfo3.setTitle("zm");
        lostThingsInfo3.setPhotoUrl(R.drawable.add_photo);

        LostThingsInfo lostThingsInfo4 = new LostThingsInfo();
        lostThingsInfo4.setTitle("zm");
        lostThingsInfo4.setPhotoUrl(R.drawable.add_photo);

        LostThingsInfo[] lostThingsInfos = {lostThingsInfo1, lostThingsInfo2, lostThingsInfo3, lostThingsInfo4};

        LostThingsInfoList.clear();
        for (int i = 0; i < 20; i++) {
            Random random = new Random();
            int index = random.nextInt(lostThingsInfos.length);
            LostThingsInfoList.add(lostThingsInfos[index]);
        }


    }


//    private void LoadSearchAnimation()//动画
//    {
//        View view = getWindow().getDecorView();
//        int CurrentScreenHeight = view.getHeight();
//        int ComponentHeight = search.getHeight();
//        float ScaleTime = (float)CurrentScreenHeight / ComponentHeight;
//        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,1.0f,1.0f,ScaleTime,Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
//        scaleAnimation.setFillAfter(true);
//        scaleAnimation.setFillBefore(false);
//        scaleAnimation.setFillEnabled(true);
//        scaleAnimation.setRepeatCount(0);
//        scaleAnimation.setDuration(500);
//        scaleAnimation.setStartOffset(500);
//        search.startAnimation(scaleAnimation);
//        search.setAlpha(1);
//        search.animate().alpha(0).setDuration(500).setStartDelay(500);
//
//    }

    private void LoadNickNameAndAvatar() {
        UserAccount ua = FindLostThingsApplication.getUserService().getUserAccount();
        String NickName = ua.getNickname();
        String AvatarUrl = ua.getImageUrl();

        NickNameTextView.setText(NickName);
        Glide.with(MainActivity.this).load(AvatarUrl).into(ToolbarUserAvatar);
        Glide.with(MainActivity.this).load(AvatarUrl).into(NavigationHeaderBigAvatar);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START)){
            mDrawerLayout.closeDrawers();
        }
        else {
            long curr = System.currentTimeMillis();
            if(curr - LastPressedMillSeconds <= 1000) {
                finish();
            }
            else{
                LastPressedMillSeconds = curr;
                Toast.makeText(MainActivity.this,"再按一次退出程序", Toast.LENGTH_SHORT).show();
            }
        }
    }
}