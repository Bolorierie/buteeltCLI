public class Flashcard {
    private String question;
    private String answer;
    private int mistakes;
    private int correctCount;
    private long lastMistakeTime; // NEW

    public Flashcard(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.mistakes = 0;
        this.correctCount = 0;
        this.lastMistakeTime = 0;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getMistakes() {
        return mistakes;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public long getLastMistakeTime() { // NEW
        return lastMistakeTime;
    }

    public void incrementMistakes() {
        mistakes++;
        lastMistakeTime = System.currentTimeMillis(); // mark the time of mistake
    }

    public void incrementCorrectCount() {
        correctCount++;
    }
}
