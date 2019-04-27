package misaka.nemesiss.com.findlostthings.Adapter;

import android.content.Context;
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter;
import misaka.nemesiss.com.findlostthings.Model.SchoolInfo;

import java.util.List;

public class SchoolInfoCategoryAdapter extends MaterialSpinnerAdapter<SchoolInfo>
{
    public SchoolInfoCategoryAdapter(Context context, List<SchoolInfo> items)
    {
        super(context, items);
    }
}
