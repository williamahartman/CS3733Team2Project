package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Elizabeth on 12/12/2015.
 */
public class SplashScreen extends JWindow{
    public SplashScreen (String fileName, Frame frame, int pauseTime){
        super(frame);
        JLabel label = new JLabel(new ImageIcon(fileName));
        getContentPane().add(label, BorderLayout.CENTER);
        pack();
        Dimension splashSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = label.getPreferredSize();
        setLocation(splashSize.width / 2 - (labelSize.width / 2),
                splashSize.height / 2 - (labelSize.height / 2));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                setVisible(false);
                dispose();
            }
        });

        final int pause = pauseTime;
        final Runnable closeRunner = new Runnable() {
            //@Override
            public void run() {
                setVisible(false);
                dispose();
            }
        };

        Runnable waitRunner = new Runnable() {
            //@Override
            public void run() {
                try {
                    Thread.sleep(pause);
                    SwingUtilities.invokeAndWait(closeRunner);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        setVisible(true);
        Thread splashScreenThread = new Thread(waitRunner, "SplashScreenThread");
        splashScreenThread.start();
    }
}
