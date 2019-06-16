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

class Arguments {
   @Parameter(names={"--input", "-i"})
    static int input;
    @Parameter(names={"--output", "-o"})
    static int output;
    @Parameter(names={"--encode", "-e"})
    static int encode;
    @Parameter(names={"--decode", "-d"})
    static int decode;
    @Parameter(names={"--fps"})
    static int fps;
    @Parameter(names={"--binarization"})
    static int binarization;
    @Parameter(names={"--negative"})
    static int negative;
    @Parameter(names={"--averaging"})
    static int averaging;
    @Parameter(names={"--nTiles"})
    static int nTiles; // --nTiles <value,...>
    @Parameter(names={"--seekRange"})
    static int seekRange;
    @Parameter(names={"--GOP"})
    static int GOP;
    @Parameter(names={"--quality"})
    static int quality;
    @Parameter(names={"--batch", "-b"})
    static int batch;
    
}

public class TM_Proyecto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        JCommander.newBuilder()
            .addObject(arguments)
            .build()
            .parse(args);
        
        System.out.printf("%d %d", arguments.input, arguments.output);
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
