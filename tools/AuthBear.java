package tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthBear {
    public static String getBearer(String html) {
        String regex = "<meta name=\"access-token\" content=\"(.*?)\">";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            String accessToken = matcher.group(1);
            return accessToken;
        } else {
            return null;
        }
    }
}







