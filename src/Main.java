import java.io.FileNotFoundException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Scanner;
import java.io.IOException;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a path to a directory:");
        String inputPath = scanner.nextLine();
        System.out.println(" ");

        try { //start try
            Path dirPath = Paths.get(inputPath);

            if (!Files.isDirectory(dirPath)) {
                System.out.println("Not a valid path to a directory");
                return;
            }


            boolean exit = false; //sets it to false until exit from while loop
            while (!exit) { //while true go through the loop
                System.out.println("Menu: ");
                System.out.println("(1) Display Contents of the Directory");
                System.out.println("(2) Copy specified file");
                System.out.println("(3) Move specified file");
                System.out.println("(4) Delete specified file");
                System.out.println("(5) Create directory within a directory");
                System.out.println("(6) Delete directory within a directory");
                System.out.println("(7) Search for a file within a specified directory");
                System.out.println("(8) Exit execution");
                     int choice = scanner.nextInt(); //convert the users input to an integer
                     scanner.nextLine(); //gets ready for next user input
                switch (choice) {
                    case 1: //"(1) Display Contents of the Directory"
                        displayDirectoryContents(dirPath);
                        break;
                    case 2: //Copy specified file
                        String sourceFilePath = getInput(scanner, "Enter source path for the file");
                        String targetFilePath = getInput(scanner, "Enter target path for new directory");
                        copyFile(sourceFilePath, targetFilePath);
                        break;
                    case 3: //Move specified file - works
                        System.out.println("Enter source file path");
                        String srcFile = scanner.nextLine();
                        System.out.println("Enter target file path");
                        String targetFile = scanner.nextLine();
                        moveFile(dirPath.resolve(srcFile), dirPath.resolve(targetFile));
                        break;
                    case 4: //Delete specified file -woah works - delete using absolute file path of text.txt
                        System.out.println("Enter name of the file to delete:");
                        String fileToDelete = scanner.nextLine();
                        deleteFile(dirPath.resolve(fileToDelete));
                        break;
                    case 5: //Create directory within a directory -- works now
                        System.out.println("Enter name of directory to create: ");
                        String newDir = scanner.nextLine();
                        createDirectory(dirPath.resolve(newDir));
                        break;
                    case 6: //Delete directory within a directory --works now
                        System.out.println("Enter file path directory to delete: ");
                        String dirToDelete = scanner.nextLine();
                        deleteDirectory(dirPath.resolve(dirToDelete));
                        break;
                    case 7: //Search for a file or directory within a specified directory --returns name if found
                        System.out.println("Enter the file or directory name to search for");
                        String searchTerm = scanner.nextLine();
                        searchFiles(dirPath, searchTerm);
                        break;
                    case 8: //Exit execution -- works
                        exit = true;
                        break;
                    default: //error handling for if user inputs anything other than 1-8
                        System.out.println("Not a valid option");
                }
            } //ending bracket of while loop

        } catch (InvalidPathException e) {
            System.out.println("Not a valid directory path.");
        }
    }

    //methods below
    private static String getInput(Scanner scanner, String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }
    private static void displayDirectoryContents(Path directoryPath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            System.out.println("Contents of the directory");
            for (Path entry : stream) {
                BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                String typeOfFile;
                if (attrs.isDirectory()) { // if/else to tell what type of attribute/file type
                    typeOfFile = "directory";
                } else if (attrs.isRegularFile()) {
                    typeOfFile = "regular file";
                } else {
                    typeOfFile = "not a regular file nor directory";
                }
                long size = attrs.size(); //size of file
                LocalDateTime lastModified = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneOffset.UTC); //time
                String formattedDateTime = lastModified.toString().replace("T", "-->"); //converts object to String, T becomes "-->".
                System.out.printf("%-10s %-10d %-20s %s%n", typeOfFile, size, formattedDateTime, entry.getFileName());
            }
        } catch (IOException e) {
            System.out.println("Error reading directory contents: " + e.getMessage());
        }
    }
    private static void copyFile(String sourceFilePath, String targetFilePath) {
        try {
            Path sourcePath = Paths.get(sourceFilePath);
            Path targetPath = Paths.get(targetFilePath);
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File successfully copied");
        } catch (IOException e) {
            System.out.println("Error occurred when copying file: " + e.getMessage());
        }
    }
    private static void moveFile(Path sourceFilePath, Path targetFilePath) {
        try {
            Files.move(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File moved successfully");
        } catch (IOException e) {
            System.out.println("Error occurred moving file: " + e.getMessage());
        }
    }
    private static void deleteFile(Path fileToDelete) {
        try {
            Files.delete(fileToDelete);
            System.out.println("File deleted.");
        } catch (IOException e) {
            System.out.println("Error trying to delete file: " + e.getMessage());
        }
    }
    private static void createDirectory(Path directoryToCreate) {
        try {
            Files.createDirectory(directoryToCreate);
            System.out.println("Directory created successfully.");
        } catch (IOException e) {
            System.out.println("Error when creating directory: " + e.getMessage());
        }
    }
    private static void deleteDirectory(Path dir) {
        try {
            Files.delete(dir);
            System.out.println("Directory deleted.");
        } catch (IOException e) {
            System.out.println("Error trying to delete Directory: " + e.getMessage());
        }
    }
    private static void searchFiles(Path dirPath, String searchTerm) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, searchTerm)) {
            System.out.println("Search results - name is below if found");
            for (Path entry : stream) {
                System.out.println(entry.getFileName());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error finding file: " + e.getMessage()); //
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}