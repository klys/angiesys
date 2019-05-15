/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package angiesys;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Admin2
 */
public class facturas extends javax.swing.JFrame {
String dom = "N/A", tel = "N/A";//valores por defecto de domicilio y telefono del cliente
boolean confirm=false;
    /**
     * Creates new form facturas
     */
    public facturas() {
        initComponents();
        jList1.setModel(listmodel);
        //jTabbedPane1.setSelectedIndex(0);
        cargar_clien();
        cargarModosdPago();
        cargarIVA();
        setMoneda(cargarMoneda());
        fid = crearFactura();
        cargarFechaFactura();
        jButton4.setEnabled(false);
        URL iconURL = getClass().getResource("/images/facturas.png");
        // iconURL is null when not found
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());
    }

    public facturas(int fid) {
        this.fid = fid;
        initComponents();
        jList1.setModel(listmodel);
        
        //cargarModosdPago();
        jComboBox4.setVisible(false);
        jPanel7.setVisible(false);
        //cargarIVA();
        setMoneda(cargarMoneda());
        //fid = crearFactura();
        cargarFechaFactura();
        cargarProductos();
        jLabel26.setText(this.fid + "");
        cargarTotales();
        jButton4.setVisible(false);
        view = true;
        cargar_clien2();
        URL iconURL = getClass().getResource("/images/facturas.png");
        // iconURL is null when not found
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());
    }

    boolean view = false;

    DefaultListModel listmodel = new DefaultListModel();

    ArrayList<Integer> clientes_id = new ArrayList<Integer>();
    ArrayList<String> clientes_rif = new ArrayList<String>();
    ArrayList<String> clientes_nombres = new ArrayList<String>();

    int cliente_id = -1;
    int fid = -1;

    int pidtoadd = -1;
    double preciotoadd = 0.0;

    double ivapor = 0.0;

    ArrayList<Double> producto_precio = new ArrayList<Double>();
    ArrayList<Double> producto_cantidad = new ArrayList<Double>();
    ArrayList<Boolean> producto_exento = new ArrayList<Boolean>();

    double subtotal = 0.0;
    double iva = 0.0;
    double total = 0.0;
    double exento = 0.0;
    String moneda = "$";

    boolean cliente = true;

    public void cargarFacturaCliente() {

    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
        //jLabel18.setText(moneda);
        //jLabel19.setText(moneda);
        //jLabel17.setText(moneda);
        //jLabel30.setText(moneda);
        //jLabel33.setText(moneda);
    }

    public int crearFactura() {
        sqlite db = new sqlite();
        int toreturn = db.insert("facturas", "ivapor", "'" + ivapor + "'");
        db.cerrar();
        jLabel26.setText(toreturn + "");
        return toreturn;
    }

    public void cargarFechaFactura() {
        sqlite db = new sqlite();
        ResultSet rs = db.select("facturas", "fecha", "WHERE id = " + fid);
        try {
            if (rs.next()) {
                jLabel27.setText(rs.getString("fecha"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(facturas.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.cerrar();

    }

    public void cargarTotales() {
        sqlite db = new sqlite();
        ResultSet rs = db.select("facturas", "subtotal, total, iva, ivapor, exento", "WHERE id = " + fid);
        try {
            if (rs.next()) {
                setIva(rs.getDouble("iva"));
                setTotal(rs.getDouble("total"));
                setExento(rs.getDouble("exento"));
                setSubtotal(rs.getDouble("subtotal"));
                asignarIVApor(rs.getDouble("ivapor"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(facturas.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.cerrar();
    }

    public void cargarIVA() {
        sqlite db = new sqlite();
        ResultSet rs = db.select("configuracion", "valor", "WHERE nombre = 'iva'");
        try {
            if (rs.next()) {
                asignarIVApor(Double.parseDouble(rs.getString("valor")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(facturas.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.cerrar();
    }

    public String cargarMoneda() {
        String toreturn = "";
        sqlite db = new sqlite();
        ResultSet rs = db.select("configuracion", "valor", "WHERE nombre = 'moneda'");
        try {
            if (rs.next()) {
                toreturn = rs.getString("valor");
            }
        } catch (SQLException ex) {
            Logger.getLogger(facturas.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.cerrar();
        return toreturn;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
        jLabel14.setText(conf.truncateDecimal(this.subtotal, 2) + " " + moneda);
    }

    public void setIva(double iva) {
        this.iva = iva;
        jLabel13.setText(conf.truncateDecimal(this.iva, 2) + " " + moneda);
    }

    public void setExento(double exento) {
        this.exento = exento;
        jLabel29.setText(conf.truncateDecimal(this.exento, 2) + " " + moneda);
    }

    public void setTotal(double total) {
        this.total = total;
        jLabel16.setText(conf.truncateDecimal(this.total, 2) + " " + moneda);
    }

    public void asignarIVApor(double ivapor) {
        this.ivapor = ivapor;
        jLabel32.setText(Double.toString(ivapor) + "%");
    }

    public void setCliente(String nombre, String rif, int cid) {
        jLabel2.setText(nombre);
        jLabel4.setText(rif);
        cliente_id = cid;
        jTabbedPane1.setSelectedIndex(0);
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setEnabledAt(0, true);
        confirm=true;
    }

    public void cargarModosdPago() {
        sqlite db = new sqlite();
        ResultSet rs = db.select("modosdepago", "nombre", null);
        jComboBox4.removeAllItems();
        try {
            while (rs.next()) {
                jComboBox4.addItem(rs.getString("nombre"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(facturas.class.getName()).log(Level.SEVERE, null, ex);
        }

        db.cerrar();
    }

    public void cargarProductos() {
        producto_precio.clear();
        producto_cantidad.clear();
        producto_exento.clear();
        DefaultTableModel modelo = new DefaultTableModel();
        jTable1.setModel(modelo);
        sqlite db = new sqlite();
        ResultSet rs = db.select("productos_facturas as pf", "p.nombre as nombre, p.descripcion as descripcion, pf.cantidad as cantidad, pf.precio as precio, pf.exento as exento", "INNER JOIN productos p ON p.id = pf.productos_id WHERE facturas_id = " + fid);
        try {
            ResultSetMetaData rsMd = rs.getMetaData();
            //La cantidad de columnas que tiene la consulta
            int cantidadColumnas = rsMd.getColumnCount();
            //System.out.println("Cantidad de columnas: "+cantidadColumnas);
            for (int cn = 1; cn <= cantidadColumnas; cn++) {
                modelo.addColumn(rsMd.getColumnName(cn));
            }

            while (rs.next()) {
                Object[] fila = new Object[cantidadColumnas];
                for (int i = 0; i < cantidadColumnas; i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                modelo.addRow(fila);
                producto_cantidad.add(rs.getDouble("cantidad"));
                producto_precio.add(rs.getDouble("precio"));
                producto_exento.add(rs.getBoolean("exento"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(tabla_estandar.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.cerrar();
    }

    public void setProductoToAdd(String nombre, String precio, String stock, String exento, int pid) {
        jLabel9.setText(nombre);
        jLabel8.setText(precio);
        jLabel10.setText(stock);
        jLabel36.setText(exento);
        pidtoadd = pid;
        jButton3.setEnabled(true);
    }

    public void insertarProductosFactura() {
        sqlite db = new sqlite();
        String exento;
        if (jLabel36.getText().equals("Si")) {
            exento = "1";
        } else {
            exento = "0";
        }
        if (db.insert("productos_facturas", "productos_id, cantidad, precio, exento, facturas_id", "" + pidtoadd + " , " + jTextField3.getText() + " , " + jLabel8.getText() + " , " + exento + ", " + fid) > 0) {
            db.cerrar();
            cargarProductos();
            calcularTotales();
        }

    }

    public void calcularTotales() {
        double subtotal = 0, iva = 0, exento = 0, total;
        for (int i = 0; i < producto_precio.size(); i++) {
            subtotal += producto_precio.get(i) * producto_cantidad.get(i);
            if (!producto_exento.get(i)) {
                iva += (producto_precio.get(i) * producto_cantidad.get(i)) * (ivapor / 100);
            } else {
                exento += (producto_precio.get(i) * producto_cantidad.get(i)) * (ivapor / 100);
            }
        }
        total = subtotal + iva;
        setSubtotal(subtotal);
        setIva(iva);
        setExento(exento);
        setTotal(total);
        //jLabel14.setText(truncateDecimal(subtotal,4)+"");
        //jLabel13.setText(truncateDecimal(iva,4)+"");
        //jLabel29.setText(truncateDecimal(exento,4)+"");
        //jLabel16.setText(truncateDecimal(total,4)+"");
    }

    public void cancelarFactura() {
        sqlite db = new sqlite();
        if (db.delete("facturas", "id = " + fid)) {
            if (db.delete("productos_facturas", "facturas_id = " + fid)) {
                this.dispose();
            }
        }
        db.cerrar();
    }

    public void guardarFactura() {
        sqlite db = new sqlite();
        if (db.update("facturas", "facturada = 1, total = '" + this.total + "', iva = '" + this.iva + "' , subtotal = '" + this.subtotal + "' , exento = '" + this.exento + "', modoPago = '" + jComboBox4.getSelectedItem().toString() + "', clientes_id = " + this.cliente_id, "id = " + fid)) {
            JOptionPane.showMessageDialog(null, "Factura guardada!");
            db.cerrar();
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Un error ocurrio y la factura no pudo ser guardada, vuelva a intentar o contacte con el administrador del sistema.");
            db.cerrar();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel23 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        jLabel23.setText("jLabel23");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Facturas - Angie");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/facturas.png"))); // NOI18N
        jLabel1.setText("Factura");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("N/A");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("N/A");

        jLabel20.setText("Nombre del Cliente:");

        jLabel21.setText("RIF o CI:");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Modo de Pago:");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21))
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                        .addComponent(jLabel22)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Cliente", jPanel3);

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setText("Buscar por");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, -1));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "RIF o CI", "NOMBRE" }));
        jPanel4.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 13, -1, -1));

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });
        jPanel4.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 14, 70, -1));

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setAutoscrolls(false);
        jScrollPane1.setViewportView(jList1);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(221, 0, 179, 70));

        jButton1.setText("SELECCIONAR");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, -1, 28));

        jButton2.setText("Nuevo");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jTabbedPane1.addTab("Buscar y Seleccionar Cliente", jPanel4);

        jPanel2.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 49, 400, 100));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Agregar producto"));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CODIGO", "NOMBRE", "ID" }));
        jPanel7.add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 16, -1, -1));

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });
        jPanel7.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(103, 17, 80, -1));

        jLabel5.setText("Nombre");
        jPanel7.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(201, 16, -1, -1));

        jLabel6.setText("Precio");
        jPanel7.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(201, 36, -1, -1));

        jLabel7.setText("Stock");
        jPanel7.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(201, 56, -1, -1));

        jLabel8.setText("N/A");
        jPanel7.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(256, 36, -1, -1));

        jLabel9.setText("N/A");
        jPanel7.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(256, 16, -1, -1));

        jLabel10.setText("N/A");
        jPanel7.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(256, 56, -1, -1));

        jButton3.setText("Agregar");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));
        jPanel7.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, 30, -1));

        jLabel34.setText("Cantidad");
        jPanel7.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, -1, 10));

        jLabel35.setText("Exento:");
        jPanel7.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, -1, -1));

        jLabel36.setText("N/A");
        jPanel7.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 50, -1, -1));

        jPanel2.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 162, 408, 77));

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
        jScrollPane2.setViewportView(jTable1);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 245, 408, 110));

        jLabel11.setText("SubTotal");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 360, -1, -1));

        jLabel12.setText("IVA");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, -1, -1));

        jLabel13.setText("0");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 380, -1, -1));

        jLabel14.setText("0");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 360, -1, -1));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setText("TOTAL");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 380, -1, -1));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 0, 0));
        jLabel16.setText("0");
        jPanel2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 380, -1, -1));

        jLabel24.setText("Factura No.");
        jPanel2.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, -1, -1));

        jLabel25.setText("Fecha:");
        jPanel2.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, -1, -1));

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 0, 0));
        jLabel26.setText("jLabel26");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, -1, -1));

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setText("jLabel27");
        jPanel2.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, -1, -1));

        jLabel28.setText("Exento");
        jPanel2.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 400, -1, 10));

        jLabel29.setText("0");
        jPanel2.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 400, 110, 10));

        jLabel31.setText("IVA%");
        jPanel2.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, -1, 10));

        jLabel32.setText("0");
        jPanel2.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 360, -1, 10));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, 420));

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/visto-bueno.png"))); // NOI18N
        jButton4.setText("Guardar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 440, -1, -1));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imprimir.png"))); // NOI18N
        jButton5.setText("Imprimir");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 440, -1, -1));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/equis.png"))); // NOI18N
        jButton6.setText("Cancelar");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 440, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyTyped
        // TODO add your handling code here:
        sqlite db = new sqlite();
        ResultSet rs = db.select("productos", "*", "WHERE codigo LIKE '%" + jTextField2.getText() + "%'");
        try {
            if (rs.next()) {
                String exento = "No";
                if (rs.getBoolean("exento")) {
                    exento = "Si";
                } else {
                    exento = "No";
                }
                setProductoToAdd(rs.getString("nombre"), rs.getString("precio"), rs.getString("cantidad"), exento, rs.getInt("id"));
            } else {
                jButton3.setEnabled(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(facturas.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.cerrar();
    }//GEN-LAST:event_jTextField2KeyTyped

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        new clientes(this).setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        //jLabel2.setText(clientes_nombres.get(jList1.getSelectedIndex()));
        //jLabel4.setText(clientes_rif.get(jList1.getSelectedIndex()));
        //cliente_id = clientes_id.get(jList1.getSelectedIndex());
        setCliente(clientes_nombres.get(jList1.getSelectedIndex()), clientes_rif.get(jList1.getSelectedIndex()), clientes_id.get(jList1.getSelectedIndex()));
         
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        // TODO add your handling code here:
        sqlite db = new sqlite();
        if (jComboBox1.getSelectedIndex() == 0) {
            ResultSet rs = db.select("clientes", "id, nombre, rif", "WHERE rif LIKE '%" + jTextField1.getText() + "%' LIMIT 3");
            try {
                clientes_id.clear();
                clientes_rif.clear();
                clientes_nombres.clear();
                listmodel.removeAllElements();
                while (rs.next()) {
                    clientes_id.add(rs.getInt("id"));
                    clientes_rif.add(rs.getString("rif"));
                    clientes_nombres.add(rs.getString("nombre"));
                    listmodel.addElement(rs.getString("nombre"));
                }
                if (!listmodel.isEmpty()) {
                    jList1.setSelectedIndex(0);
                    jButton1.setEnabled(true);
                } else {
                    jButton1.setEnabled(false);
                }

            } catch (SQLException ex) {
                Logger.getLogger(facturas.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (jComboBox1.getSelectedIndex() == 1) {
            ResultSet rs = db.select("clientes", "id, nombre, rif", "WHERE nombre LIKE '%" + jTextField1.getText() + "%' LIMIT 3");
            try {
                clientes_id.clear();
                clientes_rif.clear();
                clientes_nombres.clear();
                listmodel.removeAllElements();
                while (rs.next()) {
                    clientes_id.add(rs.getInt("id"));
                    clientes_rif.add(rs.getString("rif"));
                    clientes_nombres.add(rs.getString("nombre"));
                    listmodel.addElement(rs.getString("nombre"));
                }
                if (!listmodel.isEmpty()) {
                    jList1.setSelectedIndex(0);
                    jButton1.setEnabled(true);
                } else {
                    jButton1.setEnabled(false);
                }

            } catch (SQLException ex) {
                Logger.getLogger(facturas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        db.cerrar();
    }//GEN-LAST:event_jTextField1KeyTyped

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        try {
            if (Integer.parseInt(jTextField3.getText()) > 0 && !jTextField3.getText().equals("")) {
                insertarProductosFactura();
                jTextField2.setText("");
                jTextField3.setText("");
                jButton4.setEnabled(true);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser un numero entero!");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        if (view == false) {
            cancelarFactura();
        }
        this.dispose();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int valor = Integer.valueOf(conf.cargar("facturaCliente"));// TODO add your handling code here:
        if( valor==0){
                guardarFactura();
    }
        else{
        if( confirm==true){
        guardarFactura();
        }
        else{
        JOptionPane.showMessageDialog(null, "Error, debe Seleccionar un cliente debido condiciones de Configuracion.\n"
                                          + "Seleccione el Cliente en el panel: 'Buscar y Seleccionar Cliente' de la parte superior");
        }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        sqlite db = new sqlite();
        System.out.println(this.cliente_id);
        String ciud=conf.cargar("ciudad");
        try {
            String ubicacionreporte = System.getProperty("user.dir") + "/src/reportes/Factura.jasper";// TODO add your handling code here:
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(ubicacionreporte);
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("fid", this.fid);
            parametros.put("nombrep", jLabel2.getText());
            parametros.put("rifcip",jLabel4.getText() );
            parametros.put("telefonop", this.tel);
            parametros.put("domiciliop", this.dom);
            parametros.put("ciudadp", ciud);
            JasperPrint print = JasperFillManager.fillReport(jasperReport, parametros, db.dame_conexion());
            
            JasperViewer view = new JasperViewer(print, false);
            // JasperExportManager.exportReportToPdfFile("dir");
            view.setVisible(true);
            JasperPrintManager.printReport(print, true); 
            // view.setTitle("Factura");
            //********enviamos pdf a la impresora
//            JRPrintServiceExporter jrprintServiceExporter = new JRPrintServiceExporter();
//            jrprintServiceExporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
//            jrprintServiceExporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, impresora);
//            jrprintServiceExporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.TRUE);
//            jrprintServiceExporter.exportReport();
            this.setVisible(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    db.cerrar();
    }//GEN-LAST:event_jButton5ActionPerformed
    public void cargar_clien() {
        int valor = Integer.valueOf(conf.cargar("facturaCliente"));//controla si factura requiere inf de cliente
        
        
        if (valor == 0) { // no necesita
            jTabbedPane1.setEnabledAt(1, false);
            jTabbedPane1.setSelectedIndex(0);
            System.out.println("This facture no require infClient");
            jLabel2.setText("N/A");
            jLabel4.setText("N/A");
            
        } else {//requiere inf. del cliente
             jTabbedPane1.setEnabledAt(0, false);
            jTabbedPane1.setSelectedIndex(1);
//            
        }
       
    }

    public void cargar_clien2(){
    jTabbedPane1.setEnabled(false);
    jTabbedPane1.setSelectedIndex(0);
    sqlite db = new sqlite();
    int val_c;
        try {
            ResultSet rs = db.select("facturas", "*", "WHERE id = " + fid + "");//selecciona el id del cliente en la factura
            if(rs.next()){
            val_c=rs.getInt("clientes_id");
             
             jLabel22.setText("Modo Pago: "+rs.getString("modoPago"));
            ResultSet rs2 = db.select("clientes", "*", "Where id=" + val_c + "");//Se cargan los datos del cliente a la facturA

                if (rs2.next()) {
                    
                    jLabel2.setText(rs2.getString("nombre"));
                    jLabel4.setText(rs2.getString("rif"));
                    dom = rs2.getString("direccion");
                    if (dom.equals("")) {
                        dom = "Sin Registro";
                    }
                    tel = rs2.getString("telefono");// **dom &&**tel: variables para almacenar domicilio y telefono
                }
              }
        } catch (Exception e) {
        }
        
         db.cerrar();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(facturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(facturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(facturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(facturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new facturas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
