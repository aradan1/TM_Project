/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm_proyecto;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;



class Pair{

    int x;
    int y;
    Pair(int x, int y){
        this.x=x;
        this.y=y;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
        this.row = r;
        this.col = c;
    }
    
    public int getX() {
        return p.x;
    }
    
    public int getY() {
        return p.y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}

public class MotionEstimation {
    public static String blockSearch(BufferedImage image, BufferedImage reference, int threshold, int numtiles, int seekRange){
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        
        String dades = "";
        int tileW= (image.getWidth()-1)/numtiles;
        int tileH= (image.getHeight()-1)/numtiles;
        for(int x=0; x+tileW< image.getWidth(); x+=tileW){
            for(int y=0; y+tileH< image.getHeight(); y+=tileH){
                
                String temp = motionEstimation(result.getSubimage(x,y,tileW,tileH), reference, threshold, new Tessela(new Pair(x,y), tileW, tileH), seekRange);
                
                dades+=temp;
            }
        }
        
        return dades;
    }
    
    private static String motionEstimation(BufferedImage tile, BufferedImage reference, int threshold, Tessela coords, int seekRange){
        String result = "";
        Tessela temp;
        
        temp = checkNearTiles(tile, reference, coords, seekRange);
        
        while(temp.row!=seekRange || temp.col!=seekRange){
            
            temp=checkNearTiles(tile, reference, temp, seekRange);
        }
        
        int value = tilesMatch(tile, reference.getSubimage(temp.p.x, temp.p.y, temp.w, temp.h));
        
        if(value < threshold){
            result+=""+coords.getX()+" "+coords.getY()+" "+temp.getX()+" "+temp.getX()+"%";
        }
        System.out.println("333");
        return result;
    }
    
    private static Tessela checkNearTiles(BufferedImage image, BufferedImage reference, Tessela coords, int seekRange){
        int range = (seekRange*2)+1;
        int best=-1;
        int valueTemp;
        Tessela result = new Tessela(new Pair(coords.getX(), coords.getY()), coords.getW(), coords.getH());
        
        int x = coords.getX()-(seekRange*coords.getW());
        int y = coords.getY()-(seekRange*coords.getH());
        
       for(int i = 0; i < range; i++){
           x+=(i*coords.getW());
           
        System.out.println(x+" "+coords.getW());
        
           if(x>=0 && x+coords.getW()<reference.getWidth()){
               
                for(int j = 0; j < range; j++){
                    y+=(j*coords.getH());
                    if(y>=0 && y+coords.getH()<reference.getHeight()){
                        
                        System.out.println("LOL");
                        
                         valueTemp=tilesMatch(image, reference.getSubimage(x, y, coords.getW(), coords.getH()));
                         if(best<0 || valueTemp<best){
                             best=valueTemp;
                             
                             result.setPos(i, j);
                             result.p.x=x;
                             result.p.y=y;
                             
                         }
                    }
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
