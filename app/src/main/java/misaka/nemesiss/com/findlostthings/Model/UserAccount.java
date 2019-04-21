package misaka.nemesiss.com.findlostthings.Model;

public class UserAccount
{
    private String Nickname;
    private String ImageUrl;

    public String getNickname()
    {
        return Nickname;
    }

    public String getImageUrl()
    {
        return ImageUrl;
    }

    public void setNickname(String nickname)
    {
        Nickname = nickname;
    }

    public void setImageUrl(String imageUrl)
    {
        ImageUrl = imageUrl;
    }
}
