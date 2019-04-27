package misaka.nemesiss.com.findlostthings.InfrastructureExtension.RadioGroupExtension;

import android.view.View;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SimpleRadioGroup {
    private ArrayList<RadioButton> radioButtonGroup = new ArrayList<>();
    private OnRadioButtonChecked mListener;

    public void setOnRadioButtonCheckedListener(OnRadioButtonChecked listener){
        mListener = listener;
    }
    public void removeOnRadioButtonCheckedListener(){
        mListener = null;
    }
    public interface OnRadioButtonChecked {
        void handle(RadioButton buttonInstance);
    }

    public void AddRadioButton(RadioButton btn){
        radioButtonGroup.add(btn);
        btn.setOnClickListener(this::HandlerRadioButtonClicked);
    }

    private void HandlerRadioButtonClicked(View view) {

        ClearChecked();
        RadioButton rb = (RadioButton) view;
        rb.setChecked(true);
        if(mListener!=null) mListener.handle(rb);
    }

    public ArrayList<RadioButton> GetRadioButtonGroup(){
        return radioButtonGroup;
    }

    private void ClearChecked(){
        for (RadioButton radioButton : radioButtonGroup) {
            radioButton.setChecked(false);
        }
    }

    public void CheckById(int ResourceID){
        ClearChecked();
        for (RadioButton radioButton : radioButtonGroup) {
            if(radioButton.getId() == ResourceID){
                radioButton.setChecked(true);
                break;
            }
        }
    }
}
