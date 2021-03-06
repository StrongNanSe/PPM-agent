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

    private Logger logger = LogManager.getLogger(SendStatusScheduler.class);

    @Scheduled(initialDelay = 1000 * 15, fixedRate = 1000 * 7)
    public void autoSendInfoAndStatus() {
        if (!CommunicationServiceImpl.isParasolInfoSaved) {
            communicationService.sendParasol();
        } else {
            try (FileWriter fileWriter =
                         new FileWriter(CommunicationServiceImpl.filPathInfo.getProperty("sendStatusPath.file"))) {
                fileWriter.write("" + LocalDateTime.now());

                fileWriter.flush();
            } catch (IOException e) {
                logger.error("IOException Occurred in method autoSendStatus");
            }

            communicationUtil.autoStatusWatch();
        }
    }
}
