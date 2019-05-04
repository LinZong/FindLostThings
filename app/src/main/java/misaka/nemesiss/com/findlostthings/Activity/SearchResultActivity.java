package misaka.nemesiss.com.findlostthings.Activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import misaka.nemesiss.com.findlostthings.Adapter.SearchResultAdapter;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Model.SearchLostThingsInfo;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Tasks.GetSearchLostThingsInfoTask;
import misaka.nemesiss.com.findlostthings.Tasks.MyPublishListTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends FindLostThingsActivity {
    private List<LostThingsInfo> LostThingsInfoList = new ArrayList<>();
    private SearchResultAdapter adapter;

    @BindView(R.id.SearchProgressContainer)
    RelativeLayout container;
    @BindView(R.id.SearchProgress)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.SearchResultToolbar);
        setSupportActionBar(toolbar);

        AppUtils.ToolbarShowReturnButton(SearchResultActivity.this,toolbar);

        RecyclerView recyclerView = findViewById(R.id.SearchResultView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchResultAdapter(LostThingsInfoList, SearchResultActivity.this);
        recyclerView.setAdapter(adapter);


        SearchLostThingsInfo searchReq = (SearchLostThingsInfo) getIntent().getSerializableExtra("SearchLostThingsInfo");
        if(searchReq != null) {
            new GetSearchLostThingsInfoTask((result) -> {
                if(result != null) {
                    LostThingsInfoList.clear();
                    LostThingsInfoList.addAll(result);
                    adapter.notifyDataSetChanged();
                    container.setVisibility(View.GONE);
                }
            }).execute(searchReq);
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }
}
