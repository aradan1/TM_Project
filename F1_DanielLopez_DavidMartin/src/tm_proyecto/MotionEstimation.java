/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm_proyecto;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
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
    
   public static void motionDecode(ArrayList<BufferedImage> images, String metadata){
       char c;
       int i = 0;
       c = metadata.charAt(i);
       
       int width=-1;
       int height=-1;
       String temp="";
       
       while(c != '#'){
           if(c == ' '){
               width=Integer.parseInt(temp);
               temp = "";
           }else{
               temp+=c;
           }
           i++;
           c=metadata.charAt(i);
       }
       height = Integer.parseInt(temp);
       
       
       // donde se copia
       Pair copy = new Pair(0,0);
       
       // donde se pega
       Pair paste = new Pair(0,0);
       
       if(width == -1 || height == -1){
           System.out.println("Error: "+width+"x"+height);
           return ;
       }
       
       temp = "";
       
       // la pagina 0 nunca estara codificada
       int page=1;
       
        i++;
       // hay que leer el string char a char y recrear las imagenes originales, despues pasarle un filtro de suavizado (near average)
       while(i < metadata.length() && page<images.size()){
           
            c=metadata.charAt(i);
            
           // Siguiente pagina
            if(c=='#'){
                //images.set(page, Filtres.averaging(images.get(page), 2));
                System.out.println("Image "+page+" decoded");
                page++;
            }
            // 1r valor, x de la tesela cambiada
            else if(c=='a'){
                paste.x = Integer.parseInt(temp);
                temp = "";
            // 2o valor, y de la tesela cambiada
            }else if(c=='b'){
                paste.y = Integer.parseInt(temp);
                temp = "";
            // 3r valor, x de la tesela referencia
            }else if(c=='c'){
                copy.x = Integer.parseInt(temp);
                temp = "";
            
            // Ultimo numero entrado, recreamos tesela
            }else if(c=='d'){
                copy.y = Integer.parseInt(temp);
                temp = "";
                
                for(int x = 0; x<width; x++){
                    for(int y = 0; y<height; y++){
                        // tenemos las coordenadas, hay que meter la tessela de page-1 (copy) en page (paste)
                        images.get(page).setRGB(paste.x+x, paste.y+y, images.get(page-1).getRGB(copy.x+x, copy.y+y));
                    }
                }
            }else{
                temp+=c;
            }
            
            i++;
       }
   }
    
    public static String motionEncode(ArrayList<BufferedImage> images){
        
        String result="";
        
        
        for(int i = images.size()-1; i>0; i--){
            String temp = "#";
            if(i%Args.GOP != 0){
                temp=blockSearch(images.get(i), images.get(i-1), Args.quality, Args.nTiles, Args.seekRange, Args.mode)+temp;
                System.out.println("Image "+i+" encoded");
            }
                // estructura: num image+info tessela
                result=""+temp+result;
                     
        }
        
        // Asumimos que todas las imagenes son del mismo tamaño
        result=(images.get(0).getWidth()/Args.nTiles)+" "+(images.get(0).getHeight()/Args.nTiles)+"#"+result;
        
        return result;
    } 
    
    
    public static String blockSearch(BufferedImage image, BufferedImage reference, int threshold, int numtiles, int seekRange, int mode){
        /*
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        */
        String temp;
        String dades = "";
        int tileW= (image.getWidth())/numtiles;
        int tileH= (image.getHeight())/numtiles;
        for(int x=0; x+tileW< image.getWidth()+1; x+=tileW){
            for(int y=0; y+tileH< image.getHeight()+1; y+=tileH){
                
                if(mode==1){
                        temp = motionEstimationDiam(image.getSubimage(x,y,tileW,tileH), reference, threshold, new Tessela(new Pair(x,y), tileW, tileH), seekRange);
                }else{
                        temp = motionEstimation(image.getSubimage(x,y,tileW,tileH), reference, threshold, new Tessela(new Pair(x,y), tileW, tileH), seekRange);
                }
                
                if(!temp.isEmpty()){
                    int tR=0;
                    int tB=0;
                    int tG=0;
                    int tA=0;
                    for(int j =y; j< y+tileH; j++){
                        for(int i =x; i< x+tileW; i++){
                            int p = image.getRGB(i,j);
                            int a = (p>>24)&0xff;
                            int r = (p>>16)&0xff;
                            int g = (p>>8)&0xff;
                            int b = p&0xff;
                            tR+=r;
                            tB+=b;
                            tG+=g;
                            tA+=a;
                        }
                    }
                    tR/=(tileW*tileH);
                    tB/=(tileW*tileH);
                    tG/=(tileW*tileH);
                    tA/=(tileW*tileH);
                    
                    int p= (tA<<24) | (tR<<16) | (tG<<8) | tB;
                    //int p= (tA<<24) | (255<<16) | (255<<8) | 255;
                    
                    for(int j =y; j< y+tileH; j++){
                        for(int i =x; i< x+tileW; i++){
                            image.setRGB(i, j, p);
                        }
                    }
                }
                
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
        while((temp.row!=seekRange || temp.col!=seekRange) && (timeout<5)){

            temp=checkNearTiles(tile, reference, temp, seekRange);
            timeout++;
        }

        int value = tilesMatch(tile, reference.getSubimage(temp.getX(), temp.getY(), temp.getW(), temp.getH()));

        if(value < threshold){
            result+=""+coords.getX()+"a"+coords.getY()+"b"+temp.getX()+"c"+temp.getY()+"d";
            
        }
        return result;
    }
    
    private static String motionEstimationDiam(BufferedImage tile, BufferedImage reference, int threshold, Tessela coords, int seekRange){
        String result = "";
        Tessela prev = coords;
        seekRange=2;
        Tessela temp;
        // Compute large diamond
        while ((seekRange % 2) == 0){
            temp = minLargeDiamond(tile, prev, seekRange);
            while(!prev.equals(temp)){
                prev = temp;
                temp = minLargeDiamond(tile, prev, seekRange);

            }
            seekRange = seekRange/2;
        }
        // Compute little diamond
        // Prev is the result from the large diamond 
        // or the initial block if the seekRange is 1
        temp = minLittleDiamond(tile, prev);
        while(prev != temp){
            prev = temp;
            temp = minLittleDiamond(tile, prev);
            
        }
        
        int value = tilesMatch(tile, reference.getSubimage(temp.getX(), temp.getY(), temp.getW(), temp.getH()));
        
        if((value < threshold)){
            result+=""+coords.getX()+"a"+coords.getY()+"b"+temp.getX()+"c"+temp.getY()+"d";
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
        BufferedImage temp = null;
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
        if (t.p.y >= 0 && t.p.x >= 0 && t.p.x*t.w <= image.getWidth() && t.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(t.p.x, t.p.y, t.w, t.h);
            calc = tilesMatch(image, temp);
            largeDiamond.put(t, calc);
        }
        
        Tessela minTessela = t;
        int minValue = 999999999;
        
        // Get minimum value
        for(Map.Entry<Tessela, Integer> entry : largeDiamond.entrySet()) {
            Tessela key = entry.getKey();
            int value = entry.getValue();
            if (value < minValue){
                minValue = value;
                minTessela = key;
            }
            //System.out.println("2");
        }

        return minTessela;
    }


    public static Tessela minLittleDiamond(BufferedImage image, Tessela t){
        Map<Tessela, Integer> littleDiamond = new HashMap<>();
        BufferedImage temp;
        int calc;
        Tessela tesTemp;
        int sx = t.w;
        int sy = t.h;
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
        if (t.p.y >= 0 && t.p.x >= 0 && t.p.x*t.w <= image.getWidth() && t.p.y*t.h <= image.getHeight()){
            temp = image.getSubimage(t.p.x, t.p.y, t.w, t.h);
            calc = tilesMatch(image, temp);
            littleDiamond.put(t, calc);
        }
        
        Tessela minTessela = t;
        int minValue = 999999999;

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
