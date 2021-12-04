package kr.co.ppm.agent.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device")
public class CommunicationController {
    @Autowired
    private CommunicationService communicationService;

    @GetMapping("/{action}")
    public String receiveParasolControl(@PathVariable String action) {
        return communicationService.receiveControl(action);
    }
}
