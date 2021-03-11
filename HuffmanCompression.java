import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

/**
 * Huffman Tree Node Class
 *
 * Contains all the information to instantiate a node object to be
 * put in a tree. Including the character the node represents,
 * the frequency of that character in the text, and the node's children
 * (if it has any)
 *
 * It implements serializable so any node objects can be stored to a file
 * and it's size analysed
 */
class HuffmanTreeNode implements Serializable{
    int frequency;
    char character;
    HuffmanTreeNode left;
    HuffmanTreeNode right;
}


/**
 * Huffman Compression Class
 *
 * Contains all the relevant code to compress and decompress a text file
 */
public class HuffmanCompression {
    private static ArrayList<String> dictionary = new ArrayList<>();
    private static HuffmanTreeNode finalRoot;

    /**
     * readTextFile
     * Reads each line in a text file and stores that string of a line into
     * an array list.
     * @param fileName is the text file you wish to read in
     * @return a list of lines of the file as strings
     */
    public static ArrayList<String> readTextFile(String fileName) throws FileNotFoundException {
        File myFile = new File(fileName);
        ArrayList<String> data= new ArrayList<>();

        Scanner fileReader = new Scanner(myFile);

        // read each line
        while (fileReader.hasNextLine()) {
            // store as a string in the data list
            data.add(fileReader.nextLine() + "§");
        }
        fileReader.close();

        return data;
    }

    /**
     * getListOfCharacters
     * Takes the lines of the file and produces a list of all the unique characters
     * @param file is the list of lines we read from the file using readTextFile
     * @return a character array list of all the unique characters
     */
    public static ArrayList<Character> getListOfCharacters(ArrayList<String> file){
        ArrayList<Character> chars = new ArrayList<>();

        for(String line : file) {
            // look through every character
            for (char character : line.toCharArray()) {
                // if its not already been stored, store it
                if (!chars.contains(character)) {
                    chars.add(character);
                }
            }
        }
        return chars;
    }

    /**
     * getListOfFrequencies
     * takes the unique list of characters and provides a new list of the
     * number of occurrences of each of these letters
     * @param file is the list of lines we read from the file using readTextFile
     * @param charArray  character array list of all the unique characters
     * @return is a character array storing all the corresponding frequencies of each character
     */
    public static ArrayList<Integer> getListOfFrequencies(ArrayList<String> file, ArrayList<Character> charArray){

        ArrayList<Integer> frequencies = new ArrayList<>();

        for(Character character : charArray){
            // check each character in the unique list
            int counter = 0;
            for(String line : file) {
                for (char fileChar : line.toCharArray()) {
                    // check against every character of the input file
                    if (fileChar == character) {
                        // add one to the frequency for every match
                        counter += 1;
                    }
                }
            }
            frequencies.add(counter);
        }

        return frequencies;
    }

    /**
     * sort
     * A selection sort algorithm which takes a list of all the created nodes
     * and sorts them from lowest frequency to highest
     * @param list an arraylist of unsorted huffman nodes
     * @return returns an arraylist of sorted huffman nodes
     */
    public static ArrayList<HuffmanTreeNode> sort(ArrayList<HuffmanTreeNode> list){

        // SELECTION SORT
        // lowest frequency node is selected from the unsorted list and swapped with the leftmost node

        for (int i = 0; i < list.size(); i++) {
            int newPosition = i;

            for (int j = i; j < list.size(); j++) {
                if (list.get(j).frequency < list.get(newPosition).frequency) {
                    newPosition = j;
                }
            }

            HuffmanTreeNode min = list.get(newPosition);
            list.set(newPosition, list.get(i));
            list.set(i, min);
        }
        return list;
    }

