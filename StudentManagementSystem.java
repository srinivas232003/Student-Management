import java.sql.*;
import java.util.Scanner;

 class StudentManagementSystem {
    private static final String DATABASE_URL = "jdbc:sqlite:students.db";
    private static Connection conn;

    public static void main(String[] args) {
        try {
            connectDatabase();
            createTables();
            runApp();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    // Connect to SQLite database
    public static void connectDatabase() {
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Close the database connection
    public static void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Create the student and grades tables if they don't exist
    public static void createTables() {
        String studentTable = "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "attendance INTEGER NOT NULL" +
                ");";

        String gradesTable = "CREATE TABLE IF NOT EXISTS grades (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id INTEGER," +
                "subject TEXT NOT NULL," +
                "grade INTEGER NOT NULL," +
                "FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(studentTable);
            stmt.execute(gradesTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Main menu to run the application
    public static void runApp() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nStudent Management System:");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Add Grade");
            System.out.println("4. View Grades");
            System.out.println("5. Update Attendance");
            System.out.println("6. Delete Student");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addStudent(scanner);
                    break;
                case 2:
                    viewAllStudents();
                    break;
                case 3:
                    addGrade(scanner);
                    break;
                case 4:
                    viewGrades(scanner);
                    break;
                case 5:
                    updateAttendance(scanner);
                    break;
                case 6:
                    deleteStudent(scanner);
                    break;
                case 7:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Add a new student
    public static void addStudent(Scanner scanner) {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        System.out.print("Enter attendance: ");
        int attendance = scanner.nextInt();

        String sql = "INSERT INTO students(name, attendance) VALUES(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, attendance);
            pstmt.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all students
    public static void viewAllStudents() {
        String sql = "SELECT * FROM students";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nStudents:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Attendance: " + rs.getInt("attendance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a grade for a student
    public static void addGrade(Scanner scanner) {
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter subject: ");
        String subject = scanner.nextLine();
        System.out.print("Enter grade: ");
        int grade = scanner.nextInt();

        String sql = "INSERT INTO grades(student_id, subject, grade) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, subject);
            pstmt.setInt(3, grade);
            pstmt.executeUpdate();
            System.out.println("Grade added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View grades for a specific student
    public static void viewGrades(Scanner scanner) {
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();

        String sql = "SELECT subject, grade FROM grades WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nGrades for student ID " + studentId + ":");
            while (rs.next()) {
                System.out.println("Subject: " + rs.getString("subject") + ", Grade: " + rs.getInt("grade"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update attendance for a student
    public static void updateAttendance(Scanner scanner) {
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        System.out.print("Enter new attendance: ");
        int attendance = scanner.nextInt();

        String sql = "UPDATE students SET attendance = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendance);
            pstmt.setInt(2, studentId);
            pstmt.executeUpdate();
            System.out.println("Attendance updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a student
    public static void deleteStudent(Scanner scanner) {
        System.out.print("Enter student ID to delete: ");
        int studentId = scanner.nextInt();

        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.executeUpdate();
            System.out.println("Student deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
