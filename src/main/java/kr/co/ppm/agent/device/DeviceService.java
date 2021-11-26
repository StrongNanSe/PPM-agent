package kr.co.ppm.agent.device;

public interface DeviceService {
    public void receiveControl(String control);
    public void sendParasolStatus();
    public void sendParasol();
}
