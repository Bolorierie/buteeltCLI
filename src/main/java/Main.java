public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No flashcards file provided.");
            System.out.println("Usage: flashcard <cards-file> [options]");
            System.out.println("Use --help for more information.");
            return;
        }

        FlashcardApp app = new FlashcardApp();
        app.run(args);
    }
}
