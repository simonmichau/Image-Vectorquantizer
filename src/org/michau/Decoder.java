package org.michau;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

class Decoder {

    static Color[] codebook = null;

    /**
     * Reads transmission and reconstructs .jpg image from that.
     * Notice that the first 30 Bit contain information about width, height and bitsize of the image
     */
    public static void startDecoding() {
        System.out.println("--------------------Decoder--------------------");

        try {
            String binary = Files.readString(Paths.get("transmission.t"), StandardCharsets.UTF_8);

            System.out.println("Successfully read data from file transmission.t");

            getCodebook();

            int width = Main.BinToDec(binary.substring(0, 12));
            int height = Main.BinToDec(binary.substring(12, 24));
            int bit = Main.BinToDec(binary.substring(24, 30));

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            binary = binary.substring(30);

            System.out.println("Image format: " + width + "x" + height + "; " + bit + " " + binary.length());

            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    img.setRGB(i, j, codebook[Main.BinToDec(binary.substring(0,bit))].getRGB());
                    binary = binary.substring(bit);
                }
            }

            //write image
            File f = new File("out.jpg");
            ImageIO.write(img, "jpg", f);
        }
        catch (IOException e){
            System.out.println("Could not read from file. " + e);
        }

    }

    /**
     * Reads Codebook from file
     */
    public static void getCodebook(){
        try {
            String binary = Files.readString(Paths.get("codebook.cb"), StandardCharsets.UTF_8);
            System.out.println("Successfully read data from file codebook.cb");

            Color[] res = new Color[binary.length()/24];

            for (int i=0; i<res.length; i++){
                int r = Main.BinToDec(binary.substring(i*24, i*24+8));
                int g = Main.BinToDec(binary.substring(i*24+8, i*24+16));
                int b = Main.BinToDec(binary.substring(i*24+16, i*24+24));
                res[i] = new Color(r,g,b);
            }

            codebook = res;
        }
        catch (IOException e){
            System.out.println("Could not read Codebook. " + e);
        }
    }

}

