package kr.co.ppm.agent.device;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class StatusWatchUtil implements Runnable {
    TemperatureUtil temperatureUtil;
    final String filePath = "/home/pi/Desktop/watching/auto";
	
	public StatusWatchUtil(TemperatureUtil temperatureUtil) {
		this.temperatureUtil = temperatureUtil;
	}

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(filePath);
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            boolean isFirst = true;

            while (true) {
                WatchKey watchKey = watchService.take();
                watchKey.pollEvents();

                if (isFirst) {
                    try (FileWriter fileWriter =
                                 new FileWriter(filePath + File.separator + "autoTemp.txt")) {
                        fileWriter.write(temperatureUtil.measure());

                        isFirst = false;

                        continue;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    isFirst = true;
                }

                if (!watchKey.reset()) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}