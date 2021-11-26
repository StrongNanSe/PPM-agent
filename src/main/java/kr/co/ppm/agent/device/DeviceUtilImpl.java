package kr.co.ppm.agent.device;

import org.springframework.stereotype.Component;

@Component
public class DeviceUtilImpl implements DeviceUtil{
    @Override
    public int angleMeasure() {
        return 90;
    }

    @Override
    public int temperatureMeasure() {
        return 23;
    }

    @Override
    public int windSpeedMeasure() {
        return 3;
    }

    @Override
    public char rainfallDetect() {
        return 'Y';
    }

    @Override
    public void warnNotice(String action) {

    }

    @Override
    public void action(String Control) {

    }

    @Override
    public void emergencyStop() {

    }

    @Override
    public boolean detectObject() {
        return false;
    }
}
