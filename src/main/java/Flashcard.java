public class Flashcard {
    private String question;
    private String answer;
    private int mistakes;
    private int correctCount;
    private long lastMistakeTime;

    public Flashcard(String question, String answer) {
        this(question, answer, 0, 0);
    }

    public Flashcard(String question, String answer, int mistakes, long lastMistakeTime) {
        this.question = question;
        this.answer = answer;
        this.mistakes = mistakes;
        this.correctCount = 0;
        this.lastMistakeTime = lastMistakeTime;
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

    public long getLastMistakeTime() {
        return lastMistakeTime;
    }

    public void incrementMistakes() {
        mistakes++;
        lastMistakeTime = System.currentTimeMillis();
    }

    public void incrementCorrectCount() {
        correctCount++;
    }

    public String serialize() {
        return question + "|" + answer + "|" + mistakes + "|" + lastMistakeTime;
    }
}
