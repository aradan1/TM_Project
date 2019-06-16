/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm_proyecto;

import java.awt.image.BufferedImage;
import java.util.ArrayList;



/**
 *
 * @author Daniel
 */
public class TM_Proyecto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
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
