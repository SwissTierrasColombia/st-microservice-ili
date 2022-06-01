package com.ai.st.microservice.ili.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.ai.st.microservice.ili.services.tracing.SCMTracing;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ZipService {

    private final static Logger log = LoggerFactory.getLogger(ZipService.class);

    public List<String> unzip(String fileZip, File destDir) throws IOException {

        List<String> paths = new ArrayList<>();

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();

            paths.add(newFile.getName());

        }
        zis.closeEntry();
        zis.close();

        return paths;
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {

        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static String zipping(File file, String zipName, String fileName, String namespace) {

        try {

            String path = namespace + File.separatorChar + zipName + ".zip";

            new File(namespace).mkdirs();
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }

            byte[] buffer = new byte[1024];

            FileOutputStream fos = new FileOutputStream(f);

            ZipOutputStream o = new ZipOutputStream(fos);
            ZipEntry e = new ZipEntry(fileName);
            o.putNextEntry(e);

            FileInputStream in = new FileInputStream(file.getAbsolutePath());

            int len;
            while ((len = in.read(buffer)) > 0) {
                o.write(buffer, 0, len);
            }

            in.close();
            o.closeEntry();

            o.close();
            fos.close();

            return path;

        } catch (Exception e) {
            String messageError = String.format("Error comprimiendo el archivo : %s", e.getMessage());
            SCMTracing.sendError(messageError);
            log.error(messageError);
        }

        return null;
    }

    public static String removeAccents(String str) {

        final String original = "ÁáÉéÍíÓóÚúÑñÜü";
        final String replace = "AaEeIiOoUuNnUu";

        if (str == null) {
            return null;
        }
        char[] array = str.toCharArray();
        for (int indice = 0; indice < array.length; indice++) {
            int pos = original.indexOf(array[indice]);
            if (pos > -1) {
                array[indice] = replace.charAt(pos);
            }
        }
        return new String(array);
    }

}
