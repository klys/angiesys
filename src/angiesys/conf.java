/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package angiesys;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bersnard
 */
/**
 * USAGE: conf.cargar("facturaCliente")
 * @author Bersnard
 */
public class conf {
    public static String cargar(String conf) {
        sqlite db = new sqlite();
        ResultSet rs = db.select("configuracion", "valor", "WHERE nombre = '"+conf+"'");
        try {
            if (rs.next()) {
                conf = rs.getString("valor");
            }
        } catch (SQLException ex) {
            Logger.getLogger(conf.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.cerrar();
        return conf;
    }
    
    
    public static BigDecimal truncateDecimal(double x,int numberofDecimals)
    {
        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
    }
}
