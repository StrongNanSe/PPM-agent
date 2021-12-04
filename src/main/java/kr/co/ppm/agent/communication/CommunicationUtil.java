package kr.co.ppm.agent.communication;

import java.io.IOException;
import java.util.Map;

public interface CommunicationUtil {
    public void watchService();
    public String sendPostType(String url, String postBody) throws IOException;
    public Map<String, String> parseResponseCode(String response);
}
