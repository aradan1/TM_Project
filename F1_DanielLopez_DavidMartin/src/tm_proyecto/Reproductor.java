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
 * Controlador del reproductor de vídeo, 
 * administra cuando tiene que cambiar los frames del visor
 * y actualiza la imagen que tiene que mostrar
 */
public class Reproductor {
    
    private static boolean next = true;
    
    /**
     * Recibe todas las imagenes y cuantos fps tendrá el video
     * Crea un timer para poder administrar cada cuanto tiene que enviar un frame.
     * Llamará a los métodos de la clase Visor para actualizar la imagen en cada frame
     * @param images
     * @param fps
     * @throws InterruptedException 
     */
    public static void reproducirImagenes(ArrayList<BufferedImage> images, int fps) throws InterruptedException{
        
        Visor v = new Visor(); // Creamos la ventana vacía
        
        Timer timer = new Timer();
        
        // TimerTask para controlar la velocidad de los frames
        TimerTask task = new TimerTask() {
            @Override
            public void run() {  
                Reproductor.next=true;
            }
        };
        
        // Empezamos dentro de 0ms y luego lanzamos la tarea cada 1000ms/ numero de imagenes por segundo
        long period = (long)1000/fps;
        
        timer.schedule(task, 0, period);
        
        // Para cada imagen, se esperará el tiempo de un frame y actualizará la imagen del visor
        for(BufferedImage image : images){
            while(!next){Thread.sleep((long)0.001);}    
            v.setImage(image);
            v.changeFrame();
            Reproductor.next=false;
        }
                
        timer.cancel(); // Paramos el schedule
        v.dispose(); // Cerramos la ventana
    }
}
