package kr.co.ppm.agent.communication;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.apache.ibatis.io.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Service
public class CommunicationServiceImpl implements CommunicationService {
    @Autowired
    private CommunicationUtil communicationUtil;

    public static boolean isParasolInfoSaved = false;
    private static String parasolStatus = "F";
    private static String beforeTemperature = "";
    private static Properties parasolInfo;
    private static Properties systemInfo;
    public static Properties filpathInfo;

    private Logger logger = LogManager.getLogger(CommunicationServiceImpl.class);

    static {
        String parasolPath = "properties/parasol.properties";
        String systemPath = "properties/system.properties";
        String filepathPath = "properties/filepath.properties";

        parasolInfo = new Properties();
        systemInfo = new Properties();
        filpathInfo = new Properties();

        try {
            parasolInfo.load(Resources.getResourceAsStream(parasolPath));
            systemInfo.load(Resources.getResourceAsStream(systemPath));
            filpathInfo.load(Resources.getResourceAsStream(filepathPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String receiveControl(String action) {
        Gson code = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", "200");
        jsonObject.addProperty("message", "null");

        try (FileWriter fileWriter =
                      new FileWriter(filpathInfo.getProperty("commandPath.file"))) {
            fileWriter.write(action);
        } catch (IOException e) {
            logger.error("IOException Occurred in method receiveControl");

            jsonObject.addProperty("code", "500");
            jsonObject.addProperty("message", "IOException Occurred");

            return code.toJson(jsonObject);
        }

        if (parasolStatus.equals(action)) {
            jsonObject.addProperty("move", CommunicationServiceImpl.parasolStatus);

            System.out.println(" sameStatus -> action : " + action + ", generalStatus : " + CommunicationServiceImpl.parasolStatus);

            return code.toJson(jsonObject);
        } else {
            System.out.println(" diferentStatus -> action : " + action + ", generalStatus : " + CommunicationServiceImpl.parasolStatus);

            CommunicationServiceImpl.parasolStatus = action;

            jsonObject.addProperty("move", CommunicationServiceImpl.parasolStatus);

            return code.toJson(jsonObject);
        }
    }

    @Override
    public void sendParasolStatus(String temperature, String move) {
        String parasolId = CommunicationServiceImpl.parasolInfo.getProperty("id");
        String url = "http://" +systemInfo.getProperty("system.ipaddress") + "/status";
        String code = "";

        if (!"".equals(temperature)) {
            beforeTemperature = temperature;

        } else {
            logger.error("this Temp is Error!! is Blink!");
            System.out.println("\n" + temperature + "\n");
            temperature = beforeTemperature;
        }

        Gson statusInfo = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("parasolId", parasolId);
        jsonObject.addProperty("status", parasolStatus);
        jsonObject.addProperty("temperature", temperature);
        jsonObject.addProperty("move", move);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), statusInfo.toJson(jsonObject));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Map<String, String> responseParse = communicationUtil.parseResponseCode(response.body().string());

            if ("200".equals(responseParse.get("code"))) {
                logger.info("");
                logger.info("====================================================");
                logger.info("");
                logger.info("                Send Parasol Status");
                logger.info("                parasolId : " + parasolId);
                logger.info("                status : " + parasolStatus);
                logger.info("                temperature : " + temperature);
                logger.info("");
                logger.info("====================================================");
            } else {
                logger.error(responseParse.get("message"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException Occurred in method sendParasolStatus");
        }
    }

    @Override
    public void sendParasol() {
        String url = "http://" + systemInfo.getProperty("system.ipaddress") + "/parasol/info";
        String code = "";

        Gson Info = new Gson();
        JsonObject jsonObject = new JsonObject();

        Set<Object> list = parasolInfo.keySet();
        for (Object obj : list) {
            jsonObject.addProperty(obj.toString(), parasolInfo.getProperty(obj.toString()));
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Info.toJson(jsonObject));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Map<String, String> responseParse = communicationUtil.parseResponseCode(response.body().string());

            if ("200".equals(responseParse.get("code"))) {
                logger.info("");
                logger.info("====================================================");
                logger.info("");
                logger.info("              Send Parasol Information");
                logger.info("");
                logger.info("====================================================");

                isParasolInfoSaved = true;
            } else {
                logger.error(responseParse.get("message"));
            }
        } catch (IOException e) {
            logger.error("IOException Occurred in method sendParasol");
        }
    }
}
