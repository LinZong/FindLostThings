package misaka.nemesiss.com.findlostthings.Model.Response;

        import misaka.nemesiss.com.findlostthings.Model.LostThingsCategory;

        import java.util.List;

public class LostThingsCategoryResponse extends CommonResponse {
    private List<LostThingsCategory> CategoryList;

    public List<LostThingsCategory> getCategoryList() {
        return CategoryList;
    }

    public void setCategoryList(List<LostThingsCategory> categoryList) {
        CategoryList = categoryList;
    }
}
