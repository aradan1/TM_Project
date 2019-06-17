/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm_proyecto;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 *
 * @author Daniel
 */
public class Filtres {
    
    
    public static BufferedImage binary(BufferedImage image, int thresh){
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        
        for(int y =0; y< image.getHeight(); y++){
            for(int x =0; x< image.getWidth(); x++){
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;
                int grey = (r+g+b)/3;
                if(grey>thresh){// 127 = 255/2
                    p = (a<<24) | (255<<16) | (255<<8) | 255;
                }else{
                    p = (a<<24) | (0<<16) | (0<<8) | 0;
                }
                //p = (a<<24) | (grey<<16) | (grey<<8) | grey;
                result.setRGB(x, y, p);
            }
        }
        
        return result;
    }
    
    
    public static BufferedImage negative(BufferedImage image){
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        
        for(int y =0; y< image.getHeight(); y++){
            for(int x =0; x< image.getWidth(); x++){
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;
                
                p = (a<<24) | (255-r<<16) | (255-g<<8) | 255-b;
                result.setRGB(x, y, p);
            }
        }
        
        return result;
    }
    
    
// matrix 3x3
    public static BufferedImage averaging(BufferedImage image, int boundary){
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        int bUp = (boundary-1)/2;
        int bDown = (boundary)/2;
        for(int y =0; y< image.getHeight(); y++){
            for(int x =0; x< image.getWidth(); x++){
                int x1=x-bUp;
                int y1=y-bUp;
                
                int x2=x+bDown;
                int y2=y+bDown;
                
                if(x1<0)
                    x1=0;
                if(y1<0)
                    y1=0;
                if(x2>= image.getWidth())
                    x2=image.getWidth() -1;
                if(y2>= image.getHeight())
                    y2=image.getHeight() -1;
                
                int p = computeNearAverage(image.getSubimage(x1, y1, x2-x1, y2-y1));
                result.setRGB(x, y, p);
            }
        }
        
        return result;
    }
    
    
    private static int computeNearAverage(BufferedImage image){
        int avgA=0;
        int avgR=0;
        int avgG=0;
        int avgB=0;
        int total =  image.getWidth()*image.getHeight();
        for(int x=0; x< image.getWidth();x++){
            for(int y=0; y<image.getHeight(); y++){
                int p = image.getRGB(x,y);
                avgA+= (p>>24)&0xff;
                avgR+= (p>>16)&0xff;
                avgG+= (p>>8)&0xff;
                avgB+= p&0xff;
            }
        }
        avgA=avgA/total;
        avgR=avgR/total;
        avgG=avgG/total;
        avgB=avgB/total;
        
        return ((avgA<<24) | (avgR<<16) | (avgG<<8) | avgB);
    }
        
}
