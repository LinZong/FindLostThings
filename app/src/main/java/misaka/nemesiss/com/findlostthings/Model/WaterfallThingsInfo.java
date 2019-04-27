package misaka.nemesiss.com.findlostthings.Model;

public class WaterfallThingsInfo
{
    private int status;
    private String EndItemId;
    private int  HaveFetchedItemCount;
    private int Count;

    public void setCount(int count)
    {
        Count = count;
    }

    public void setEndItemId(String endItemId)
    {
        EndItemId = endItemId;
    }

    public void setHaveFetchedItemCount(int haveFetchedItemCount)
    {
        HaveFetchedItemCount = haveFetchedItemCount;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getCount()
    {
        return Count;
    }

    public int getHaveFetchedItemCount()
    {
        return HaveFetchedItemCount;
    }

    public String getEndItemId()
    {
        return EndItemId;
    }

    public int getStatus()
    {
        return status;
    }
}
