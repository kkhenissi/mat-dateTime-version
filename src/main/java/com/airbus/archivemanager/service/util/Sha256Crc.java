package com.airbus.archivemanager.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class Sha256Crc {

    public static String generateSha(String pathFile) throws NoSuchAlgorithmException, IOException {
        String encoded = null;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream input = Files.newInputStream(Paths.get(pathFile))) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = input.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
        }

        byte[] hash = digest.digest();
        encoded = Base64.getEncoder().encodeToString(hash);

        return encoded;
    }
}
