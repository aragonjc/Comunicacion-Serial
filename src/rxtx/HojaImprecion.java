package rxtx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class HojaImprecion extends JFrame {

    /////////////////// ATRIBUTOS
    private int ite;

    private GridBagConstraints gbc;

    private JMenuBar jmbBar;
    private JMenu jmArc, jmAyu;
    private JMenuItem jmiAbr, jmiNue, jmiImp, jmiAce;

    private JLabel jlDat;
    private JPanel jpDat, jpFig, jpCol;
    private JButton jbEst, jbTri, jbCir;
    private JButton jbRoj, jbAzu, jbAma;

    private JTabbedPane jtpPes;
    private LinkedList<JTextField[][]> llHoj;
    private Color col;

    private Thread hEnv, hRes;

    private ComSer c;

    /////////////////// CONSTRUCTOR
    public HojaImprecion() {
        formatoFormulario();
        formatoComponentes();
        this.pack();
        this.setSize(600, 650);
        this.setLocationRelativeTo(null);
    }

    // Formato del Formulario
    private void formatoFormulario() {
        this.setTitle("Impresora");
        this.setLayout(new GridBagLayout());
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // Formato de los Componentes
    private void formatoComponentes() {
        //////////////////// Variables
        // linked list matriz JTextField de pestañas
        llHoj = new LinkedList<JTextField[][]>();

        // conexion serial
        c = new ComSer();
        c.ini("COM1");

        // color predeterminado
        col = Color.red;

        // hilo de actualizacion de resepcion
        hRes = new Thread(new Runnable() {
            @Override
            public void run() {
                int est = 0, est2 = 0, est3 = 0;
                int conIte = 0;
                String cad = "";

                while (true) {
                    char car = (char) c.lee();

                    if ((int) car != 65535) {
                        switch (est) {
                            case 0:
                                if (car != '>' && car != '?') {
                                    cad += "X: " + car + " - ";
                                    est = 1;
                                }
                                break;
                            case 1:
                                if (car != '>' && car != '?') {
                                    cad += "Y: " + car;
                                    est = 2;
                                }
                                break;
                            case 2:
                                if (car != '>' && car != '?') {
                                    cad += car;
                                    est = 3;
                                }
                                break;
                            case 3:
                                switch (est2) {
                                    case 0:
                                        if (car == '?') {
                                            est2 = 1;
                                        }
                                        break;
                                    case 1:
                                        if (car == '>') {
                                            est2 = 0;
                                            conIte++;
                                            if (hEnv.isAlive() == true) {
                                                hEnv.resume();
                                            }
                                            if (conIte == ite) {
                                                jmiImp.setEnabled(true);
                                                conIte = 0;
                                            }
                                        }
                                        break;
                                }

                                switch (est3) {
                                    case 0:
                                        if (car == '<') {
                                            jmiImp.setEnabled(false);
                                            est3 = 1;
                                        }
                                        break;
                                    case 1:
                                        if (car == '>') {
                                            jmiImp.setEnabled(true);
                                            est3 = 0;
                                        }
                                        break;
                                }

                                jlDat.setText(cad);

                                est = 0;
                                cad = "";
                                break;
                        }
                    }
                }
            }
        });
        hRes.start();
        //////////////////// Menu
        jmbBar = new JMenuBar();

        // menu archivo
        jmArc = new JMenu("Archivo");

        jmiAbr = new JMenuItem("Abrir");
        jmiAbr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionBotonAbr(e);
            }
        });
        jmArc.add(jmiAbr);

        jmiNue = new JMenuItem("Nuevo");
        jmiNue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionBotonNue(e);
            }
        });
        jmArc.add(jmiNue);

        jmiImp = new JMenuItem("Imprimir");
        jmiImp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionBotonImp(e);
            }
        });
        jmArc.add(jmiImp);

        jmbBar.add(jmArc);

        // menu reportes
        jmAyu = new JMenu("Ayuda");

        jmiAce = new JMenuItem("Acerca de...");
        jmiAce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionBotonAyu(e);
            }
        });
        jmAyu.add(jmiAce);

        jmbBar.add(jmAyu);

        this.setJMenuBar(jmbBar);

        //////////////////// Lavel Datos
        jpDat = new JPanel();
        jpDat.setBorder(new TitledBorder("Datos"));

        establecerGBC(0, 0, 1, 1, 0.1, 0.3);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        this.add(jpDat, gbc);

        jlDat = new JLabel("x: y:");
        jpDat.add(jlDat);

        //////////////////// Figuras
        jpFig = new JPanel();
        jpFig.setLayout(new GridBagLayout());
        jpFig.setBorder(new TitledBorder("Figuras"));

        establecerGBC(0, 1, 1, 1, 0.1, 1.0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        this.add(jpFig, gbc);

        jbEst = new JButton();
        jbEst.setIcon(new ImageIcon("estrella.png"));
        jbEst.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionBotonEst(e);
            }
        });
        jbCir = new JButton();
        jbCir.setIcon(new ImageIcon("circulo.png"));
        jbCir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionBotonCir(e);
            }
        });
        jbTri = new JButton();
        jbTri.setIcon(new ImageIcon("triangulo.png"));
        jbTri.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionBotonTri(e);
            }
        });

        establecerGBC(0, 0, 1, 1, 1.0, 1.0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        jpFig.add(jbEst, gbc);

        establecerGBC(0, 1, 1, 1, 1.0, 1.0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        jpFig.add(jbCir, gbc);

        establecerGBC(0, 2, 1, 1, 1.0, 1.0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        jpFig.add(jbTri, gbc);

        //////////////////// Colores
        jpCol = new JPanel();
        jpCol.setLayout(new GridBagLayout());
        jpCol.setBorder(new TitledBorder("Colores"));

        establecerGBC(0, 2, 1, 1, 0.1, 0.3);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        this.add(jpCol, gbc);

        jbRoj = new JButton("Rojo");
        jbRoj.setBackground(Color.red);
        jbRoj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionBotonRoj(e);
            }
        });
        jbAzu = new JButton("Azul");
        jbAzu.setBackground(Color.blue);
        jbAzu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionBotonAzu(e);
            }
        });
        jbAma = new JButton("Amarillo");
        jbAma.setBackground(Color.yellow);
        jbAma.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionBotonAma(e);
            }
        });

        establecerGBC(0, 0, 1, 1, 1.0, 1.0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        jpCol.add(jbRoj, gbc);

        establecerGBC(0, 1, 1, 1, 1.0, 1.0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        jpCol.add(jbAzu, gbc);

        establecerGBC(0, 2, 1, 1, 1.0, 1.0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        jpCol.add(jbAma, gbc);

        //////////////////// Pestañas
        jtpPes = new JTabbedPane();

        establecerGBC(1, 0, 1, 3, 1.0, 1.0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        this.add(jtpPes, gbc);

        //////////////////// Hoja de Datos
        creHoj();
    }

    // Funcion que establece los parametros para el grid bag constraints
    public void establecerGBC(int x, int y, int w, int h, double wx, double wy) {
        gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gbc.weightx = wx;
        gbc.weighty = wy;
    }

    /////////////////// METODOS
    private void accionBotonAbr(ActionEvent evt) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Archivo", "txt", "txt"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            creHoj();
            
            String tex = leeArc(fc.getSelectedFile());
            String tex2 = "";

            for (int i = 0; i < tex.length(); i++) {
                char car = tex.charAt(i);
                if ((car > 47 && car < 58) || car == ',' || car == '}' || car == '\n') {
                    tex2 += tex.charAt(i);
                }
            }
            tex2 = tex2.replaceAll("}", "\n");
            tex2 = tex2.replaceAll("\n\n", "\n");
            tex2 = tex2.replaceAll(",\n", "\n");
            tex2 = tex2.trim();

            String[] vFil = tex2.split("\n");

            JTextField[][] jtTem = llHoj.get(jtpPes.getTabCount() - 1);
            for (int i = 0; i < vFil.length; i++) {
                String[] vCol = vFil[i].split(",");
                if (vCol.length == 2) {
                    int x = Integer.parseInt(vCol[0]) - 1;
                    int y = Integer.parseInt(vCol[1]) - 1;

                    if ((x >= 0 && x < jtTem.length) && (y >= 0 && y < jtTem[0].length)) {
                        jtTem[x][y].setText("1");
                        jtTem[x][y].setBackground(Color.red);
                    }
                }
            }
            jtpPes.setSelectedIndex(jtpPes.getTabCount() - 1);
        } else {
            JOptionPane.showMessageDialog(this, "Error al Abrir Archivo...");
        }
    }

    private void accionBotonNue(ActionEvent evt) {
        creHoj();
    }

    private void accionBotonImp(ActionEvent evt) {
        jmiImp.setEnabled(false);
        JTextField[][] jtTem = llHoj.get(jtpPes.getSelectedIndex());
        int est = 0;
        String coo = "", env = "";

        for (int j = 0; j < jtTem[0].length; j++) {
            for (int i = 0; i < jtTem.length; i++) {
                if (jtTem[i][j].getText().equals("1") || jtTem[i][j].getText().equals("2") || jtTem[i][j].getText().equals("3")) {
                    int x = i + 1;
                    int y = j + 1;
                    int z = Integer.valueOf(jtTem[i][j].getText());

                    if (y < 10) {
                        coo = x + "0" + y + z;
                    } else {
                        coo = x + "" + y + z;
                    }

                    switch (est) {
                        case 0:
                            env += coo;
                            est++;
                            break;
                        case 1:
                            env += coo;
                            est++;
                            break;
                        case 2:
                            env += coo;
                            est = 0;
                            env += "\n";
                            break;
                    }
                }
            }
        }
        if (est == 1) {
            env += coo;
            env += coo;
        } else if (est == 2) {
            env += coo;
        }
        String[] vDat = env.split("\n");

        ite = vDat.length;
        hEnv = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < vDat.length; i++) {
                    for (int j = 0; j < 3; j++) {
                        for (int k = 0; k < vDat[i].length(); k++) {
                            c.env(vDat[i].charAt(k));
                        }
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                        }
                    }

                    c.env('A');

                    if (i != vDat.length - 1) {
                        hEnv.suspend();
                    }
                }
            }
        });
        hEnv.start();
    }

    private void accionBotonAyu(ActionEvent evt) {
        JFrame jf = new JFrame();
        jf.setTitle("Acerca de...");
        jf.setSize(400, 300);
        jf.setLocationRelativeTo(null);
        jf.setLayout(new BorderLayout());
        JLabel jl = new JLabel("Texto de la info...", SwingConstants.CENTER);
        jf.add(jl, BorderLayout.CENTER);
        jf.show();
    }

    private void accionBotonEst(ActionEvent evt) {
        JTextField[][] jtTem = llHoj.get(jtpPes.getSelectedIndex());
        jtTem[0][6].setText("3");
        jtTem[0][6].setBackground(Color.yellow);
        jtTem[0][7].setText("3");
        jtTem[0][7].setBackground(Color.yellow);
        jtTem[0][11].setText("3");
        jtTem[0][11].setBackground(Color.yellow);
        jtTem[0][12].setText("3");
        jtTem[0][12].setBackground(Color.yellow);
        jtTem[1][6].setText("3");
        jtTem[1][6].setBackground(Color.yellow);
        jtTem[1][8].setText("3");
        jtTem[1][8].setBackground(Color.yellow);
        jtTem[1][10].setText("3");
        jtTem[1][10].setBackground(Color.yellow);
        jtTem[1][13].setText("3");
        jtTem[1][13].setBackground(Color.yellow);
        jtTem[2][5].setText("3");
        jtTem[2][5].setBackground(Color.yellow);
        jtTem[2][9].setText("3");
        jtTem[2][9].setBackground(Color.yellow);
        jtTem[2][12].setText("3");
        jtTem[2][12].setBackground(Color.yellow);
        jtTem[3][3].setText("3");
        jtTem[3][3].setBackground(Color.yellow);
        jtTem[3][4].setText("3");
        jtTem[3][4].setBackground(Color.yellow);
        jtTem[3][11].setText("3");
        jtTem[3][11].setBackground(Color.yellow);
        jtTem[4][3].setText("3");
        jtTem[4][3].setBackground(Color.yellow);
        jtTem[4][4].setText("3");
        jtTem[4][4].setBackground(Color.yellow);
        jtTem[4][11].setText("3");
        jtTem[4][11].setBackground(Color.yellow);
        jtTem[5][5].setText("3");
        jtTem[5][5].setBackground(Color.yellow);
        jtTem[5][9].setText("3");
        jtTem[5][9].setBackground(Color.yellow);
        jtTem[5][12].setText("3");
        jtTem[5][12].setBackground(Color.yellow);
        jtTem[6][6].setText("3");
        jtTem[6][6].setBackground(Color.yellow);
        jtTem[6][8].setText("3");
        jtTem[6][8].setBackground(Color.yellow);
        jtTem[6][10].setText("3");
        jtTem[6][10].setBackground(Color.yellow);
        jtTem[6][13].setText("3");
        jtTem[6][13].setBackground(Color.yellow);
        jtTem[7][6].setText("3");
        jtTem[7][6].setBackground(Color.yellow);
        jtTem[7][7].setText("3");
        jtTem[7][7].setBackground(Color.yellow);
        jtTem[7][11].setText("3");
        jtTem[7][11].setBackground(Color.yellow);
        jtTem[7][12].setText("3");
        jtTem[7][12].setBackground(Color.yellow);
    }

    private void accionBotonCir(ActionEvent evt) {
        JTextField[][] jtTem = llHoj.get(jtpPes.getSelectedIndex());
        jtTem[0][7].setText("1");
        jtTem[0][7].setBackground(Color.red);
        jtTem[0][8].setText("1");
        jtTem[0][8].setBackground(Color.red);
        jtTem[0][9].setText("1");
        jtTem[0][9].setBackground(Color.red);
        jtTem[1][5].setText("1");
        jtTem[1][5].setBackground(Color.red);
        jtTem[1][6].setText("1");
        jtTem[1][6].setBackground(Color.red);
        jtTem[1][10].setText("1");
        jtTem[1][10].setBackground(Color.red);
        jtTem[1][11].setText("1");
        jtTem[1][11].setBackground(Color.red);
        jtTem[2][4].setText("1");
        jtTem[2][4].setBackground(Color.red);
        jtTem[2][12].setText("1");
        jtTem[2][12].setBackground(Color.red);
        jtTem[3][3].setText("1");
        jtTem[3][3].setBackground(Color.red);
        jtTem[3][13].setText("1");
        jtTem[3][13].setBackground(Color.red);
        jtTem[4][3].setText("1");
        jtTem[4][3].setBackground(Color.red);
        jtTem[4][13].setText("1");
        jtTem[4][13].setBackground(Color.red);
        jtTem[5][4].setText("1");
        jtTem[5][4].setBackground(Color.red);
        jtTem[5][12].setText("1");
        jtTem[5][12].setBackground(Color.red);
        jtTem[6][5].setText("1");
        jtTem[6][5].setBackground(Color.red);
        jtTem[6][6].setText("1");
        jtTem[6][6].setBackground(Color.red);
        jtTem[6][10].setText("1");
        jtTem[6][10].setBackground(Color.red);
        jtTem[6][11].setText("1");
        jtTem[6][11].setBackground(Color.red);
        jtTem[7][7].setText("1");
        jtTem[7][7].setBackground(Color.red);
        jtTem[7][8].setText("1");
        jtTem[7][8].setBackground(Color.red);
        jtTem[7][9].setText("1");
        jtTem[7][9].setBackground(Color.red);
    }

    private void accionBotonTri(ActionEvent evt) {
        JTextField[][] jtTem = llHoj.get(jtpPes.getSelectedIndex());
        jtTem[0][10].setText("2");
        jtTem[0][10].setBackground(Color.blue);
        jtTem[0][11].setText("2");
        jtTem[0][11].setBackground(Color.blue);
        jtTem[0][12].setText("2");
        jtTem[0][12].setBackground(Color.blue);
        jtTem[1][8].setText("2");
        jtTem[1][8].setBackground(Color.blue);
        jtTem[1][9].setText("2");
        jtTem[1][9].setBackground(Color.blue);
        jtTem[1][12].setText("2");
        jtTem[1][12].setBackground(Color.blue);
        jtTem[2][6].setText("2");
        jtTem[2][6].setBackground(Color.blue);
        jtTem[2][7].setText("2");
        jtTem[2][7].setBackground(Color.blue);
        jtTem[2][12].setText("2");
        jtTem[2][12].setBackground(Color.blue);
        jtTem[3][4].setText("2");
        jtTem[3][4].setBackground(Color.blue);
        jtTem[3][5].setText("2");
        jtTem[3][5].setBackground(Color.blue);
        jtTem[3][12].setText("2");
        jtTem[3][12].setBackground(Color.blue);
        jtTem[4][4].setText("2");
        jtTem[4][4].setBackground(Color.blue);
        jtTem[4][5].setText("2");
        jtTem[4][5].setBackground(Color.blue);
        jtTem[4][12].setText("2");
        jtTem[4][12].setBackground(Color.blue);
        jtTem[5][6].setText("2");
        jtTem[5][6].setBackground(Color.blue);
        jtTem[5][7].setText("2");
        jtTem[5][7].setBackground(Color.blue);
        jtTem[5][12].setText("2");
        jtTem[5][12].setBackground(Color.blue);
        jtTem[6][8].setText("2");
        jtTem[6][8].setBackground(Color.blue);
        jtTem[6][9].setText("2");
        jtTem[6][9].setBackground(Color.blue);
        jtTem[6][12].setText("2");
        jtTem[6][12].setBackground(Color.blue);
        jtTem[7][10].setText("2");
        jtTem[7][10].setBackground(Color.blue);
        jtTem[7][11].setText("2");
        jtTem[7][11].setBackground(Color.blue);
        jtTem[7][12].setText("2");
        jtTem[7][12].setBackground(Color.blue);
    }

    private void accionBotonRoj(ActionEvent evt) {
        col = Color.red;
    }

    private void accionBotonAzu(ActionEvent evt) {
        col = Color.blue;
    }

    private void accionBotonAma(ActionEvent evt) {
        col = Color.yellow;
    }

    private void creHoj() {
        JPanel jp = new JPanel();
        jp.setLayout(new GridBagLayout());

        JTextField[][] jtaHoj = new JTextField[8][16];

        for (int i = 0; i < jtaHoj.length; i++) {
            for (int j = 0; j < jtaHoj[0].length; j++) {
                jtaHoj[i][j] = new JTextField("0");
                jtaHoj[i][j].setHorizontalAlignment(JTextField.CENTER);

                jtaHoj[i][j].addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        JTextField jtf = (JTextField) e.getComponent();
                        if (jtf.getText().equals("0")) {
                            if (col.equals(Color.red)) {
                                jtf.setText("1");
                            } else if (col.equals(Color.blue)) {
                                jtf.setText("2");
                            } else if (col.equals(Color.yellow)) {
                                jtf.setText("3");
                            }
                            jtf.setBackground(col);
                        } else {
                            jtf.setText("0");
                            jtf.setBackground(Color.white);
                        }
                    }

                    @Override
                    public void focusLost(FocusEvent e) {

                    }
                });

                jtaHoj[i][j].addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {

                    }

                    @Override
                    public void keyPressed(KeyEvent e) {

                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        JTextField jtf = (JTextField) e.getComponent();
                        if (jtf.getText().equals("1")) {
                            jtf.setBackground(Color.red);
                        } else if (jtf.getText().equals("2")) {
                            jtf.setBackground(Color.blue);
                        } else if (jtf.getText().equals("3")) {
                            jtf.setBackground(Color.yellow);
                        } else {
                            jtf.setBackground(Color.white);
                            jtf.setText("0");
                        }
                    }
                });

                establecerGBC(i, j, 1, 1, 1.0, 1.0);
                gbc.fill = GridBagConstraints.BOTH;
                jp.add(jtaHoj[i][j], gbc);
            }
        }

        llHoj.add(jtaHoj);
        int t = jtpPes.getTabCount() + 1;
        jtpPes.add("Dibujo " + t, jp);
    }

    private String leeArc(File arc) {
        String tex = "";
        try {
            FileReader fr = new FileReader(arc);
            BufferedReader br = new BufferedReader(fr);

            while (br.ready()) {
                tex += br.readLine() + "\n";
            }

            br.close();
            fr.close();
        } catch (Exception e) {
        }
        tex = tex.toLowerCase();
        return tex;
    }
}
