package com.itss;

import java.io.BufferedWriter;
//import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * TODO: Document me!
 *
 * @author allam
 *
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println("Testing 1, 2, 3 ... Testing");
        String filename = "module.xml";
        try {
            FileWriter fw = new FileWriter(filename,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println("<text>Writing...</text>");
            pw.close();
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }        
    }

}
