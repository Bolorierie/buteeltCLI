import java.util.*;

public class RecentMistakesFirstSorter implements CardOrganizer {
    @Override
    public List<Flashcard> organize(List<Flashcard> cards) {
        cards.sort((c1, c2) -> Long.compare(c2.getLastMistakeTime(), c1.getLastMistakeTime()));
        return cards;
    }
}
