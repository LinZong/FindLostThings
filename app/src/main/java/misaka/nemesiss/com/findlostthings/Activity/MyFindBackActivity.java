package misaka.nemesiss.com.findlostthings.Activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import misaka.nemesiss.com.findlostthings.Adapter.MyPublishLostThingsInfoAdapter;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Tasks.MyFindListTask;
import java.util.ArrayList;
import java.util.List;

public class MyFindBackActivity extends FindLostThingsActivity
{

    private List<LostThingsInfo> LostThingsInfoList = new ArrayList<>();
    private MyPublishLostThingsInfoAdapter adapter;
    private boolean IsLoadingMore = false;
    private RecyclerView recyclerView;
    private TextView NoFindRecordHint;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_find_back);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        NoFindRecordHint = findViewById(R.id.noFindRecordHint);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyPublishLostThingsInfoAdapter(LostThingsInfoList,MyFindBackActivity.this);
        recyclerView.setAdapter(adapter);

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

    public void RefreshLostThingsInfo( boolean ClearBefore) {
        if(!IsLoadingMore) {
            IsLoadingMore = true;
            new MyFindListTask(TaskRet -> {
                if(TaskRet!=null&&TaskRet.size()!=0) {
                    if(ClearBefore) {
                        LostThingsInfoList.clear();
                    }
                    LostThingsInfoList.addAll(TaskRet);
                }else
                {
                    recyclerView.setVisibility(View.GONE);
                    NoFindRecordHint.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                IsLoadingMore = false;
            }).execute();
        }
        else {
            Log.d("MyFindBackActivity","刷新请求Pending");
        }
    }
}
