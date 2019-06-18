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
    public static ArrayList<BufferedImage> extractImagesZip(String zip) throws FileNotFoundException, IOException{

        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
        File metadatos;
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

        return images;
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
     * @param zipName
     * @throws IOException 
     */
    public static void imagesToZip(ArrayList<BufferedImage> images, String zipName) throws IOException{
        
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

    
    public static StringBuffer string2ASCIIbin(StringBuffer input) {
        int ASCIIrange = 256;
        StringBuffer output = new StringBuffer("");
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            int j = (int) c;
            output.append(int2bin(j, ASCIIrange - 1));
        //System.out.println(c + " = " + j + " = " + int2bin(j, 256-1) + " = " + (char)Integer.parseInt(int2bin(j, 256-1), 2));
        }

        return (output);
    }

    /**
     * @param input cadena de caracteres "1" y "0" con los códigos ASCII de todas las letras a decodificar
     * @return output cadena de caracteres (letras) decodificadas
     */
    public static StringBuffer ASCIIbin2string(StringBuffer input) {
        int ASCIImodulo = 8;
        StringBuffer output = new StringBuffer("");
        for (int i = 0; i <= input.length() - ASCIImodulo; i = i + ASCIImodulo) {
            char c = (char) Integer.parseInt(input.substring(i, i + ASCIImodulo), 2);
            //System.out.println(input.substring(i, i + ASCIImodulo) + " = " + Integer.parseInt(input.substring(i, i + ASCIImodulo), 2) + " = " + c);
            output.append(c);
        }

        return (output);
    }
    
    /**
     * @param valor numero entero a codificar en binario natural
     * @param maxval valor del màximo entero codificable (determina el número de bits con que se codificara valor)
     * @return output cadena binaria al código binario natural de valor
     */
    public static String int2bin(int valor, int maxval) {
        int numbits = getNumBits(maxval);
        String binstring = Integer.toBinaryString((1 << 31) | (valor));
        return (binstring.substring(binstring.length() - numbits));
    }

    /**
     * @param valor número entero
     * @return output número de bits necesarios para codificar entero en binario natural
     */
    public static int getNumBits(int valor) {
        return (Integer.toBinaryString(valor).length());
    }
}
