package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Scott on 12/6/2015.
 */
public class ImageFromMap {

    public ImageFromMap(){

    }

    public void saveComponentAsJPEG(Component myComponent, String filename) {
        Dimension size = myComponent.getSize();
        BufferedImage myImage = new BufferedImage((int) size.getWidth(),
                (int) size.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g2 = myImage.createGraphics();
        g2.drawImage(myImage,  (int) size.getWidth(), (int) size.getHeight(), null);
        myComponent.paint(g2);
        try {
            ImageIO.write(myImage, "JPEG", new File(filename));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
