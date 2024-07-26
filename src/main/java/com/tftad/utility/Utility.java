package com.tftad.utility;

import com.tftad.exception.InvalidRequest;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Utility {

    public String extractVideoId(String url) {
        validateVideoUrl(url);
        return applyRegexToExtractVideoId(url);
    }

    private void validateVideoUrl(String url) {
        if (url == null || !url.contains("youtu")) {
            throw new InvalidRequest("url", "올바른 유튜브 영상 주소를 입력해주세요");
        }
    }

    private String applyRegexToExtractVideoId(String url) {
        String regex = "(?<=(v%3D-|v=|v/|youtu.be/))[\\w-]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group();
        }
        throw new InvalidRequest("url", "올바른 유튜브 영상 주소를 입력해주세요");
    }
}
