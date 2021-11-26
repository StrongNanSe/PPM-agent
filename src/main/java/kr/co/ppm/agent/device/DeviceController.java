package kr.co.ppm.agent.device;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;
    private Logger logger = LogManager.getLogger(DeviceController.class);

    //실험용 코드
    @GetMapping("/test")
    public ModelAndView test() {
        deviceService.sendParasol();
        return new ModelAndView("test");
    }

    //실험용 코드
    @GetMapping("/status")
    public ModelAndView status() {
        deviceService.sendParasolStatus();
        return new ModelAndView("status");
    }

    @GetMapping("/{action}")
    public String receiveParasolControl(@PathVariable String action) {
        String code = deviceService.receiveControl(action);
        return code;
    }
}
