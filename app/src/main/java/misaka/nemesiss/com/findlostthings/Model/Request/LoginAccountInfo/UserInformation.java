package misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo;

public class UserInformation {
    private long Id;
    private String Nickname;
    private int RealPersonValid;
    private String RealPersonIdentity;
    private String QQ;
    private String WxID;
    private String PhoneNumber;
    private String Email;

    public long getId() {
        return Id;
    }

    public String getNickname() {
        return Nickname;
    }

    public int getRealPersonValid() {
        return RealPersonValid;
    }

    public String getQQ() {
        return QQ;
    }

    public String getWxID() {
        return WxID;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public void setRealPersonValid(int realPersonValid) {
        RealPersonValid = realPersonValid;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
    }

    public void setWxID(String wxID) {
        WxID = wxID;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getRealPersonIdentity() {
        return RealPersonIdentity;
    }

    public void setRealPersonIdentity(String realPersonIdentity) {
        RealPersonIdentity = realPersonIdentity;
    }
}