    /**
     * createHuffmanTree
     * will read the text file in, and will create a node for every character, put these in a list,
     * it will call the sorting algorithm on this list and build the huffman tree using huffman
     * encoding principles
     * @param fileName the name of the text file to compress
     */
    public static void createHuffmanTree(String fileName){
        ArrayList<String> nonCompressedFile = new ArrayList<>();

        // turn file into a list of lines as strings
        System.out.println("\nCreating Huffman tree ...\n");
        try {
            nonCompressedFile = readTextFile(fileName);
        }
        catch (FileNotFoundException e) {
            System.out.println("File Not Found");
            System.exit(0);
        }

        // create lists of the unique characters and their frequencies
        ArrayList<Character> charArray = getListOfCharacters(nonCompressedFile);
        ArrayList<Integer> charFreqArray = getListOfFrequencies(nonCompressedFile, charArray);
        int n = charArray.size();

        ArrayList<HuffmanTreeNode> list = new ArrayList<>();

        // create a huffman node for each character
        for (int i = 0; i < n; i++) {
            HuffmanTreeNode node = new HuffmanTreeNode();

            node.character = charArray.get(i);
            node.frequency = charFreqArray.get(i);

            node.left = null;
            node.right = null;

            list.add(node);
        }

        // sort the list based on frequencies
        list = sort(list);
        HuffmanTreeNode root = null;

        while (list.size() > 1) {

            // get first two (least frequent) nodes
            HuffmanTreeNode firstMinimum = list.get(0);
            list.remove(0);

            HuffmanTreeNode secondMinimum = list.get(0);
            list.remove(0);

            // join them together in a new node
            HuffmanTreeNode newJoinedNode = new HuffmanTreeNode();

            newJoinedNode.frequency = firstMinimum.frequency + secondMinimum.frequency;
            newJoinedNode.character = '-';
            // make the 2 nodes children of the new node
            newJoinedNode.left = firstMinimum;
            newJoinedNode.right = secondMinimum;

            // make the root this new node
            root = newJoinedNode;

            // add root to the list
            list.add(root);

            // sort the list
            list = sort(list);
        }

        if(root!= null) {
            // create the dictionary of codes and characters
            createDictionary(root, "");
        }
        else{
            System.out.println("File Empty or only contains one character");
            System.exit(0);
        }

        finalRoot = root;
    }

    /**
     * assignCodes
     * contains the main functionality for deriving a matching code for each character.
     * It will traverse the huffman tree by recursively calling itself with every root of the tree
     * @param root the root of a completely formed huffman tree
     * @param code an empty string to be built upon, eventually forming the code for a character
     */
    public static void createDictionary(HuffmanTreeNode root, String code){

        // check root isn't an end leaf
        if (root.left == null && root.right == null) {
            // a character is found, so append the dictionary list with the character and its code
            dictionary.add(root.character + code);
            return;
        }

        // traverse entire left side of the root
        createDictionary(root.left, code + "0");
        // traverse right side of the root
        createDictionary(root.right, code + "1");
    }

    /**
     * getBinary
     * will produce an array of bytes of the huffman encoding string
     * @param encodedString a string representing the entire encoded file
     * @return an array of bytes of this encoded string
     */
    public static byte[] getBinary(String encodedString){
        StringBuilder sBuilder = new StringBuilder(encodedString);
        while (sBuilder.length() % 8 != 0) {
            sBuilder.append('0');
        }
        encodedString = sBuilder.toString();

        // create array with enough room for string to be stored split into 8 bits
        byte[] data = new byte[encodedString.length() / 8];

        // split string into 8 bits per byte and store in array
        for (int i = 0; i < encodedString.length(); i++) {
            char c = encodedString.charAt(i);
            if (c == '1') {
                data[i >> 3] |= 0x80 >> (i & 0x7);
            }
        }
        return data;
    }

