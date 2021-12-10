package kr.co.ppm.agent.device;

import java.io.*;
import java.nio.file.*;

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
			
			//WatchKey watchKey = watchService.take();
            //watchKey.pollEvents();
            //watchKey.reset();

            while (true) {		
                WatchKey watchKey = watchService.take();
				
				System.out.println("File Directory auto is Modifided");
				
				Thread.sleep(50);
				
                watchKey.pollEvents();
				int temperature;
                while((temperature = temperatureUtil.measure()) == 0) {
                }

                try (BufferedWriter bufferedWriter = 
                		new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFilePath)))) {
                	bufferedWriter.write("" + temperature);
                } catch (Exception e) {
                    e.printStackTrace();
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
