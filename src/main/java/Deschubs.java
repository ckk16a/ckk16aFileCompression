/**
 * Software Engineering II
 * Fall 2019
 * Carson Kelley
 *
 *Decompress a file
 *execute: java Deschubs <filename>.hh|ll/archive.zl
 */

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Deschubs {

    // alphabet size of extended ASCII
    private static final int R = 256;
    private static final int L = 4096;       // number of codewords = 2^W
    private static final int W = 12;

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf("."));
        else return "";
    }

    private static String stripExtension(File f){
        String path = f.getAbsolutePath();
        if(path == null)
            return null;
        int pos = path.lastIndexOf(".");
        if(pos == -1)
            return path;
        return path.substring(0, pos);
    }

    private static String read(String filepath) throws IOException{
        String c = "";
        try{
            c = new String (Files.readAllBytes(Paths.get(filepath)));
        } catch(IOException e){
            e.printStackTrace();
        }
        return c;
    }

    // Huffman trie node
    private static class Node implements Comparable<Node> {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            assert (left == null && right == null) || (left != null && right != null);
            return (left == null && right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }

    // expand Huffman-encoded input from standard input and write to standard output
    public static void expandH(File f) throws IOException {

        if(!f.exists())
            throw new RuntimeException("The file or archive does not exist");

        if(read(f.getAbsolutePath()).equals("")){
            File temp = new File(stripExtension(f));
            temp.createNewFile();
            return;
        }

        BinaryIn in = null;
        in = new BinaryIn(f.getAbsolutePath());

        // read in Huffman trie from input stream
        Node root = readTrie(in); 

        // number of bytes to write
        int length = in.readInt();

        BinaryOut out = null;
        out = new BinaryOut(stripExtension(f));

        // decode using the Huffman trie
        for (int i = 0; i < length; i++) {
            Node x = root;
            while (!x.isLeaf()) {
                boolean bit = in.readBoolean();
                if (bit) x = x.right;
                else     x = x.left;
            }
            out.write(x.ch);
        }
        in.close();
        out.close();
    }

    public static void expandL(File f) throws IOException{

        if(!f.exists())
            throw new RuntimeException("The file or archive does not exist");

        //If empty file
        if(read(f.getAbsolutePath()).equals("")){
            File temp = null;
            if(getFileExtension(f).equals(".zl"))
                temp = new File(f.getAbsolutePath());
            else
                temp = new File(stripExtension(f));

            temp.createNewFile();
            return;
        }
        
        String[] st = new String[L];
        BinaryIn in = null;
        in = new BinaryIn(f.getAbsolutePath());
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;

        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = in.readInt(W);
        String val = st[codeword];

        BinaryOut out = null;
        //Stripping the extension if file is not an archive
        if(getFileExtension(f).equals(".zl"))
            out = new BinaryOut(f.getAbsolutePath());
        else
            out = new BinaryOut(stripExtension(f));

        while (true) {
            out.write(val);
            codeword = in.readInt(W);

            if (codeword == R) 
                break;

            String s = st[codeword];
            if (i == codeword) 
                s = val + val.charAt(0);   // special case hack
            if (i < L) 
                st[i++] = val + s.charAt(0);

            val = s;
        }
        in.close();
        out.close();
    }


    private static Node readTrie(BinaryIn in) {
        boolean isLeaf = in.readBoolean();
        if (isLeaf) {
	    char x = in.readChar();
            return new Node(x, -1, null, null);
        }
        else {
            return new Node('\0', -1, readTrie(in), readTrie(in));
        }
    }


    public static void main(String[] args) throws IOException {
        if(args.length == 0)
            throw new RuntimeException("No argument given.");

        File ar1 = new File(args[0]);
        
        if(getFileExtension(ar1).equals(".hh"))
            expandH(ar1);

        else if(getFileExtension(ar1).equals(".ll"))
            expandL(ar1);

        else if(getFileExtension(ar1).equals(".zl")){
            expandL(ar1);
            Untars.main(new String[] {ar1.getAbsolutePath()});
        }

        else{
            throw new RuntimeException("This file type not supported");
        }
    }

}