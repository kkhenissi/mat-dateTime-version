package com.airbus.archivemanager.karate.scenarii;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;


public class KarateUtil {
    public String fileExist(final String fileName) {
        final File f = new File(fileName);
        if (f.exists()) {
            return "True";
        } else {
            return "False";
        }
    }

    public String createFile(final String fileName) throws FileNotFoundException {
        final String data = "Test data";

        try (FileOutputStream out = new FileOutputStream(fileName)) {
            byte[] array = new byte[7]; // length is bounded by 7
            new Random().nextBytes(array);
            String generatedString = new String(array, Charset.forName("UTF-8"));
            out.write(data.getBytes());
            out.write(fileName.getBytes());
            out.write(generatedString.getBytes());
        } catch (final IOException e) {
            return e.getMessage();
        }
        return "OK";

    }

    public String createFiles(final String path, final String fileName, final String nb) throws FileNotFoundException {
        final String data = "Test data";

        try (FileOutputStream out = new FileOutputStream(path +fileName)) {
            out.write(data.getBytes());
        } catch (final IOException e) {
            return e.getMessage();
        }
        return "OK";

    }

    public String deleteFile(final String fileName)  {
        String ret = "OK";
        try {
            Files.delete(Paths.get(fileName));
        } catch (final java.nio.file.NoSuchFileException e) {
            ret = "OK";
        } catch (final IOException e) {
            ret = "ERROR to delete '"+ fileName + "' Error: " + e.getMessage();
        }
        return ret;

    }

}
