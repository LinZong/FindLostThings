package misaka.nemesiss.com.findlostthings.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding.view.RxView;
import misaka.nemesiss.com.findlostthings.R;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends FindLostThingsActivity
{

    @BindView(R.id.MainActivity_GreetButton)
    Button greetBtn;
    @BindView(R.id.MainActivity_GreetText)
    TextView greetText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        RxView.clicks(greetBtn)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe((aVoids)->greetText.setText("Hello guys who are in 失物招领App dev group!"));

    }
}