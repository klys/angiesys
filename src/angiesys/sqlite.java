/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package angiesys;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin2
 */
public class sqlite {
    private Connection con = null;
    sqlite() {
            
        try {
          Class.forName("org.sqlite.JDBC");
          con = DriverManager.getConnection("jdbc:sqlite:data");
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
        }
        //System.out.println("Opened database successfully");
    }
    
    public void cerrar() {
        try {
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(sqlite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection dame_conexion(){
        return this.con;
    }
    
    public int insert (String tabla, String valueNames, String valores) {
        try {
            Statement stmt = null;
            String sql = "INSERT INTO "+tabla+" ("+valueNames+") VALUES ("+valores+")";
            //System.out.println(sql);
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            
            return stmt.getGeneratedKeys().getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(sqlite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public ResultSet select (String tabla, String columnas, String adds) {
        try {
            Statement stmt = null;
            String sql = "SELECT "+columnas+" FROM "+tabla;
            if (adds != null) {
                sql += " "+adds;
            }
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(sqlite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public boolean update(String tabla, String set, String where) {
        try {
            Statement stmt = null;
            String sql = "UPDATE "+tabla+" SET "+set+" WHERE "+where;
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(sqlite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean delete (String tabla, String where) {
        try {
            Statement stmt = null;
            String sql = "DELETE FROM "+tabla+" WHERE "+where;
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(sqlite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
