package misaka.nemesiss.com.findlostthings.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import misaka.nemesiss.com.findlostthings.Fragment.Setting.SettingBaseFragment;
import misaka.nemesiss.com.findlostthings.Fragment.Setting.SettingFragment;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.io.Serializable;
import java.util.Stack;

public class SettingActivity extends FindLostThingsActivity
{
    private static Toolbar toolbar;
    private static FragmentManager fm;
    private static Stack<SettingBaseFragment> SettingFragmentsList = new Stack<>();

    private static TextView SettingTitleTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        toolbar = findViewById(R.id.toolbar);
        SettingTitleTextView = findViewById(R.id.SettingTitleTextView);

        fm = getSupportFragmentManager();
        AppUtils.ToolbarShowReturnButton(SettingActivity.this,toolbar);
        ToNextFragment(new SettingFragment(),"设置",null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                HandleFragmentsBack();
                break;
            }
        }
        return true;
    }

    public static void ToNextFragment(SettingBaseFragment fragment, String SettingTitle, @Nullable Serializable arguments){
        Bundle bundle = new Bundle();
        bundle.putSerializable("Argument",arguments);
        int CurrentBackStackCount = fm.getBackStackEntryCount();
        fragment.setArguments(bundle);
        fragment.setSettingTitle(SettingTitle);
        FragmentTransaction ft = fm.beginTransaction();
        if(CurrentBackStackCount > 0)
            ft.setCustomAnimations(R.anim.slide_in,R.anim.slide_out,R.anim.pop_slide_in,R.anim.pop_slide_out);
        ft.replace(R.id.SettingFragmentContainer,fragment);
        ft.addToBackStack(null);
        SettingFragmentsList.add(fragment);
        SettingTitleTextView.setText(fragment.getSettingTitle());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
            HandleFragmentsBack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void HandleFragmentsBack() {
        //检查Fragment栈是否有Fragment, 有的话pop，没有就finish这个Activity。
        int CurrentBackStackCount = fm.getBackStackEntryCount();
        if(CurrentBackStackCount > 1) {
            fm.popBackStack();
            SettingFragmentsList.pop();
            SettingBaseFragment TopFragment = SettingFragmentsList.peek();
            SettingTitleTextView.setText(TopFragment.getSettingTitle());
        }
        else finish();
    }
}
