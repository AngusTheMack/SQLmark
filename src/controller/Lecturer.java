package controller;

import model.*;
import model.Error;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The class the lecturer uses to enter in information pertaining to the assignment
 * @author Angus Mackenzie
 * @version 04/09/2018
 */
public class Lecturer {
    private model.Assignment assignmentModel;
    private List<model.Student> studentModels;

    /**
     * Receives students from DB
     * @throws Error if it cannot get the students
     */
    public Lecturer() throws Error {
        try{
            this.assignmentModel = new model.Assignment();
            this.studentModels = WorkingData.getStudents();
        }catch(Exception e){
            Runner createAdminData = new Runner("admin_data","root","68(MNPq]+_9{fk>q","db/admin_data.sql",true);
            createAdminData.executeScript();
            Runner createDataStore = new Runner("data_store","root","68(MNPq]+_9{fk>q","db/data_store.sql",true);
            createDataStore.executeScript();
        }

    }

    /**
     * Clears all admin_data databases
     * @return output from the clear function
     * @throws Error if it can't delete
     */
    public String clearAdminData() throws Error{
        Database db = new Database("");
        return db.clearAdmin();
    }

    /**
     * clears all databases
     * @return output from the delete statement
     * @throws Error if it can't delete
     */
    public String clearAll() throws Error{
        Database db = new Database("");
        return db.clearAll();
    }
    /**
     * Clears the data from the a specific database
     * @param tableName to clear data from
     * @return output from delete query
     * @throws Error if cannot delete
     */
    public String clear(String tableName) throws Error{
        Database db = new Database();
        return db.clear(tableName);
    }

    //TODO Make this use the same name as the database
    /**
     * Takes in a file and loads the data to run the assignment with
     * @param filename to load in
     * @throws Error if the file reader breaks
     */
    public void loadData(String filename) throws Error{
        try{
            CSV csvReader = new CSV(filename);
            String tablename = filename;
            if(filename.contains(".csv")){
                //get rid of the csv part and simply use the filename
                tablename = filename.substring(0,filename.length()-4);
            }
            List<String> columnNames = csvReader.parseLine();
            List<String> columnTypes = csvReader.parseLine();
            Database db = new Database();
            db.prepareCreate(columnNames, columnTypes,tablename);
            db.execute();
            List<String> input = csvReader.parseLine();
            while(input!=null){
                db.prepareInsert(input);
                db.execute();
                input = csvReader.parseLine();
            }
        }catch (Exception e){
            throw new Error("Couldn't read file "+filename,e);
        }

    }

    /**
     * Takes in the file with questions and answers
     * @param filename to read
     * @throws Error if the file reader breaks
     */
    public void loadQuestions(String filename) throws Error{
        try{
            CSV csvReader = new CSV('|', filename);
            List<String> columnNames = csvReader.parseLine();
            List<String> columnTypes = csvReader.parseLine();
            Database db = new Database("admin_data");
            String tableName = "questions";
            db.prepareCreate(columnNames, columnTypes, tableName);
            db.execute();
            List<String> input = csvReader.parseLine();
            while(input!=null){
                db.prepareInsert(input);
                db.execute();
                input = csvReader.parseLine();
            }
        }catch(Exception e){
            throw new Error("Couldn't read filename "+filename,e);
        }

    }



    /**
     * Takes in the filename for the students
     * @param filename students to read
     * @throws Error if the file reader or DB break
     */
    public void loadStudents(String filename) throws Error{
        CSV csvReader = new CSV(filename);
        try {
            List<String> columNames = csvReader.parseLine();
            Database db = new Database("admin_data");
            List<String> columnTypes = csvReader.parseLine();
            String tableName = "students";
            db.prepareCreate(columNames, columnTypes,tableName);
            db.execute();
            List<String> input = csvReader.parseLine();
            while (input != null) {
                db.prepareInsert(input);
                db.execute();
                input = csvReader.parseLine();
            }
        }catch(Exception e){
            throw new Error("Couldn't load csv file "+filename,e);
        }
    }

    //TODO Make This Get the Mark
     /**
     * Outputs all the students with their highest mark to the filename inputted
     * @param filename to write to
     * @throws Error if there is an issue writing to the file
     */
    public void exportStudents(String filename) throws Error{
        List<String> heading = new ArrayList<>();
        heading.add("student_num");
        heading.add("highest_mark");
        try{
            CSV csv = new CSV(filename,heading);
            // TODO: Export students and marks to CSV
            for(model.Student student : studentModels) {
                List<String> row = new ArrayList<>();
                row.add(student.getStudentNum());
                row.add(student.getHighestMark()+"");
                csv.writeLine(row);
            }
            csv.closeWriter();
        }catch(Exception e){
            throw new Error("Couldn't find the file "+filename, e);
        }
    }

    /**
     * The driver that runs when the lecturer starts the application
     * @param args for commandline arguments (none)
     */
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            Lecturer lecturer = new Lecturer();
            view.Lecturer lectureView = new view.Lecturer(lecturer,sc);
        } catch (Error error) {
            error.printStackTrace();
        }
    }
}
