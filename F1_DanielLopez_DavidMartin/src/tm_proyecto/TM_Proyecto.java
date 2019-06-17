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
    static String output = "test";
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
    static int nTiles; // --nTiles <num tesseles, nColumnes, nFiles, ampleTessela, altTessela>
    @Parameter(names={"--seekRange"})
    static int seekRange = -1;
    @Parameter(names={"--GOP"})
    static int GOP = 3;
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
        
        System.out.printf("%d %d", args.input, args.output);
        
        try {
            
           ArrayList<BufferedImage> images = ZipManager.extractImagesZip("Cubo.zip");
           ArrayList<BufferedImage> negative = new ArrayList<>();
           ArrayList<BufferedImage> binary = new ArrayList<>();
           ArrayList<BufferedImage> average = new ArrayList<>();
           for(BufferedImage image: images){
               negative.add(Filtres.negative(image));
               binary.add(Filtres.binary(image));
               average.add(Filtres.averaging(image));
               
           }    
           Reproductor.reproducirImagenes(images, 60);
           Reproductor.reproducirImagenes(negative, 60);
           Reproductor.reproducirImagenes(binary, 60);
           Reproductor.reproducirImagenes(average, 60);
           
           ZipManager.unzipTo("Cubo.zip", "prueba");
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
    }

    
    
    
    
    
    
    
    
    
   
}
