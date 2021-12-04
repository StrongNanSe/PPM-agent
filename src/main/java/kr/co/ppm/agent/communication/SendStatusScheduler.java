package kr.co.ppm.agent.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class SendStatusScheduler {
    @Autowired
    CommunicationService communicationService;

    @Autowired
    CommunicationUtil communicationUtil;

    private final String filePath = "/home/pi/Desktop/watching/auto/send.txt";
    private Logger logger = LogManager.getLogger(SendStatusScheduler.class);

    @Scheduled(fixedRate = 1000 * 10)
    public void autoSendInfo() {
        if (!CommunicationServiceImpl.isParasolInfoSaved) {
            communicationService.sendParasol();

            logger.info("sendInfo");
        } else {
            try (FileWriter fileWriter =
                         new FileWriter(filePath)) {
                fileWriter.write("" + LocalDateTime.now());
            } catch (IOException e) {
                logger.error("IOException Occurred in method autoSendStatus");
            }

            communicationUtil.watchService();

            logger.info("sendStatusInfo");
        }
    }
}
