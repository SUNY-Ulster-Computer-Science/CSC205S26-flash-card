import java.io.*;
import java.util.*;

/**
 * CardStorage - handles saving and loading flash cards to/from a file.
 * Cards are stored in a simple pipe-delimited text format.
 */
public class CardStorage {

    private static final String DEFAULT_FILE = "flashcards.dat";

    public static void saveCards(List<FlashCard> cards, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (FlashCard card : cards) {
                // Escape any pipe characters in question/answer to avoid parse issues
                String q = card.getQuestion().replace("|", "\\|");
                String a = card.getAnswer().replace("|", "\\|");
                writer.write(q + "|" + a);
                writer.newLine();
            }
        }
    }

    public static List<FlashCard> loadCards(String filename) throws IOException {
        List<FlashCard> cards = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return cards;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split on unescaped pipe only
                String[] parts = line.split("(?<!\\\\)\\|", 2);
                if (parts.length == 2) {
                    String q = parts[0].replace("\\|", "|");
                    String a = parts[1].replace("\\|", "|");
                    cards.add(new FlashCard(q, a));
                }
            }
        }
        return cards;
    }

    public static void saveCards(List<FlashCard> cards) throws IOException {
        saveCards(cards, DEFAULT_FILE);
    }

    public static List<FlashCard> loadCards() throws IOException {
        return loadCards(DEFAULT_FILE);
    }
}
