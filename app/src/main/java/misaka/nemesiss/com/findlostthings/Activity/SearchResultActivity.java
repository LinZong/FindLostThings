package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

public class SearchResultActivity extends AppCompatActivity
{

    @BindView(R.id.SearchResultToolbar)
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        AppUtils.ToolbarShowReturnButton(SearchResultActivity.this,toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return true;
    }
}
