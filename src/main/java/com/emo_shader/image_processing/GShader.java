package com.emo_shader.image_processing;

import java.awt.image.BufferedImage;

import com.emo_shader.ImageHandler;

public abstract class GShader {
    public final ImageHandler imgH;
    public GShader(ImageHandler imgH) {
        this.imgH = imgH;
    }

    public abstract BufferedImage shade();
}
