/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm_proyecto;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Daniel
 */
public class Reproductor {
    
    private static boolean next = true;
    
    public static void reproducirImagenes(ArrayList<BufferedImage> images, int fps) throws InterruptedException{
        
        Visor v = new Visor(); // Creamos la ventana vacia
        
        Timer timer = new Timer();
        
        TimerTask task = new TimerTask() {
            @Override
            public void run() {  
                Reproductor.next=true;
            }
        };
        
        // Empezamos dentro de 0ms y luego lanzamos la tarea cada 1000ms/ numero de imagenes por segundo
        long period = (long)1000/fps;
        
        timer.schedule(task, 0, period);
        
        for(BufferedImage image : images){
            while(!next){Thread.sleep((long)0.001);}    // Un poco feo pero funciona
            v.setImage(image);
            v.changeFrame();
            Reproductor.next=false;
        }
                
        timer.cancel(); // Paramos el schedule
        v.dispose(); // Cerramos la ventana
    }
}
