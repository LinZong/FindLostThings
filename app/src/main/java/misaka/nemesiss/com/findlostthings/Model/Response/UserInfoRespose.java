package misaka.nemesiss.com.findlostthings.Model.Response;


import misaka.nemesiss.com.findlostthings.Services.User.UserInfo;

public class UserInfoRespose extends CommonRespose {
    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}

