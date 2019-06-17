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
 * @author Daniel
 */
public class ZipManager {

    public static ArrayList<BufferedImage> extractImagesZip(String zip) throws FileNotFoundException, IOException{

        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
        File metadatos;
        FileInputStream fileInputStream = new FileInputStream(zip);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream );
        ZipInputStream zin = new ZipInputStream(bufferedInputStream);
        ZipEntry ze = null;
        while ((ze = zin.getNextEntry()) != null) {
                File file = new File("tmp");
                OutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[9000];
                int len;
                while ((len = zin.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.close();

                BufferedImage image = ImageIO.read(file);
                if(image == null){
                    metadatos = file;
                }else{
                    images.add(image);
                }
        }
        zin.close();
        bufferedInputStream.close();
        fileInputStream.close();

        return images;
    }


    public static void unzipTo(String zip, String folder) throws IOException{

        if(!(new File(folder).exists())){
                FileSystem fileSystem = FileSystems.getDefault();
                Files.createDirectory(fileSystem.getPath(folder));
        }

        FileInputStream fileInputStream = new FileInputStream(zip);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream );
        ZipInputStream zin = new ZipInputStream(bufferedInputStream);
        ZipEntry ze = null;
        while ((ze = zin.getNextEntry()) != null) {
                String fName = ze.getName().substring(0, ze.getName().lastIndexOf('.'));
                File file = new File(folder+"/"+fName+".jpeg");
                OutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[9000];
                int len;
                while ((len = zin.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.close();
                BufferedImage image = ImageIO.read(file);
                out = new FileOutputStream(file);
                ImageIO.write(image, "jpeg", out);
                out.close();

        }
        zin.close();
        bufferedInputStream.close();
        fileInputStream.close();
    }
    
    public static void imagesToZip(ArrayList<BufferedImage> images, String zipName) throws IOException{
        
        File zipFile = new File(zipName);
        try ( //images
                FileOutputStream fos = new FileOutputStream(zipFile); 
                BufferedOutputStream bos = new BufferedOutputStream(fos)){
        
            try (ZipOutputStream zos = new ZipOutputStream(bos)){
                
                int i = 0;
                for(BufferedImage image : images){
                    
                    zos.putNextEntry(new ZipEntry(i+".jpeg"));
                    ImageIO.write(image, "jpeg", zos);
                    
                    zos.closeEntry();
                    i++;
                }
                zos.close();
            }
        }
        
           
    }

    public static void imagesToFolder(ArrayList<BufferedImage> images, String folder) throws IOException{
        if(!(new File(folder).exists())){
                FileSystem fileSystem = FileSystems.getDefault();
                Files.createDirectory(fileSystem.getPath(folder));
        }
        int i = 0;
        for(BufferedImage image : images){
                File file = new File(folder+"/"+i+".jpeg");
                OutputStream out = new FileOutputStream(file);
                ImageIO.write(image, "jpeg", out);
                out.close();
                i++;
        }
    }

}
