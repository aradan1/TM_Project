/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm_proyecto;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;



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
        int tileW= (image.getWidth())/numtiles;
        int tileH= (image.getHeight())/numtiles;
        for(int x=0; x+tileW< image.getWidth()+1; x+=tileW){
            for(int y=0; y+tileH< image.getHeight()+1; y+=tileH){
                
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
        int timeout = 0;
        while((temp.row!=seekRange || temp.col!=seekRange) && (timeout<100)){

            temp=checkNearTiles(tile, reference, temp, seekRange);
            timeout++;
        }

        int value = tilesMatch(tile, reference.getSubimage(temp.getX(), temp.getY(), temp.getW(), temp.getH()));

        if((value < threshold) && (timeout<100)){
            result+=""+coords.getX()+" "+coords.getY()+" "+temp.getX()+" "+temp.getX()+"%";
        }
        System.out.println("333");
        return result;
    }
    
    private static String motionEstimationDiam(BufferedImage tile, BufferedImage reference, int threshold, Tessela coords, int seekRange){
        String result = "";
        Tessela prev = null;
        
        // Compute large diamond
        Tessela temp = minLargeDiamond(tile, coords, seekRange);
        while(prev != temp){
            prev = temp;
            temp = minLargeDiamond(tile, prev, seekRange);
            
        }
        // Compute little diamond
        temp = minLittleDiamond(tile, coords, seekRange);
        prev=null;
        while(prev != temp){
            prev = temp;
            temp = minLittleDiamond(tile, prev, seekRange);
            
        }
        
        int value = tilesMatch(tile, reference.getSubimage(temp.getX(), temp.getY(), temp.getW(), temp.getH()));
        
        if((value < threshold)){
            result+=""+coords.getX()+" "+coords.getY()+" "+temp.getX()+" "+temp.getX()+"%";
        }
        
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
        
           if(x>=0 && x+coords.getW()<=reference.getWidth()){
               
                for(int j = 0; j < range; j++){
                    if(y>=0 && y+coords.getH()<=reference.getHeight()){
                        
                         valueTemp=tilesMatch(image, reference.getSubimage(x, y, coords.getW(), coords.getH()));
                         if(best<0 || valueTemp<best){
                             best=valueTemp;
                             
                             result.setPos(i, j);
                             result.p.x=x;
                             result.p.y=y;
                             
                         }
                    }
                    
                    y+=(coords.getH());
                }
           }
           
           x+=(coords.getW());
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
    
    public static Tessela minLargeDiamond(BufferedImage image, Tessela t, int seekRange){

        Map<Tessela, Integer> largeDiamond = new HashMap<>();
        BufferedImage temp;
        int calc;
        Tessela tesTemp;
        int sx = seekRange*t.w;
        int sy = seekRange*t.h;
        // X+2S Y
        tesTemp = new Tessela(new Pair(t.p.x+2*sx, t.p.y), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            largeDiamond.put(tesTemp, calc);
        }

        // X-2S Y
        tesTemp = new Tessela(new Pair(t.p.x-2*sx, t.p.y), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            largeDiamond.put(tesTemp, calc);
        }


        // X Y+2S
        tesTemp = new Tessela(new Pair(t.p.x, t.p.y+2*sy), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            largeDiamond.put(tesTemp, calc);
        }

        // X Y-2S
        tesTemp = new Tessela(new Pair(t.p.x, t.p.y-2*sy), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            largeDiamond.put(tesTemp, calc);
        }

        // X+S Y+S
        tesTemp = new Tessela(new Pair(t.p.x+sx, t.p.y+sy), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            largeDiamond.put(tesTemp, calc);
        }

        // X-S Y-S
        tesTemp = new Tessela(new Pair(t.p.x-sx, t.p.y-sy), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            largeDiamond.put(tesTemp, calc);
        }

        // X-S Y+S
        tesTemp = new Tessela(new Pair(t.p.x-sx, t.p.y+sy), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            largeDiamond.put(tesTemp, calc);
        }

        // X+S Y-S
        tesTemp = new Tessela(new Pair(t.p.x+sx, t.p.y-sy), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            largeDiamond.put(tesTemp, calc);
        }
        // Center xy as first min value
        temp = image.getSubimage(t.p.x, t.p.y, t.w, t.h);
        Tessela minTessela = t;
        int minValue = tilesMatch(image, temp);
        
        largeDiamond.put(minTessela, minValue);
        
        // Get minimum value
        for(Map.Entry<Tessela, Integer> entry : largeDiamond.entrySet()) {
            Tessela key = entry.getKey();
            int value = entry.getValue();
            if (value < minValue){
                minValue = value;
                minTessela = key;
            }
            System.out.println("2");
        }

        return minTessela;
    }


    public static Tessela minLittleDiamond(BufferedImage image, Tessela t, int seekRange){
        Map<Tessela, Integer> littleDiamond = new HashMap<>();
        BufferedImage temp;
        int calc;
        Tessela tesTemp;
        int sx = (seekRange/2)*t.w;
        int sy = (seekRange/2)*t.h;
        // X+S Y
        tesTemp = new Tessela(new Pair(t.p.x+sx, t.p.y), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            littleDiamond.put(tesTemp, calc);
        }

        // X-S Y
        tesTemp = new Tessela(new Pair(t.p.x-sx, t.p.y), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            littleDiamond.put(tesTemp, calc);
        }


        // X Y+S
        tesTemp = new Tessela(new Pair(t.p.x, t.p.y+sy), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            littleDiamond.put(tesTemp, calc);
        }

        // X Y-S
        tesTemp = new Tessela(new Pair(t.p.x, t.p.y-sy), t.w, t.h);
        if (tesTemp.p.y >= 0 && tesTemp.p.x >= 0 && tesTemp.p.x*t.w <= image.getWidth() && tesTemp.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(tesTemp.p.x, tesTemp.p.y, tesTemp.w, tesTemp.h);
            calc = tilesMatch(image, temp);
            littleDiamond.put(tesTemp, calc);
        }



        // Center xy as first min value
        temp = image.getSubimage(t.p.x, t.p.y, t.w, t.h);
        Tessela minTessela = t;
        int minValue = tilesMatch(image, temp);
        
        littleDiamond.put(minTessela, minValue);

        // Get minimum value
        for(Map.Entry<Tessela, Integer> entry : littleDiamond.entrySet()) {
            Tessela key = entry.getKey();
            int value = entry.getValue();

            if (value < minValue){
                minValue = value;
                minTessela = key;
            }
        }

        return minTessela;
    }
}
