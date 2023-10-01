package com.emo_shader;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.emo_shader.image_processing.GShader;
import com.emo_shader.image_processing.emo_shader.EmoShader;

public class Main {
    public static void main(String[] args) {
        final JFrame jf = new JFrame();
        final ImageHandler imgH = new ImageHandler();
        final GShader shader = new EmoShader(imgH);
        final JPanel jp = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                imgH.paint(this, g2);
                g2.dispose();
            }
        };
        imgH.setShader(shader);
        final MyBigAbominationOfAListener listener = new MyBigAbominationOfAListener(jf, jp, imgH);
        
        jf.add(jp);
        jf.addMouseListener(listener);
        jf.setPreferredSize(new Dimension(500, 300));
        jf.pack();
        jp.setDoubleBuffered(false);
        jf.setTitle("Emo Shader");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jp.setVisible(true);
        jf.setVisible(true);
        jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}