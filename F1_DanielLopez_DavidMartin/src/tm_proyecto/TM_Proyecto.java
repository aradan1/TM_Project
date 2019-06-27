/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm_proyecto;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import java.awt.image.BufferedImage;
import java.io.File;
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
    static int seekRange =1;
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
        
        System.out.printf("%s %s\n", Args.input, Args.output);
        
        try {
            ZipData data = ZipManager.extractImagesZip(Args.input);
           ArrayList<BufferedImage> images = data.getImages();
           ArrayList<BufferedImage> output = new ArrayList<>();
           for(BufferedImage image: images){
               if(Args.binarization>-1)
                   image = Filtres.binary(image, Args.binarization);
               if(Args.negative)
                   image = Filtres.negative(image);
               if(Args.averaging>0)
                   image = Filtres.averaging(image, Args.averaging);
               
               output.add(image);
           }
           
           // Imagenes sin comprimir directas en jpg
           ZipManager.imagesToZip(output, "",  "test_no_encoded.zip");
           System.out.println("Saved to test_no_encoded.zip");
           
           
           String meta="";
           for(int i = output.size()-1; i>0; i--){
                if(Args.GOP*(i/Args.GOP)!=i){
                    // estructura: num image+info tessela
                     meta+=""+i+MotionEstimation.blockSearch(output.get(i), output.get(i-1), Args.quality, Args.nTiles, Args.seekRange)+"#";
                     
                    System.out.println("Imagen "+i+" completada");
                }
            }
           
           
           /////////////     NO CONSEGUIMOS QUE FUNCIONE BIEN
           if(Args.encode){

           
           }
           
           if(Args.decode){
               
               
           }
           
           if(!Args.batch){
                Reproductor.reproducirImagenes(output, Args.fps);
           }
           
           
           
           ZipManager.imagesToZip(output, meta, "test_encoded.zip");
           System.out.println("Saved to test_encoded.zip");
           
           data = ZipManager.extractImagesZip("test_encoded.zip");
           System.out.println(data.getMetadata().equals(meta));
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
    }

}
