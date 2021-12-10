package kr.co.ppm.agent.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/device")
public class CommunicationController {
    @Autowired
    private CommunicationService communicationService;

    @Autowired
    private CommunicationUtil communicationUtil;

    @GetMapping("/{action}")
    public String receiveParasolControl(@PathVariable String action) {
        System.out.println("Request is Arrived" + LocalDateTime.now());

        String code =  communicationService.receiveControl(action);

        communicationUtil.activeStatusWatch();

        return code;
    }
}
