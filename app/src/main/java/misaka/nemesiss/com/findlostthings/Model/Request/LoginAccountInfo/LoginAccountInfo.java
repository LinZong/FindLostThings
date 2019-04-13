package misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo;

public class LoginAccountInfo {
    private String OpenID;
    private String AccessToken;
    private String NickName;
    private String CurrentDeviceAndroidID;

    public String getAccessToken() {
        return AccessToken;
    }

    public String getCurrentDeviceAndroidID() {
        return CurrentDeviceAndroidID;
    }

    public String getNickName() {
        return NickName;
    }

    public String getOpenID() {
        return OpenID;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public void setCurrentDeviceAndroidID(String currentDeviceAndroidID) {
        CurrentDeviceAndroidID = currentDeviceAndroidID;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public void setOpenID(String openID) {
        OpenID = openID;
    }
}
