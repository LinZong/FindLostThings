package misaka.nemesiss.com.findlostthings.Adapter;

import android.content.Context;
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter;
import misaka.nemesiss.com.findlostthings.Model.LostThingDetail;

import java.util.List;

public class LostThingDetailAdapter extends MaterialSpinnerAdapter<LostThingDetail>
{
    public LostThingDetailAdapter(Context context, List<LostThingDetail> items) {
        super(context, items);
    }
}
