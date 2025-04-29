import java.io.*;
import java.util.*;

public class FlashcardApp {
    private List<Flashcard> flashcards = new ArrayList<>();

    public void run(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        // Check if --help is present
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

        // Parse options
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--order":
                    if (i + 1 >= args.length) {
                        System.out.println("Error: Missing value for --order option.");
                        return;
                    }
                    order = args[++i];
                    if (!order.equals("random") && !order.equals("worst-first") && !order.equals("recent-mistakes-first")) {
                        System.out.println("Error: Invalid value for --order. Allowed values: random, worst-first, recent-mistakes-first.");
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
                        if (repetitions < 1) {
                            throw new NumberFormatException();
                        }
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

        int cardIndex = 0;
        while (cardIndex < flashcards.size()) {
            Flashcard card = flashcards.get(cardIndex);
            int correctInRow = 0;
            int wrongAttempts = 0; // count wrong attempts for this card

            while (correctInRow < repetitions) {
                String question = invertCards ? card.getAnswer() : card.getQuestion();
                String answer = invertCards ? card.getQuestion() : card.getAnswer();

                System.out.println("Question: " + question);
                System.out.print("Your answer: ");
                String userAnswer = scanner.nextLine();

                if (userAnswer.trim().equalsIgnoreCase(answer.trim())) {
                    System.out.println("Correct!");
                    card.incrementCorrectCount();
                    correctInRow++;
                    wrongAttempts = 0; // reset wrong attempts
                } else {
                    System.out.println("Wrong. Correct answer: " + answer);
                    card.incrementMistakes();
                    wrongAttempts++;

                    // Reorganize flashcards using RecentMistakesFirstSorter
                    RecentMistakesFirstSorter sorter = new RecentMistakesFirstSorter();
                    flashcards = sorter.organize(flashcards);

                    // If too many wrong attempts, move to next card
                    if (wrongAttempts >= 3) {
                        System.out.println("(Too many wrong attempts, moving to next card)");
                        break;
                    }

                    // Restart from the first card
                    
                    break;
                }
                System.out.println();
            }
            cardIndex++;
        }

        scanner.close();
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
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private void organizeFlashcards(String order) {
        if (order.equals("random")) {
            Collections.shuffle(flashcards);
        } else if (order.equals("worst-first")) {
            flashcards.sort((c1, c2) -> Integer.compare(c2.getMistakes(), c1.getMistakes()));
        } else if (order.equals("recent-mistakes-first")) {
            RecentMistakesFirstSorter sorter = new RecentMistakesFirstSorter();
            flashcards = sorter.organize(flashcards);
        }
    }

    private void printUsage() {
        System.out.println("Usage: flashcard <cards-file> [options]");
        System.out.println("Options:");
        System.out.println("  --help                  Show help information");
        System.out.println("  --order <order>          Sorting type (default: random)");
        System.out.println("                           Options: random, worst-first, recent-mistakes-first");
        System.out.println("  --repetitions <num>      Set number of correct repetitions required per card (default: 1)");
        System.out.println("  --invertCards            Invert the questions and answers (default: false)");
    }
}
