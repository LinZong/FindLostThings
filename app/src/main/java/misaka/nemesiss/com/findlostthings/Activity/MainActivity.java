package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import de.hdodenhof.circleimageview.CircleImageView;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.UserAccount;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.User.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Services.User.LostThingsInfoAdapter;
import misaka.nemesiss.com.findlostthings.Tasks.PostInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends FindLostThingsActivity
{

    @BindView(R.id.ToolbarUserAvatar)
    CircleImageView ToolbarUserAvatar;
    @BindView(R.id.float_ab)
    FloatingActionButton PublishLostThingBtn;

    TextView NickNameTextView;

    CircleImageView NavigationHeaderBigAvatar;

    private DrawerLayout mDrawerLayout;
    private List<LostThingsInfo> LostThingsInfoList = new ArrayList<>();
    private LostThingsInfoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null)
//        {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//        actionBar.setHomeAsUpIndicator()
        // navigationView.setCheckedItem(R.id.set_up);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {

                switch (item.getItemId())
                {
                    case R.id.my_find:
                       //
                        Intent intent=new Intent(MainActivity.this,Mypublish.class);
                        startActivity(intent);
                        break;
                    case R.id.set_up:
                        //mDrawerLayout.closeDrawers();
                        Intent intent1=new Intent(MainActivity.this,SetUp.class);
                        startActivity(intent1);
                        break;
                    case R.id.user_nickNameAndImage:
                        Intent intent2=new Intent(MainActivity.this,ShowOrChangeUserInfo.class);
                        startActivity(intent2);
                        break;
                        default:
                            mDrawerLayout.closeDrawers();
                            break;
                }
                return true;
            }
        });

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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                refreshLostThingsInfo();
            }
        });

        LoadUserAccountInfo();
        ToolbarUserAvatar.setOnClickListener(this::ClickAvatarToOpenDrawers);
        PublishLostThingBtn.setOnClickListener(v ->{
            startActivity(new Intent(MainActivity.this,PickupImageActivity.class));
        });
    }

    private void ClickAvatarToOpenDrawers(View view)
    {
        mDrawerLayout.openDrawer(Gravity.START);
    }

    public void refreshLostThingsInfo()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(2000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        initLostThingsInfo();
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }


    public void initLostThingsInfo()
    {
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
        for (int i = 0; i < 20; i++)
        {
            Random random = new Random();
            int index = random.nextInt(lostThingsInfos.length);
            LostThingsInfoList.add(lostThingsInfos[index]);
        }
//
//        WaterfallThingsInfo waterfallThingsInfo = new WaterfallThingsInfo();
//        waterfallThingsInfo.setCount(10);
//        waterfallThingsInfo.setHaveFetchedItemCount(0);
//        waterfallThingsInfo.setEndItemId("[Guid(\"EB41C40D-C17E-4641-9524-BD543BC95042\")]");
//        new WaterfallThingsInfoTask(TaskRet ->
//        {
//            for (LostThingsInfo sb : TaskRet)
//            {
//                LostThingsInfoList.add(sb);
//            }
//        }).execute(waterfallThingsInfo);
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


    private void LoadUserAccountInfo()
    {
        Tencent tencent = FindLostThingsApplication.getQQAuthService();
        QQToken qqToken = tencent.getQQToken();
        UserInfo ua = new UserInfo(MainActivity.this, qqToken);
        ua.getUserInfo(CommonUserInfoListener);
    }

    private IUiListener CommonUserInfoListener = new IUiListener()
    {
        @Override
        public void onComplete(Object o)
        {
            JSONObject jsonObject = (JSONObject) o;
            try
            {
                String name = jsonObject.getString("nickname");
                String imgUrl = jsonObject.getString("figureurl_qq_2");  //头像url
                String openID = FindLostThingsApplication.getQQAuthService().getOpenId();
                UserAccount ua = FindLostThingsApplication.getLoginUserAccount();
                ua.setImageUrl(imgUrl);
                ua.setNickname(name);
                NickNameTextView.setText(name);
                Glide.with(MainActivity.this).load(imgUrl).into(ToolbarUserAvatar);
                Glide.with(MainActivity.this).load(imgUrl).into(NavigationHeaderBigAvatar);
                new PostInformationAsyncTask((res) ->
                {
                    if (res.getStatusCode() != 0)
                    {
                        Log.d("QQAuthLoginActivity", "上报数据给服务器出现异常!.");
                    }
                    Log.d("QQAuthLoginActivity", "成功上报数据给服务器.");
                }).execute(openID, name, AppUtils.getAndroidId(FindLostThingsApplication.getContext()));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError)
        {
            Log.d("QQAuthLoginActivity", "获取个人信息出现异常!.");
        }

        @Override
        public void onCancel()
        {

        }
    };
}