package com.tftad.utility;

import com.tftad.config.property.JwtProperty;
import com.tftad.exception.InvalidRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UtilityTest {
    @Mock
    private JwtProperty jwtProperty;

    @InjectMocks
    private Utility utility;

    @Test
    @DisplayName("유튜브 주소에서 비디오 아이디를 가져온다")
    void test1() {
        String[] validUrls = {
                "http://www.youtube.com/watch?v=video-id",
                "http://youtube.com/watch?v=video-id&feature=em-uploademail",
                "http://m.youtube.com/watch?v=video-id&feature=feedrec_grec_index",
                "https://www.youtube.com/watch?v=video-id#t=0m10s",
                "https://youtube.com/watch?v=video-id&feature=channel",
                "https://www.youtube.com/v/video-id",
                "https://youtube.com/v/video-id?version=3&autohide=1",
                "https://youtu.be/video-id?feature=youtube_gdata_player",
                "http://www.youtube.com/oembed?url=http%3A//www.youtube.com/watch?v%3D-video-id&format=json"
        };

        for (String url : validUrls) {
            assertEquals(utility.extractVideoId(url), "video-id");
        }
    }

    @Test
    @DisplayName("youtube video url input을 검증한다")
    void test2() {
        String[] noIdUrls = {
                "https://www.youtube.com/watch", // no id
                "https://www.twitch.com/watch?v=video-id", // non-youtube domain
                "https://www.youtube.com/v/<script>alert('XSS');</script>",
                "https://www.youtube.com/v=../../etc/passwd",
        };

        String[] willBeFilteredUrls = {
                "https://www.youtube.com/v/DROP TABLE users;",
                "https://www.youtube.com/watch?&v=query=SELECT * FROM users",
        };

        for (String url : noIdUrls) {
            assertThrows(InvalidRequest.class, () -> utility.extractVideoId(url));
        }

        for (String url : willBeFilteredUrls) {
            String filtered = utility.extractVideoId(url);

            String regex = "^[\\w-]+$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(filtered);

            assertTrue(matcher.find());
        }
    }
}