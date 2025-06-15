package backend.academy.bot.service.impl;

import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class LinkValidatorService {
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https://|http://)(www\\.)?(github\\.com/[^/]+/[^/]+|stackoverflow\\.com/questions/\\d+).*");

    public boolean isValid(String url) {
        return URL_PATTERN.matcher(url).matches();
    }
}
