/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.ibm.watson.developer_cloud.personality_insights.v3.PersonalityInsights;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Content;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Profile;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.ProfileOptions;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.util.GsonSingleton;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
//import javax.swing.text.AbstractDocument.Content;

/**
 *
 * @author Dominik
 */
public class TestAPI {

    public static void main(String[] args) throws IOException {
        IamOptions options = new IamOptions.Builder()
                .apiKey("4GEbcvdahqkbkaAWPHdkzrlwbJ2AA-dWQTOgjYnIqt9O")
                .build();

        PersonalityInsights personalityInsights = new PersonalityInsights("2017-10-13", options);
        personalityInsights.setEndPoint("https://gateway-fra.watsonplatform.net/personality-insights/api");

        String test = "People go on about places like Starbucks being unpersonal and all that, but what if that's what you want? I'd be lost if people like that got their way and there was nothing unpersonal in the world. I like to know that there are big places without windows where no one gives a shit. You need confidence to go into small places with regular customers... I'm happiest in the Virgin Megastore and Borders and Starbucks and Pizza Express, where no one gives a shit and no one knows who you are. My mum & dad are always going on about how soulless those places are, and I'm like Der. That's the point.";

        JSONObject obj = new JSONObject();


        JSONArray jsonTest = new JSONArray();
        
        ArrayList<String> tester = new ArrayList<>();
        String bruh = String.format("\"content\": \"%s\",\n\"contenttype\": \"text/plain\",\n\"created\": 13455464,\n\"id\": \"534785734895\",\n\"language\": \"en\"", test);
        tester.add(bruh);
        jsonTest.add(tester);
        
//        obj.put("content", test);
//        obj.put("contenttype", "text/plain");
//        obj.put("created", "12345678");
//        obj.put("id", "039040932409");
//        obj.put("language", "en");
        obj.put("contentItems", jsonTest);
        //System.out.println(bruh);

        Files.write(Paths.get("D:\\Schulordner\\POS Stuff\\Project_Personality-Traits\\test.json"), obj.toJSONString().getBytes());
//        System.out.println("Successfully Copied JSON Object to File...");
//	System.out.println("\nJSON Object: " + obj);

        try {
            JsonReader jsonReader = new JsonReader(new FileReader("D:\\Schulordner\\POS Stuff\\Project_Personality-Traits\\profile.json"));
            FileReader reader = new FileReader("D:\\Schulordner\\POS Stuff\\Project_Personality-Traits\\profile.json");
            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(new BufferedReader(new FileReader("D:\\Schulordner\\POS Stuff\\Project_Personality-Traits\\profile.json")));
            //System.out.println(object.getAsJsonArray("contentItems").get(0).getAsJsonObject().get("content"));
            //object.getAsJsonArray("contentItems").get(0).getAsJsonObject().remove("content");
            
            //System.out.println(object.getAsJsonArray("contentItems"));

            Content content = GsonSingleton.getGson().fromJson(object, Content.class);

            ProfileOptions profileOptions = new ProfileOptions.Builder()
                    .content((com.ibm.watson.developer_cloud.personality_insights.v3.model.Content) content)
                    .consumptionPreferences(true)
                    .rawScores(true)
                    .build();

            Profile profile = personalityInsights.profile(profileOptions).execute();
            System.out.println(profile.getPersonality().get(4).getPercentile());
            System.out.println(profile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
