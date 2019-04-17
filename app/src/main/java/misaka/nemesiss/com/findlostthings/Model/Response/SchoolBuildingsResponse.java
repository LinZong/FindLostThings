package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Services.User.MySchoolBuildings;

import java.util.List;

public class SchoolBuildingsResponse extends CommonResponse {
    private List<MySchoolBuildings> SchoolBuildings;

    public void setSchoolBuildings(List<MySchoolBuildings> schoolBuildings) {
        SchoolBuildings = schoolBuildings;
    }

    public List<MySchoolBuildings> getSchoolBuildings() {
        return SchoolBuildings;
    }
}
