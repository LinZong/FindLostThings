package misaka.nemesiss.com.findlostthings.Services.User;

public class MyResponse {
    private long ExpiredTime;
    private String Expiration;
    private MyCredentials Credentials;
    private String RequestId;

    public void setCredentials(MyCredentials credentials) {
        Credentials = credentials;
    }

    public void setExpiration(String expiration) {
        Expiration = expiration;
    }

    public void setExpiredTime(long expiredTime) {
        ExpiredTime = expiredTime;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public MyCredentials getCredentials() {
        return Credentials;
    }

    public long getExpiredTime() {
        return ExpiredTime;
    }

    public String getExpiration() {
        return Expiration;
    }

    public String getRequestId() {
        return RequestId;
    }
}
