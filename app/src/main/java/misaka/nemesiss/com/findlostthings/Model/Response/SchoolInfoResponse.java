package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Services.User.SchoolInfo;

import java.util.List;

public class SchoolInfoResponse extends CommonResponse {
    private List<SchoolInfo> SupportSchools;

    public void setSupportSchools(List<SchoolInfo> supportSchools) {
        SupportSchools = supportSchools;
    }

    public List<SchoolInfo> getSupportSchools() {
        return SupportSchools;
    }
}
