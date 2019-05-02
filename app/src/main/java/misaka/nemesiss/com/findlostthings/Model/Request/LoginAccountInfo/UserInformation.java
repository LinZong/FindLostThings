package misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Model.AccountContacts;

import java.io.Serializable;

public class UserInformation extends AccountContacts implements Serializable,Cloneable {
    private long Id;
    private String Nickname;
    private int RealPersonValid;
    private String RealPersonIdentity;


    public long getId() {
        return Id;
    }

    public String getNickname() {
        return Nickname;
    }

    public int getRealPersonValid() {
        return RealPersonValid;
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


    public String getRealPersonIdentity() {
        return RealPersonIdentity;
    }

    public void setRealPersonIdentity(String realPersonIdentity) {
        RealPersonIdentity = realPersonIdentity;
    }

    public String ToJson() {
        return new Gson().toJson(this,UserInformation.class);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}