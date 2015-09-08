import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.print.attribute.standard.RequestingUserName;

public class MP1 {
    
    Random generator;
    /** Username **/
    String userName;
    
    /** File Name **/
    String inputFileName;
    
    /** Word Count HashMap **/
    final HashMap<String, WordCount> wordCountMap = new HashMap<String, WordCount>();
    
    /** Hashmap of Common Words **/
    final HashMap<String, String>stopWordsHashMap = new HashMap<String,String>();
    
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    /**
     * Initializing the MP1
     * Creating the stopwordsHash
     * @param userName
     * @param inputFileName
     */
    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
        getStopWordsHashMap();
    }
    
    
    
    private List<String> readLineFromFile() throws Exception{
        // final List<String> fileContents = Files.readAllLines( FileSystems.getDefault().getPath(  inputFileName ) );
        final BufferedReader bfread = new BufferedReader( new FileReader( new File( inputFileName ) ) );
        final ArrayList<String> fileContents = new ArrayList<String>();
        String line = null;
        while ( ( line = bfread.readLine() ) != null ) {
            //process each line in some way
            fileContents.add( line );
          }
        bfread.close();
        return fileContents;
    }
    
    
    /**
     * Reading only Titles that are random generated based on seeding with my user Id
     * @return List of Strings of Titles that need to be processed
     */
    private List<String> readingTitlesForProcessing() throws Exception {
        // reading from file
        final List<String> fileContents = readLineFromFile();
        // getting the indices that are selected
        final Integer[] indices = getIndexes();
        // creating the List of Strings (titles) that need to be analyzed
        ArrayList<String> returnArrayList = new ArrayList<String>( indices.length );
        for( int index: indices ) {
            // converting it to lower case and adding it to the return arrayList
            returnArrayList.add( fileContents.get( index ).toLowerCase() );
        }
        return returnArrayList;
    }
    
    /**
     * To Make a hashmap of stopwords for faster processing
     * @return a hashmap containing the stopwords
     */
    private HashMap<String, String> getStopWordsHashMap() {
        for( String word: stopWordsArray ) {
            stopWordsHashMap.put(word, word);
        }
        return stopWordsHashMap;
    }
    
    /** Creating Object to store the WordCount **/
    final class WordCount {
        private String word;
        private int count;
        
        public WordCount( String word ) {
            this.word = word;
            count = 1;
        }
        
        public String getWord() {
            return word;
        }
        public void setWord(String word) {
            this.word = word;
        }
        public int getCount() {
            return count;
        }
        public void setCount(int count) {
            this.count = count;
        }
        
        public WordCount incrementCount() {
            count++;
            return this;
        }
        
        @Override
        public String toString(){
            return "Word: " + word + "\t" + "Count: " + count; 
        }
        
    }
    
    final class WordCountComparator implements Comparator<WordCount> {
        /** 
         * Method to compare first the count - to allow sorting in 
         * decreasing order of word count
         * If equal, then do lexigraphical ordering
         */
        @Override
        public final int compare(WordCount o1, WordCount o2) {
            final int diff = o2.count - o1.count;
            if( 0 != diff ) {
                return diff;
            }
            return o1.word.compareTo( o2.word );
        }
    }
    
    /**
     * Processing the word in Title
     * @param word
     * @return void
     */
    final private void processWord( final String word ) {
        if( null == stopWordsHashMap.get( word ) ) {
            WordCount wordCount = wordCountMap.get( word );
            if( null == wordCount ) {
                wordCount = new WordCount( word );
                wordCountMap.put( word, wordCount );
            } else {
                wordCount.incrementCount();
            }
        }
    }
    
    /** 
     * Processing the Title
     * @param title
     * @return void
     */
    final void process( final String title) {
        final StringTokenizer stringTokenizer = new StringTokenizer( title , delimiters);
        while ( stringTokenizer.hasMoreTokens() ) {
            final String word = stringTokenizer.nextToken().trim();
            processWord(word);
        }
    }
    
    /**
     * Calculating Word Count
     * @throws Exception
     * @return void
     */
    final private void calculatingWordCounts() throws Exception {
        final List<String> titlesForProcessing = readingTitlesForProcessing();
        for( String title: titlesForProcessing ) {
            process(title);
        }
    }
    
    /**
     * Get the top 20 words from the Hashmap of Word Count
     * @return a String array of size 20
     */
    final private String[] getTop20Words() {
        List<WordCount> worldCountList = new ArrayList<WordCount>( wordCountMap.values() );
        Collections.sort( worldCountList , new WordCountComparator() );
        final String[] ret = new String[20];
        for( int i= 0; i < 20; i++ ) {
            ret[ i ] = worldCountList.get( i ).getWord();
        }
        return ret;
    }

    /**
     * @return String[] of top 20 words
     * @throws Exception
     */
    final public String[] process() throws Exception {
        // String Tokenizer the words
        calculatingWordCounts();
        return getTop20Words();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}
