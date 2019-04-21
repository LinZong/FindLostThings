package misaka.nemesiss.com.findlostthings.Utils;

import java.util.UUID;

public class UUIDGenerator
{
    public static String Generate() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}