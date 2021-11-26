package kr.co.ppm.agent.device;

public interface DeviceUtil {
    public int angleMeasure();
    public int temperatureMeasure();
    public int windSpeedMeasure();
    public char rainfallDetect();
    public void warnNotice(String action);
    public void action(String Control);
    public void emergencyStop();
    public boolean detectObject();
}
