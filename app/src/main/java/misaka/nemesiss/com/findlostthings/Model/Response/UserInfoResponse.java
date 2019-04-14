package misaka.nemesiss.com.findlostthings.Model.Response;


import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;


public class UserInfoResponse extends CommonResponse {
    private UserInformation UserInfo;

    public UserInformation getUserInfo() {
        return UserInfo;
    }

    public void setUserInfo(UserInformation userInfo) {
        this.UserInfo = userInfo;
    }
}

