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
    private ArrayList<Profile> profiles = new ArrayList<>();
    private Connection conn;
    private int id;
    //private HashMap<String, Double> results = new HashMap<String, Double>();

    public BL(){
          
    }
    
    /**
     * Add User
     * 
     * Adds users
     * 
     * @param u User that should be added
     */
    public void addUsers(User u){
        users.add(u);
        fireContentsChanged(this, 0, users.size()-1);
    }
    
    /**
     * Delete User
     * 
     * Deletes users
     * 
     * @param i Position of the User that should be deleted
     */
    public void delete(int i){
        users.remove(i);
        fireContentsChanged(this, 0, users.size()-1);
    }
    
    
    public void save(File f, String text) throws FileNotFoundException, IOException{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
       
        for (User user : users) {
            oos.writeObject(user);
        }
          
        oos.flush();
        oos.close();
    }
    
    public void loadFromFile(File f) throws FileNotFoundException, IOException, ClassNotFoundException{
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));

        try {
            Object o = ois.readObject();
            while (o != null) {
                users.add((User) o);
                o = ois.readObject();
            }
        } catch (EOFException eofExc) {
            //this catch is only to determine end of file
        }
        
        fireContentsChanged(this, 0, users.size()-1);
        ois.close();      
    }
    
    public void addResults(File f, User u) throws SQLException{
        HashMap<String, Double> results = new HashMap<>();
        conn = DriverManager.getConnection("jdbc:postgresql://localhost/Projekt", "postgres", "root");
        
        IamOptions options = new IamOptions.Builder()
                .apiKey("4GEbcvdahqkbkaAWPHdkzrlwbJ2AA-dWQTOgjYnIqt9O")
                .build();

        PersonalityInsights personalityInsights = new PersonalityInsights("2017-10-13", options);
        personalityInsights.setEndPoint("https://gateway-fra.watsonplatform.net/personality-insights/api");
        
        try {
            JsonReader jsonReader = new JsonReader(new FileReader("D:\\Schulordner\\POS Stuff\\Project_Personality-Traits\\profile.json"));
            FileReader reader = new FileReader("D:\\Schulordner\\POS Stuff\\Project_Personality-Traits\\profile.json");
            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(new BufferedReader(new FileReader("D:\\Schulordner\\POS Stuff\\Project_Personality-Traits\\profile.json")));

            Content content = GsonSingleton.getGson().fromJson(object, Content.class);

            ProfileOptions profileOptions = new ProfileOptions.Builder()
                    .content((com.ibm.watson.developer_cloud.personality_insights.v3.model.Content) content)
                    .consumptionPreferences(true)
                    .rawScores(true)
                    .build();

            Profile profile = personalityInsights.profile(profileOptions).execute();
            //u.setProfile(profile);
            
            
            for (int i = 0; i < 5; i++) {
                results.put(profile.getPersonality().get(i).getName(), truncateDecimal(profile.getPersonality().get(i).getPercentile(),2).doubleValue());
            }
            u.setTraits(results);
            System.out.println(u.getTraits());
            fireContentsChanged(this, 0, users.size()-1);
            
            Statement stat = conn.createStatement();
            
            String requestId = "SELECT MAX(ID) FROM Benutzer";
            ResultSet rs = stat.executeQuery(requestId);
            if(rs.next()){
                id = rs.getInt(1) + 1;
            }
            
            String sql = "INSERT INTO Benutzer(ID, Name, Trait1, Trait2, Trait3, Trait4, Trait5) VALUES("+id+","
                    + "'" + u.getName()+"'"+","
                    + "'Openness = "+u.getTraits().get("Openness")+"'"+","
                    + "'Conscientiousness = "+u.getTraits().get("Conscientiousness")+"'"+","
                    + "'Emotional range = "+u.getTraits().get("Emotional range")+"'"+","
                    + "'Extraversion = "+u.getTraits().get("Extraversion")+"'"+","
                    + "'Agreeableness = "+u.getTraits().get("Agreeableness")+"'"+")";
            
            stat.executeUpdate(sql);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
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
