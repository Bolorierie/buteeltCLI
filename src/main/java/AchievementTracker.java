import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AchievementTracker {
    private Map<AchievementType, Boolean> achievements = new HashMap<>();

    public void updateAchievements(List<Flashcard> cards) {
        boolean allCorrect = true;

        for (Flashcard card : cards) {
            if (card.getMistakes() > 0) {
                allCorrect = false;
            }

            if (card.getMistakes() >= 5) {
                achievements.put(AchievementType.REPEAT, true);
            }

            if (card.getCorrectCount() >= 3) {
                achievements.put(AchievementType.CONFIDENT, true);
            }
        }

        if (allCorrect) {
            achievements.put(AchievementType.CORRECT, true);
        }
    }

    public void displayAchievements() {
        System.out.println("Achievements unlocked:");
        if (achievements.isEmpty()) {
            System.out.println("  None yet.");
        } else {
            for (Map.Entry<AchievementType, Boolean> entry : achievements.entrySet()) {
                if (entry.getValue()) {
                    System.out.println("  - " + entry.getKey());
                }
            }
        }
    }
}
