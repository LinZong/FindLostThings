package misaka.nemesiss.com.findlostthings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity
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
