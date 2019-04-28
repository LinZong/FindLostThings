package misaka.nemesiss.com.findlostthings.Model;

public class UserAccount
{
    public UserAccount() {

    }

    public UserAccount(String nickName,String imageUrl) {
        Nickname = nickName;
        ImageUrl = imageUrl;
    }

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
