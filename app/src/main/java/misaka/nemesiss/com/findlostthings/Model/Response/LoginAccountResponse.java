package misaka.nemesiss.com.findlostthings.Model.Response;

public class LoginAccountResponse extends CommonRespose {
    private long UserID;
    private String LastLoginDeviceAndroidID;

    public long getUserID() {
        return UserID;
    }

    public String getLastLoginDeviceAndroidID() {
        return LastLoginDeviceAndroidID;
    }

    public void setLastLoginDeviceAndroidID(String lastLoginDeviceAndroidID) {
        LastLoginDeviceAndroidID = lastLoginDeviceAndroidID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }
}
