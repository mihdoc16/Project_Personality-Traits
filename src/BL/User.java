/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BL;

import com.ibm.watson.developer_cloud.personality_insights.v3.model.Profile;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Dominik
 */
public class User implements Serializable{
    private String name;
    private HashMap<String, Double> traits = new HashMap<>();

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Double> getTraits() {
        return traits;
    }

    public void setTraits(HashMap<String, Double> traits) {
        this.traits = traits;
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }
    
}
