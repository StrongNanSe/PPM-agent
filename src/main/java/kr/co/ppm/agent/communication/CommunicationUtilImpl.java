package kr.co.ppm.agent.communication;

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
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(CommunicationServiceImpl.filpathInfo.getProperty("autoStatusPath"));
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey watchKey = watchService.take();
            watchKey.pollEvents();

            char[] buffer = new char[5];
            try (FileReader fileReader = new FileReader(
                    CommunicationServiceImpl.filpathInfo.getProperty("autoStatusPath.file"))) {
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

            Path path = Paths.get(CommunicationServiceImpl.filpathInfo.getProperty("activeStatusPath"));
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey watchKey = watchService.take();
            watchKey.pollEvents();

            char[] buffer = new char[5];
            try (FileReader fileReader = new FileReader(
                    CommunicationServiceImpl.filpathInfo.getProperty("activeStatusPath.file"))) {
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
