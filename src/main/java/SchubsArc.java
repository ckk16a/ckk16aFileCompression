/**
 * Software Engineering II
 * Fall 2019
 * Carson Kelley
 *
 *compress an archive with LZW
 *execute: java SchubsArc <archive-name>
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;

public class SchubsArc {
    private static final int R = 256;        // number of input chars
    private static final int L = 4096;       // number of codewords = 2^W
    private static final int W = 12;         // codeword width

    private static String read(String filepath) throws IOException{
    	String c = "";
    	try{
    		c = new String (Files.readAllBytes(Paths.get(filepath)));
    	} catch(IOException e){
    		e.printStackTrace();
    	}
    	return c;
    }

    public static void compress(File f) throws IOException {

    	if(!f.exists())
    		throw new RuntimeException("This file does not exist: " + f.getAbsolutePath());
    	 
        String input = read(f.getAbsolutePath());

        if(input.equals("")){
            File temp = new File(f.getAbsolutePath());
            temp.createNewFile();
            return;
        }

        BinaryOut out = null;
        out = new BinaryOut(f.getAbsolutePath());

        if(input.length() > 0){
	        TST<Integer> st = new TST<Integer>();
	        for (int i = 0; i < R; i++)
	            st.put("" + (char) i, i);
	        int code = R+1;  // R is codeword for EOF

	        while (input.length() > 0) {
	            String s = st.longestPrefixOf(input);  // Find max prefix match s.
	            out.write(st.get(s), W);      // Print s's encoding.
	            int t = s.length();
	            if (t < input.length() && code < L)    // Add s to symbol table.
	                st.put(input.substring(0, t + 1), code++);
	            input = input.substring(t);            // Scan past s in input.
	        }

	        out.write(R, W);
	        out.close();
	    }
    } 

    public static void main(String[] args) throws IOException{
    	
    	if(args.length == 0)
            throw new RuntimeException("No arguments given.");

		ArrayList<String> toTar = new ArrayList<String>();
        toTar.add(args[0] + ".zl");

		for(int i = 1; i < args.length; i++){
        	toTar.add(args[i]);
        }
        Tarsn.main(toTar.toArray(new String[toTar.size()]));
        compress(new File(toTar.get(0)));

        toTar.clear();
        toTar = null;
    }
}