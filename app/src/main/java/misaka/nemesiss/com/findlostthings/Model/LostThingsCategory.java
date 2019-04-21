package misaka.nemesiss.com.findlostthings.Model;

public class LostThingsCategory {
    private int Id;
    private String Name;

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
