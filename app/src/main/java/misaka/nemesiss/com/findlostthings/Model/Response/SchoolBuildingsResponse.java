package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Services.User.MySchoolBuildings;

public class SchoolBuildingsResponse extends CommonResponse {
    private MySchoolBuildings SchoolBuildings;

    public void setSchoolBuildings(MySchoolBuildings schoolBuildings) {
        SchoolBuildings = schoolBuildings;
    }

    public MySchoolBuildings getSchoolBuildings() {
        return SchoolBuildings;
    }
}
