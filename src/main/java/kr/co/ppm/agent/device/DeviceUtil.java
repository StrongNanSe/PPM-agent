package kr.co.ppm.agent.device;

public interface DeviceUtil {
    void temperatureMeasure(int temperature);
    void warnNotice() throws Exception;
    void action(String control);
    void emergencyStop();
    boolean detectObject();
}
