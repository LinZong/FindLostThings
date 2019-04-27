package misaka.nemesiss.com.findlostthings.Model.Response;
import misaka.nemesiss.com.findlostthings.Model.MyResponse;

public class GetStoreBucketKeyResponse extends CommonResponse {
    private String FullBucketName;
    private String Region;
    private MyResponse Response;

    public void setResponse(MyResponse response) {
        Response = response;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public void setFullBucketName(String fullBucketName) {
        FullBucketName = fullBucketName;
    }

    public String getRegion() {
        return Region;
    }

    public String getFullBucketName() {
        return FullBucketName;
    }

    public MyResponse getResponse() {
        return Response;
    }
}
