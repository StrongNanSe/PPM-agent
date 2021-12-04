package kr.co.ppm.agent.device;

public interface DeviceUtil {
    public void temperatureMeasure(int temperature);
    public void warnNotice();
    public void action(String control);
    public void emergencyStop();
    public boolean detectObject();
}