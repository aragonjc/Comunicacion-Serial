package rxtx;

////////////////// 
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class ComSer {

    ////////////////// VARIABLES
    SerialPort ps;
    OutputStream ou;
    InputStream in;
    public char buf;

    ////////////////// CONSTRUCTOR
    public ComSer() {
        ou = null;
        in = null;
    }

    ////////////////// METODOS
    public void ini(String nom) {
        Enumeration lp = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier p;
        while (lp.hasMoreElements()) {
            p = (CommPortIdentifier) lp.nextElement();
            System.out.println("Puertos" + p.getName());
            if (p.getName().equals(nom)) {
                try {
                    ps = (SerialPort) p.open("PUERTO SERIAL", 2000);
                    ps.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    ps.setDTR(true);
                    ou = ps.getOutputStream();
                    in = ps.getInputStream();
                } catch (Exception e) {
                }
                break;
            }
        }
    }

    public void fin() {
        ps.close();
    }

    // Envio
    public void env(char d) {
        try {
            ou.write(d);
            buf = d;
        } catch (Exception e) {
        }
    }
    
    // Reenvio
    public void env() {
        try {
            ou.write(buf);
            System.out.println(buf);
        } catch (Exception e) {
        }
    }
    
    // Leer
    public int lee(){
        try {
            return in.read();
        } catch (Exception e) {
        }
        return 0;
    }
}
