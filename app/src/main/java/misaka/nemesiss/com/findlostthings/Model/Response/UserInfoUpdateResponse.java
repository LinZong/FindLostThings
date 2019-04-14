package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;

public class UserInfoUpdateResponse extends CommonResponse {
    private UserInformation Updated;

    public UserInformation getUserInfo() {
        return Updated;
    }

    public void setUserInfo(UserInformation userInfo) {
        this.Updated = userInfo;
    }
}