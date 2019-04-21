package misaka.nemesiss.com.findlostthings.Services.User;

public class MySchoolBuildings {
    private int Id;
    private String BuildingName;
    private double Latitude;
    private double Longitude;
    private String BuilddingAddress;

    public void setId(int id) {
        Id = id;
    }

    public void setBuilddingAddress(String builddingAddress) {
        BuilddingAddress = builddingAddress;
    }

    public void setBuildingName(String buildingName) {
        BuildingName = buildingName;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public int getId() {
        return Id;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public String getBuilddingAddress() {
        return BuilddingAddress;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    @Override
    public String toString()
    {
        return getBuildingName();
    }
}
