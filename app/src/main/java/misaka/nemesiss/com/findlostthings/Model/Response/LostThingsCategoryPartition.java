package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;

public class LostThingsCategoryPartition extends CommonResponse {
    private int CategoryId;
    private LostThingsCategory CategoryDetails;

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public void setLostThingsCategory(LostThingsCategory lostThingsCategory) {
        this.CategoryDetails = lostThingsCategory;
    }


    public int getCategoryId() {
        return CategoryId;
    }

    public LostThingsCategory getLostThingsCategory() {
        return CategoryDetails;
    }
}