    /**
     * getString
     * will take an array of bytes of the encoded text file, and turn it into a string
     * @param bytes n array of bytes of this encoded string
     * @return a string representing the entire encoded file
     */
    static String GetString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);

        for (int i = 0; i < Byte.SIZE * bytes.length; i++) {
            // build string back of bits
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        }

        return sb.toString();
    }

    /**
     * compress
     * contains the main functionality of the compression algorithm, it will traverse the original
     * text file and match a code for each character, using the huffman tree. it will then append this
     * code to a long string, eventually forming a string of the entire text, now encoded. It will then
     * write this to a binary file.
     * @param fileName the name of the text file to compress
     */
    public static void compress(String fileName){
        ArrayList<String> nonCompressedFile = new ArrayList<>();

        // create list of lines in text file
        try {
            nonCompressedFile = readTextFile(fileName);
        }
        catch(IOException e){
            System.out.println("File Not Found");
            System.exit(0);
        }

        String codeWord = "";

        System.out.println("Translating file to binary codes ...\n");
        // translate each character into its corresponding code and append to the long codeword string
        for(String line : nonCompressedFile) {
            for (Character character : line.toCharArray()) {
                for (String translated : dictionary) {
                    if (translated.charAt(0) == character) {
                        codeWord = codeWord + translated.substring(1);
                    }
                }
            }
        }

        // create the name of the new compressed file
        String[] newFileNameArray = fileName.split("\\.");
        String newFileName = newFileNameArray[0] + "compressed.bin";
        String treeFile = newFileNameArray[0] + "HuffmanTree.ser";

        File byteFile = new File(newFileName);
        File huffmanFile = new File(treeFile);

        // convert codeword to bytes
        byte[] convertedToBytes = getBinary(codeWord);

        // write bytes to a binary file
        System.out.println("Writing to compressed file ...\n");
        try (FileOutputStream fos = new FileOutputStream(byteFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos))
        {
            bos.write(convertedToBytes);
        }
        catch (IOException e) {
            System.out.println("File Could Not Be Saved");
            System.exit(0);
        }

        // write huffman tree to a file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(huffmanFile)))
        {
            oos.writeObject(finalRoot);
        }
        catch (IOException e) {
            System.out.println("File Could Not Be Saved");
            System.exit(0);
        }
    }

    /**
     * decompress
     * the decompression algorithm, it will search the string of the encoded text file sequentially,
     * using the huffman tree to output the translated code into a new text file
     * @param fileName then name of the file that has been compressed
     * @param root the huffman tree used for compression
     */
    public static void decompress(String fileName, HuffmanTreeNode root){
        String decompressed = "";
        HuffmanTreeNode copiedRoot = root;

        System.out.println("\nReading in file to decompress ...\n");
        try {
            // read file of bytes
            FileInputStream fis = new FileInputStream(fileName);
            BufferedInputStream bis = new BufferedInputStream(fis);

            // translate byte array to a string
            byte[] allBytes = bis.readAllBytes();
            String bitString = GetString(allBytes);

            // translate back to characters by traversing the huffman tree
            System.out.println("Translating file ...\n");
            for(Character character : bitString.toCharArray()){
                if (character == '0') {
                    root = root.left;
                }
                else if(character == '1') {
                    root = root.right;
                }
                if(root.right == null && root.left == null){
                    if(root.character == '§'){
                        decompressed = decompressed + "\n";
                    }
                    else {
                        decompressed = decompressed + root.character;
                    }
                    root = copiedRoot;
                }
            }

            fis.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            // create name for new file
            String[] split = fileName.split("\\.");
            String newTextFileName = split[0]+"decompressed.txt";

            // write decompressed string back to a text file
            System.out.println("Writing to text file ...\n");
            FileWriter writer = new FileWriter(newTextFileName);
            writer.write(decompressed);
            writer.close();

        } catch (IOException e) {
            System.out.println("Couldn't Write to Text File");
        }

    }

    public static void main(String[] args){

        // get user to input a file name

        Scanner userInput = new Scanner(System.in);
        boolean keepGoing = true;

        while(keepGoing) {
            System.out.println("Please enter 1 to COMPRESS and 2 to DECOMPRESS");
            int input = userInput.nextInt();

            if (input == 1) {
                Scanner userInput1 = new Scanner(System.in);
                System.out.println("Please enter filename to compress:");
                String userFile = userInput1.nextLine();

                if (userFile.length() > 4 && userFile.endsWith(".txt")) {
                    try {
                        createHuffmanTree(userFile);
                        compress(userFile);
                        System.out.println("Compression Completed!\n");
                    } catch (Exception e) {
                        System.out.println("Please enter a valid file");
                    }
                } else {
                    System.out.println("Please enter a valid file");
                }
            } else if (input == 2) {
                Scanner userInput2 = new Scanner(System.in);
                System.out.println("Please enter filename to decompress:\n");
                String decompressFile = userInput2.nextLine();

                if (decompressFile.length() > 4 && decompressFile.endsWith(".bin")) {
                    try {
                        String treeFileName = decompressFile.substring(0, decompressFile.length() - 14);
                        treeFileName = treeFileName + "HuffmanTree.ser";

                        ObjectInputStream in = new ObjectInputStream(new FileInputStream(treeFileName));
                        Object obj = in.readObject();

                        if (obj instanceof HuffmanTreeNode) {
                            HuffmanTreeNode root = (HuffmanTreeNode) obj;
                            decompress(decompressFile, root);
                        }
                        in.close();

                        System.out.println("Decompression Completed!\n");
                    } catch (Exception e) {
                        System.out.println("Please enter a valid file");
                    }
                } else {
                    System.out.println("Please enter a valid file");
                }
            } else {
                System.exit(0);
            }
        }
    }
}
