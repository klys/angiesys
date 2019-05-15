/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package angiesys;

/**
 *
 * @author Admin
 */
public class AngieSys {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        sesionario sesion = new sesionario();
        sesion.setVisible(true);
        sesion.setLocationRelativeTo(null);
    }
    private static int usuario_id = -1;
    private static int ivapor = 10;
    public static void setUsuarioId(int id) {
        usuario_id = id;
    }
    
    public static int getUsuarioId() {
        return usuario_id;
    }
    
    public static int getIva() {
        return ivapor;
    }
    
    public static void setIva(int ivapor_new) {
        ivapor = ivapor_new;
    }
}
