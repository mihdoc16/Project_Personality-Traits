/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dominik
 */
public class CreateTable {
    
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/Projekt", "postgres", "root");
            String sql = "DROP TABLE Benutzer;"
                    + "CREATE TABLE Benutzer"
                    + "("
                    + "     ID integer NOT NULL PRIMARY KEY,"
                    + "     Name character varying NOT NULL,"
                    + "     Openness character varying,"
                    + "     Conscientiousness character varying,"
                    + "     Emotional_range character varying,"
                    + "     Extraversion character varying,"
                    + "     Agreebleness character varying"
                    + ")";
            Statement stat = conn.createStatement();
            stat.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(CreateTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
