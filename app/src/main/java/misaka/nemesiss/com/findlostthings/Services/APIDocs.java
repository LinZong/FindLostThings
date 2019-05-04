package misaka.nemesiss.com.findlostthings.Services;

public class APIDocs
{
    //public static final String DeploymentAddress = "http://192.168.88.126:8970";
    public static final String DeploymentAddress = "http://111.230.238.192/learn/lost";
    public static final String RequestAddress = "/user/login";
    public static final String UserInfo = "/user/info";
    public static final String LostThingsCategory = "/category";
    public static final String LostThingsCategoryPartition = "/category/detail?id=";
    public static final String GetStoreBucketKey = "/tencent/coskey";
    public static final String SchoolInfo = "/school/list/";
    public static final String SchoolBuildings = "/school/building?id=";
    public static final String LostThingsInfo = "/thing/publish";
    public static final String ThingList = "/thing/list?";
    public static final String MyPublishList = "/thing/mylist?type=1";
    public static final String MyFindList = "/thing/mylist?type=0";
    public static final String ThingsUpdate = "/thing/update";
    public static final String ThingsSearch = "/thing/search";
    public static final String CheckUpdate = "/common/update";

    public static final String FullAddress = DeploymentAddress + RequestAddress;
    public static final String FullUserInfo = DeploymentAddress + UserInfo;
    public static final String FullLostThingsCategory = DeploymentAddress + LostThingsCategory;
    public static final String FullLostThingsCategoryPartition = DeploymentAddress + LostThingsCategoryPartition;
    public static final String FullGetStoreBucketKey = DeploymentAddress + GetStoreBucketKey;
    public static final String FullSchoolInfo = DeploymentAddress + SchoolInfo;
    public static final String FullSchoolBuildings = DeploymentAddress + SchoolBuildings;
    public static final String FullLostThingsInfo = DeploymentAddress + LostThingsInfo;
    public static final String FullThingList = DeploymentAddress + ThingList;
    public static final String FullMyPublishList = DeploymentAddress + MyPublishList;
    public static final String FullMyFindList = DeploymentAddress + MyFindList;
    public static final String FullThingsUpdate = DeploymentAddress + ThingsUpdate;
    public static final String FullThingsSearch = DeploymentAddress + ThingsSearch;
    public static final String FullCheckUpdate = DeploymentAddress + CheckUpdate;

}
