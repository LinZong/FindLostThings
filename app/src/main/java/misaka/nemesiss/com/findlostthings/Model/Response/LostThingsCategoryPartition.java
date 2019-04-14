package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;

public class LostThingsCategoryPartition extends CommonRespose {
    private int CategoryId;
    private LostThingsCategory lostThingsCategory;

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public void setLostThingsCategory(LostThingsCategory lostThingsCategory) {
        this.lostThingsCategory = lostThingsCategory;
    }


    public int getCategoryId() {
        return CategoryId;
    }

    public LostThingsCategory getLostThingsCategory() {
        return lostThingsCategory;
    }
}
