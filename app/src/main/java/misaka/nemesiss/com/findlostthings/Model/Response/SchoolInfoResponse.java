package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Services.User.SchoolInfo;

public class SchoolInfoResponse extends CommonResponse {
    private SchoolInfo SupportSchools;

    public void setSupportSchools(SchoolInfo supportSchools) {
        SupportSchools = supportSchools;
    }

    public SchoolInfo getSupportSchools() {
        return SupportSchools;
    }
}
