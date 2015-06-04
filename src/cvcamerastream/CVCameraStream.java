package cvcamerastream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author sherman
 */
public class CVCameraStream extends JFrame {
    public CVCameraStream() {
        setBounds(0, 0, 640, 480);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ServerSocket ss; 
        Socket s;
        byte[] ipv4 = {(byte) 146, (byte) 244, (byte) 91, (byte) 139};
        int[] idx = {0};  int[] bgr = new int[3];
        byte[][] data = new byte[10][640*480*3];            
        BufferedImage[] buffers = new BufferedImage[10];      
        
        for (int x = 0; x < 10; x++)
                buffers[x] = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
        
        MyPanel panel = new MyPanel(buffers, idx);
        this.add(panel);
        
        try {
            ss = new ServerSocket(27014, 10, InetAddress.getByName("localhost"));
            s = ss.accept();
            DataInputStream dis = new DataInputStream(s.getInputStream());
            
            try {
                while (true) {
                    long timeout = System.currentTimeMillis();
                    while (dis.available() < 1) {
                        if (System.currentTimeMillis() - timeout > Timeout.timeout)
                            throw new Timeout();
                    }
                    System.out.println(dis.available());

                    dis.readFully(data[idx[0]]);                    
                    Graphics2D currentBuffer = buffers[idx[0]].createGraphics();
  
                    for (int x = 0; x < 480; x++)
                        for (int y = 0; y < 640; y++) {
                            for (int z = 0; z < 3; z++) {
                                bgr[z] = data[idx[0]][(x * 3 * 640) + (y * 3) + z];
                                if (bgr[z] < 0)
                                    bgr[z] = 256 - (bgr[z] * -1);
                            }
                            currentBuffer.setColor(new Color(bgr[2], bgr[1], bgr[0]));
                            currentBuffer.fillRect(y, x, 1, 1);
                        }
                    
                    panel.paintComponent(this.getGraphics());
                    if (idx[0] < 9)
                        idx[0]++;
                    else
                        idx[0] = 0;
                } 
            } catch (Timeout e) {
                System.out.println(e.getMessage());
                OutputStream out = s.getOutputStream();
                out.write((new String("close")).getBytes());
            }
        }  catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public class Timeout extends Exception {
        static final long timeout = 10000;
        
        public String getMessage() {
            return "Timeout of " + Long.toString(timeout) + "ms exceeded";
        }
    }
    
    public class MyPanel extends JPanel {
        BufferedImage[] buffer;
        int[] idx;
        public MyPanel (BufferedImage[] buffer, int[] idx) {
            this.buffer = buffer;
            this.idx = idx;
        }
       
        public void paintComponent(Graphics g) {
            if (g == null)
                g = getGraphics();
            
            if (buffer[idx[0]] != null)
                g.drawImage(buffer[idx[0]], 0, 0, null);
        }
    }
    
    public static void main(String[] args) {
        CVCameraStream m = new CVCameraStream();
    }    
}
