package misaka.nemesiss.com.findlostthings.Model;

import java.io.Serializable;

public class LostThingsInfo implements Serializable {
    private String Id;
    private String Title;
    private int ThingCatId;
    private int ThingDetailId;
    private long FoundTime;
    private long PublishTime;
    private long GivenTime;
    private int Isgiven;
    private String FoundAddress;
    private long Publisher;
    private String PublisherContacts;
    private String GivenContacts;
    private long Given;
    private String ThingPhotoUrls;
    private String FoundAddrDescription;
    private String ThingAddiDescription;

    private int photoUrl;

    public void setPhotoUrl(int photoUrl)
    {
        this.photoUrl = photoUrl;
    }

    public int getPhotoUrl()
    {
        return photoUrl;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setFoundAddrDescription(String foundAddrDescription) {
        FoundAddrDescription = foundAddrDescription;
    }

    public void setFoundAddress(String foundAddress) {
        FoundAddress = foundAddress;
    }

    public void setFoundTime(long foundTime) {
        FoundTime = foundTime;
    }

    public void setGiven(long given) {
        Given = given;
    }

    public void setGivenContacts(String givenContacts) {
        GivenContacts = givenContacts;
    }

    public void setGivenTime(long givenTime) {
        GivenTime = givenTime;
    }

    public void setIsgiven(int isgiven) {
        Isgiven = isgiven;
    }

    public void setPublisher(long publisher) {
        Publisher = publisher;
    }

    public void setPublisherContacts(String publisherContacts) {
        PublisherContacts = publisherContacts;
    }

    public void setPublishTime(long publishTime) {
        PublishTime = publishTime;
    }

    public void setThingAddiDescription(String thingAddiDescription) {
        ThingAddiDescription = thingAddiDescription;
    }

    public void setThingCatId(int thingCatId) {
        ThingCatId = thingCatId;
    }

    public void setThingDetailId(int thingDetailId) {
        ThingDetailId = thingDetailId;
    }

    public void setThingPhotoUrls(String thingPhotoUrls) {
        ThingPhotoUrls = thingPhotoUrls;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getIsgiven() {
        return Isgiven;
    }

    public int getThingCatId() {
        return ThingCatId;
    }

    public int getThingDetailId() {
        return ThingDetailId;
    }

    public long getFoundTime() {
        return FoundTime;
    }

    public long getGiven() {
        return Given;
    }

    public long getGivenTime() {
        return GivenTime;
    }

    public long getPublisher() {
        return Publisher;
    }

    public long getPublishTime() {
        return PublishTime;
    }

    public String getFoundAddress() {
        return FoundAddress;
    }

    public String getGivenContacts() {
        return GivenContacts;
    }

    public String getFoundAddrDescription() {
        return FoundAddrDescription;
    }

    public String getId() {
        return Id;
    }

    public String getPublisherContacts() {
        return PublisherContacts;
    }

    public String getThingAddiDescription() {
        return ThingAddiDescription;
    }

    public String getTitle() {
        return Title;
    }

    public String getThingPhotoUrls() {
        return ThingPhotoUrls;
    }

}
