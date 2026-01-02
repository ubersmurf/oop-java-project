package util;

import java.io.File;
import java.net.URISyntaxException;

public class FileHelper {

    public static File getFile(String fileName) {
        // 1. Önce standart yolu dene (IDE için)
        File standardFile = new File(fileName);
        if (standardFile.exists()) {
            return standardFile;
        }

        // 2. Bulamazsa (JAR çift tıklama sorunu), JAR'ın yanına bak
        try {
            File jarPath = new File(FileHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File jarDirectory = jarPath.getParentFile();
            File jarSiblingFile = new File(jarDirectory, fileName);
            
            // Dosya veya klasör (data/) orada mı diye bakıyoruz
            return jarSiblingFile;
            
        } catch (URISyntaxException e) {
            return standardFile;
        }
    }
}