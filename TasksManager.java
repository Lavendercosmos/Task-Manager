import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

class Task {
    String name;
    String description;
    boolean isComplete;
    String deadline;

    Task(String name, String description, boolean isComplete, String deadline) {
        this.name = name;
        this.description = description;
        this.isComplete = isComplete;
        this.deadline = deadline;
    }

}

class User {
    String username;
    String password;
    ArrayList<Task> tasks;

    User(String username, String password) {
        this.username = username;
        this.password = password;
        this.tasks = loadTasks();
    }

    // load tasks from saved file
    private ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        String fileName = username + "_tasks.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    Task task = new Task(parts[0], parts[1], Boolean.parseBoolean(parts[2]),parts[3]);
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            // Handle IOException
            // If the file doesn't exist tasks will be an empty list.
        }

        return tasks;
    }

    //save task to file
    public void saveTasks() {
        String fileName = username + "_tasks.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Task task : tasks) {
                writer.println(task.name + "," + task.description + "," + task.isComplete + "," + task.deadline);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


public class TasksManager {
    private static final String USERFILE = "usersCredentials.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<User> users = loadUsers();
        User currentUser = null;
        clearScreen();

        System.out.println("\n*        Task Manager        *");
        while (true) {
            if (currentUser == null) {
                System.out.println("\n-------------------------");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Exit");
                System.out.println("-------------------------");
                System.out.print("Please select between option 1-3: ");

                try {
                    int initialChoice = scanner.nextInt();
                    scanner.nextLine();
                    clearScreen();

                    if (initialChoice == 1 || initialChoice == 2 || initialChoice == 3) {

                        switch (initialChoice) {
                            case 1: // login
                                currentUser = login(scanner, users);
                                break;

                            case 2: // register
                                register(scanner, users);
                                break;

                            case 3: // exit program
                                existTaskManager();

                            default:
                                System.out.println("\nInvalid choice. Please enter a valid option");
                        }
                    } else {
                        System.out.println("\nInvalid choice. Please enter between 1-3 options");
                    }
                } catch (java.util.InputMismatchException e) {
                    clearScreen();
                    scanner.nextLine(); // Consume the invalid input 
                    System.out.println("    \n    Invalid choice. Please enter between 1-3 options");
                    System.out.println("\n         Task Manager");
                }
            } else {
                System.out.println("\n----------------------------");
                System.out.println("1. Add Task");
                System.out.println("2. Edit Task");
                System.out.println("3. Mark Task as Complete");
                System.out.println("4. View Tasks");
                System.out.println("5. Delete Task");
                System.out.println("6. Logout");
                System.out.println("7. Exit");
                System.out.println("----------------------------");
                System.out.print("Please select between option 1-7: ");
                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    clearScreen();

                    if (choice == 1 || choice == 2 || choice == 3 || choice == 4 || choice == 5 || choice == 6
                            || choice == 7) {
                        switch (choice) {
                            case 1: // add task
                                clearScreen();
                                if (currentUser != null) {
                                    addTask(currentUser, scanner);
                                }
                                break;

                            case 2: // edit task
                                clearScreen();
                                if (currentUser != null) {
                                    editTask(currentUser, scanner);
                                }
                                break;

                            case 3: // mark task as complete
                                clearScreen();
                                if (currentUser != null) {
                                    markTask(currentUser, scanner);
                                }
                                break;

                            case 4: // view task
                                clearScreen();
                                if (currentUser != null) {
                                    if (!currentUser.tasks.isEmpty()) {
                                        viewTasks(currentUser);
                                    } else {
                                        System.out.println("\nTask: Task is empty");
                                    }
                                }
                                break;

                            case 5: // delete task
                                clearScreen();
                                if (currentUser != null) {
                                    deleteTask(currentUser, scanner);
                                }
                                break;

                            case 6: // logout
                                clearScreen();
                                if (currentUser != null) {
                                    logOut(currentUser);
                                }
                                break;

                            case 7: // exit program
                                existTaskManager();

                            default:
                                break;
                        }
                    } else {
                        System.out.println("\nInvalid choice. Please enter a valid option");
                    }
                } catch (java.util.InputMismatchException e) {
                    clearScreen();
                    scanner.nextLine(); // Consume the invalid input 
                    System.out.println("\nInvalid choice. Please enter a valid option");
                    System.out.println("\n         Task Manager");
                }
            }
        }
    }//End main method


    //Clear the screen
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


    //Load user from saved file
    private static ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(USERFILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.add(new User(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }


    //Save user to the database
    private static void saveUsers(ArrayList<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERFILE))) {
            for (User user : users) {
                writer.println(user.username + "," + user.password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //find user from data base
    private static User findUser(String username, ArrayList<User> users) {
        for (User user : users) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }


    //register method
    private static void register(Scanner scanner, ArrayList<User> users) {

        System.out.print("Enter new username: ");
        String registerUsername = scanner.nextLine();
        while (registerUsername.isEmpty()) {
            System.out.println("Username cannot be empty.");
            System.out.print("Enter username: ");
            registerUsername = scanner.nextLine();
        }

        System.out.print("Enter password: ");
        String registerPassword = scanner.nextLine();
        while (registerPassword.isEmpty()) {
            System.out.println("Password cannot be empty.");
            System.out.print("Enter password: ");
            registerPassword = scanner.nextLine();
        }

        User registerUser = findUser(registerUsername, users);
        clearScreen();

        if (registerUser == null) {
            registerUser = new User(registerUsername, registerPassword);
            users.add(registerUser);
            saveUsers(users);
            System.out.println("\nAccount created successfully.");
        } else {
            System.out.println("\nUsername already exists. Please enter another username");
        }
    }//End register method


    //Login method
    private static User login(Scanner scanner, ArrayList<User> users) {
        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();
        while (loginUsername.isEmpty()) {
            System.out.println("Username cannot be empty.");
            System.out.print("Enter username: ");
            loginUsername = scanner.nextLine();
        }
        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();
        while (loginPassword.isEmpty()) {
            System.out.println("Password cannot be empty.");
            System.out.print("Enter password: ");
            loginPassword = scanner.nextLine();
        }
        User loginUser = findUser(loginUsername, users);
        clearScreen();
        if (loginUser != null && loginUser.password.equals(loginPassword)) {
            System.out.println("\n    Login successful.");
            System.out.println("\n    Current user: " + loginUsername);
            return loginUser;
        } else {
            System.out.println("\nIncorrect username or password. Please try again");
            return null;
        }
    }//End login method


    //Add task
    public static void addTask(User currentUser, Scanner scanner) {
        System.out.println("       Add task       ");
        System.out.println("-------------------------");
        System.out.print("Enter name task: ");
        String nameTask = scanner.nextLine();
        while (nameTask.isEmpty()) {
            System.out.println("please input the required task name");
            System.out.print("Enter name task: ");
            nameTask = scanner.nextLine();
        }

        System.out.print("Enter task description: ");
        String taskDescription = scanner.nextLine();
        while (taskDescription.isEmpty()) {
            System.out.println("please input the required task description");
            System.out.print("Enter task description: ");
            taskDescription = scanner.nextLine();
        }

        System.out.print("Enter deadline: ");
        String deadline = scanner.nextLine();
        while (taskDescription.isEmpty()) {
            System.out.println("please input the required deadline");
            System.out.print("Enter task deadline: ");
            deadline = scanner.nextLine();
        }
        Task newTask = new Task(nameTask, taskDescription, false, deadline); 
        
        currentUser.tasks.add(newTask);
        currentUser.saveTasks();
        clearScreen();
        System.out.println("\n  Task added successfully.");
    }//End add task method


    //check if the number is a valid index in arraylist
    private static boolean isValidIndex(User currentUser, int index) {
        try {
            return index > 0 && index <= currentUser.tasks.size();
        } catch (NumberFormatException e) {
            return false;
        }
    }


    //view task 
    private static void viewTasks(User currentUser) {
        clearScreen();
        System.out.println("\nTask: ");
        for (int i = 0; i < currentUser.tasks.size(); i++) {
            Task task = currentUser.tasks.get(i);
            String status = task.isComplete ? "Complete" : "In Progress";
            String deadline = task.deadline != null ? task.deadline : "Not set";
            System.out.println("-------------------------------------------");
            System.out.println((i + 1) + "." + task.name);
            System.out.println(">>>Description: " + task.description);
            System.out.println(">>>Status: " + status);
            System.out.println(">>>Deadline: " + deadline);
        }
        System.out.println("-------------------------------------------");
    }

    
    //Edit task
    public static void editTask(User currentUser, Scanner scanner) {
        System.out.println("\nEdit Task");
        viewTasks(currentUser);
        System.out.print("\nEnter number task to edit: ");
    int taskIndexEdit = scanner.nextInt();
        scanner.nextLine();
        boolean loop = true;
        while (loop == true) {
            if (isValidIndex(currentUser, taskIndexEdit)) {
                System.out.println("1. Edit name");
                System.out.println("2. Edit description");
                System.out.println("3. Edit date");
                System.out.println("4. change task");
                System.out.print("Enter number: ");
                try{
                int editChoice = scanner.nextInt();
                scanner.nextLine();
                if (editChoice == 1 || editChoice == 2 || editChoice == 3 || editChoice == 4) {
                    switch (editChoice) {
                        case 1:// Edit task's name
                            System.out.print("Enter new name for the task: ");
                            String newName = scanner.nextLine();
                            currentUser.tasks.get(taskIndexEdit - 1).name = newName;
                            currentUser.saveTasks();
                            clearScreen();
                            System.out.println("\nTask edited successfully.");
                            loop = false;
                            break;
                        case 2:// Edit task's description
                            viewTasks(currentUser);
                            System.out.print("Enter new description for the task: ");
                            String newDescription = scanner.nextLine();
                            currentUser.tasks.get(taskIndexEdit - 1).description = newDescription;
                            currentUser.saveTasks();
                            clearScreen();
                            System.out.println("\nTask edited successfully.");
                            loop = false;
                            break;
                        case 3:// Edit task's deadline
                            viewTasks(currentUser);
                            System.out.print("Enter new deadline for the task: ");
                            String newDeadline = scanner.nextLine();
                            currentUser.tasks.get(taskIndexEdit - 1).deadline = newDeadline;
                            currentUser.saveTasks();
                            clearScreen();
                            System.out.println("\nTask edited successfully.");
                            loop = false;
                            break;
                        case 4://Back
                            editTask(currentUser, scanner);
                            loop = false;
                            break;
                        default:
                            break;
                    }
                } else {
                    clearScreen();
                    System.out.println("\nInvalid choice. Please enter a valid option");
                    continue;
                }
                } catch (java.util.InputMismatchException e) {
                clearScreen();
                System.out.println("\nInvalid choice. Please enter a valid option");
                scanner.nextLine();
                }
            } else {
                editTask(currentUser, scanner);
            }
        }
    }//End edit task method


    //Mark task as completed
    public static void markTask(User currentUser, Scanner scanner) {
        if (currentUser.tasks.isEmpty()) {
            System.out.println("\nTask: Task is empty");

        } else {
            System.out.println("\nMark task as complete");
            viewTasks(currentUser);
            System.out.print("Enter task index to mark as complete: ");
            int taskIndexMarkComplete = scanner.nextInt();
            scanner.nextLine();

            if (isValidIndex(currentUser, taskIndexMarkComplete)) {
                currentUser.tasks.get(taskIndexMarkComplete - 1).isComplete = true;
                currentUser.saveTasks();
                clearScreen();
                System.out.println("\nTask marked as complete.");
            } else {
                System.out.println("\nInvalid task index");
            }
            
        }
    }//End markTask method

    //deleteTask
    public static void deleteTask(User currentUser, Scanner scanner) {
        if (!currentUser.tasks.isEmpty()) {
            System.out.println("\nDelete task");
            viewTasks(currentUser);
            System.out.print("Enter task index to delete: ");

            int DeleteTaskIndex = scanner.nextInt();
            scanner.nextLine();

            if (isValidIndex(currentUser, DeleteTaskIndex)) {
                currentUser.tasks.remove(DeleteTaskIndex - 1);
                currentUser.saveTasks();
                clearScreen();
                System.out.println("\nTask deleted successfully.");
            } else {
                System.out.println("\nInvalid task index");
            }
        } else {
            System.out.println("\nTask: Task is empty");
        }
    }//End delete task

    //logout
    public static void logOut(User currentUser) {
        currentUser = null;
        main(null);
    }

    //Exit program
    public static void existTaskManager() {
        System.out.println("\nExiting the Task Manager.\n");
        System.exit(0);
    }

}