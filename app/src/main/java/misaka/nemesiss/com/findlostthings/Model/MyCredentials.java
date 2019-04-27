package misaka.nemesiss.com.findlostthings.Model;

public class MyCredentials {
    private String Token;
    private String TmpSecretId;
    private String TmpSecretKey;

    public void setTmpSecretId(String tmpSecretId) {
        TmpSecretId = tmpSecretId;
    }

    public void setTmpSecretKey(String tmpSecretKey) {
        TmpSecretKey = tmpSecretKey;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getTmpSecretId() {
        return TmpSecretId;
    }

    public String getTmpSecretKey() {
        return TmpSecretKey;
    }

    public String getToken() {
        return Token;
    }
}
