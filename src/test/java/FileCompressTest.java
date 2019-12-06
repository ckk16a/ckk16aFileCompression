import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;

import java.util.Random;
import java.lang.StringBuilder;

/**
 * Unit test for simple App.
 */
public class FileCompressTest extends TestCase
{
    private final String sep = System.getProperty("file.separator");

    String directory = "src" + sep + "files";

    Random r = new Random();
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public FileCompressTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( FileCompressTest.class );
    }

    //Util functions

    //Creates a directory with a file
    public void createDirectory(File dir, int amount)throws IOException{
        //Creates the existing directory
        if(dir.exists()){
            dir.delete();
        }
        dir.mkdir();
        for(int i = 0; i < amount; i++){
            File f1 = new File(dir, getRandomString(r.nextInt(10)+1)+".txt");
            createContent(f1, getRandomString(r.nextInt(10)));
        }
    }

    //Gets file name without extension
    private static String stripExtension(String f){
        if(f == null)
            return null;
        int pos = f.lastIndexOf(".");
        if(pos == -1)
            return f;
        return f.substring(0, pos);
    }

    //Converts a file array to a string array
    private static String[] filetoString(File[] f){
        String[] names = new String[f.length];
        for (int i = 0; i < f.length; i++) {
            names[i] = f[i].getAbsolutePath();
        }
        return names;
    }

    //Adds given extension to file names
    private String[] addExtension(String[] s, String e){
        String[] n = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            n[i] = s[i] + e;
        }
        return n;
    }

    private void createTestDir() throws IOException{
        File files = new File("src" + sep + "files");
        if(!files.exists()){
            files.mkdir();
        }
    }

    //Checks to make sure two files are identical
    public boolean allBytesSame(File f1, File f2) throws IOException
    {
        FileInputStream in1 = null;
        FileInputStream in2 = null;

        in1 = new FileInputStream(f1);
        in2 = new FileInputStream(f2);

        int m1 = in1.read();
        int m2 = in2.read();

        while(m1 != -1 && m2 != -1)
        {
            if(m1 != m2){
                if (in1 != null)
                    in1.close();
                if (in2 != null)
                    in2.close();
                return false;
            }
            else{
                m1 = in1.read();
                m2 = in2.read();
            }
        }
        
        if (in1 != null)
            in1.close();
        if (in2 != null)
            in2.close();

        return true; 
    }

    //Puts content in the files
    public void createContent(File f, String text)throws IOException{
    	if(f.exists()){
    		f.delete();
    	}
    	f.createNewFile();

    	//Puts things in the file
    	FileOutputStream out = new FileOutputStream(f.toString());
        byte[]b = text.getBytes();
        for(int i = 0; i < b.length; i++)
            out.write(b[i]);

        if(out != null)
            out.close();
    }

    //Makes random strings for dynamic testing
    public String getRandomString(int length) throws IOException{
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        Random ran = new Random();
        for(int i = 0; i < length; i++){
            sb.append(chars.charAt(ran.nextInt(chars.length())));
        }
        return sb.toString();
    }

    //Increases the size of the string
    public String[] increaseSize(String[] original, int amount) {
       String[]temp = new String[amount + 1];

       for (int i = 0; i < amount; i++){
          temp[i] = original[i];
       }
       temp[amount] = "0";
       return temp;
    }

    //Copys a file into another
    public void copyFile(File f1, File f2)throws IOException{
        //Copies everything in the file
        FileInputStream in = new FileInputStream(f1);
        FileOutputStream out = new FileOutputStream(f2);

        try{
            for(long i = 0; i < f1.length(); i++){
                out.write(in.read());
            }
        } finally{
            if(in != null)
                in.close();
            if(out != null)
                out.close();
        }
    }

    //Recursively deletes all directories and files
	public static void delete(File orig)throws IOException{
		if(orig.isDirectory()){
			String files[] = orig.list();
			for(String file : files){
				File f1 = new File(orig, file);
				delete(f1);
			}
			orig.delete();
		}
		else
			orig.delete();
	}

    //Test Cases

	//Tests the compression of 1 file via LZW
    public void testFileCompress1() throws IOException {

        createTestDir();

        //Makes Test1 Directory
        File dir = new File(directory + sep + "test1");
        if(dir.exists())
            delete(dir);
        dir.mkdir();

        //Makes Test1 File
        String filename = "test1";
        File test = new File(dir + sep + filename + ".txt");
        if(test.exists())
            delete(test);

        createContent(test, getRandomString(8));

        File copy = new File(dir + sep + filename + "copy.txt");
        copyFile(test, copy);

        //Calls LZW to compress, then decompress the 'test' file
        SchubsL.main(new String[] {dir + sep + filename + ".txt"});
        Deschubs.main(new String[] {dir + sep + filename + ".txt.ll"});
        //Makes sure the file is the same as it was before compression
        assertTrue(allBytesSame(test, copy));
    }

    //Tests the compression of 1 file via Huffman
    public void testFileCompress2() throws IOException {
        createTestDir();

        //Makes Test2 Directory
        File dir = new File(directory + sep + "test2");
        if(dir.exists())
            delete(dir);
        dir.mkdir();

        //Makes Test2 File
        String filename = "test2";
        File test = new File(dir + sep + filename + ".txt");
        if(test.exists())
            delete(test);

        createContent(test, getRandomString(8));

        File copy = new File(dir + sep + filename + "copy.txt");
        copyFile(test, copy);

        //Calls LZW to compress, then decompress the 'test' file
        SchubsH.main(new String[] {dir + sep + filename + ".txt"});
        Deschubs.main(new String[] {dir + sep + filename + ".txt.hh"});
        //Makes sure the file is the same as it was before compression
        assertTrue(allBytesSame(test, copy));
    }

    //Tests the compression of a GLOB via LZW
    public void testFileCompress3() throws IOException {
        createTestDir();

        //Makes Test3 Directory
        File dir = new File(directory + sep + "test3");
        if(dir.exists())
            delete(dir);

        //Makes Test3 GLOB
        createDirectory(dir, 3);

        //Makes a list of all original files names and copies
        File files[] = dir.listFiles();
        ArrayList<String> copies = new ArrayList<String>();

        //Goes through list, making copies of every file
        for(File file : files){
            String name = stripExtension(file.getName());
            copies.add(name + "copy.txt");
            File copy = new File(file.getParentFile(), name + "copy.txt");
            copyFile(file, copy);
        }

        //Converts the file array to a string array and calls SchubsL to compress all files in GLOB
        String[] names = filetoString(files);
        SchubsL.main(names);

        //Decompresses all files in GLOB
        String[] names2 = addExtension(names, ".ll");
        for(int i = 0; i < names2.length; i++){
            Deschubs.main(new String[] {names2[i]});
        }
        
        //Makes sure all files are the same as they were before compression
        for(int i = 0; i < names.length; i++){
            File temp = new File(names[i]);
            assertTrue(allBytesSame(temp, new File(temp.getParentFile(), stripExtension(temp.getName()) + "copy.txt")));
        }
    }

    //Tests the compression of a GLOB via Huffman
    public void testFileCompress4() throws IOException {
        createTestDir();

        //Makes Test4 Directory
        File dir = new File(directory + sep + "test4");
        if(dir.exists())
            delete(dir);

        //Makes Test4 GLOB
        createDirectory(dir, 3);

        //Makes a list of all original files names and copies
        File files[] = dir.listFiles();
        ArrayList<String> copies = new ArrayList<String>();

        //Goes through list, making copies of every file
        for(File file : files){
            String name = stripExtension(file.getName());
            copies.add(name + "copy.txt");
            File copy = new File(file.getParentFile(), name + "copy.txt");
            copyFile(file, copy);
        }

        //Converts the file array to a string array and calls SchubsH to compress all files in GLOB
        String[] names = filetoString(files);
        SchubsH.main(names);

        //Decompresses all files in GLOB
        String[] names2 = addExtension(names, ".hh");
        for(int i = 0; i < names2.length; i++){
            Deschubs.main(new String[] {names2[i]});
        }
        
        //Makes sure all files are the same as they were before compression
        for(int i = 0; i < names.length; i++){
            File temp = new File(names[i]);
            assertTrue(allBytesSame(temp, new File(temp.getParentFile(), stripExtension(temp.getName()) + "copy.txt")));
        }
    }

    //Tests the compression of an archive via LZW
    public void testFileCompress5() throws IOException {
        createTestDir();

        //Makes Test5 Directory
        File dir = new File(directory + sep + "test5");
        if(dir.exists())
            delete(dir);

        //Makes Test5 GLOB
        createDirectory(dir, 5);

        //Makes a list of all original files names and copies
        File files[] = dir.listFiles();
        ArrayList<String> copies = new ArrayList<String>();

        //Goes through list, making copies of every file
        for(File file : files){
            String name = stripExtension(file.getName());
            copies.add(name + "copy.txt");
            File copy = new File(file.getParentFile(), name + "copy.txt");
            copyFile(file, copy);
        }

        //Converts the file array to a string array, adds archive at the beginning and calls SchubsArc to tars the files and compress
        String[] names = filetoString(files);
        String[] namesArc = new String[names.length + 1];
        for(int i = names.length - 1; i > -1; i--)
            namesArc[i+1] = names[i];
        namesArc[0] = dir + sep + "archive";
        SchubsArc.main(namesArc);

        //Decompresses and untars the archive
        Deschubs.main(new String[] {dir + sep + "archive.zl"});
        
        //Makes sure all files are the same as they were before compression
        for(int i = 0; i < names.length; i++){
            File temp = new File(names[i]);
            assertTrue(allBytesSame(temp, new File(temp.getParentFile(), stripExtension(temp.getName()) + "copy.txt")));
        }
    }

    //Tests that compression of an archive via Huffman does not work
    public void testFileCompress6() throws IOException {
        createTestDir();

        //Makes Test6 Directory variable
        File dir = new File(directory + sep + "test6");

        //Makes sure on call for huffman archive that it gives a runtime error
        try{
            Deschubs.main(new String[] {dir + sep + "archive.zh"});
        } catch(Exception e){
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    //Tests the compression of 1 file via LZW and deletes the file before decompression, tests the creation of the file again
    public void testFileCompress7() throws IOException {
        createTestDir();

        //Makes Test7 Directory
        File dir = new File(directory + sep + "test7");
        if(dir.exists())
            delete(dir);
        dir.mkdir();

        //Makes Test7 File
        String filename = "test7";
        File test = new File(dir + sep + filename + ".txt");
        if(test.exists())
            delete(test);

        createContent(test, getRandomString(8));

        File copy = new File(dir + sep + filename + "copy.txt");
        copyFile(test, copy);

        //Calls LZW to compress, then decompress the 'test' file
        SchubsL.main(new String[] {dir + sep + filename + ".txt"});
        test.delete();
        Deschubs.main(new String[] {dir + sep + filename + ".txt.ll"});
        //Makes sure the file is the same as it was before compression
        assertTrue(allBytesSame(test, copy));
    }

    //Tests the compression of 1 file via Huffman and deletes the file before decompression, tests the creation of the file again
    public void testFileCompress8() throws IOException {
        createTestDir();

        //Makes Test8 Directory
        File dir = new File(directory + sep + "test8");
        if(dir.exists())
            delete(dir);
        dir.mkdir();

        //Makes Test8 File
        String filename = "test8";
        File test = new File(dir + sep + filename + ".txt");
        if(test.exists())
            delete(test);

        createContent(test, getRandomString(8));

        File copy = new File(dir + sep + filename + "copy.txt");
        copyFile(test, copy);

        //Calls LZW to compress, then decompress the 'test' file
        SchubsH.main(new String[] {dir + sep + filename + ".txt"});
        test.delete();
        Deschubs.main(new String[] {dir + sep + filename + ".txt.hh"});
        //Makes sure the file is the same as it was before compression
        assertTrue(allBytesSame(test, copy));
    }

    //Tests the compression of an archive via LZW and deletes the files before decompression, tests the creation of the file again
    public void testFileCompress9() throws IOException {
        createTestDir();

        //Makes Test9 Directory
        File dir = new File(directory + sep + "test9");
        if(dir.exists())
            delete(dir);

        //Makes Test9 GLOB
        createDirectory(dir, 5);

        //Makes a list of all original files names and copies
        File files[] = dir.listFiles();
        ArrayList<String> copies = new ArrayList<String>();

        //Goes through list, making copies of every file
        for(File file : files){
            String name = stripExtension(file.getName());
            copies.add(name + "copy.txt");
            File copy = new File(file.getParentFile(), name + "copy.txt");
            copyFile(file, copy);
        }

        //Converts the file array to a string array, adds archive at the beginning and calls SchubsArc to tars the files and compress
        String[] names = filetoString(files);
        String[] namesArc = new String[names.length + 1];
        for(int i = names.length - 1; i > -1; i--)
            namesArc[i+1] = names[i];
        namesArc[0] = dir + sep + "archive";
        SchubsArc.main(namesArc);

        //Deletes all files (not the archive)
        for(int i = 0; i < names.length; i++){
            File f = new File(names[i]);
            f.delete();
        }

        //Decompresses and untars the archive
        Deschubs.main(new String[] {dir + sep + "archive.zl"});
        
        //Makes sure all files are the same as they were before compression
        for(int i = 0; i < names.length; i++){
            File temp = new File(names[i]);
            assertTrue(allBytesSame(temp, new File(temp.getParentFile(), stripExtension(temp.getName()) + "copy.txt")));
        }
    }

    //Tests the compression of an empty file via LZW
    public void testFileCompress10() throws IOException {
        createTestDir();

        //Makes Test10 Directory
        File dir = new File(directory + sep + "test10");
        if(dir.exists())
            delete(dir);
        dir.mkdir();

        //Makes Test10 File
        String filename = "test10";
        File test = new File(dir + sep + filename + ".txt");
        if(test.exists())
            delete(test);

        createContent(test, "");

        File copy = new File(dir + sep + filename + "copy.txt");
        copyFile(test, copy);

        //Calls LZW to compress, then decompress the 'test' file
        SchubsL.main(new String[] {dir + sep + filename + ".txt"});
        Deschubs.main(new String[] {dir + sep + filename + ".txt.ll"});
        //Makes sure the file is the same as it was before compression
        assertTrue(allBytesSame(test, copy));
    }

    //Tests the compression of an empty file via Huffman
    public void testFileCompress11() throws IOException {
        createTestDir();

        //Makes Test11 Directory
        File dir = new File(directory + sep + "test11");
        if(dir.exists())
            delete(dir);
        dir.mkdir();

        //Makes Test11 File
        String filename = "test11";
        File test = new File(dir + sep + filename + ".txt");
        if(test.exists())
            delete(test);

        createContent(test, "");

        File copy = new File(dir + sep + filename + "copy.txt");
        copyFile(test, copy);

        //Calls LZW to compress, then decompress the 'test' file
        SchubsH.main(new String[] {dir + sep + filename + ".txt"});
        Deschubs.main(new String[] {dir + sep + filename + ".txt.hh"});
        //Makes sure the file is the same as it was before compression
        assertTrue(allBytesSame(test, copy));
    }

    //Tests the compression of an archive with an empty file via LZW
    public void testFileCompress12() throws IOException {
        createTestDir();

        //Makes Test12 Directory
        File dir = new File(directory + sep + "test12");
        if(dir.exists())
            delete(dir);

        //Makes a dynamic filled Test12 fle
        createDirectory(dir, 1);

        //Makes an empty Test12 file
        String filename = "test12empty";
        File test = new File(dir + sep + filename + ".txt");
        if(test.exists())
            delete(test);

        createContent(test, "");

        //Makes a list of all original files names and copies
        File files[] = dir.listFiles();
        ArrayList<String> copies = new ArrayList<String>();

        //Goes through list, making copies of every file
        for(File file : files){
            String name = stripExtension(file.getName());
            copies.add(name + "copy.txt");
            File copy = new File(file.getParentFile(), name + "copy.txt");
            copyFile(file, copy);
        }

        //Converts the file array to a string array, adds archive at the beginning and calls SchubsArc to tars the files and compress
        String[] names = filetoString(files);
        String[] namesArc = new String[names.length + 1];
        for(int i = names.length - 1; i > -1; i--)
            namesArc[i+1] = names[i];
        namesArc[0] = dir + sep + "archive";
        SchubsArc.main(namesArc);

        //Decompresses and untars the archive
        Deschubs.main(new String[] {dir + sep + "archive.zl"});
        
        //Makes sure all files are the same as they were before compression
        for(int i = 0; i < names.length; i++){
            File temp = new File(names[i]);
            assertTrue(allBytesSame(temp, new File(temp.getParentFile(), stripExtension(temp.getName()) + "copy.txt")));
        }
    }

     //Tests the compression of an archive with no input files via LZW
    public void testFileCompress13() throws IOException {
        createTestDir();

        //Makes Test13 Directory
        File dir = new File(directory + sep + "test13");
        if(dir.exists())
            delete(dir);

        //Makes Test13 GLOB
        createDirectory(dir, 0);

        File archive = new File(dir, "archive");

        //Calls SchubsArc to tars and compress an empty archive
        SchubsArc.main(new String[] {dir + sep + "archive"});

        //Decompresses and untars the archive
        Deschubs.main(new String[] {dir + sep + "archive.zl"});
        
        //Makes sure there are still no files except the archive
        assertTrue(dir.list().length == 1);

        //Makes sure the archive has no files in it
        assertTrue(archive.length() == 0);
    }

    //Tests for LZW Runtime error if the file does not exist
    public void testFileCompress14() throws IOException {
        createTestDir();

        //Calls LZW with a file that does not exist
        String dir = directory + sep + "test14";
        String filename = "test14";
        try{
            SchubsL.main(new String[] {dir + sep + filename + ".txt"});
        } catch(Exception e){
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    //Tests for Huffman Runtime error if the file does not exist
    public void testFileCompress15() throws IOException {
        createTestDir();

        //Calls Huffman with a file that does not exist
        String dir = directory + sep + "test15";
        String filename = "test15";
        try{
            SchubsH.main(new String[] {dir + sep + filename + ".txt"});
        } catch(Exception e){
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    //Tests for Archive Runtime error if the archive does not exist
    public void testFileCompress16() throws IOException {
        createTestDir();

        //Calls Archive with a file that does not exist
        try{
            SchubsArc.main(new String[] {});
        } catch(Exception e){
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    //Tests for LZW Runtime error if there are no arguments
    public void testFileCompress17() throws IOException {
        createTestDir();

        //Calls LZW with no arguments
        try{
            SchubsL.main(new String[] {});
        } catch(Exception e){
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    //Tests for Huffman Runtime error if there are no arguments
    public void testFileCompress18() throws IOException {
        createTestDir();

        //Calls Huffman with no arguments
        try{
            SchubsH.main(new String[] {});
        } catch(Exception e){
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    //Tests Deschubs for a Runtime error with no arguments given
    public void testFileCompress19() throws IOException{
        createTestDir();

        //Calls Deschubs with no arguments
        try{
            Deschubs.main(new String[] {});
        } catch(Exception e){
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    //Tests Deschubs for a Runtime error with a non existent file
    public void testFileCompress20() throws IOException{
        createTestDir();
        
        //Calls Deschubs with a file that does not exist
        String dir = directory + sep + "test20";
        String filename = "test20";
        try{
            Deschubs.main(new String[] {dir + sep + filename + ".txt"});
        } catch(Exception e){
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }
}