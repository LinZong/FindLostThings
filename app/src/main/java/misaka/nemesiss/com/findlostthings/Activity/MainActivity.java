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
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.UserAccount;
import misaka.nemesiss.com.findlostthings.Model.WaterfallThingsInfo;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Adapter.LostThingsInfoAdapter;
import misaka.nemesiss.com.findlostthings.Tasks.WaterfallThingsInfoTask;
import misaka.nemesiss.com.findlostthings.Utils.TopSmoothScroller;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FindLostThingsActivity {

    @BindView(R.id.ToolbarUserAvatar)
    CircleImageView ToolbarUserAvatar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    TextView NickNameTextView;
    CircleImageView NavigationHeaderBigAvatar;

    private DrawerLayout mDrawerLayout;
    private List<LostThingsInfo> WaterfallInfoList = new ArrayList<>();
    private LostThingsInfoAdapter adapter;


    // 上拉加载，下滑加载相关
    private WaterfallThingsInfo InitWaterfall;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager WaterfallLayoutManager;
    private boolean IsLoadingMore = false;
    private int EveryTimeLoadInfoCount = 30;

    //Drawer动画相关
    private Runnable ShouldHandleMenuClicked = null;
    private float CurrentSlideOffset = 0.0f;

    //再按一次退出相关
    private long LastPressedMillSeconds = 0;

    private void InitComponents() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        NickNameTextView = navigationView.getHeaderView(0).findViewById(R.id.nick_name);
        NavigationHeaderBigAvatar = navigationView.getHeaderView(0).findViewById(R.id.head_photo);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        WaterfallLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(WaterfallLayoutManager);
        adapter = new LostThingsInfoAdapter(WaterfallInfoList,MainActivity.this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this::HandleSwipeRefresh);
        ToolbarUserAvatar.setOnClickListener(this::ClickAvatarToOpenDrawers);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (isSlideCloseToBottom(recyclerView)) {
                        loadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        NavigationHeaderBigAvatar.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity.this, ShowOrChangeUserInfo.class);
            startActivity(intent2);
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                super.onDrawerSlide(drawerView, slideOffset);

                if (CurrentSlideOffset > slideOffset && slideOffset < 0.015f && ShouldHandleMenuClicked != null) {
                    ShouldHandleMenuClicked.run();
                    ShouldHandleMenuClicked = null;
                    runOnUiThread(() -> {
                        navigationView.setCheckedItem(R.id.menu_none);
                    });
                }
                CurrentSlideOffset = slideOffset;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        InitComponents();

        InitWaterfall = new WaterfallThingsInfo();
        InitWaterfall.setHaveFetchedItemCount(0);
        InitWaterfall.setCount(EveryTimeLoadInfoCount);

        RefreshLostThingsInfo(InitWaterfall,true);

        LoadNickNameAndAvatar();
    }

    private void HandleSwipeRefresh() {

        RefreshLostThingsInfo(InitWaterfall,true);
    }


    private boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null)
            return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    private boolean isSlideCloseToBottom(RecyclerView recyclerView) {
        if (recyclerView == null)
            return false;
        int extent = recyclerView.computeVerticalScrollExtent();
        int offset = recyclerView.computeVerticalScrollOffset();
        int range = recyclerView.computeVerticalScrollRange();

        return (extent + offset) >= (range - (2 * extent));
    }

    private void loadMore() {
        LostThingsInfo lostThingsInfo = WaterfallInfoList.get(WaterfallInfoList.size()-1);
        Log.d("MainActivity","到底，底部元素为"+lostThingsInfo.getTitle());
        String id = lostThingsInfo.getId();
        WaterfallThingsInfo info = new WaterfallThingsInfo();
        info.setHaveFetchedItemCount(info.getHaveFetchedItemCount() + info.getCount());
        info.setCount(EveryTimeLoadInfoCount);
        info.setEndItemId(id);
        RefreshLostThingsInfo(info,false);
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
                ShouldHandleMenuClicked = () -> {
                    Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent1);
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

    public void RefreshLostThingsInfo(WaterfallThingsInfo RefreshInfo,boolean ClearBefore) {
        if(!IsLoadingMore) {
            swipeRefreshLayout.setRefreshing(true);
            IsLoadingMore = true;
            new WaterfallThingsInfoTask(TaskRet -> {
                if(TaskRet!=null&&TaskRet.size()!=0) {
                    //LostThingsInfoList.clear();
                    if(ClearBefore) {
                        WaterfallInfoList.clear();
                    }
                    WaterfallInfoList.addAll(TaskRet);
                }else
                    Toast.makeText(MainActivity.this,"已经是最新啦",Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                IsLoadingMore = false;
            }).execute(RefreshInfo);
        }
        else {
            Log.d("MainActivity","刷新请求Pending");
        }
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
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            default:
                break;

        }
        return true;
    }


    @OnClick({R.id.BackToTop})
    public void GoBackToTop(View v)
    {
        final TopSmoothScroller mScroller = new TopSmoothScroller(MainActivity.this);
        mScroller.setTargetPosition(0);
        WaterfallLayoutManager.startSmoothScroll(mScroller);
    }

    private void LoadNickNameAndAvatar() {
        UserAccount ua = FindLostThingsApplication.getUserService().getUserAccount();
        if (ua != null) {
            String NickName = ua.getNickname();
            String AvatarUrl = ua.getImageUrl();

            NickNameTextView.setText(NickName);
            Glide.with(MainActivity.this).load(AvatarUrl).into(ToolbarUserAvatar);
            Glide.with(MainActivity.this).load(AvatarUrl).into(NavigationHeaderBigAvatar);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            long curr = System.currentTimeMillis();
            if (curr - LastPressedMillSeconds <= 1000) {
                finish();
            } else {
                LastPressedMillSeconds = curr;
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            }
        }
    }
}