package net.wellfield.magpie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class MagpieFileUtil {

    public static byte[] fileToByteArray(File file) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FileInputStream fileInputStream = new FileInputStream(file);
     
            byte[] buffer = new byte[16384];
     
            for (int len = fileInputStream.read(buffer); len > 0; len = fileInputStream.read(buffer)) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
     
            fileInputStream.close();
     
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
