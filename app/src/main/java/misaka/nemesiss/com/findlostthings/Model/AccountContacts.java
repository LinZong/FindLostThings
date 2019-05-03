package misaka.nemesiss.com.findlostthings.Model;

import java.io.Serializable;

public class AccountContacts implements Serializable, Cloneable {
    private String QQ;
    private String WxID;
    private String PhoneNumber;
    private String Email;

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
}
