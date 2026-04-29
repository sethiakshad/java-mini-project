import java.io.IOException;
import java.util.*;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    public static volatile boolean isPrintingUI = false;
    
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin123";
    public static final int MAX_OPTIONS = 4;

    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(40));
                System.out.println("      QUIZ MANAGEMENT SYSTEM ");
                System.out.println("=".repeat(40));
                System.out.println("1. Admin Login");
                System.out.println("2. Student Registration");
                System.out.println("3. Student Login");
                System.out.println("4. Exit");
                System.out.print("Choice: ");

                String choice = safeNextLine();
                if (choice == null) {
                    System.out.println("\nInput stream closed. Exiting...");
                    System.exit(0);
                }
                switch (choice) {
                    case "1": adminLogin(); break;
                    case "2": studentRegistration(); break;
                    case "3": studentLogin(); break;
                    case "4": 
                        System.out.println("Exiting... Goodbye!");
                        System.exit(0);
                    default: System.out.println("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred. Returning to main menu.");
            }
        }
    }
    private static String safeNextLine() {
        try {
            if (sc.hasNextLine()) {
                return sc.nextLine();
            }
        } catch (Exception e) {}
        return null;
    }
    private static int safeNextInt() {
        while (true) {
            String input = safeNextLine();
            if (input == null) {
                System.out.println("Input stream closed. Exiting...");
                System.exit(0);
            }
            try {
                return Integer.parseInt(input.trim());
            } catch (Exception e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
    private static void adminLogin() {
        System.out.print("Admin Username: ");
        String user = safeNextLine();
        System.out.print("Admin Password: ");
        String pass = safeNextLine();

        if (user == null || pass == null) return;

        if (ADMIN_USERNAME.equals(user) && ADMIN_PASSWORD.equals(pass)) {
            adminMenu();
        } else {
            System.out.println("Invalid Admin credentials!");
        }
    }
    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN PANEL ---");
            System.out.println("1. Create Quiz");
            System.out.println("2. View Leaderboard");
            System.out.println("3. Delete Current Quiz");
            System.out.println("4. Export Leaderboard (CSV)");
            System.out.println("5. Logout");
            System.out.print("Choice: ");

            String choice = safeNextLine();
            if (choice == null) return;

            switch (choice) {
                case "1": createQuiz(); break;
                case "2": viewLeaderboard(); break;
                case "3": 
                    QuizManager.getInstance().deleteQuiz();
                    System.out.println("Quiz and leaderboard have been reset.");
                    break;
                case "4": exportLeaderboard(); break;
                case "5": return;
                default: System.out.println("Invalid choice!");
            }
        }
    }
    private static void createQuiz() {
        if (QuizManager.getInstance().getCurrentQuiz() != null) {
            System.out.println("A quiz already exists. Please delete it first.");
            return;
        }
        try {
            System.out.print("Quiz Title: ");
            String title = safeNextLine();
            if (title == null) return;

            System.out.print("Number of Questions: ");
            int num = safeNextInt();
            System.out.print("Points for correct answer: ");
            int pos = safeNextInt();
            System.out.print("Negative points for wrong answer: ");
            int neg = safeNextInt();
            System.out.print("Time Limit (in seconds): ");
            int time = safeNextInt();
            System.out.print("Shuffle questions? (y/n): ");
            String shuffleIn = safeNextLine();
            boolean shuffle = "y".equalsIgnoreCase(shuffleIn);

            Quiz q = new Quiz(title, pos, neg, time, shuffle);
            q.createQuiz(); 

            for (int i = 0; i < num; i++) {
                System.out.println("\nAdding Question " + (i + 1) + ":");
                System.out.print("Question Text: ");
                String text = safeNextLine();
                if (text == null) return;

                String[] opts = new String[MAX_OPTIONS];
                for (int j = 0; j < MAX_OPTIONS; j++) {
                    System.out.print("Option " + (j + 1) + ": ");
                    opts[j] = safeNextLine();
                    if (opts[j] == null) return;
                }
                System.out.print("Correct Option (1-" + MAX_OPTIONS + "): ");
                int correct = safeNextInt();
                q.addQuestion(new Question(text, opts, correct));
            }
            QuizManager.getInstance().createQuiz(q);
            System.out.println("Quiz created successfully!");
        } catch (Exception e) {
            System.out.println("Error creating quiz.");
        }
    }
    private static void viewLeaderboard() {
        List<Student> leaderboard = QuizManager.getInstance().getLeaderboard();
        if (leaderboard.isEmpty()) {
            System.out.println("No results available.");
            return;
        }
        System.out.println("\n--- LEADERBOARD ---");
        System.out.printf("%-5s %-10s %-15s %-10s\n", "Rank", "MIS", "Name", "Score");
        int rank = 1;
        for (Student s : leaderboard) {
            System.out.printf("%-5d %-10s %-15s %-10.2f\n", 
                rank++, s.getMis(), s.getName(), s.getLastScore());
        }
    }

    private static void exportLeaderboard() {
        try {
            QuizManager.getInstance().exportLeaderboardToCSV("leaderboard.csv");
            System.out.println("Leaderboard exported to leaderboard.csv");
        } catch (IOException e) {
            System.out.println("Export failed.");
        }
    }
    private static void studentRegistration() {
        System.out.print("Enter MIS: ");
        String mis = safeNextLine();
        System.out.print("Enter Name: ");
        String name = safeNextLine();
        System.out.print("Enter Password: ");
        String pass = safeNextLine();

        if (mis == null || name == null || pass == null) return;

        if (QuizManager.getInstance().registerStudent(mis, name, pass)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Error: MIS already exists!");
        }
    }
    private static void studentLogin() {
        System.out.print("Enter MIS: ");
        String mis = safeNextLine();
        System.out.print("Enter Password: ");
        String pass = safeNextLine();

        if (mis == null || pass == null) return;

        Student s = QuizManager.getInstance().loginStudent(mis, pass);
        if (s != null) {
            studentMenu(s);
        } else {
            System.out.println("Invalid MIS or Password!");
        }
    }
    private static void studentMenu(Student student) {
        while (true) {
            System.out.println("\n--- STUDENT MENU (" + student.getName() + ") ---");
            if (student.isAttempted()) {
                System.out.println("Status: Quiz Attempted");
                System.out.println("Score: " + student.getLastScore());
                System.out.println("1. Logout");
            } else {
                System.out.println("1. Start Quiz");
                System.out.println("2. Logout");
            }
            System.out.print("Choice: ");

            String choice = safeNextLine();
            if (choice == null) return;
            if ("1".equals(choice)) {
                if (student.isAttempted()) return;
                else startQuiz(student);
            } else if ("2".equals(choice)) {
                return;
            }
        }
    }
    private static void startQuiz(Student student) {
        Quiz quiz = QuizManager.getInstance().getCurrentQuiz();
        if (quiz == null) {
            System.out.println("No active quiz.");
            return;
        }
        System.out.println("\n--- Instructions ---");
        quiz.displayQuiz(); 
        System.out.println("Type 'I AGREE' to begin.");
        String agree = safeNextLine();
        if (!"I AGREE".equals(agree)) {
            System.out.println("Agreement required.");
            return;
        }
        List<Question> questions = quiz.getQuestions();
        int score = 0;
        int total = questions.size();
        isPrintingUI = true;
        TimerThread timer = new TimerThread(quiz.getTimeLimit());
        timer.start();

        for (int i = 0; i < total; i++) {
            if (timer.isTimesUp()) break;

            isPrintingUI = true;
            Question q = questions.get(i);
            System.out.println("\n---------------------------------");
            System.out.println("Question " + (i + 1) + " / " + total);
            System.out.println("---------------------------------");
            System.out.println("Question: " + q.getText() + "\n");
            
            String[] opts = q.getOptions();
            for (int j = 0; j < MAX_OPTIONS; j++) {
                System.out.println((j + 1) + ". " + opts[j]);
            }
            System.out.println();

            int answer = -1;
            while (!timer.isTimesUp()) {
                System.out.print("\n\033[1A\033[KEnter your answer: ");
                isPrintingUI = false;
                
                String input = readInputWithTimer(timer);
                
                isPrintingUI = true;
                if (input == null) break; 

                try {
                    answer = Integer.parseInt(input.trim());
                    if (answer >= 1 && answer <= MAX_OPTIONS) break;
                    System.out.println("Invalid option.");
                } catch (Exception e) {
                    System.out.println("Enter a number.");
                }
            }
            if (timer.isTimesUp()) break;

            if (answer == q.getCorrectOption()) score += quiz.getPositivePoints();
            else score -= quiz.getNegativePoints();
        }
        timer.stopTimer();
        isPrintingUI = true;
        double max = total * quiz.getPositivePoints();
        double pct = (max == 0) ? 0 : (double) score / max * 100;
        student.setResults(score, pct);

        System.out.println("\n--- QUIZ ENDED ---");
        System.out.println("Score: " + score);
    }
    private static String readInputWithTimer(TimerThread timer) {
        return safeNextLine();
    }
}