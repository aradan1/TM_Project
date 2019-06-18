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

class Pair{

    int x;
    int y;
    Pair(int x, int y){
        this.x=x;
        this.y=y;
    }
}

class Tessela{
    Pair p;
    int w;
    int h;
    
    int col;
    int row;
    Tessela(Pair p, int width, int height){
        this.p=p;
        this.w=width;
        this.h=height;
    }
    
    public void setPos(int c, int r){
        this.col = c;
        this.row = r;
    }
    
}


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
    
    public static BufferedImage testPrev(BufferedImage image, BufferedImage reference, int threshold){
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        for(int x=0; x< image.getWidth();x++){
            for(int y=0; y<image.getHeight(); y++){
                int p = image.getRGB(x,y);
                int a = Math.abs((p>>24)&0xff- (reference.getRGB(x,y)>>24)&0xff);
                int r = Math.abs((p>>16)&0xff- (reference.getRGB(x,y)>>16)&0xff);
                int g = Math.abs((p>>8)&0xff- (reference.getRGB(x,y)>>8)&0xff);
                int b = Math.abs(p&0xff- (reference.getRGB(x,y))&0xff);
                if(a+r+g+b<threshold){
                    p = ((p>>24)&0xff) | (0<<16) | (255<<8) | 255;
                    result.setRGB(x, y, p);
                }
            }
        }
        
        return result;
    }
        
    
    public static String blockSearch(BufferedImage image, BufferedImage reference, int threshold, int numtiles, int seekRange){
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        
        String dades = "";
        int tileW= image.getWidth()/numtiles;
        int tileH= image.getHeight()/numtiles;
        
        for(int x=0; x+tileW< image.getWidth();x+=tileW){
            for(int y=0; y+tileH< image.getHeight(); y+=tileH){
                String temp = motionEstimation(result.getSubimage(x,y,tileW,tileH), reference, threshold, new Tessela(new Pair(x,y), tileW, tileH), seekRange);
            }
        }
        
        return dades;
    }
    
    private static String motionEstimation(BufferedImage tile, BufferedImage reference, int threshold, Tessela coords, int seekRange){
        String result = "";
        Tessela temp;
        
        temp = checkNearTiles(tile, reference, coords, seekRange);
        
        while(temp.row!=seekRange && temp.col!=seekRange){
            
            System.out.println(temp.row+" "+temp.col);
            
            temp=checkNearTiles(tile, reference, temp, seekRange);
        }
        
        int value = tilesMatch(tile, reference.getSubimage(temp.p.x, temp.p.y, temp.w, temp.h));
        
        if(value < threshold){
            result+=""+coords.p.x+" "+coords.p.y+" "+temp.p.x+" "+temp.p.y+"%";
        }
        System.out.println("333");
        return result;
    }
    
    private static Tessela checkNearTiles(BufferedImage image, BufferedImage reference, Tessela coords, int seekRange){
        int range = (seekRange*2)+1;
        int best=-1;
        int valueTemp;
        Tessela result = coords;
        
        int x = coords.p.x-(seekRange*coords.w);
        int y = coords.p.y-(seekRange*coords.h);
        
        if(x<0)
            x=0;
        if(y<0)
            y=0;
        
       for(int i = 0; (i < range) && (x+(i*coords.w))<reference.getWidth(); i++){
           x+=(i*coords.w);
           for(int j = 0; (j < range) && (y+(j*coords.h))<reference.getHeight(); j++){
               y+=(j*coords.h);
               
               
               valueTemp=tilesMatch(image, reference.getSubimage(x, y, coords.w, coords.h));
               if(best<0 || valueTemp<best){
                   best=valueTemp;
                   
                   result.setPos(i, j);
                   result.p.x=x;
                   result.p.y=y;
               }
           }
       }
        
        
        return result;
    }
    
    private static int tilesMatch(BufferedImage image, BufferedImage reference){
        int value = 0;
        
        for(int x=0; x< image.getWidth();x++){
            for(int y=0; y<image.getHeight(); y++){
                int p = image.getRGB(x,y);
                int a = Math.abs((p>>24)&0xff- (reference.getRGB(x,y)>>24)&0xff);
                int r = Math.abs((p>>16)&0xff- (reference.getRGB(x,y)>>16)&0xff);
                int g = Math.abs((p>>8)&0xff- (reference.getRGB(x,y)>>8)&0xff);
                int b = Math.abs(p&0xff- (reference.getRGB(x,y))&0xff);
                value+=r+g+b;
            }
        }
        
        return value;
    }
    
}
