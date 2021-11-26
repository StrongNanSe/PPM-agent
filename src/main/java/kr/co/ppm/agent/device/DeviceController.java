package kr.co.ppm.agent.device;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
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

    @GetMapping("/{contorl}")
    public String receiveParasolControl(String control) {
        deviceService.receiveControl(control);
        return "200";
    }
}
