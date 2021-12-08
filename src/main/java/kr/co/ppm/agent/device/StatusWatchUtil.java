package kr.co.ppm.agent.device;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

public class StatusWatchUtil implements Runnable {
    TemperatureUtil temperatureUtil;
    final String checkFilePath = "/home/pi/Desktop/watching/auto";
    final String saveFilePath = "/home/pi/Desktop/watching/autostatus/autoTemp.txt";

	public StatusWatchUtil(TemperatureUtil temperatureUtil) {
		this.temperatureUtil = temperatureUtil;
	}

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(checkFilePath);
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey watchKey = watchService.take();
            watchKey.pollEvents();
            watchKey.reset();

            while (true) {
                watchKey = watchService.take();

                Thread.sleep(50);

                watchKey.pollEvents();

                int temperature;
                while((temperature = temperatureUtil.measure()) == 0) {
                }

                try (FileWriter fileWriter =
                             new FileWriter(saveFilePath)) {
                    fileWriter.write("" + temperature);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println(LocalDateTime.now() + " : " + temperature);

                if (!watchKey.reset()) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}