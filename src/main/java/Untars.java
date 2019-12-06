/**
 * Software Engineering II
 * Fall 2019
 * Carson Kelley
 *
 *extracting files from an archive
 *execute: java Untars archive-name
 */

import java.io.IOException;
import java.io.File;

public class Untars
{
	public static void main(String[] args) throws IOException{
		BinaryIn in = null;
		BinaryOut out = null;

		char sep = (char) 255;  // all ones 11111111

		//nerf through archive, extracting files
		//int lengthoffilename, sep, filename, sep, lengthoffile, sep, bits

		try{
			in = new BinaryIn(args[0]);

			while(!in.isEmpty()){
				int filenamesize = in.readInt();
				sep = in.readChar();
				String filename = "";
				for(int i = 0; i < filenamesize; i++)
					filename += in.readChar();

				sep = in.readChar();
				long filesize = in.readLong();
				sep = in.readChar();

				out = new BinaryOut(filename);
				
				for(int i = 0; i < filesize; i++)
					out.write( in.readChar());

				if(out != null)
					out.close();
			}
		} finally{
			if(out != null)
				out.close();
		}
}
}