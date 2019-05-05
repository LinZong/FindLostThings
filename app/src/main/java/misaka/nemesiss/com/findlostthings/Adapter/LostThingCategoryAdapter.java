package misaka.nemesiss.com.findlostthings.Adapter;

import android.content.Context;
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter;
import misaka.nemesiss.com.findlostthings.Model.LostThingDetail;
import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;

import java.util.List;

public class LostThingCategoryAdapter extends MaterialSpinnerAdapter<LostThingsCategory>
{
    public LostThingCategoryAdapter(Context context, List<LostThingsCategory> items)
    {
        super(context, items);
    }
}
