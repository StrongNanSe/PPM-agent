package kr.co.ppm.agent.device;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ParasolStatus implements Serializable{
    private String serialNo;
    private String uniqueNo;
    private String status;
    private int temperature;
    private int windSpeed;
    private char rainFall;
    private LocalDateTime dateTime;

    public ParasolStatus() {

    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getUniqueNo() {
        return uniqueNo;
    }

    public void setUniqueNo(String uniqueNo) {
        this.uniqueNo = uniqueNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public char getRainFall() {
        return rainFall;
    }

    public void setRainFall(char rainFall) {
        this.rainFall = rainFall;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
