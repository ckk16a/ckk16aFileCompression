/**
 * Software Engineering II
 * Fall 2019
 * Carson Kelley
 *
 *begin to copy many files to one, long file
 *execute: java Tars archive-name file1 [file2...]
 */

import java.io.IOException;
import java.io.File;

public class Tarsn{
	public static void main(String[] args) throws IOException{
		File in1 = null;
		BinaryIn bin1 = null;
		BinaryOut out = null;

		char separator = (char) 255; // all ones 11111111

		out = new BinaryOut(args[0]);

		try{
			//Tars both files given
			for(int i = 1; i < args.length; i++)
			{
				//notice the input file start at arg[1]. not arg[0]
				in1 = new File(args[i]);
				if(!in1.exists() || !in1.isFile())
					throw new RuntimeException("One or more files does not exist.");

				long filesize = in1.length();
				int filenamesize = args[i].length();

				//archive file is at args[0]
				//layout: file-name-length, separator, filename, file-size, file

				out.write(filenamesize);
				out.write(separator);

				out.write(args[i]);
				out.write(separator);

				out.write(filesize);
				out.write(separator);

				bin1 = new BinaryIn(args[i]);
				while(!bin1.isEmpty()){
					out.write(bin1.readChar());
				}
				if(bin1 != null)
					bin1.close();
			}
		} finally{
			if(out != null)
				out.close();
		}
	}
}