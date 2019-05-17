/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BL;

import BL.User;
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
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.AbstractListModel;

/**
 *
 * @author Dominik
 */
public class BL extends AbstractListModel{

    private ArrayList<User> users = new ArrayList<>();
    private Connection conn;
    private int id;

    public BL(){
    }
    
    /**
     * Adds users
     * 
     * @param u User that should be added
     */
    public void addUsers(User u){
        users.add(u);
        fireContentsChanged(this, 0, users.size()-1);
    }
    
    /**
     * Deletes users
     * 
     * @param i Position of the User that should be deleted
     */
    public void delete(int i){
        users.remove(i);
        fireContentsChanged(this, 0, users.size()-1);
    }
    
    /**
     * Saves users into a file
     * 
     * @param f File which the data is being saved into
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void save(File f) throws FileNotFoundException, IOException{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
       
        for (User user : users) {
            oos.writeObject(user);
        }
          
        oos.flush();
        oos.close();
    }
    
    /**
     * Loads users from a file
     * 
     * @param f File from which the users should be loaded
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void loadFromFile(File f) throws FileNotFoundException, IOException, ClassNotFoundException{
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));

        try {
            Object o = ois.readObject();
            while (o != null) {
                users.add((User) o);
                o = ois.readObject();
            }
        } catch (EOFException eofExc) {
        }
        
        fireContentsChanged(this, 0, users.size()-1);
        ois.close();      
    }
    
    /**
     * gets the personality traits from the cloud and sets them for the user
     * 
     * @param u User which gets the personality traits
     * @param text The text from the user
     * @throws SQLException 
     */
    public void addResults(User u, String text) throws SQLException{
        HashMap<String, Double> results = new HashMap<>();
        conn = DriverManager.getConnection("jdbc:postgresql://localhost/Projekt", "postgres", "postgres");
        
        IamOptions options = new IamOptions.Builder()
                .apiKey("4GEbcvdahqkbkaAWPHdkzrlwbJ2AA-dWQTOgjYnIqt9O")
                .build();

        PersonalityInsights personalityInsights = new PersonalityInsights("2017-10-13", options);
        personalityInsights.setEndPoint("https://gateway-fra.watsonplatform.net/personality-insights/api");
        
        String toAnalyze = "{ \"contentItems\": [{\"content\":\""+text+"\", \"contenttype\":\"text/plain\", \"created\":1447639154000, \"id\":\"666073008692314113\", \"language\":\"en\"}]}";

        JsonParser parser = new JsonParser();
        JsonObject test = parser.parse(toAnalyze).getAsJsonObject();
        System.out.println(test);   

        Content content = GsonSingleton.getGson().fromJson(toAnalyze, Content.class);

        ProfileOptions profileOptions = new ProfileOptions.Builder()
            .content((com.ibm.watson.developer_cloud.personality_insights.v3.model.Content) content)
            .consumptionPreferences(true)
            .rawScores(true)
            .build();

        Profile profile = personalityInsights.profile(profileOptions).execute();      
            
        for (int i = 0; i < 5; i++) {
            results.put(profile.getPersonality().get(i).getName(), truncateDecimal(profile.getPersonality().get(i).getPercentile(),2).doubleValue());
        }
            
        u.setTraits(results);
            
        Statement stat = conn.createStatement();
            
        String requestId = "SELECT MAX(ID) FROM Benutzer";
        ResultSet rs = stat.executeQuery(requestId);
        if(rs.next()){
            id = rs.getInt(1) + 1;
        }
            
        String sql = "INSERT INTO Benutzer(ID, Name, Openness, Conscientiousness, Emotional_range, Extraversion, Agreebleness) VALUES("+id+","
            + "'" + u.getName()+"'"+","
            + "'Openness = "+u.getTraits().get("Openness")+"'"+","
            + "'Conscientiousness = "+u.getTraits().get("Conscientiousness")+"'"+","
            + "'Emotional range = "+u.getTraits().get("Emotional range")+"'"+","
            + "'Extraversion = "+u.getTraits().get("Extraversion")+"'"+","
            + "'Agreeableness = "+u.getTraits().get("Agreeableness")+"'"+")";
            
        stat.executeUpdate(sql);
    }
    
    @Override
    public int getSize() {
        return users.size();
    }

    @Override
    public User getElementAt(int i) {
        return users.get(i);
    }
    
    private static BigDecimal truncateDecimal(double x,int numberofDecimals)
    {
        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
    }
    
}
