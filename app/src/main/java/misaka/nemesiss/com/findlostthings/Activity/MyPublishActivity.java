package misaka.nemesiss.com.findlostthings.Activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Adapter.LostThingsInfoAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyPublishActivity extends FindLostThingsActivity
{
    private List<LostThingsInfo> LostThingsInfoList = new ArrayList<>();
    private LostThingsInfoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypublish);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initLostThingsInfo();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LostThingsInfoAdapter(LostThingsInfoList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                refreshLostThingsInfo();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
    }

    public void refreshLostThingsInfo()
    {
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    private void initLostThingsInfo()
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

    }
}
