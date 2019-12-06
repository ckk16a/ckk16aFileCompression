/**
 * Software Engineering II
 * Fall 2019
 * Carson Kelley
 *
 *compress a file with Huffman
 *execute: java SchubsH <filename> or <GLOB>
 */

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;

public class SchubsH {

    // alphabet size of extended ASCII
    private static final int R = 256;

    private static String read(String filepath) throws IOException{
    	String c = "";
    	try{
    		c = new String (Files.readAllBytes(Paths.get(filepath)));
    	} catch(IOException e){
    		e.printStackTrace();
    	}
    	return c;
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf("."));
        else return "";
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



    // compress bytes from standard input and write to standard output
    public static void compress(File f)throws IOException {
        // read the input
        if(!f.exists())
    		throw new RuntimeException("This file does not exist: " + f.getAbsolutePath());
    	 
        String s = read(f.getAbsolutePath());

        if(s.equals("")){
            File temp = new File(f.getAbsolutePath() + ".hh");
            temp.createNewFile();
            return;
        }

        char[] input = s.toCharArray();

        // tabulate frequency counts
        int[] freq = new int[R];
        for (int i = 0; i < input.length; i++)
            freq[input[i]]++;

        // build Huffman trie
        Node root = buildTrie(freq);

        // build code table
        String[] st = new String[R];
        buildCode(st, root, "");

        BinaryOut out = null;
        out = new BinaryOut(f.getAbsolutePath() + ".hh");

        // print trie for decoder
        writeTrie(root, out);

        // print number of bytes in original uncompressed message
        out.write(input.length);

        // use Huffman code to encode input
        for (int i = 0; i < input.length; i++) {
            String code = st[input[i]];
            for (int j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '0') {
                    out.write(false);
                }
                else if (code.charAt(j) == '1') {
                    out.write(true);
                }
                else throw new RuntimeException("Illegal state");
            }
        }

        // close output stream
        out.close();
    }

    // build the Huffman trie given frequencies
    private static Node buildTrie(int[] freq) {

        // initialze priority queue with singleton trees
        MinPQ<Node> pq = new MinPQ<Node>();
        for (char i = 0; i < R; i++)
            if (freq[i] > 0)
                pq.insert(new Node(i, freq[i], null, null));

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left  = pq.delMin();
            Node right = pq.delMin();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.insert(parent);
        }
        return pq.delMin();
    }


    // write bitstring-encoded trie to standard output
    private static void writeTrie(Node x, BinaryOut out) {
        if (x.isLeaf()) {
            out.write(true);
            out.write(x.ch);
            return;
        }
        out.write(false);

        writeTrie(x.left, out);
        writeTrie(x.right, out);
    }

    // make a lookup table from symbols and their encodings
    private static void buildCode(String[] st, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(st, x.left,  s + '0');
            buildCode(st, x.right, s + '1');
        }
        else {
            st[x.ch] = s;
        }
    }

    public static void main(String[] args) throws IOException{
        if(args.length == 0)
            throw new RuntimeException("No arguments given.");
    	
        for(int i = 0; i < args.length; i++){
        	File f = new File(args[i]);
        	compress(f);
        }
    }
}