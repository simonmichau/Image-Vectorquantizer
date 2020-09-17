package org.michau;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    /**
     * Returns a n-Bit binary representation of a given decimal number
     * @param dec Any non negative decimal number of type Integer
     * @param bit Bitlength of the output binary number
     * @return n-Bit binary representation of @dec
     * @throws NumberFormatException if decimal number is negative
     */
    static String DecToBin(int dec, int bit) {
        if (dec < 0 || bit < 1) {
            System.out.println("Conversion of decimal to binary failed. Make sure both input and bitlength is of Integer value and not negative.");
            return null;
        }

        String bin = "";
        for (int i=0; i < bit; i++){
            bin = (Integer.toString(dec % 2)).concat(bin);
            dec = dec/2;
        }

        return bin;
    }

    /**
     * Returns corresponding decimal number to given binary number
     * @param bin binary number
     * @return decimal number
     */
    static int BinToDec(String bin) throws NumberFormatException{
        int dec;
        dec = Integer.parseInt(bin, 2);
        return dec;
    }

    /**
     * Stores String in .txt file
     * @param text String text to store
     * @param filename storage file name
     */
    static void store(String text, String filename, String extension) {
        Path path = Paths.get(filename + "." + extension);

        try {
            Files.writeString(path, text);
            System.out.println("IVQ: Successfully written data to " + filename + "." + extension);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates some means of compression performance to evaluate the performance of the VQ
     */
    private static void analyze(){
        System.out.println("--------------------Analyzer--------------------");
        BufferedImage in = null;
        BufferedImage out = null;
        int width=0;
        int height=0;

        //read image
        try {
            File f = new File("in.jpg");
            in = ImageIO.read(f);
            f = new File("out.jpg");
            out = ImageIO.read(f);
            width = in.getWidth();
            height = in.getHeight();
        }
        catch(IOException e) {
            System.out.println(e);
        }

        int maxError = 0;
        double totalDistortion = 0;
        double meanSquaredError = 0;
        double quadraticOutput = 0;
        for (int y=0; y < height; y++){
            for (int x = 0; x < width; x++){
                Color a = new Color(in.getRGB(x,y));
                Color b = new Color(out.getRGB(x,y));
                int tmp = (Math.abs(a.getRed()-b.getRed()) + Math.abs(a.getGreen()-b.getGreen()) + Math.abs(a.getBlue()-b.getBlue())); //difference between a and b
                quadraticOutput += Math.pow((b.getRed()+ b.getGreen() + b.getBlue()), 2);
                if (tmp > maxError)
                    maxError = tmp;
                totalDistortion += tmp;
                meanSquaredError += Math.pow(tmp, 2);
            }
        }

        System.out.println("Total Distortion: " + totalDistortion);
        System.out.println("Average Distortion per Pixel: " + totalDistortion/(width*height));
        System.out.println("Mean squared error: " + meanSquaredError/(width*height));
        System.out.println("Signal-to-noise ratio: " + (quadraticOutput/meanSquaredError));
        System.out.println("Maximum error: " + maxError);


    }

    public static void main(String[] args) throws IOException {
           try {
               switch (args[0]) {
                   case "-e":
                       Encoder.startEncoding(Integer.parseInt(args[1]));
                       break;
                   case "-d":
                       Decoder.startDecoding();

                       analyze();
                       break;
                   case "-a":
                       Encoder.startEncoding(Integer.parseInt(args[1]));
                       Decoder.startDecoding();

                       analyze();
                       break;
                   default:
                       throw new ArrayIndexOutOfBoundsException();
               }
           } catch(ArrayIndexOutOfBoundsException e){
               System.out.println("Syntax: java -jar IVQ.jar [-e <codevector amount> | Encoding picture with specified amount of codevectors.");
               System.out.println("                           -d                     | Decoding from codebook.txt and transmission.txt and printing analysis");
               System.out.println("                           -a                     | Encoding then Decoding");
               System.out.println("                           -h                     | Help]");
           }



    }
}
