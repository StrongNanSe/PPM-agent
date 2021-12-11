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
import java.util.Map;

@Service
public class CommunicationUtilImpl implements CommunicationUtil {
    @Autowired
    CommunicationService communicationService;

    private Logger logger = LogManager.getLogger(CommunicationUtilImpl.class);

    @Override
    public void autoStatusWatch() {
        String autoStatusPath = "/home/pi/Desktop/watching/autostatus";

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(autoStatusPath);
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey watchKey = watchService.take();
            watchKey.pollEvents();

            char[] buffer = new char[5];
            try (FileReader fileReader = new FileReader(autoStatusPath + File.separator + "autoTemp.txt")) {
                fileReader.read(buffer);
            } catch (IOException e) {
                logger.error("IOException Occurred in method autoStatusWatch");
            }

            communicationService.sendParasolStatus(new String(buffer).trim(), "N");
        } catch (Exception e) {
            logger.error("Exception Occurred in method autoStatusWatch");
        }
    }

    @Override
    public void activeStatusWatch() {
        String activeStatusPath = "/home/pi/Desktop/watching/activestatus";

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(activeStatusPath);
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey watchKey = watchService.take();
            watchKey.pollEvents();

            char[] buffer = new char[5];
            try (FileReader fileReader = new FileReader(activeStatusPath + File.separator + "activeTemp.txt")) {
                fileReader.read(buffer);
            } catch (IOException e) {
                logger.error("IOException Occurred in method activeStatusWatch");
            }

            communicationService.sendParasolStatus(new String(buffer).trim(), "Y");
        } catch (Exception e) {
            logger.error("Exception Occurred in method activeStatusWatch");
        }
    }

    @Override
    public Map<String, String> parseResponseCode(String response) {
        Map<String, String> responseParse = new Hashtable<String, String>();

        responseParse.put("code", response.split(":")[1].split("\"")[1]);
        responseParse.put("message", response.split(":")[2].split("\"")[1]);

        return responseParse;
    }
}
