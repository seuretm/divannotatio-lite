/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divaservices;


import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * ImageEncoding class of the LineSeg project
 * @author lunactic
 * date 22.09.15
 * brief
 */
class ImageEncoding {

    private static final Logger logger = Logger.getLogger(ImageEncoding.class);

    /**
     * Creates a Base64 encoding of an image
     *
     * @param image image
     * @return the image as base64 encoded string
     */
    public static String encodeToBase64(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encodeToMd5(BufferedImage image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outputStream);
            byte[] data = outputStream.toByteArray();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Base64.getEncoder().encode(data));
            byte[] hash = md.digest();
            return hexString(hash);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static BufferedImage decodeBas64(String base64) {
        try {
            byte[] buffer = Base64.getDecoder().decode(base64);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            return ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String hexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xff & aByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
