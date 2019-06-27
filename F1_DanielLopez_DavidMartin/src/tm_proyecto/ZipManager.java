/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tm_proyecto;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;


class ZipData{
    
    private ArrayList<BufferedImage> images;
    private String metadata;
    
    ZipData(ArrayList<BufferedImage> i , String m){
        this.images=i;
        this.metadata=m;
    }
    
    
    public ArrayList<BufferedImage> getImages() {
        return images;
    }

    public String getMetadata() {
        return metadata;
    }
    
}

/**
 *
 * Clase que administra la creación y lectura de archivos zip
 */
public class ZipManager {

    /**
     * Extrae y retorna el archivo de metadatos y las imagenes de un zip
     * @param zip
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static ZipData extractImagesZip(String zip) throws FileNotFoundException, IOException{

        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
        File metadatos = null;
        FileInputStream fileInputStream = new FileInputStream(zip);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream );
        ZipInputStream zin = new ZipInputStream(bufferedInputStream);
        ZipEntry ze = null;
        // Por cada item del zip
        while ((ze = zin.getNextEntry()) != null) {
                File file = new File("tmp");
                OutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[9000];
                int len;
                while ((len = zin.read(buffer)) != -1) {   // Leemos los items del zip
                    out.write(buffer, 0, len);              // Los añadimos al buffer
                }
                out.close();

                BufferedImage image = ImageIO.read(file);   // Creamos la imagen como BufferedImage
                if(image == null){ // Si no ha creado la imagen es que son los metadatos
                    metadatos = file;
                }else{
                    images.add(image);  // Si ha creado la imagen la añadimos
                }
        }
        zin.close();
        bufferedInputStream.close();
        fileInputStream.close();
        
        if(metadatos==null){
            return new ZipData(images,"") ;
        }
        return new ZipData(images,stringFromFile(metadatos)) ;
    }


    /**
     * Recibe el path a un zip y guarda todos sus contenidos en una carpeta
     * @param zip
     * @param folder
     * @throws IOException 
     */
    public static void unzipTo(String zip, String folder) throws IOException{
        // Si no existe la carpeta la crea
        if(!(new File(folder).exists())){
                FileSystem fileSystem = FileSystems.getDefault();
                Files.createDirectory(fileSystem.getPath(folder));
        }

        // Crea los input streams
        FileInputStream fileInputStream = new FileInputStream(zip);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream );
        ZipInputStream zin = new ZipInputStream(bufferedInputStream);
        ZipEntry ze = null;
        
        while ((ze = zin.getNextEntry()) != null) {
                String fName = ze.getName().substring(0, ze.getName().lastIndexOf('.'));
                File file = new File(folder+"/"+fName+".jpeg"); // Para cada item del zip creamos un File
                OutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[9000];
                int len;
                while ((len = zin.read(buffer)) != -1) {    // Leemos los inputs de zip input stream 
                    out.write(buffer, 0, len);              // y lo añadimos al buffer
                }
                out.close();
                BufferedImage image = ImageIO.read(file);   //Creamos la imagen como BufferedImage con la libreria ImageIO
                out = new FileOutputStream(file);
                ImageIO.write(image, "jpeg", out);          // La escribimos en el output stream
                out.close();

        }
        zin.close();
        bufferedInputStream.close();
        fileInputStream.close();
    }
    
    /**
     * Añade las imagenes de la lista pasada por parámetro a un zip
     * @param images
     * @param metadata
     * @param zipName
     * @throws IOException 
     */
    public static void imagesToZip(ArrayList<BufferedImage> images, String metadata, String zipName) throws IOException{
        
        File zipFile = new File(zipName);
        try (
                // Crea un file output stream y un buffered output stream
                FileOutputStream fos = new FileOutputStream(zipFile); 
                BufferedOutputStream bos = new BufferedOutputStream(fos)){
        
            try (ZipOutputStream zos = new ZipOutputStream(bos)){ // Intenta crear un zip output stream
                
                int i = 0;
                for(BufferedImage image : images){     
                    // Para cada imagen crea un zip entry i lo pone como next entry para añadirlo al zip
                    zos.putNextEntry(new ZipEntry(i+".jpeg"));
                    ImageIO.write(image, "jpeg", zos);  // Usamos la libreria ImageIO para escribir la imagen
                    
                    zos.closeEntry();
                    i++;
                }
                // si hay metadata
                if(!metadata.isEmpty()){
                    // Añadimos el fichero con metadata
                    byte[] strToBytes = metadata.getBytes();

                    zos.putNextEntry(new ZipEntry("data"));
                    zos.write(strToBytes);
                    zos.closeEntry();
                }
                zos.close();
            }
        }
        
           
    }
    
    
    public static void toZip(ArrayList<BufferedImage> images, String zipName, File metadata) throws IOException{
        
        File zipFile = new File(zipName);
        try (
                // Crea un file output stream y un buffered output stream
                FileOutputStream fos = new FileOutputStream(zipFile); 
                BufferedOutputStream bos = new BufferedOutputStream(fos)){
        
            try (ZipOutputStream zos = new ZipOutputStream(bos)){ // Intenta crear un zip output stream
                
                int i = 0;
                for(BufferedImage image : images){     
                    // Para cada imagen crea un zip entry i lo pone como next entry para añadirlo al zip
                    zos.putNextEntry(new ZipEntry(i+".jpeg"));
                    ImageIO.write(image, "jpeg", zos);  // Usamos la libreria ImageIO para escribir la imagen
                    
                    zos.closeEntry();
                    i++;
                }
                
                // Agregamos el archivo de metadatos
                FileInputStream fis = new FileInputStream(metadata);
                zos.putNextEntry(new ZipEntry("metadata"));
                byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}
		zos.closeEntry();               
                
                zos.close();
            }
        }
        
           
    }
    
    

    /**
     *  Recibe una lista serie de imagenes, crea una carpeta y las añade todas 
    */
    public static void imagesToFolder(ArrayList<BufferedImage> images, String folder) throws IOException{
        if(!(new File(folder).exists())){   // Si no existe la carpeta la crea
                FileSystem fileSystem = FileSystems.getDefault();
                Files.createDirectory(fileSystem.getPath(folder));
        }
        int i = 0;
        for(BufferedImage image : images){      // Para cada una de las imagenes la añade a la carpeta
                File file = new File(folder+"/"+i+".jpeg");
                OutputStream out = new FileOutputStream(file);
                ImageIO.write(image, "jpeg", out);
                out.close();
                i++;
        }
    }

    
    
    public static void stringToFile(String file, String content) throws FileNotFoundException, IOException{
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] strToBytes = content.getBytes();
        outputStream.write(strToBytes);
 
        outputStream.close();
    }
    
    public static String stringFromFile(File file) throws FileNotFoundException, IOException{
        FileInputStream inputStream = new FileInputStream(file);
        String result = "";
        int temp;
        while ((temp = inputStream.read()) != -1) {
            // convert to char and display it
            result+=(char)temp;
        }
        inputStream.close();
        return result;
    }
    
}
