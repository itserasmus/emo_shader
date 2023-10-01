package com.emo_shader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.emo_shader.image_processing.GShader;

public final class ImageHandler {
    public BufferedImage img;
    public BufferedImage image;
    public GShader shader;

    public void setShader(GShader shader) {
        this.shader = shader;
    }
    
    public void paint(JPanel panel, Graphics2D g) {
        g.setColor(new Color(0x101010));
        g.fillRect(0, 0, panel.getWidth(), panel.getHeight());

        if(img == null) {return;}
        double pW = panel.getWidth();
        double pH = panel.getHeight();
        Rectangle r = new Rectangle(0, 0, (int)(pW*0.47), (int)(pH*0.94));
        double apr = img.getWidth()*1.0/img.getHeight();
        if(apr < r.width*1.0/r.height) {
            int newWidth = (int)(r.height * apr);
            r.width = newWidth;
        } else {
            int newHeight = (int)(r.width / apr);
            r.height = newHeight;
        }
        r.x = (panel.getWidth() / 2 - r.width) / 2;
        r.y = (panel.getHeight() - r.height) / 2;


        
        g.drawImage(this.image, r.x, r.y, r.width, r.height, null);
        g.drawImage(img, r.x + panel.getWidth()/2, r.y, r.width, r.height, null);
    }

    public void shade() {
        img = shader.shade();
    }
}
