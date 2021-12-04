package kr.co.ppm.agent.communication;

import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Service
public class CommunicationUtilImpl implements CommunicationUtil {
    @Autowired
    CommunicationService communicationService;
    private String statusPath = "/home/pi/Desktop/watching/status";
    private Logger logger = LogManager.getLogger(CommunicationUtilImpl.class);

    @Override
    public void watchService() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(statusPath);
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey watchKey = watchService.take();

            Path context = (Path) watchKey.pollEvents().get(0).context();

            char[] buffer = new char[5];
            try (FileReader fileReader = new FileReader(statusPath + File.separator + context.getFileName())) {
                fileReader.read(buffer);
            } catch (IOException e) {
                logger.error("IOException Occurred in method watchService");
            }

            communicationService.sendParasolStatus(new String(buffer).trim());
        } catch (Exception e) {
            logger.error("Exception Occurred in method watchService");
        }
    }

    @Override
    public String sendPostType(String url, String postBody)  {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postBody);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            logger.error("IOException Occurred in method sendPostType");
        }

        return "error";
    }

    @Override
    public Map<String, String> parseResponseCode(String response) {
        Map<String, String> responseParse = new Hashtable<String, String>();

        responseParse.put("code", response.split(":")[1].split("\"")[1]);
        responseParse.put("errorCode", response.split("[{]")[2].split(":")[1].split("\"")[1]);
        responseParse.put("message", response.split("[{]")[2].split(":")[2].split("\"")[1]);

        return responseParse;
    }
}
