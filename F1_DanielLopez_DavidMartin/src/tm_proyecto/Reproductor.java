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
public class Reproductor implements Runnable{
    
    // esta solo es usada en la version estatica de la clase
    private static boolean next = true;
    
    // estos solo son usados por la version runnable de la clase (necesario hacer un 'new' para ser usados)
    private ArrayList<BufferedImage> images;
    private int fps;
    private boolean pNext;
    
    Reproductor(ArrayList<BufferedImage> images, int fps){
        this.fps = fps;
        this.images = images;
        this.pNext = true;
    }
    
    /**
     * Recibe todas las imagenes y cuantos fps tendrá el video
     * Crea un timer para poder administrar cada cuanto tiene que enviar un frame.
     * Llamará a los métodos de la clase Visor para actualizar la imagen en cada frame
     * @param sImages
     * @param sFps
     * @throws InterruptedException 
     */
    public static void reproducirImagenes(ArrayList<BufferedImage> sImages, int sFps) throws InterruptedException{
        
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
        long period = (long)1000/sFps;
        
        timer.schedule(task, 0, period);
        
        // Para cada imagen, se esperará el tiempo de un frame y actualizará la imagen del visor
        for(BufferedImage image : sImages){
            while(!next){Thread.sleep((long)0.001);}    
            v.setImage(image);
            v.changeFrame();
            Reproductor.next=false;
        }
                
        timer.cancel(); // Paramos el schedule
        v.dispose(); // Cerramos la ventana
    }

    @Override
    public void run() {
        Visor v = new Visor(); // Creamos la ventana vacía
        
        Timer timer = new Timer();
        
        // TimerTask para controlar la velocidad de los frames
        TimerTask task = new TimerTask() {
            @Override
            public void run() {  
                pNext=true;
            }
        };
        
        // Empezamos dentro de 0ms y luego lanzamos la tarea cada 1000ms/ numero de imagenes por segundo
        long period = (long)1000/fps;
        
        timer.schedule(task, 0, period);
        
        // Para cada imagen, se esperará el tiempo de un frame y actualizará la imagen del visor
        for(BufferedImage image : images){
            while(!pNext){
                
                try {
                    Thread.sleep((long)0.001);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
}    
            v.setImage(image);
            v.changeFrame();
            pNext=false;
        }
                
        timer.cancel(); // Paramos el schedule
        v.dispose(); // Cerramos la ventana
    }
}
