package org.michau;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Encoder {

    static BufferedImage img = null;
    static int width = 0;
    static int height = 0;
    static int codevectors = 1024;

    /**
     * Stores n Bit binary representation of the color of a given pixelblocksize into a .txt file
     */
    public static void startEncoding(int n) {
        System.out.println("--------------------Encoder--------------------");

        /////////////////////// Create and Store the Codebook ///////////////////////////////////////
        //read image
        try {
            File f = new File("in.jpg");
            img = ImageIO.read(f);
            width = img.getWidth();
            height = img.getHeight();
        }
        catch(IOException e) {
            System.out.println(e);
        }

        codevectors = n;
        Color[] codebook = createCodebook(codevectors);

        String codeVectors = "";

        for (int i=0; i < codebook.length; i++){
            codeVectors += Main.DecToBin(codebook[i].getRed(), 8) + Main.DecToBin(codebook[i].getGreen(), 8) + Main.DecToBin(codebook[i].getBlue(), 8);
        }
        Main.store(codeVectors, "codebook", "cb");
        System.out.println("Codebook created.");

        /////////////////////// Actual Encoding /////////////////////////////////////////////////////
        int bit = (int)Math.ceil(Math.log(codevectors) / Math.log(2));
        String encoded = Main.DecToBin(width,12) + Main.DecToBin(height,12) + Main.DecToBin(bit,6); //Meta

        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                //Find closest Codebookvector and add its index to the transmission
                Color codevector = findClosestCodebookVector(codebook, new Color(img.getRGB(j,i)));
                for (int k=0; k<codebook.length; k++){
                    if (codebook[k]==codevector){
                        encoded+= Main.DecToBin(k,bit);
                    }
                }
            }
        }

        Main.store(encoded, "transmission", "t");
        System.out.println("Encoding Complete.");
    }

    /**
     * Create a Codebook with n Codebookvectors using the LBG-Algorithm
     */
    public static Color[] createCodebook(int n){
        Color[] codebook = new Color[n];

        //Starting Position
        int intervalSize = (int)(256/Math.cbrt(n));
        int k=0;
        for (int z = 0; z < (int) Math.cbrt(n); z++){
            for (int y = 0; y < (int) Math.cbrt(n); y++){
                for (int x = 0; x < (int) Math.cbrt(n); x++){
                    codebook[k] = new Color(x*intervalSize, y*intervalSize, z*intervalSize);
                    k++;
                }
            }
        }
        while (k<n){
            codebook[k] = new Color(0,0,0);
            k++;
        }

        //LBG-Algorithm
        for (int a=0; a<16; a++){
            //Nearest-Neighbor Condition
            Color[] mapping = new Color[width*height]; //Contains a CodebookVec for every Index of a trainingVec

            for (int i=0; i<height; i++){
                for (int j=0; j<width; j++){
                    Color c = new Color(img.getRGB(j, i)); //get current trainingVec
                    mapping[i*width+j] = findClosestCodebookVector(codebook, c); //get nearest CodebookVec for trainingVec
                }
            }

            //Centroid Condition
            for (int i=0; i < codebook.length; i++){ //go through Codebook
                int occurrences = 1;
                int r = 0; int g = 0; int b = 0;
                int x,y = 0;
                for (int j=0; j < mapping.length; j++){ //go through training vectors
                    if (mapping[j]==codebook[i]){
                        Color c = new Color(img.getRGB(j%width, j/width));
                        r += c.getRed();
                        g += c.getGreen();
                        b += c.getBlue();
                        occurrences++;
                    }
                }

                //Set CodebookVec to Centroid
                codebook[i] = new Color(r/occurrences, g/occurrences, b/occurrences);

                //Check if Codevector already exists
                for (int j=0; j<codebook.length; j++){  //go through Codebook
                    if (i != j){
                        if (codebook[i]==codebook[j]){
                            //new CodebookVec already in Codebook -> dodge
                            if (codebook[i].getRed()<255){
                                codebook[i] = new Color(codebook[i].getRed()+1, codebook[i].getGreen(), codebook[i].getBlue()); j=0;
                            } else {
                                if (codebook[i].getGreen()<255){
                                    codebook[i] = new Color(codebook[i].getRed(), codebook[i].getGreen()+1, codebook[i].getBlue()); j=0;
                                } else {
                                    if (codebook[i].getBlue()<255){
                                        codebook[i] = new Color(codebook[i].getRed(), codebook[i].getGreen(), codebook[i].getBlue()+1); j=0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return codebook;
    }

    /**
     * Finds the closest Codebookvector for a given Vector
     * @param codebook Set of Codebookvectors
     * @param vector Vector
     * @return Index of closest Codebookvector
     */
    public static Color findClosestCodebookVector(Color[] codebook, Color vector){
        int indexNearest = 0;
        double distanceNearest = 1000;

        for (int i=0; i<codebook.length; i++){
            double distance = Math.sqrt(Math.pow(vector.getRed()-codebook[i].getRed(),2)
                    + Math.pow(vector.getGreen()-codebook[i].getGreen(),2))
                    + Math.pow(vector.getBlue()-codebook[i].getBlue(),2);
            if (distance < distanceNearest) {
                distanceNearest = distance;
                indexNearest = i;
            }
        }
        return codebook[indexNearest];
    }
}
