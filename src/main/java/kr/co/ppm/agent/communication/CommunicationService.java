package kr.co.ppm.agent.communication;

public interface CommunicationService {
    public String receiveControl(String action);
    public void sendParasolStatus(String temperature);
    public void sendParasol();
}
