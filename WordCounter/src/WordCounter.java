import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Reads a user input file, counts the appearance of each individual word in the
 * file, and outputs a table.
 *
 * @author Sarah Schriner
 *
 */
public final class WordCounter {

    /**
     * Compare {@code String}s in lexicographic order.
     */
    private static class StringLT implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private WordCounter() {
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    public static void generateElements(String str, Set<Character> charSet) {
        assert str != null : "Violation of: str is not null";
        assert charSet != null : "Violation of: charSet is not null";

        // charInStr will be a given character in the given String 'str'
        char charInStr;
        // Ensure charSet in empty before added to it
        charSet.clear();

        // Loops until each character in String 'str' has been analyzed
        for (int i = 0; i < str.length(); i++) {
            if (!charSet.contains(str.charAt(i))) {
                charInStr = str.charAt(i);
                charSet.add(charInStr);
            }
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     * text[position, position + |nextWordOrSeparator|) and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     * entries(nextWordOrSeparator) intersection separators = {} and
     * (position + |nextWordOrSeparator| = |text| or
    * entries(text[position, position + |nextWordOrSeparator| + 1))
    * intersection separators /= {})
     * else
     * entries(nextWordOrSeparator) is subset of separators and
     * (position + |nextWordOrSeparator| = |text| or
     * entries(text[position, position + |nextWordOrSeparator| + 1))
     * is not subset of separators)
     * </pre>
     */
    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int i = 0;
        char charInText;
        String returnedStr = "";
        /*
         * If a character in String 'text' has a separator in it, the returned
         * string will be all of those separators
         */
        if (separators.contains(text.charAt(position))) {
            while (i < text.substring(position, text.length()).length()) {
                charInText = text.charAt(position + i);
                if (separators.contains(text.charAt(position + i))) {
                    returnedStr += charInText;
                    i++;
                } else {
                    i = text.substring(position, text.length()).length();
                }
            }
            /*
             * If a character in String 'text' does not have a separator in it,
             * the returned string will be the word until the next separator
             */
        } else {
            while (i < text.substring(position, text.length()).length()) {
                charInText = text.charAt(position + i);
                if (!separators.contains(text.charAt(position + i))) {
                    returnedStr += charInText;
                    i++;
                } else {
                    i = text.substring(position, text.length()).length();
                }
            }
        }

        // Return either the word or the separators
        return returnedStr;
    }

    /**
     * Finds the words and the amount of their appearances from the given
     * {@code clientFile} and adds them to the given {@code wordCounts}.
     *
     *
     * @param clientFile
     *            The given {@code SimpleReader} to read from
     * @param wordCounts
     *            The {@code Map} to hold the words and their counts
     * @param words
     *            The {@code Queue} to hold all of the words in the file
     * @requires clientFile is not empty
     * @ensures wordCounts = {wordOrSeparator, counts} from clientFile
     *
     */
    public static void createWordCountsMap(SimpleReader clientFile,
            Map<String, Integer> wordCounts, Queue<String> words) {
        assert clientFile != null : "Violation of: clientFile is not null";

        /*
         * Construct actual separator set--these are the only characters that
         * should be in the separator set
         */
        final String separatorStr = " , . - ";
        Set<Character> separatorSet = new Set1L<Character>();
        generateElements(separatorStr, separatorSet);

        // Loops until the end of clientFile is reached
        while (!clientFile.atEOS()) {
            // Position will be a character location in String 'text'
            int position = 0;
            String text = clientFile.nextLine();
            // Loops until each word and separator in text has been looked at
            while (text.length() > position) {
                /*
                 * wordOrSeparator will be either the next word or separator
                 * found in String 'text'
                 */
                String wordOrSeparator = nextWordOrSeparator(text, position,
                        separatorSet);
                // Checks whether wordOrSeparator is a word or separator
                if (!separatorSet.contains(wordOrSeparator.charAt(0))) {
                    // If wordOrSeparator is a word, it is added to the Queue
                    words.enqueue(wordOrSeparator);
                    /*
                     * If the word is not in the map, it is added with a
                     * starting count of 1
                     */
                    if (!wordCounts.hasKey(wordOrSeparator)) {
                        wordCounts.add(wordOrSeparator, 1);
                        /*
                         * If the word is already in the map, its corresponding
                         * value is increased by one.
                         */
                    } else {
                        wordCounts.replaceValue(wordOrSeparator,
                                wordCounts.value(wordOrSeparator) + 1);
                    }
                }
                position += wordOrSeparator.length();
            }
        }
    }

    /**
     * Removes duplicate words in the give {@code words).
     *
     * @param words The given {@code Queue} to remove duplicates from
     *
     * @updates words
     *
     * @requires |words| != 0
     * @ensures words = #words without duplicates
     */
    public static void removeDuplicates(Queue<String> words) {
        assert words != null : "Violation of: words is not null";
        // wordsTemp will be the queue with no duplicates
        Queue<String> wordsTemp = new Queue1L<String>();
        // inQueue will be whether a word is already in wordsTemp
        boolean inQueue = false;
        // Loops until every element in words has been seen
        while (words.length() > 0) {
            String removed = words.dequeue();
            inQueue = false;
            // Loops until every element in wordsTemp has been seen
            for (int i = 0; i < wordsTemp.length(); i++) {
                String front = wordsTemp.dequeue();
                if (front.equals(removed)) {
                    inQueue = true;
                }
                wordsTemp.enqueue(front);
            }
            if (!inQueue) {
                wordsTemp.enqueue(removed);
            }
        }
        words.transferFrom(wordsTemp);
    }

    /**
     * Creates the html document.
     *
     * @param wordCounts
     *            The given {@code Map} with the words and their counts.
     * @param index
     *            The given {@code SimpleWriter} to write the html page on
     * @param words
     *            The given {@code Queue} with the list of words
     * @param clientFileName
     *            The given {@code String} to label index page
     * @requires index.isOpen() |wordCounts| != 0
     */
    public static void createIndexPage(Map<String, Integer> wordCounts,
            SimpleWriter index, Queue<String> words, String clientFileName) {
        assert index.isOpen() : "Violation of: Output file is open";
        assert wordCounts != null : "Violation of: wordCounts is not null";

        // Outputs the opening tags to the index
        index.println("<?xml version='1.0' encoding='ISO-8859-1' ?>");
        index.println("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN'"
                + " 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>");
        index.println("<html xmlns='http://www.w3.org/1999/xhtml'>");
        index.println("<head>");
        index.println("<meta http-equiv='Content-Type'"
                + " content='text/html; charset=UTF-8' />");

        // Outputs the title of the page
        index.println("<title>WordCounts</title>");

        index.println("</head>");
        index.println("<body>");

        // Outputs page heading
        index.println("<h1>Words Counted in " + clientFileName + "</h1>");
        // This is a horizontal line
        index.println(
                "<hr style=\"height:2px;color:purple;background-color:purple\"></hr>");

        // Creates the word table
        index.println("<table border=\"1\">");
        index.println("<tr>");
        index.println("<th>Words</th>");
        index.println("<th>Counts</th>");
        index.println("</tr>");

        for (int i = 0; i < wordCounts.size(); i++) {
            String currentWord = words.front();
            words.rotate(1);
            index.println("<tr>");
            index.println("<td>" + currentWord + "</td>");
            index.println("<td>" + wordCounts.value(currentWord) + "</td>");
            index.println("</tr>");
        }
        index.println("</table>");

        // Output the closing tags of body and html
        index.println("</body>");
        index.println("</html>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        // Prompt the user for an input file
        out.println("Please enter the name of the input file: ");
        String clientFileName = in.nextLine();

        // Input File
        SimpleReader clientFile = new SimpleReader1L(clientFileName);

        // Prompt the user for an output file
        out.println("Please enter the name of the output file: ");
        String outputFileName = in.nextLine();

        // Map of the words and their counts
        Map<String, Integer> wordCounts = new Map1L<String, Integer>();

        // Queue of the words in the input file
        Queue<String> words = new Queue1L<String>();

        // Create the wordCounts map
        createWordCountsMap(clientFile, wordCounts, words);

        // Remove the duplicate words in words Queue
        removeDuplicates(words);

        // Sort the words alphabetically
        Comparator<String> order = new StringLT();
        words.sort(order);

        // Adds .html if the string doesn't already contain it
        if (!outputFileName.endsWith(".html")) {
            outputFileName += ".html";
        }

        // Adds data/ if the string doesn't already contain it
        if (!outputFileName.startsWith("data/")) {
            outputFileName = "data/" + outputFileName;
        }

        // Create the index page
        SimpleWriter index = new SimpleWriter1L(outputFileName);
        // Call createIndex() to create the index page
        createIndexPage(wordCounts, index, words, clientFileName);

        /*
         * Close input and output streams
         */
        in.close();
        out.close();
        clientFile.close();
        index.close();
    }

}
