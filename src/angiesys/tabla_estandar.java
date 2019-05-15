/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package angiesys;

import java.awt.Image;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;



/**
 *
 * @author Admin
 */
public class tabla_estandar extends javax.swing.JFrame {

    /**
     * Creates new form facturacion
     */
    private String titulo = "Tabla";
    private int seleccionado = -1;
    private double max = 22;
    public tabla_estandar(String nombre) {
        initComponents();
        this.setTitle(nombre+" - Angie");
        jLabel1.setText(nombre);
        this.titulo = nombre;
        switch(nombre) {
            case "Facturación":
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/facturas.png")));
                break;
            case "Productos":
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/productos.png")));
                break;
            case "Proveedores":
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/proveedores.png")));
                break;
            case "Clientes":
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clientes.png")));
                break;
            case "Usuarios":
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clientes.png")));
                break;
            case "Cotización":
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cotizaciones.png")));
                break;
        }
        cargarTabla(1);
        
        URL iconURL = getClass().getResource("/images/facturas.png");
        // iconURL is null when not found
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());
    }
    
    public void cargarTabla(int pagina) {
        DefaultTableModel modelo = new DefaultTableModel();
        this.jTable1.setModel(modelo);
        sqlite db = new sqlite();
        ResultSet rs = null;
        switch(this.titulo) {
            case "Facturación":
                rs = db.select("facturas", "*", "LIMIT "+max*pagina+" OFFSET "+(pagina-1)*max);
                jButton1.setText("Visualizar");
                break;
            case "Productos":
                rs = db.select("productos", "id, codigo, nombre, cantidad, exento, precio", "LIMIT "+max*pagina+" OFFSET "+(pagina-1)*max);
                break;
            case "Proveedores":
                rs = db.select("proveedores", "*", "LIMIT "+max*pagina+" OFFSET "+(pagina-1)*max);
                break;
            case "Clientes":
                rs = db.select("clientes", "*", "LIMIT "+max*pagina+" OFFSET "+(pagina-1)*max);
                break;
            case "Usuarios":
                rs = db.select("usuarios", "id, usuario, fecha", "LIMIT "+max*pagina+" OFFSET "+(pagina-1)*max);
                break;
            case "Cotización":
                rs = db.select("cotizaciones", "id, total, fecha", "LIMIT "+max*pagina+" OFFSET "+(pagina-1)*max);
                break;
            default:
                db.cerrar();
                return;
        }
        
        try {
            ResultSetMetaData rsMd = rs.getMetaData();
            //La cantidad de columnas que tiene la consulta
            int cantidadColumnas = rsMd.getColumnCount();
            for(int cn = 1; cn < cantidadColumnas; cn++) {
                modelo.addColumn(rsMd.getColumnName(cn));
            }
            
            while (rs.next()) {
                Object[] fila = new Object[cantidadColumnas];
                for (int i = 0; i < cantidadColumnas; i++) {
                    fila[i]=rs.getObject(i+1);
                }
                modelo.addRow(fila);
            }
            db.cerrar();
        } catch (SQLException ex) {
            Logger.getLogger(tabla_estandar.class.getName()).log(Level.SEVERE, null, ex);
        }
        disableBotones();
        jLabel2.setText(""+pagina);
        jLabel5.setText(""+paginacion());
    }

    public void enableBotones() {
        jButton1.setEnabled(true);
        jButton3.setEnabled(true);
    }
    
    public void disableBotones() {
        jButton1.setEnabled(false);
        jButton3.setEnabled(false);
    }
    
    public void eliminarElemento() {
        if (JOptionPane.showConfirmDialog(null, "Eliminar elemento seleccionado?") == JOptionPane.YES_OPTION)
        if (seleccionado > 0) {
            sqlite db = new sqlite();
            boolean check = false;
            switch(this.titulo) {
                case "Facturación":
                    check = db.delete("facturas", "id = "+this.seleccionado);
                    db.delete("productos_facturas", "facturas_id = "+this.seleccionado);
                    break;
                case "Productos":
                    check = db.delete("productos", "id = "+this.seleccionado);
                    break;
                case "Proveedores":
                    check = db.delete("proveedores", "id = "+this.seleccionado);
                    break;
                case "Clientes":
                    check = db.delete("clientes", "id = "+this.seleccionado);
                    break;
                case "Usuarios":
                    check = db.delete("usuarios", "id = "+this.seleccionado);    
                    break;
                case "Cotización":
                    check = db.delete("cotizaciones", "id = "+this.seleccionado);
                    break;
            }
            
            if (check) {
                JOptionPane.showMessageDialog(null, "Eliminado correctamente!");
                cargarTabla(1);
            } else JOptionPane.showMessageDialog(null, "No se pudo eliminar nada, el producto no existe o la conexion a la base de datos fallo.");
            db.cerrar();
        }
    }
    
    public int paginacion() {
        sqlite db = new sqlite();
        String tabla = "";
        double cant = 0;
        switch(this.titulo) {
                case "Facturación":
                    tabla = "facturas";
                    break;
                case "Productos":
                    tabla = "productos";
                    break;
                case "Proveedores":
                    tabla = "proveedores";
                    break;
                case "Clientes":
                    tabla = "clientes";
                    break;
                case "Usuarios":
                    tabla = "usuarios";    
                    break;
                case "Cotización":
                    tabla = "cotizaciones";
                    break;
            }
        ResultSet rs = db.select(tabla, "COUNT(id) as cuantos", null);
        try {
            if (rs.next()) {
                cant = rs.getInt("cuantos");
            }
        } catch (SQLException ex) {
            Logger.getLogger(tabla_estandar.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("Cuantos: "+cant);
        if (cant > max) {
            int round = 1;
            double round2 = cant/max;
            //System.out.println("round2: "+round2);
            if (round2-(int)round2 != 0.0) {
                //System.out.println("The number has decimals!");
                round = ((int)round2)+1;
            } else {
                round = (int)round2;
            }
            //jLabel3.setText(1+"/"+round);
            jButton4.setEnabled(true);
            jButton5.setEnabled(true);
            db.cerrar();
            //System.out.println("Paginas: "+round);
            return round;
        }
        //jLabel3.setText("1/1");
        db.cerrar();
        return 1;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Facturacion - Angie");
        setResizable(false);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/facturas.png"))); // NOI18N
        jLabel1.setText("Facturación");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setEnabled(false);

        jTextField1.setText("jTextField1");
        jTextField1.setEnabled(false);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/buscar.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/atras.png"))); // NOI18N
        jButton4.setEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/adelante.png"))); // NOI18N
        jButton5.setEnabled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel2.setText("1");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel4.setText("/");

        jLabel5.setText("1");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(27, 27, 27)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addContainerGap())))
        );

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/editar.png"))); // NOI18N
        jButton1.setText("Editar");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/nuevo.png"))); // NOI18N
        jButton2.setText("Nuevo");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/equis.png"))); // NOI18N
        jButton3.setText("Eliminar");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/recargar.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        switch(this.titulo) {
            case "Facturación":
                facturas fac = new facturas();
                fac.setLocationRelativeTo(null);
                fac.setVisible(true);
                break;
            case "Productos":
                productos p = new productos();
                p.setLocationRelativeTo(null);
                p.setVisible(true);
                
                break;
            case "Proveedores":
                proveedores pr = new proveedores();
                pr.setLocationRelativeTo(null);
                pr.setVisible(true);
                
                break;
            case "Clientes":
                clientes cl = new clientes();
                cl.setLocationRelativeTo(null);
                cl.setVisible(true);
                
                break;
            case "Usuarios":
                usuarios us = new usuarios();
                us.setLocationRelativeTo(null);
                us.setVisible(true);
                
                break;
            case "Cotización":
                cotizaciones ct= new cotizaciones();
                ct.setLocationRelativeTo(null);
                ct.setVisible(true);
                break;
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        // TODO add your handling code here:
        if(jTable1.getSelectedRow() >= 0) {
            seleccionado = Integer.parseInt(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
            enableBotones();
        } else { 
            seleccionado = -1;
            disableBotones();
        }
    }//GEN-LAST:event_jTable1MouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (seleccionado > 0) 
        switch(this.titulo) {
            case "Facturación":
                facturas fac = new facturas(seleccionado);
                fac.setLocationRelativeTo(null);
                fac.setVisible(true);
                break;
            case "Productos":
                productos p = new productos(seleccionado);
                p.setLocationRelativeTo(null);
                p.setVisible(true);
                
                break;
            case "Proveedores":
                proveedores pr = new proveedores(seleccionado);
                pr.setLocationRelativeTo(null);
                pr.setVisible(true);
                
                break;
            case "Clientes":
                clientes cl = new clientes(seleccionado);
                cl.setLocationRelativeTo(null);
                cl.setVisible(true);
                
                break;
            case "Usuarios":
                usuarios us = new usuarios(seleccionado);
                us.setLocationRelativeTo(null);
                us.setVisible(true);
                
                break;
            case "Cotización":
                
                break;
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        cargarTabla(1);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        eliminarElemento();
        disableBotones();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        int pags = Integer.parseInt(jLabel5.getText());
        int pag = Integer.parseInt(jLabel2.getText());
        if (pag < pags) {
            cargarTabla(pag+1);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        int pags = Integer.parseInt(jLabel5.getText());
        int pag = Integer.parseInt(jLabel2.getText());
        if (pag > 1) {
            cargarTabla(pag-1);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
