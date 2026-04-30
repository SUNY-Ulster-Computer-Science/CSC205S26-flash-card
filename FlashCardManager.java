import java.io.*;
import java.util.*;

public class FlashCardManager {
    private List<FlashCard> cards;

    public FlashCardManager() {
        cards = new ArrayList<>();
    }

    public void addCard(String q, String a) {
        cards.add(new FlashCard(q, a));
    }

    public void deleteCard(int index) {
        if (index >= 0 && index < cards.size()) {
            cards.remove(index);
        }
    }

    public void updateCard(int index, String q, String a) {
        if (index >= 0 && index < cards.size()) {
            cards.get(index).setQuestion(q);
            cards.get(index).setAnswer(a);
        }
    }

    public List<FlashCard> getCards() {
        return cards;
    }

    public int getCardCount() {
        return cards.size();
    }

    public void shuffleCards() {
        Collections.shuffle(cards);
    }

    // Save all cards to disk
    public void save() throws IOException {
        CardStorage.saveCards(cards);
    }

    // Load cards from disk (replaces current list)
    public void load() throws IOException {
        cards = CardStorage.loadCards();
    }
}
