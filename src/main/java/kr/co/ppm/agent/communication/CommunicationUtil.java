package kr.co.ppm.agent.communication;

import java.io.IOException;
import java.util.Map;

public interface CommunicationUtil {
    void autoStatusWatch();
    void activeStatusWatch();
    Map<String, String> parseResponseCode(String response);
}
