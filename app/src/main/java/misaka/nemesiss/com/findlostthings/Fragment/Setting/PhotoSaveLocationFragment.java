package misaka.nemesiss.com.findlostthings.Fragment.Setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.RadioGroupExtension.SimpleRadioGroup;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

public class PhotoSaveLocationFragment extends SettingBaseFragment {

    private Unbinder unbinder;

    @BindView(R.id.AppDCIMTextView)
    TextView AppDCIMTextView;
    @BindView(R.id.SystemDCIMTextView)
    TextView SystemDCIMTextView;

    @BindView(R.id.AppDataDCIMRadioButton)
    RadioButton AppDataDCIMButton;

    @BindView(R.id.SystemDCIMRadioButton)
    RadioButton SystemDCIMRadioButton;

    private SimpleRadioGroup radioGroup = new SimpleRadioGroup();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_photo_store_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);
        AddRadioButtons();
        LoadSettingToView();
        radioGroup.setOnRadioButtonCheckedListener(this::HandleStorageLocationChanged);
        return view;
    }

    private void HandleStorageLocationChanged(RadioButton btn) {
        int BtnID = btn.getId();
        switch (BtnID){
            case R.id.AppDataDCIMRadioButton:{
                appSettings.setTakePhotoStoreLocation(1);
                break;
            }
            case R.id.SystemDCIMRadioButton:{
                appSettings.setTakePhotoStoreLocation(0);
                break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void AddRadioButtons() {
        radioGroup.AddRadioButton(AppDataDCIMButton);
        radioGroup.AddRadioButton(SystemDCIMRadioButton);
    }

    private void LoadSettingToView() {
        String SystemDCIM = AppUtils.GetSystemDCIMPath();
        String AppDCIM = AppUtils.GetAppDataDCIMPath();
        AppDCIMTextView.setText(AppDCIM);
        SystemDCIMTextView.setText(SystemDCIM);

        int locFlag = appSettings.getTakePhotoStoreLocation();
        if(locFlag == 0) radioGroup.CheckById(R.id.SystemDCIMRadioButton);
        else radioGroup.CheckById(R.id.AppDataDCIMRadioButton);
    }
}
