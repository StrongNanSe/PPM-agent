package kr.co.ppm.agent.device;

import com.pi4j.io.gpio.*;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.ibatis.io.Resources;

import java.io.*;
import java.util.Properties;
import java.util.Set;

@Service
public class DeviceServiceImpl implements DeviceService{
    @Autowired
    private DeviceUtil deviceUtil;
    private Logger logger = LogManager.getLogger(DeviceServiceImpl.class);
    private static Properties properties;

    static {
        String path = "properties/parasol.properties";
         properties = new Properties();

        try {
            InputStream inputStream = Resources.getResourceAsStream(path);
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String receiveControl(String action) {
        logger.info("this actions is activatied : " + action);
//        final GpioController controller = GpioFactory.getInstance();
//
//        try {
//            final GpioPinDigitalOutput pin = controller.provisionDigitalOutputPin(RaspiPin.GPIO_01, "LED", PinState.LOW);
//
//            for (int i = 0; i < 10; i++) {
//                pin.high();
//                Thread.sleep(500);
//                pin.low();
//                Thread.sleep(500);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            controller.shutdown();
//        }

        String code = "{" +
                "    \"code\": \"200\"," +
                "    \"error\": {" +
                "        \"errorCode\": \"0\"," +
                "        \"message\": \"null\"" +
                "    }" +
                "}";

        return code;
    }

    @Override
    public void sendParasolStatus() {
        //TO DO 시스템 서버에게 상태 정보 전송
        //파라솔아이디
        String parasolId = properties.getProperty("parasolId");
        //각도
        String angle = "" + deviceUtil.angleMeasure();
        //온도
        String temperature = "" + deviceUtil.temperatureMeasure();
        //풍속
        String windSpeed = "" + deviceUtil.windSpeedMeasure();
        //강우
        String rainfall = "" + deviceUtil.rainfallDetect();

        StringBuffer parasolStatusInfo = new StringBuffer();

        parasolStatusInfo.append("{").append("\"").append("parasolId").append("\":\"").append(parasolId).append("\",")
                .append("\"").append("angle").append("\":\"").append(angle).append("\",")
                .append("\"").append("temperature").append("\":\"").append(temperature).append("\",")
                .append("\"").append("windSpeed").append("\":\"").append(windSpeed).append("\",")
                .append("\"").append("rainfall").append("\":\"").append(rainfall).append("\"").append("}");

       try{
            String response = sendPostType("http://localhost/status", parasolStatusInfo.toString());
            logger.info(response);
        } catch (Exception e) {
           //오류 처리 필요
            e.printStackTrace();
        }
    }

    //실험용
    public static void main(String[] args) {
        new DeviceUtilImpl();
        new DeviceServiceImpl().sendParasolStatus();
    }

    @Override
    public void sendParasol() {
        StringBuffer parasolInfo = new StringBuffer();

        Set<Object> list = properties.keySet();

        parasolInfo.append("{");

        for(Object obj : list) {
            parasolInfo.append("\"").append(obj.toString()).append("\"").append(":")
                    .append("\"").append(properties.getProperty(obj.toString())).append("\",");
        }

        parasolInfo.replace(parasolInfo.length() - 1, parasolInfo.length(), "");
        parasolInfo.append("}");

        try{
            String response = sendPostType("http://localhost/parasol", parasolInfo.toString().replace("parasolId", "id"));
            logger.info(response);
        } catch (Exception e) {
            //오류 처리 필요
            logger.info(e.toString());
        }
    }

    private String sendPostType(String url, String postBody) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postBody);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
