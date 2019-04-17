package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;

import java.util.List;

public class UserInfoUpdateResponse extends CommonResponse {
    protected List<UserInformation> Updated;

    public void setUpdated(List<UserInformation> updated)
    {
        Updated = updated;
    }

    public List<UserInformation> getUpdated()
    {
        return Updated;
    }
}