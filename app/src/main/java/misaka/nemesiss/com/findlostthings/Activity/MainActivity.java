package misaka.nemesiss.com.findlostthings.Activity;

import android.graphics.LinearGradient;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import misaka.nemesiss.com.findlostthings.R;

public class MainActivity extends FindLostThingsActivity
{
    @BindView(R.id.MainActivity_Search)
    ConstraintLayout search;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        search.setOnClickListener(view -> LoadSearchAnimation());

    }

    private void LoadSearchAnimation()//动画
    {
        View view = getWindow().getDecorView();
        int CurrentScreenHeight = view.getHeight();
        int ComponentHeight = search.getHeight();
        float ScaleTime = (float)CurrentScreenHeight / ComponentHeight;
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,1.0f,1.0f,ScaleTime,Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setFillBefore(false);
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setRepeatCount(0);
        scaleAnimation.setDuration(500);
        scaleAnimation.setStartOffset(500);
        search.startAnimation(scaleAnimation);
        search.setAlpha(1);
        search.animate().alpha(0).setDuration(500).setStartDelay(500);

    }
}