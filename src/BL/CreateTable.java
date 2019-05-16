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
                    + "     Trait1 character varying,"
                    + "     Trait2 character varying,"
                    + "     Trait3 character varying,"
                    + "     Trait4 character varying,"
                    + "     Trait5 character varying"
                    + ")";
            Statement stat = conn.createStatement();
            stat.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(CreateTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
