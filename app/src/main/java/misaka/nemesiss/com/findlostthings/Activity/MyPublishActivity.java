package misaka.nemesiss.com.findlostthings.Activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import misaka.nemesiss.com.findlostthings.Adapter.MyPublishLostThingsInfoAdapter;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Tasks.MyPublishListTask;
import java.util.ArrayList;
import java.util.List;

public class MyPublishActivity extends FindLostThingsActivity {
    private List<LostThingsInfo> LostThingsInfoList = new ArrayList<>();
    private MyPublishLostThingsInfoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean IsLoadingMore = false;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypublish);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyPublishLostThingsInfoAdapter(LostThingsInfoList,MyPublishActivity.this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HandleSwipeRefresh();
            }
        });
        RefreshLostThingsInfo(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void HandleSwipeRefresh() {

        RefreshLostThingsInfo(true);
    }

    public void RefreshLostThingsInfo( boolean ClearBefore) {
        if(!IsLoadingMore) {
            swipeRefreshLayout.setRefreshing(true);
            IsLoadingMore = true;
            new MyPublishListTask(TaskRet -> {
                if(TaskRet!=null&&TaskRet.size()!=0) {
                    if(ClearBefore) {
                        LostThingsInfoList.clear();
                    }
                    LostThingsInfoList.addAll(TaskRet);
                }else
                    Toast.makeText(MyPublishActivity.this,"已经是最新啦",Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                IsLoadingMore = false;
            }).execute();
        }
        else {
            Log.d("MyPublishActivity","刷新请求Pending");
        }
    }

}
