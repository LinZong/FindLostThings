package misaka.nemesiss.com.findlostthings.Model.Response;

import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;

import java.util.List;

public class MyPublishListResponse
{
    private List<LostThingsInfo> lostThingsInfos;

    public List<LostThingsInfo> getLostThingsInfos()
    {
        return lostThingsInfos;
    }

    public void setLostThingsInfos(List<LostThingsInfo> lostThingsInfos)
    {
        this.lostThingsInfos = lostThingsInfos;
    }
}
