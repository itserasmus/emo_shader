package com.emo_shader.image_processing.emo_shader;

import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;

// import javax.imageio.ImageIO;

import com.emo_shader.ImageHandler;
import com.emo_shader.image_processing.GShader;

public final class EmoShader extends GShader {
    public final double brightnessThresholdRatio = 4.18;
    public final double brightnessRadius = 0.5;
    
    public final int contrastThreshold = 165;
    public final double contrastRadius = 0.5;
    
    public final double blurRadRatio = 0.1;
    public final double regionalBrightnessSensitivity = 0.85;

    public final boolean invertOutline = false;
    
    public EmoShader(ImageHandler imgH) {
        super(imgH);
    }
    public BufferedImage shade() {
        long start = System.nanoTime();
        final BufferedImage img = imgH.image;
        final int width = img.getWidth();
        final int height = img.getHeight();
        final int[][] imgArr = new int[width][height];
        int i, j;
        int totalR = 0, totalG = 0, totalB = 0, count = 0;
        int r, g, b, c;
        int dx, ix;

        for(i = 0; i < width; i++) {
            for(j = 0; j < height; j++) {
                imgArr[i][j] = img.getRGB(i, j);
            }
        }

        final boolean[][] step1Arr = new boolean[width][height];
        final boolean[][] step2Arr = new boolean[width][height];

        final int[][] grayscale = new int[width][height];
        for(i = 0; i < width; i++) {
            for(j = 0; j < height; j++) {
                c = imgArr[i][j];
                grayscale[i][j] = (((c >>> 16) & 0xFF) + ((c >>> 8) & 0xFF) + ((c >>> 0) & 0xFF))/3;
            }
        }
        final int[][] brightnessMask = blur(grayscale);

        final BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        final int white = 0xFFFFFFFF;
        final int black = 0xFF000000;
        double curr;
        double delta;
        double factor;
        int mr, mg, mb;
        int xr, xg, xb;

        for(i = 0; i < width; i++) {
            for(j = 0; j < height; j++) {
                c = imgArr[i][j];
                totalR += ((c >>> 16) & 0xFF);
                totalG += ((c >>> 8) & 0xFF);
                totalB += ((c >>> 0) & 0xFF);
            }
        }
        final int brightnessThreshold = (int)((totalR + totalG + totalB) * brightnessThresholdRatio / (width*height*3.0));

        for(i = 0; i < width; i++) {
            for(j = 0; j < height; j++) {
                step1Arr[i][j] = false;
                curr = brightnessRadius;
                totalR = 0;
                totalG = 0;
                totalB = 0;
                count = 0;
                for(; curr <= brightnessRadius; curr++) {
                    factor = 1.0/Math.sqrt(curr);
                    for(delta = -curr; delta <= 0; delta++) {
                        dx = (int)Math.sqrt(curr*curr - delta * delta);
                        for (ix = -dx; ix <= dx; ix++) {
                            if(isInbetween(0, width-1, i+ix) && isInbetween(0, height-1, j+delta)) {
                                c = imgArr[i+(int)ix][j+(int)delta];
                                r = ((c >>> 16) & 0xFF);
                                g = ((c >>> 8) & 0xFF);
                                b = ((c >>> 0) & 0xFF);
                                totalR += r*factor;
                                totalG += g*factor;
                                totalB += b*factor;
                                count++;
                            }
                        }
                    }
                }
                if(totalR + totalG + totalB > brightnessThreshold * count * 3 * (1-regionalBrightnessSensitivity+brightnessMask[i][j]/256d*regionalBrightnessSensitivity)) {
                    step1Arr[i][j] = true;
                }
            }
        }
        for(i = 0; i < width; i++) {
            for(j = 0; j < height; j++) {
                step2Arr[i][j] = step1Arr[i][j];
                curr = contrastRadius;
                totalR = 0;
                totalG = 0;
                totalB = 0;
                perPixelFor: 
                for(; curr <= contrastRadius; curr++) {
                    factor = 1.0/Math.sqrt(curr);
                    for(delta = -curr; delta <= 0; delta++) {
                        dx = (int)Math.sqrt(curr*curr - delta * delta);
                        mr = 255; mg = 255; mb = 255;
                        xr = 0; xg = 0; xb = 0;
                        for (ix = -dx; ix <= dx; ix++) {
                            if(isInbetween(0, width-1, i+ix) && isInbetween(0, height-1, j+delta)) {
                                c = imgArr[i+(int)ix][j+(int)delta];
                                
                                r = ((c >>> 16) & 0xFF);
                                g = ((c >>> 8) & 0xFF);
                                b = ((c >>> 0) & 0xFF);
                                mr = Math.min(mr, r);
                                mg = Math.min(mr, g);
                                mb = Math.min(mr, b);
                                xr = Math.max(xr, r);
                                xg = Math.max(xr, g);
                                xb = Math.max(xr, b);
                                if(xr+xg+xb - mr-mg-mb > contrastThreshold) {
                                    step2Arr[i][j] = !(step2Arr[i][j] && invertOutline);
                                    break perPixelFor;
                                }
                                totalR += r*factor;
                                totalG += g*factor;
                            }
                        }
                    }
                }
            }
        }
        
        for(i = 0; i < width; i++) {
            for(j = 0; j < height; j++) {
                dest.setRGB(i, j, step2Arr[i][j]?white:black);
                // dest.setRGB(i, j, 0xFF000000 + 0x00010101*brightnessMask[i][j]);
            }
        }
        
        
        System.out.println("done: " + (System.nanoTime() - start));

        // File output = new File("output");

        // try {
        //     ImageIO.write(dest, "png", output);
        //     System.out.println("Image saved successfully.");
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        return dest;
    }

    public boolean isInbetween(double min, double max, double val) {
        return min <= val && val <= max;
    }

    public int[][] blur(int[][] arr) {
        final int width = arr.length;
        final int height = arr[0].length;
        final int[][] blurred1 = new int[width][height];
        final int[][] blurred2 = new int[width][height];

        final int blurRad = (int)((width+height)*0.5*blurRadRatio);

        final double[] cachedExpensiveStuff = new double[2*blurRad+1];

        double tot;
        int count;
        int upperBound;
        int i;
        int j;
        int delta;

        for(i = -blurRad; i <= blurRad; i++) {
            cachedExpensiveStuff[i+blurRad] = exp(-(i*i*1.0)/(blurRad*blurRad));
        }

        for(i = 0; i < arr.length;i++) {
            for(j = 0; j < height; j++) {
                count = 0;
                tot = 0;
                upperBound = Math.min(blurRad, width-i-1);
                for(delta = Math.max(-blurRad, -i); delta <= upperBound; delta++) {
                    count++;
                    tot += arr[i+delta][j]*cachedExpensiveStuff[delta+blurRad];
                }
                blurred1[i][j] = (int)(tot/count);
            }
        }

        for(i = 0; i < arr.length; i++) {
            for(j = 0; j < height; j++) {
                count = 0;
                tot = 0;
                upperBound = Math.min(blurRad, height-j-1);
                for(delta = Math.max(-blurRad, -j); delta <= upperBound; delta++) {
                    count++;
                    tot += blurred1[i][j+delta]*cachedExpensiveStuff[delta+blurRad];
                }
                blurred2[i][j] = (int)(tot/count);
            }
        }

        return blurred2;
    }

    public double exp(double x) {
        return Math.exp(x);
    }
}
