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
    static int nTiles=20;
    @Parameter(names={"--seekRange"})
    static int seekRange =1;
    @Parameter(names={"--GOP"})
    static int GOP = 5;
    @Parameter(names={"--quality"})
    static int quality = 10;
    @Parameter(names={"--mode"})
    static int mode = 1;
    @Parameter(names={"--batch", "-b"})
    static boolean batch = false;
    @Parameter(names={"--test", "-t"})
    static boolean test = false;
    
}

public class TM_Proyecto {

    /**
     * @param argv the command line arguments
     */
    public static void main(String[] argv) {
        Args args = new Args();
        JCommander.newBuilder().addObject(args).build().parse(argv);
        
        // este input mejor que no pueda ser modificado desde fuera, ya que cambiara en caso de querer hacer encode y decode del resultado
        String input = Args.input;
        ZipData data;
        
         try {
           
           long startTime = System.currentTimeMillis();
           
           if(Args.encode){
               // descomprimimos las imagenes
                data = ZipManager.extractImagesZip(input);
                ArrayList<BufferedImage> images = data.getImages();
                ArrayList<BufferedImage> output = new ArrayList<>();
                for(BufferedImage image: images){
                    // aplicamos el banco de filtros a las imagenes recuperadas
                    if(Args.binarization>-1)
                        image = Filtres.binary(image, Args.binarization);
                    if(Args.negative)
                        image = Filtres.negative(image);
                    if(Args.averaging>0)
                        image = Filtres.averaging(image, Args.averaging);

                    output.add(image);
                }
                   
                if(!Args.test){
                    // ******** Only for testing **********
                    // Imagenes sin comprimir directas en jpg
                    System.out.println();
                    ZipManager.imagesToZip(output, "",  "test_no_encoded.zip");
                    System.out.println("Saved to test_no_encoded.zip");
                    System.out.printf("%.3f MB\n",new File("test_no_encoded.zip").length()/1000000f);
                    System.out.println();
                    // ************************************
                }
                   
                // encontramos las teselas que nos podemos ahorrar y las anotamos en el string
                String meta=MotionEstimation.motionEncode(output);
                // las guardamos en un zip y mostramos su tamaño
                ZipManager.imagesToZip(output, meta, Args.output);
                System.out.println();
                System.out.println("Saved to "+Args.output);
                System.out.printf("%.3f MB\n", new File(Args.output).length()/1000000f);
                System.out.println();
                
                if(!Args.batch){
                    // thread reproductor del encode
                    Thread t1 = new Thread(new Reproductor(output, Args.fps));
                    t1.start();
                    //Reproductor.reproducirImagenes(output, Args.fps);
               }
                
                // por si se elige la opcion encode+decode
                input = Args.output;
           
           }
           
           
           
           
           if(Args.decode){
               // cargamos las imagenes y el fichero de metadatos
                data = ZipManager.extractImagesZip(input);
                // recuperamos la imagen original
                MotionEstimation.motionDecode(data.getImages(), data.getMetadata());
                System.out.println();
                
                if(!Args.batch){
                    // thread del reproductor del decode
                    Thread t2 = new Thread(new Reproductor(data.getImages(), Args.fps));
                    t2.start();
                    //Reproductor.reproducirImagenes(data.getImages(), Args.fps);
               }
               
                if(!Args.test){
                    // ******** Only for testing **********
                    // guardamos el resultado del decode en un zip
                    ZipManager.imagesToZip(data.getImages(), data.getMetadata(), "decoded.zip");
                    // ************************************
                }
           }
           
           
           // contamos el tiempo transcurrido de inicio a fin de la ejecucion
           long endTime = System.currentTimeMillis();
           long duration = (endTime - startTime)/1000;//    /1000 para pasar de milis a segundos
           int seconds = (int) (duration) % 60;
           int minutes = (int) (duration / 60);
            System.out.printf("Timpo transcurrido: %02d min %02d sec\n", minutes, seconds);
           
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
    }

}
