import java.io.*;
import java.util.*;

public class FlashcardApp {
    private List<Flashcard> flashcards = new ArrayList<>();

    public void run(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        for (String arg : args) {
            if (arg.equals("--help")) {
                printUsage();
                return;
            }
        }

        String fileName = args[0];
        String order = "random";
        int repetitions = 1;
        boolean invertCards = false;

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--order":
                    if (i + 1 >= args.length) {
                        System.out.println("Error: Missing value for --order option.");
                        return;
                    }
                    order = args[++i];
                    if (!order.equals("random") && !order.equals("worst-first") && !order.equals("recent-mistakes-first")) {
                        System.out.println("Error: Invalid value for --order.");
                        return;
                    }
                    break;
                case "--repetitions":
                    if (i + 1 >= args.length) {
                        System.out.println("Error: Missing value for --repetitions option.");
                        return;
                    }
                    try {
                        repetitions = Integer.parseInt(args[++i]);
                        if (repetitions < 1) throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        System.out.println("Error: --repetitions must be a positive integer.");
                        return;
                    }
                    break;
                case "--invertCards":
                    invertCards = true;
                    break;
                default:
                    System.out.println("Error: Unknown option " + args[i]);
                    return;
            }
        }

        loadFlashcards(fileName);
        organizeFlashcards(order);

        Scanner scanner = new Scanner(System.in);
        Map<Flashcard, Boolean> answeredCorrectly = new HashMap<>();
        for (Flashcard card : flashcards) {
            answeredCorrectly.put(card, false);
        }

        for (int round = 1; round <= repetitions; round++) {
            System.out.println("=== Round " + round + " ===");

            List<Flashcard> roundCards = new ArrayList<>(flashcards);
            if (order.equals("recent-mistakes-first")) {
                roundCards = new RecentMistakesFirstSorter().organize(roundCards);
            }

            for (Flashcard card : roundCards) {
                String question = invertCards ? card.getAnswer() : card.getQuestion();
                String answer = invertCards ? card.getQuestion() : card.getAnswer();

                System.out.println("Question: " + question);
                System.out.print("Your answer: ");
                String userAnswer = scanner.nextLine();

                if (userAnswer.trim().equalsIgnoreCase(answer.trim())) {
                    System.out.println("Correct!");
                    card.incrementCorrectCount();
                    answeredCorrectly.put(card, true);
                } else {
                    System.out.println("Wrong. Correct answer: " + answer);
                    card.incrementMistakes();
                }
                System.out.println();
            }
        }

        scanner.close();

        int totalScore = 0;
        for (Map.Entry<Flashcard, Boolean> entry : answeredCorrectly.entrySet()) {
            if (entry.getValue()) totalScore++;
        }

        System.out.println("Session complete.");
        System.out.println("Score: " + totalScore + " out of " + flashcards.size());
        saveFlashcards(fileName);
        
        AchievementTracker tracker = new AchievementTracker();
        tracker.updateAchievements(flashcards);
        tracker.displayAchievements();
    }

    private void loadFlashcards(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    flashcards.add(new Flashcard(parts[0].trim(), parts[1].trim()));
                } else if (parts.length == 4) {
                    String q = parts[0].trim();
                    String a = parts[1].trim();
                    int mistakes = Integer.parseInt(parts[2].trim());
                    long lastTime = Long.parseLong(parts[3].trim());
                    flashcards.add(new Flashcard(q, a, mistakes, lastTime));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    private void saveFlashcards(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Flashcard card : flashcards) {
                writer.println(card.serialize());
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
    

    private void organizeFlashcards(String order) {
        if (order.equals("random")) {
            Collections.shuffle(flashcards);
        } else if (order.equals("worst-first")) {
            flashcards.sort((c1, c2) -> Integer.compare(c2.getMistakes(), c1.getMistakes()));
        } else if (order.equals("recent-mistakes-first")) {
            flashcards = new RecentMistakesFirstSorter().organize(flashcards);
        }
    }

    private void printUsage() {
        System.out.println("Usage: flashcard <cards-file> [options]");
        System.out.println("Options:");
        System.out.println("  --help                  Show help information");
        System.out.println("  --order <order>         Sorting type (default: random)");
        System.out.println("                          Options: random, worst-first, recent-mistakes-first");
        System.out.println("  --repetitions <num>     Set number of times each card appears (default: 1)");
        System.out.println("  --invertCards           Invert the questions and answers (default: false)");
    }
}
