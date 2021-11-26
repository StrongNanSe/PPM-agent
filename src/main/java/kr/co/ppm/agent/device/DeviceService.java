package kr.co.ppm.agent.device;

public interface DeviceService {
    public String receiveControl(String action);
    public void sendParasolStatus();
    public void sendParasol();
}
