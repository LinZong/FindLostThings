package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;

import java.util.List;

public class LostThingsCategoryPartition extends CommonResponse {
    private int CategoryId;
    protected List<LostThingsCategory> CategoryDetails;

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public void setCategoryDetails(List<LostThingsCategory> categoryDetails)
    {
        CategoryDetails = categoryDetails;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public List<LostThingsCategory> getCategoryDetails()
    {
        return CategoryDetails;
    }
}
