package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Model.LostThingDetail;

import java.util.List;

public class LostThingsCategoryPartition extends CommonResponse {
    private int CategoryId;
    protected List<LostThingDetail> CategoryDetails;

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public void setCategoryDetails(List<LostThingDetail> categoryDetails)
    {
        CategoryDetails = categoryDetails;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public List<LostThingDetail> getCategoryDetails()
    {
        return CategoryDetails;
    }
}
