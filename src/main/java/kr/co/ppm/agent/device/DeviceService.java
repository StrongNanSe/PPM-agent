package kr.co.ppm.agent.device;

import kr.co.ppm.agent.model.ParasolStatus;

public interface DeviceService {
    public int angleMeasure();
    public int temperatureMeasure();
    public int windSpeedMeasure();
    public char rainfallDetect();
    public void warnNotice(String action);
    public void action(String Control);
    public void emergencyStop();
    public boolean detectObject();
    public void receiveControl();
    public void sendParasolStatus(ParasolStatus parasolStatus);
    public void sendParasol();
}
