package kr.co.ppm.agent.device;

import com.pi4j.io.gpio.*;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class DeviceUtilImpl implements DeviceUtil {
    private static String parasolStatus = "F";
    private final GpioController gpio = GpioFactory.getInstance();
    private int echo, trig, actionTemperature, autoTemperature, warnNotice;
    private long rejectionStart;
    private long rejectionTime;
    private GpioPinDigitalOutput pinTrig;
    private GpioPinDigitalInput pinEcho;
	private GpioPinDigitalOutput pinWarnNotice;

    public DeviceUtilImpl(int echo, int trig, int actionTemperature, int autoTemperature, int warnNotice, long rejectionStart, long rejectionTime) {
        this.echo = echo;
        this.trig = trig;
        this.actionTemperature = actionTemperature;
        this.autoTemperature = autoTemperature;
		this.warnNotice = warnNotice;
        this.rejectionStart = rejectionStart;
        this.rejectionTime = rejectionTime;

        pinTrig = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(trig), "pinTrig", PinState.HIGH);
        pinTrig.setShutdownOptions(true, PinState.LOW);

        pinEcho = gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(echo), PinPullResistance.PULL_DOWN);
		
		pinWarnNotice = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(warnNotice), PinState.LOW);
    }

    private int[] dataSet = { 0, 0, 0, 0, 0 };

    @Override
    public void temperatureMeasure(int temperature) {
        final String filePath = "/home/pi/Desktop/watching/status/activeTemp.txt";

        try (FileWriter fileWriter =
                     new FileWriter(filePath)) {
            fileWriter.write("" + temperature);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void warnNotice() {
		pinWarnNotice.high();
    }

    @Override
    public void action(String control) {
		if ("U".equals(control)) {
            MotorUtil.angle = 270;
        } else if ("F".equals(control)) {
            MotorUtil.angle = -270;
        } else {
            return;
        }
		
        MotorUtil motorUtil = new MotorUtil(RaspiPin.GPIO_03, RaspiPin.GPIO_04, RaspiPin.GPIO_05, RaspiPin.GPIO_06, 3);
        Thread motorThread = new Thread(motorUtil, "motorThread");

        MotorUtil.isMove = true;

        motorThread.start();
    }

    @Override
    public void emergencyStop() {
		MotorUtil.isWarn = true;
    }

    @Override
    public boolean detectObject() {
        int distance;
        long startTime, endTime, start = 0, time = 0;

        pinTrig.low();
        busyWaitMicros(2);

        pinTrig.high();
        busyWaitMicros(10);

        pinTrig.low();

        while (pinEcho.isLow()) {
            busyWaitNanos(1);
            start++;

            if (start == rejectionStart) {
                return true;
            }
        }

        startTime = System.nanoTime();

        while (pinEcho.isHigh()) {
            busyWaitNanos(1);
            time++;

            if (time == rejectionTime) {
                return true;
            }
        }

        endTime = System.nanoTime();

        distance = (int) ((endTime - startTime) / 5882.35294118);

        return distance < 100;
    }

    private void busyWaitMicros(long micros) {
        long waitUntil = System.nanoTime() + (micros * 1_000);

        while (waitUntil > System.nanoTime()) {}
    }

    private void busyWaitNanos(long nanos) {
        long waitUntil = System.nanoTime() + nanos;
        while (waitUntil > System.nanoTime()) {}
    }

    private String watchService() {
        String commandPath = "/home/pi/Desktop/watching/command";

		try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(commandPath);
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            watchService.take();

            char[] buffer = new char[5];
            try (FileReader fileReader = new FileReader(commandPath + File.separator + "command.txt")) {
                fileReader.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if ("F".equals(new String(buffer).trim())) {
                return "F";
            } else {
                return "U";
            }
		} catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void main(String[] args) throws Exception{
		DeviceUtilImpl deviceUtilImpl = new DeviceUtilImpl(0, 1, 7, 8, 2, 1000, 235229411);
		
        TemperatureUtil actionTemperatureUtil = new TemperatureUtil(deviceUtilImpl.actionTemperature);
        TemperatureUtil autoTemperatureUtil = new TemperatureUtil(deviceUtilImpl.autoTemperature);
		
		Runnable autoWatch = new StatusWatchUtil(autoTemperatureUtil);
		Thread autoWatchThread = new Thread(autoWatch, "autoWatchThread");
		
		autoWatchThread.start();   

        while(true) {
            String action = deviceUtilImpl.watchService();

            if (!DeviceUtilImpl.parasolStatus.equals(action)) {
                DeviceUtilImpl.parasolStatus = action;

                deviceUtilImpl.action(action);
            }

            while (MotorUtil.isMove) {
                if (deviceUtilImpl.detectObject()) {
                    deviceUtilImpl.emergencyStop();
					deviceUtilImpl.warnNotice();
                }

                Thread.sleep(1000);
				
				deviceUtilImpl.pinWarnNotice.low();
            }

            deviceUtilImpl.temperatureMeasure(actionTemperatureUtil.measure());
        }
    }
}
