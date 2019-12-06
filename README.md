# ckk16a
File Compression with Huffman, LZW, and Tar/UnTar functionality

# Testing instructions:
-Clone repository into a local repository
-Navigate to project folder containing the src folder with the "cd" command (ex. cd ckk16aFileCompression)
-Type "mvn test" and hit enter

# Design:
LZW and Huffman can have various tradeoffs, including Huffman sometimes having a larger tree than the file itself. So with Huffman, 
spacing can be an issue with smaller files. Huffman makes a bunch of codewords for letters and symbols and stores them in a tree
whereas LZW makes a dictionary of repeated strings, so LZW is generally better for smaller files and files with more repetition in words. 
Huffman will work better if the words seem more randomized or if the file is a series of random characters.

# CLI:
## Expanding:
-Deschubs: java Deschubs <filename>.hh|ll/archive.zl
 Deschubs uncompresses Huffman or LZW compressed files and archives

## Compressing: 
-SchubsH: java SchubsH <filename>/<GLOB>
 SchubsH compresses a file or GLOB of files using a Huffman tree
 
-SchubsL: java SchubsL <filename>/<GLOB>
 SchubsL compresses a file or GLOB of files using LZW
 
-SchubsArc: java SchubsArc <archive-name> *.txt
 SchubsArc tars a group of files and compresses the tar file
