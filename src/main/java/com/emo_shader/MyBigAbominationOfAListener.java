package com.emo_shader;

import java.awt.FileDialog;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public final class MyBigAbominationOfAListener implements java.awt.event.MouseListener {
    public final ImageHandler imgH;
    public final JFrame frame;
    public final JPanel panel;

    public MyBigAbominationOfAListener(JFrame frame, JPanel panel, ImageHandler imageHandler) {
        this.frame = frame;
        this.panel = panel;
        this.imgH = imageHandler;
    }

    @Override
    public void mouseClicked(MouseEvent ev) {
        FileDialog fileDialog = new FileDialog(frame, "Select Image", FileDialog.LOAD);
        fileDialog.setVisible(true);
        
        String directory = fileDialog.getDirectory();
        String fileName = fileDialog.getFile();

        if (directory != null && fileName != null) {
            String filePath = directory + fileName;
            try {
                BufferedImage image = ImageIO.read(new File(filePath));
                if(image == null) {return;}
                imgH.image = image;
                while(imgH.image.getWidth() == -1) {wait(1);}
                imgH.shade();
                panel.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    
}
