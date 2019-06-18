/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm_proyecto;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import java.awt.image.BufferedImage;
import java.util.ArrayList;



/**
 *
 * @author Daniel
 */

class Args {
   @Parameter(names={"--input", "-i"}, required = true)
    static String input;
    @Parameter(names={"--output", "-o"})
    static String output = "output.xdxd";
    @Parameter(names={"--encode", "-e"})
    static boolean encode = false;
    @Parameter(names={"--decode", "-d"})
    static boolean decode = false;
    @Parameter(names={"--fps"})
    static int fps = 30;
    @Parameter(names={"--binarization"})
    static int binarization = -1;
    @Parameter(names={"--negative"})
    static boolean negative = false;
    @Parameter(names={"--averaging"})
    static int averaging = -1;
    @Parameter(names={"--nTiles"})
    static int nTiles=20; // --nTiles <num tesseles, nColumnes, nFiles, ampleTessela, altTessela>
    @Parameter(names={"--seekRange"})
    static int seekRange = 1;
    @Parameter(names={"--GOP"})
    static int GOP = 5;
    @Parameter(names={"--quality"})
    static int quality = 10;
    @Parameter(names={"--batch", "-b"})
    static boolean batch = false;
    
}

public class TM_Proyecto {

    /**
     * @param argv the command line arguments
     */
    public static void main(String[] argv) {
        Args args = new Args();
        JCommander.newBuilder().addObject(args).build().parse(argv);
        
        System.out.printf("%s %s", args.input, args.output);
        
        try {
            
           ArrayList<BufferedImage> images = ZipManager.extractImagesZip(args.input);
           ArrayList<BufferedImage> output = new ArrayList<>();
           
           for(BufferedImage image: images){
               if(args.binarization>-1)
                   image = Filtres.binary(image, args.binarization);
               if(args.negative)
                   image = Filtres.negative(image);
               if(args.averaging>0)
                   image = Filtres.averaging(image, args.averaging);
               
               output.add(image);
           }
           
           String meta="";
           //System.out.println(output.size());
           for(int i = output.size()-1; i>0; i--){
               if(args.GOP*(i/args.GOP)!=i){
                    meta+=""+i+"to"+(i-1)+Filtres.blockSearch(output.get(i), output.get(i-1), args.quality, args.nTiles, args.seekRange)+"#\n";
               }
           }
           System.out.println(meta);
           System.out.println("done");
           
           if(!args.batch){
                Reproductor.reproducirImagenes(output, args.fps);
           }
           
           ZipManager.imagesToFolder(output, "test2");
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
    }

    
    
    
    
    
    
    
    
    
   
}
