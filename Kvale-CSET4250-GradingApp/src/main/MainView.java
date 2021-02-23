package main;

import java.awt.EventQueue;
import java.sql.*;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import main.ResultsTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class MainView {

	private JFrame frame;
	private JTable table_ResultTable;
	private JTextField txtBox_Add_Assignment_AssignmentName;
	private JTextField txtBox_AddEdit_Assignment_DefaultWeight;
	private JTextField txtBox_Add_Assignment_ClassName;
	private JTextField txtBox_Add_Assignment_AssignmentGrade;
	private JTextField txtDefaultStudent;
	private JTextField textFieldClassName;
	private JTextField classNameTextBoxDelete;
	private JTextField assignmentNameTextBoxDelete;
	private JTable table;

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		
		/* Declares SQL variables */
	    Connection sqlConnection = null; //connection object for SQL-Lite
	    Statement sqlStatement = null; //stmt sent to sqlLite
	    ResultSet resultSet;
	    String dbMetadata = "";
	    HashMap <String, Integer> allStudentsHashmap = new HashMap<>();
	    HashMap <String, Integer> allClassesHashmap = new HashMap<>();
	    HashMap <String, Integer> allAssignmentsHashmap = new HashMap<>();
	    
		/**
		 * This opens the SQL database and queries if it is initialized
		 */
	   try {
	    	//tries to open a connection to sqlLite
	       Class.forName("org.sqlite.JDBC");
	       sqlConnection = DriverManager.getConnection("jdbc:sqlite:gradingApp.db");
	       System.out.println("Opened database connection");
	       
	       try {
	    	   //tries to access the initialized database
		       sqlStatement = sqlConnection.createStatement();
		       String sql = "SELECT tbl_name FROM sqlite_master WHERE type = 'table' and (name = 'Student' or name = 'Class' or name = 'Enrollment' or name = 'Assignment' or name = 'Submission')"; 
		       resultSet = sqlStatement.executeQuery(sql);
		       int resultSetSize = 0; //how many entries are returned from the query
		       String resultSetRows = ""; //the string values of the tables queried
	            // loop through the result set to determine the number of table matches and obtains table metadata
	            while (resultSet.next()) {
	            	//while there are results left to iterate through, it will increase the size of the total and add the result to the resultstring
			    	int i = 1;
			    	resultSetRows = resultSetRows + resultSet.getString(resultSet.getMetaData().getColumnName(i)) + " ";
	            	resultSetSize ++;
	            	i++;
	            	}
	            
		       if (resultSetSize < 5)
		       {
		    	   //if the database does not have those 5 tables, it throws a custom Database not initialized exception
		 	        System.out.println("Database Not Initialized: " + String.valueOf(resultSetSize));
		    	    throw new DatabaseNotInitialized("Database is not Initialized Yet");
		       }
		       else {
		    	   System.out.println("Database Initialized: " + String.valueOf(resultSetSize));
		       }		
		       dbMetadata = resultSetRows;
			   System.out.println("Currently initialized tables are: " + dbMetadata);
		       sqlStatement.close();
		       
		       
		       //Gets student data
		       sqlStatement = sqlConnection.createStatement();
		       sql = "SELECT StudentID, StudentName FROM Student;"; 
		       resultSet = sqlStatement.executeQuery(sql);
	           while (resultSet.next()) {
	            	//while there are results left to iterate through, glean student data and add it to the student hashmap
	        	   int studentID = resultSet.getInt("StudentID");
	        	   String studentName = resultSet.getString("StudentName");
	        	   allStudentsHashmap.put(studentName, studentID);
	            }
	           //System.out.println(allStudentsHashmap.toString());
	           		
	       }
	       catch (Exception e) {
	    	   //if database is not initialized, create one for the program
		       sqlStatement = sqlConnection.createStatement();
		       String sql = 
		    		   		  //creates the student table
		    		   		  "CREATE TABLE Student " +
		                      "( StudentID INTEGER PRIMARY KEY  ," +
		                      " StudentName           VARCHAR (50)    NOT NULL  UNIQUE); " + 
		                      
		                      //Creates class table
		                      "CREATE TABLE Class " +
		                      "( ClassID INTEGER PRIMARY KEY  ," +
		                      " ClassName           VARCHAR (50)    NOT NULL  UNIQUE); " + 
		                      
		                      //Creates Assignment table
		                      "CREATE TABLE Assignment " +
		                      "( AssignmentID INTEGER PRIMARY KEY ," +
		                      " AssignmentNAME          VARCHAR (50)   NOT NULL, " +
		                      "ClassID INT NOT NULL, " +
		                      "Type VARCHAR (10) NOT NULL," +
		                      "Weight INT NOT NULL DEFAULT 1," +
		                      "FOREIGN KEY (ClassID) REFERENCES Class(ClassID)); " +
		                      
		                      //Creates enrollment table
		                      "CREATE TABLE Enrollment " +
		                      "(EnrollmentID INTEGER PRIMARY KEY ," +
		                      "ClassID INT NOT NULL, " +
		                      "StudentID INT NOT NULL, " +
		                      "FOREIGN KEY (ClassID) REFERENCES Class(ClassID)," + 
		                      "FOREIGN KEY (StudentID) REFERENCES Student(StudentID));" +
		                      
		                      //Creates Submission table
		                      "CREATE TABLE Submission " +
		                      "(SubmissionID INTEGER PRIMARY KEY ," +
		                      "AssignmentID INT NOT NULL, " +
		                      "StudentID INT NOT NULL, " +
		                      "Grade INT NOT NULL, " +
		                      "FOREIGN KEY (AssignmentID) REFERENCES Assignment(AssignmentID)," + 
		                      "FOREIGN KEY (StudentID) REFERENCES Student(StudentID));";
		       
		       sqlStatement.executeUpdate(sql);
			   System.out.println("Initialized the following tables: Student Class Assignment Enrollment Submission");
		       sqlStatement.close();
	       }
	       sqlConnection.close();
	    } catch ( Exception e ) {
	       System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	       System.exit(0);
	    }

	  
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView window = new MainView(allStudentsHashmap, allClassesHashmap, allAssignmentsHashmap);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainView(HashMap allStudentsHashmap, HashMap allClassesHashmap, HashMap allAssignmentsHashmap) {
		
		initialize(allStudentsHashmap, allClassesHashmap, allAssignmentsHashmap);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(HashMap allStudentsHashmap, HashMap allClassesHashmap, HashMap allAssignmentsHashmap) {
		frame = new JFrame();
		frame.setBounds(100, 100, 806, 633);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		String currentlySelectedStudent = "";
		
		////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////  CURRENTLY SELECTED STUDENT UI ELEMENT   //////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		//container for the ui element
		JPanel panel_Current_Student = new JPanel();
		panel_Current_Student.setBorder(new LineBorder(Color.GRAY, 1, true));
		panel_Current_Student.setBounds(38, 11, 303, 30);
		frame.getContentPane().add(panel_Current_Student);
		
		//label for the current student
		JLabel lblCurrentStudent = new JLabel("Current Student:");
		panel_Current_Student.add(lblCurrentStudent);
		
		//Changeable label for whatever the current student is
		JLabel lblCurrentStudentResult = new JLabel("None");
		panel_Current_Student.add(lblCurrentStudentResult);
		lblCurrentStudentResult.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblCurrentStudentResult.setVerticalAlignment(SwingConstants.BOTTOM);
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////  RESULT TABLE UI ELEMENT   ////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		//scroll pane container
		JScrollPane scrollPane_ResultTable = new JScrollPane();
		scrollPane_ResultTable.setBounds(351, 11, 429, 269);
		frame.getContentPane().add(scrollPane_ResultTable);
		
		//result table
		ResultsTableModel resultsTableModel = new ResultsTableModel();
		table_ResultTable = new JTable();
		scrollPane_ResultTable.setViewportView(table_ResultTable);
		table_ResultTable.setModel(resultsTableModel);
		
	
		
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////  RESULT TABLE for averages UI ELEMENT   ///////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		//scroll pane container
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(351, 291, 429, 132);
		frame.getContentPane().add(scrollPane);

		AveragesTableModel averagesTableModel = new AveragesTableModel();
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(averagesTableModel);
		
		////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////  ADD  ASSIGNMENT UI ELEMENT   ///////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		//panel container for add edit assignment
		JPanel panel_Add_Assignment = new JPanel();
		panel_Add_Assignment.setBorder(new LineBorder(Color.GRAY, 1, true));
		panel_Add_Assignment.setBounds(38, 49, 303, 231);
		frame.getContentPane().add(panel_Add_Assignment);
		panel_Add_Assignment.setLayout(null);
		
		// label for add edit assignment
		JLabel lblAddAssignment = new JLabel("Add  Assignment");
		lblAddAssignment.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblAddAssignment.setBounds(10, 7, 206, 20);
		panel_Add_Assignment.add(lblAddAssignment);
		
		//label for assignment name
		JLabel lbl_Add_Assignment_AssignmentName = new JLabel("Assignment Name:");
		lbl_Add_Assignment_AssignmentName.setBounds(10, 66, 109, 22);
		panel_Add_Assignment.add(lbl_Add_Assignment_AssignmentName);
		
		//label for assignment type
		JLabel lbl_Add_Assignment_AssignmentType = new JLabel("Assignment Type:");
		lbl_Add_Assignment_AssignmentType.setBounds(10, 93, 109, 22);
		panel_Add_Assignment.add(lbl_Add_Assignment_AssignmentType);
			
		//label for assignment's class name
		JLabel lbl_Add_Assignment_ClassName = new JLabel("Class Name:");
		lbl_Add_Assignment_ClassName.setBounds(10, 38, 109, 22);
		panel_Add_Assignment.add(lbl_Add_Assignment_ClassName);	
		
		//label for assignment grade
		JLabel lbl_Add_Assignment_AssignmentGrade = new JLabel("Assignment Grade:");
		lbl_Add_Assignment_AssignmentGrade.setBounds(10, 153, 124, 22);
		panel_Add_Assignment.add(lbl_Add_Assignment_AssignmentGrade);
		
		//combo box for either HW or test
		JComboBox comboBox_Add_Assignment_AssignmentType = new JComboBox();
		comboBox_Add_Assignment_AssignmentType.setBounds(146, 93, 124, 20);
		panel_Add_Assignment.add(comboBox_Add_Assignment_AssignmentType);
		comboBox_Add_Assignment_AssignmentType.setModel(new DefaultComboBoxModel(new String[] {"Homework", "Test"}));
		
		//assignment name textbox		
		txtBox_Add_Assignment_AssignmentName = new JTextField();
		txtBox_Add_Assignment_AssignmentName.setText("Default Name");
		txtBox_Add_Assignment_AssignmentName.setBounds(146, 66, 124, 20);
		panel_Add_Assignment.add(txtBox_Add_Assignment_AssignmentName);
		txtBox_Add_Assignment_AssignmentName.setColumns(10);
				
		//class name for assignment
		txtBox_Add_Assignment_ClassName = new JTextField();
		txtBox_Add_Assignment_ClassName.setText("Default Class");
		txtBox_Add_Assignment_ClassName.setColumns(10);
		txtBox_Add_Assignment_ClassName.setBounds(146, 38, 124, 20);
		panel_Add_Assignment.add(txtBox_Add_Assignment_ClassName);
		
		//assignment grade textbox
		txtBox_Add_Assignment_AssignmentGrade = new JTextField();
		txtBox_Add_Assignment_AssignmentGrade.setText("-100");
		txtBox_Add_Assignment_AssignmentGrade.setColumns(10);
		txtBox_Add_Assignment_AssignmentGrade.setBounds(161, 152, 109, 20);
		panel_Add_Assignment.add(txtBox_Add_Assignment_AssignmentGrade);
		
		////*** submit btn for the add  assignment function ***/////
		JButton btn_Add_Assignment_Submit = new JButton("Submit");
		btn_Add_Assignment_Submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				 try {
					   //SQL-lite variables//
					   Connection sqlConnection = null; //connection object for SQL-Lite
					   Statement sqlStatement = null; //stmt sent to sqlLite
					   ResultSet resultSet;
					   String studentName = "";
					   String assignmentName = "";
					   String className = "";
					   String assignmentType = "";
					   int assignmentGrade = -1;
					   int assignmentID = -1;
					   int classID = -1;
					   int studentID = -1;
					   
					   //The following is a reg expression data validation for the assignment name textbox //
						assignmentName = txtBox_Add_Assignment_AssignmentName.getText();
						if (assignmentName.matches("^[a-zA-Z0-9 _-]{1,50}$")) {
							System.out.println("AssignmentName: Data Valid");
						}
						else {
				 	       System.out.println("Data Not Valid Error");
				    	   throw new DataNotValid("Data Not Valid Error");
						}
						
						   //The following is a reg expression data validation for the className textbox //
						className = txtBox_Add_Assignment_ClassName.getText();
						if (className.matches("^[a-zA-Z0-9 _-]{1,50}$")) {
							System.out.println("ClassName: Data Valid");
						}
						else {
				 	       System.out.println("Data Not Valid Error");
				    	   throw new DataNotValid("Data Not Valid Error");
						}
					   						
						   //The following is a reg expression data validation for the assignment grade textbox //
						try {
							assignmentGrade = Integer.valueOf(txtBox_Add_Assignment_AssignmentGrade.getText());
						} 				   
						catch (Exception e) {
					 	       System.out.println("Data Not Valid Error");
					    	   throw new DataNotValid("Data Not Valid Error");	   
						   }
						if (txtBox_Add_Assignment_AssignmentGrade.getText().matches("^[0-9]{0,3}$") && 0 <= assignmentGrade && 100 >= assignmentGrade) {
							System.out.println("Grade: Data Valid");
						}
						else {
				 	       System.out.println("Data Not Valid Error");
				    	   throw new DataNotValid("Data Not Valid Error");
						}
						
					   //tries to open a connection to sqlLite
				       Class.forName("org.sqlite.JDBC");
				       sqlConnection = DriverManager.getConnection("jdbc:sqlite:gradingApp.db");
				       studentID = (int) allStudentsHashmap.get(lblCurrentStudentResult.getText());
				      
				     //SQL Add class Statement
				       String sql;
					try {
						sqlStatement = sqlConnection.createStatement();   
						   sql = "INSERT INTO Class ( ClassName )" + 
						   		"VALUES" + "('" + className + "');";
						   sqlStatement.executeUpdate(sql);
						   sqlStatement.close();
						   System.out.println("Completed add statement: " + className);
					} catch (Exception e) {
						 System.out.println("Class already present");
					}
				       
				       //SQL Get Class information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql = "SELECT ClassID, ClassName FROM Class WHERE ClassName LIKE '" + className + "';";
				       resultSet = sqlStatement.executeQuery(sql);
				
				       // loop through the result set
					  while (resultSet.next()) { 
						  String classNameResult = resultSet.getString("ClassName"); 
						  classID = resultSet.getInt("ClassID");
						  System.out.println("Added to Class: Class Name: " + classNameResult + " ClassID: " + classID);
						  }
					  allClassesHashmap.put(className, classID);
			          sqlStatement.close();
			          
			          
			          try {
				       //SQL Add Enrollment Statement
				       sqlStatement = sqlConnection.createStatement();   
				       sql = "INSERT INTO Enrollment ( ClassID, StudentID )" + 
				       		"VALUES" + " ( ' " + allClassesHashmap.get(className) + "' , '" + allStudentsHashmap.get(lblCurrentStudentResult.getText()) + "' );";
				       sqlStatement.executeUpdate(sql);
				       sqlStatement.close();
					   System.out.println("Completed enrollment statement: ClassID = " + allClassesHashmap.get(className) + ", StudentID = "+ allStudentsHashmap.get(lblCurrentStudentResult.getText()));
				 		} 
			          catch (Exception e) {
				 			System.out.println("Enrollment already present");
				 		}

		    		   assignmentType =	(String) comboBox_Add_Assignment_AssignmentType.getSelectedItem();
		    		   
		    		   //SQL Add Assignment Statement
				       sqlStatement = sqlConnection.createStatement();   
				       sql = "INSERT INTO Assignment ( ClassID , AssignmentName, Type )" + 
				       		"VALUES" + "('" + classID + "' , '" + assignmentName + "' ,'" + assignmentType + "');";
				       sqlStatement.executeUpdate(sql);
				       sqlStatement.close();
					   System.out.println("Completed add statement: " + assignmentName);
					   
					   //SQL Get Assignment information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql = "SELECT AssignmentID, AssignmentName FROM Assignment WHERE AssignmentName LIKE '" + assignmentName + "';";
				       resultSet = sqlStatement.executeQuery(sql);
				
				       // loop through the result set
					  while (resultSet.next()) { 
						  String assignmentNameResult = resultSet.getString("AssignmentName"); 
						  assignmentID = resultSet.getInt("AssignmentID");
						  System.out.println("Added to Assignment: AssignmentName: " + assignmentNameResult + " AssignmentID: " + assignmentID);
						  }
					  allAssignmentsHashmap.put(assignmentName, assignmentID);
			          sqlStatement.close();
			          
			          
		    		   //SQL Add Submission Statement
				       sqlStatement = sqlConnection.createStatement();   
				       sql = "INSERT INTO Submission ( AssignmentID , StudentID, Grade )" + 
				       		"VALUES" + "('" + assignmentID + "' , '" + studentID + "' ,'" + assignmentGrade + "');";
				       sqlStatement.executeUpdate(sql);
				       sqlStatement.close();

					   System.out.println("Completed add statement: " + assignmentName);
					   
					   studentName = lblCurrentStudentResult.getText();
				       //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, Assignment.AssignmentName AS AssignmentName, Submission.Grade AS Grade" + 
				    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
				    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
				    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
				    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
						       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType, AssignmentName";
						       
						      
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       Object [][] tempObject = new Object [99][4];
				       // loop through the result set
					  while (resultSet.next()) { 
						  int i = resultSet.getRow()-1;
						  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
						  String classNameResult = resultSet.getString("ClassName");
						  String assignmentNameResult = resultSet.getString("AssignmentName");
						  int submissionGradeResult = resultSet.getInt("Grade");
						  tempObject [i][0] = classNameResult;
						  tempObject [i][1] = assignmentNameResult;
						  tempObject [i][2] = assignmentTypeResult;
						  tempObject [i][3]= submissionGradeResult;
						  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + assignmentNameResult + ", " + submissionGradeResult);
						  }
				       resultsTableModel.setTableData(tempObject);
					   //updates the current student result label
					  lblCurrentStudentResult.setText(studentName);
			           sqlStatement.close();
			           
				       //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, AVG(Submission.Grade) AS Average" + 
				    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
				    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
				    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
				    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
						       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType";
						       
						      
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       Object [][] tempObjectAverages = new Object [99][3];
				       // loop through the result set
					  while (resultSet.next()) { 
						  int i = resultSet.getRow()-1;
						  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
						  String classNameResult = resultSet.getString("ClassName");
						  double calculatedAverageResult = resultSet.getDouble("Average");
						  tempObjectAverages [i][0] = classNameResult;
						  tempObjectAverages [i][1] = assignmentTypeResult;
						  tempObjectAverages [i][2] = calculatedAverageResult;
						  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + calculatedAverageResult);
						  }
					  averagesTableModel.setTableData(tempObjectAverages);
					   //updates the current student result label
					  lblCurrentStudentResult.setText(studentName);
					 
			           //Query & SQL close
			           sqlStatement.close();
			           
					   
					   
				      sqlConnection.close();
				    } 
				   catch (DataNotValid e) {
					   JOptionPane.showMessageDialog(null, "Data Submitted is not valid, please only use the following characters for names: a-z, A-Z, 0-9, _ , - ; please only use 0-100 for numbers");		   
				   }
				   catch ( Exception e ) {
				       System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				       JOptionPane.showMessageDialog(null, "Student name is already present. Please select a different one.");
				    }
			}
		});
		btn_Add_Assignment_Submit.setBounds(94, 183, 99, 36);
		panel_Add_Assignment.add(btn_Add_Assignment_Submit);
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////   DELETE ASSIGNMENT UI ELEMENT   ////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		//ui container for the delete assignment function
		JPanel panel_DeleteAssignment = new JPanel();
		panel_DeleteAssignment.setBorder(new LineBorder(Color.GRAY, 1, true));
		panel_DeleteAssignment.setBounds(37, 291, 304, 132);
		frame.getContentPane().add(panel_DeleteAssignment);
		panel_DeleteAssignment.setLayout(null);
		
		//label for the name for the delete function
		JLabel lblDeleteAssignment = new JLabel("Delete Assignment");
		lblDeleteAssignment.setBounds(88, 6, 128, 17);
		lblDeleteAssignment.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel_DeleteAssignment.add(lblDeleteAssignment);
		
		//label for the name of the assignment's class
		JLabel label_1 = new JLabel("Class Name:");
		label_1.setBounds(21, 34, 109, 22);
		panel_DeleteAssignment.add(label_1);
		
		//label for the name of the assignment	
		JLabel label_2 = new JLabel("Assignment Name:");
		label_2.setBounds(21, 62, 109, 22);
		panel_DeleteAssignment.add(label_2);
		
		//assignment's class textbox
		classNameTextBoxDelete = new JTextField();
		classNameTextBoxDelete.setText("Default Class");
		classNameTextBoxDelete.setColumns(10);
		classNameTextBoxDelete.setBounds(157, 34, 124, 20);
		panel_DeleteAssignment.add(classNameTextBoxDelete);
		
		//assignment's name textbox
		assignmentNameTextBoxDelete = new JTextField();
		assignmentNameTextBoxDelete.setText("Default Name");
		assignmentNameTextBoxDelete.setColumns(10);
		assignmentNameTextBoxDelete.setBounds(157, 62, 124, 20);
		panel_DeleteAssignment.add(assignmentNameTextBoxDelete);
		
		////*** delete btn for the delete assignment function ***/////
		JButton button_2 = new JButton("Delete");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					   //SQL-lite variables//
					   Connection sqlConnection = null; //connection object for SQL-Lite
					   Statement sqlStatement = null; //stmt sent to sqlLite
					   ResultSet resultSet;
					   String studentName = lblCurrentStudentResult.getText();
					   String assignmentName = "";
					   String className = "";
					   int classID = -1;
						   
					   //The following is a reg expression data validation for the studentName textbox //
						assignmentName = assignmentNameTextBoxDelete.getText();
						if (assignmentName.matches("^[a-zA-Z0-9 _-]{1,13}$")) {
							System.out.println("Delete Submitted: Data Valid");
						}
						else {
				 	       System.out.println("Data Not Valid Error");
				    	   throw new DataNotValid("Data Not Valid Error");
						}
						
						  //The following is a reg expression data validation for the studentName textbox //
						className = classNameTextBoxDelete.getText();
						if (className.matches("^[a-zA-Z0-9 _-]{1,13}$")) {
							System.out.println("Delete Submitted: Data Valid");
							}
						else {
				 	       System.out.println("Data Not Valid Error");
				    	   throw new DataNotValid("Data Not Valid Error");
									}
					   
					   //tries to open a connection to sqlLite
				       Class.forName("org.sqlite.JDBC");
				       sqlConnection = DriverManager.getConnection("jdbc:sqlite:gradingApp.db");
				       
				       //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       String sql = "SELECT ClassID, ClassName FROM Class WHERE ClassName LIKE '" + className + "';";
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       // loop through the result set
					
					  while (resultSet.next()) { 
					  String classResultName = resultSet.getString("ClassName"); 
					  classID = resultSet.getInt("ClassID");
					  System.out.println("Remaining students with that name: " + classResultName +
					  " StudentID: " + classID); 
					  }

				       //SQL Delete student Statement
				       sqlStatement = sqlConnection.createStatement();   
				       sql = "DELETE FROM Assignment WHERE ClassID LIKE '" + classID + "';";
				       sqlStatement.executeUpdate(sql);
				       sqlStatement.close();
					   System.out.println("Completed delete statement: " + className);
			           sqlStatement.close();
			           
			           
			         //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, Assignment.AssignmentName AS AssignmentName, Submission.Grade AS Grade" + 
				    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
				    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
				    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
				    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
						       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType, AssignmentName";
						       
						      
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       Object [][] tempObject = new Object [99][4];
				       // loop through the result set
					  while (resultSet.next()) { 
						  int i = resultSet.getRow()-1;
						  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
						  String classNameResult = resultSet.getString("ClassName");
						  String assignmentNameResult = resultSet.getString("AssignmentName");
						  int submissionGradeResult = resultSet.getInt("Grade");
						  tempObject [i][0] = classNameResult;
						  tempObject [i][1] = assignmentNameResult;
						  tempObject [i][2] = assignmentTypeResult;
						  tempObject [i][3]= submissionGradeResult;
						  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + assignmentNameResult + ", " + submissionGradeResult);
						  }
				       resultsTableModel.setTableData(tempObject);
					   //updates the current student result label
					  lblCurrentStudentResult.setText(studentName);
			           sqlStatement.close();
			           
				       //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, AVG(Submission.Grade) AS Average" + 
				    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
				    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
				    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
				    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
						       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType";
						       
						      
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       Object [][] tempObjectAverages = new Object [99][3];
				       // loop through the result set
					  while (resultSet.next()) { 
						  int i = resultSet.getRow()-1;
						  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
						  String classNameResult = resultSet.getString("ClassName");
						  double calculatedAverageResult = resultSet.getDouble("Average");
						  tempObjectAverages [i][0] = classNameResult;
						  tempObjectAverages [i][1] = assignmentTypeResult;
						  tempObjectAverages [i][2] = calculatedAverageResult;
						  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + calculatedAverageResult);
						  }
					  averagesTableModel.setTableData(tempObjectAverages);
					   //updates the current student result label
					  lblCurrentStudentResult.setText(studentName);
					 
			           //Query & SQL close
			           sqlStatement.close();
			           
			           
				       sqlConnection.close();
				    } 
				   catch (DataNotValid e) {
					   JOptionPane.showMessageDialog(null, "Data Submitted is not valid, please only use the following characters: a-z, A-Z, 0-9, _ , - ");		   
				   }
				   catch ( Exception e ) {
				       System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				       JOptionPane.showMessageDialog(null, "Assignment name is no longer present. Please select a different one.");
				    }
			}
		});
		button_2.setBounds(102, 95, 95, 22);
		panel_DeleteAssignment.add(button_2);
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////   ADD / DELETE CLASS UI ELEMENT   ///////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		//ui container for add / delete class ftn
		JPanel panel_AddDelete_Class = new JPanel();
		panel_AddDelete_Class.setBorder(new LineBorder(Color.GRAY, 1, true));
		panel_AddDelete_Class.setBounds(38, 434, 303, 123);
		frame.getContentPane().add(panel_AddDelete_Class);
		panel_AddDelete_Class.setLayout(null);
		
		//title label for the add / delete class ftn
		JLabel lblAddClass = new JLabel("Add / Delete Class");
		lblAddClass.setBounds(88, 6, 126, 17);
		lblAddClass.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel_AddDelete_Class.add(lblAddClass);
		
		//lbl for the name of the class to mod
		JLabel lblClassName_1 = new JLabel("Class Name:");
		lblClassName_1.setBounds(53, 34, 77, 26);
		panel_AddDelete_Class.add(lblClassName_1);
		
		//name of the class textbox
		textFieldClassName = new JTextField();
		textFieldClassName.setText("Default Class");
		textFieldClassName.setColumns(10);
		textFieldClassName.setBounds(135, 34, 105, 26);
		panel_AddDelete_Class.add(textFieldClassName);
		
		////*** add btn for the add class function ***/////
		JButton addClassBTN = new JButton("Add");
		addClassBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				   try {
					   //SQL-lite variables//
					   Connection sqlConnection = null; //connection object for SQL-Lite
					   Statement sqlStatement = null; //stmt sent to sqlLite
					   ResultSet resultSet;
					   String className;
					   int classID = -1;
					   
					   //The following is a reg expression data validation for the className textbox //
						className = textFieldClassName.getText();
						if (className.matches("^[a-zA-Z0-9 _-]{1,50}$")) {
							System.out.println("Add Submitted: Data Valid");
						}
						else {
				 	       System.out.println("Data Not Valid Error");
				    	   throw new DataNotValid("Data Not Valid Error");
						}
					   
						
					   //tries to open a connection to sqlLite
				       Class.forName("org.sqlite.JDBC");
				       sqlConnection = DriverManager.getConnection("jdbc:sqlite:gradingApp.db");
				       
				      
				       //SQL Add class Statement
				       sqlStatement = sqlConnection.createStatement();   
				       String sql = "INSERT INTO Class ( ClassName )" + 
				       		"VALUES" + "('" + className + "');";
				       sqlStatement.executeUpdate(sql);
				       sqlStatement.close();
					   System.out.println("Completed add statement: " + className);
				       
				       //SQL Get Class information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql = "SELECT ClassID, ClassName FROM Class WHERE ClassName LIKE '" + className + "';";
				       resultSet = sqlStatement.executeQuery(sql);
				
				       // loop through the result set
					  while (resultSet.next()) { 
						  String classNameResult = resultSet.getString("ClassName"); 
						  classID = resultSet.getInt("ClassID");
						  System.out.println("Added to Class: Class Name: " + classNameResult + " ClassID: " + classID);
						  }
					  allClassesHashmap.put(className, classID);
			          sqlStatement.close();
				       //SQL Add Enrollment Statement
				       sqlStatement = sqlConnection.createStatement();   
				       sql = "INSERT INTO Enrollment ( ClassID, StudentID )" + 
				       		"VALUES" + " ( ' " + allClassesHashmap.get(className) + "' , '" + allStudentsHashmap.get(lblCurrentStudentResult.getText()) + "' );";
				       sqlStatement.executeUpdate(sql);
				       sqlStatement.close();
					   System.out.println("Completed enrollment statement: ClassID = " + allClassesHashmap.get(className) + ", StudentID = "+ allStudentsHashmap.get(lblCurrentStudentResult.getText()));
				       sqlConnection.close();
				    } 
				   catch (DataNotValid e) {
					   JOptionPane.showMessageDialog(null, "Data Submitted is not valid, please only use the following characters: a-z, A-Z, 0-9, _ , - ");		   
				   }
				   catch ( Exception e ) {
				       System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				       JOptionPane.showMessageDialog(null, "Class is already present. Please add assignments to it.");
				    }
			}
		});
		addClassBTN.setBounds(63, 71, 77, 41);
		panel_AddDelete_Class.add(addClassBTN);
		////*** End Add btn for the add class function ***/////
		
		
		////*** delete btn for the delete class function ***/////
		JButton button_1 = new JButton("Delete");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					   //SQL-lite variables//
					   Connection sqlConnection = null; //connection object for SQL-Lite
					   Statement sqlStatement = null; //stmt sent to sqlLite
					   ResultSet resultSet;
					   String studentName = lblCurrentStudentResult.getText();
					   String assignmentName = "";
					   String className = "";
					   int classID = -1;
						   
					   className = textFieldClassName.getText();
						if (className.matches("^[a-zA-Z0-9 _-]{1,50}$")) {
							System.out.println("Delete Submitted: Data Valid");
						}
						else {
				 	       System.out.println("Data Not Valid Error");
				    	   throw new DataNotValid("Data Not Valid Error");
						}
					   
					   //tries to open a connection to sqlLite
				       Class.forName("org.sqlite.JDBC");
				       sqlConnection = DriverManager.getConnection("jdbc:sqlite:gradingApp.db");
				       
				       //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       String sql = "SELECT ClassID, ClassName FROM Class WHERE ClassName LIKE '" + className + "';";
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       // loop through the result set
					
					  while (resultSet.next()) { 
					  String classResultName = resultSet.getString("ClassName"); 
					  classID = resultSet.getInt("ClassID");
					  System.out.println("Remaining students with that name: " + classResultName +
					  " StudentID: " + classID); 
					  }

				       //SQL Delete student Statement
				       sqlStatement = sqlConnection.createStatement();   
				       sql = "DELETE FROM Class WHERE ClassID LIKE '" + classID + "';";
				       sqlStatement.executeUpdate(sql);
				       sqlStatement.close();
					   System.out.println("Completed delete statement: " + className);
			           sqlStatement.close();
			           
			           
			         //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, Assignment.AssignmentName AS AssignmentName, Submission.Grade AS Grade" + 
				    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
				    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
				    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
				    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
						       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType, AssignmentName";
						       
						      
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       Object [][] tempObject = new Object [99][4];
				       // loop through the result set
					  while (resultSet.next()) { 
						  int i = resultSet.getRow()-1;
						  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
						  String classNameResult = resultSet.getString("ClassName");
						  String assignmentNameResult = resultSet.getString("AssignmentName");
						  int submissionGradeResult = resultSet.getInt("Grade");
						  tempObject [i][0] = classNameResult;
						  tempObject [i][1] = assignmentNameResult;
						  tempObject [i][2] = assignmentTypeResult;
						  tempObject [i][3]= submissionGradeResult;
						  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + assignmentNameResult + ", " + submissionGradeResult);
						  }
				       resultsTableModel.setTableData(tempObject);
					   //updates the current student result label
					  lblCurrentStudentResult.setText(studentName);
			           sqlStatement.close();
			           
				       //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, AVG(Submission.Grade) AS Average" + 
				    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
				    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
				    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
				    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
						       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType";
						       
						      
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       Object [][] tempObjectAverages = new Object [99][3];
				       // loop through the result set
					  while (resultSet.next()) { 
						  int i = resultSet.getRow()-1;
						  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
						  String classNameResult = resultSet.getString("ClassName");
						  double calculatedAverageResult = resultSet.getDouble("Average");
						  tempObjectAverages [i][0] = classNameResult;
						  tempObjectAverages [i][1] = assignmentTypeResult;
						  tempObjectAverages [i][2] = calculatedAverageResult;
						  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + calculatedAverageResult);
						  }
					  averagesTableModel.setTableData(tempObjectAverages);
					   //updates the current student result label
					  lblCurrentStudentResult.setText(studentName);
					 
			           //Query & SQL close
			           sqlStatement.close();
				       sqlConnection.close();
				    } 
				   catch (DataNotValid e) {
					   JOptionPane.showMessageDialog(null, "Data Submitted is not valid, please only use the following characters: a-z, A-Z, 0-9, _ , - ");		   
				   }
				   catch ( Exception e ) {
				       System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				       JOptionPane.showMessageDialog(null, "Student name is already present. Please select a different one.");
				    }
			}
		});
		button_1.setBounds(163, 71, 77, 41);
		panel_AddDelete_Class.add(button_1);
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////   CHANGE TO STUDENT FUNCTION UI ELEMENT   ////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		//ui container for the change student function
		JPanel panel_ChangeStudent = new JPanel();
		panel_ChangeStudent.setBorder(new LineBorder(Color.GRAY, 1, true));
		panel_ChangeStudent.setBounds(351, 434, 212, 123);
		frame.getContentPane().add(panel_ChangeStudent);
		panel_ChangeStudent.setLayout(null);
		
		//label for the change student function
		JLabel lblChangeToStudent = new JLabel("Change To Student");
		lblChangeToStudent.setBounds(38, 6, 135, 17);
		lblChangeToStudent.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel_ChangeStudent.add(lblChangeToStudent);
		
		//label for the name of the student
		JLabel label = new JLabel("Student Name:");
		label.setFont(new Font("Tahoma", Font.PLAIN, 9));
		label.setBounds(10, 34, 77, 26);
		panel_ChangeStudent.add(label);
				
		//dropdown box that allows selection of the current students that are in the database
		JComboBox comboBox_1 = new JComboBox();
		DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel (new String[] {});
	
        // forEach(action) method to iterate map 
		allStudentsHashmap.forEach((k,v) -> defaultComboBoxModel.addElement(k));
		comboBox_1.setModel(defaultComboBoxModel);
		comboBox_1.setBounds(90, 37, 112, 20);
		panel_ChangeStudent.add(comboBox_1);
		
		////*** Change btn for the change student function ***/////
		JButton btnChangeActive = new JButton("Change Active");
		btnChangeActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 try {
					 String comboBoxSelection = String.valueOf(comboBox_1.getSelectedItem());
					 
					 //SQL-lite variables//
				   Connection sqlConnection = null; //connection object for SQL-Lite
				   Statement sqlStatement = null; //stmt sent to sqlLite
				   ResultSet resultSet;
				   String studentName = comboBoxSelection;
				
				   					
				   //tries to open a connection to sqlLite
			       Class.forName("org.sqlite.JDBC");
			       sqlConnection = DriverManager.getConnection("jdbc:sqlite:gradingApp.db");
			       
			      
			       //SQL Get Student information (Name, and ID)
			       sqlStatement = sqlConnection.createStatement();
			       String sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, Assignment.AssignmentName AS AssignmentName, Submission.Grade AS Grade" + 
			    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
			    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
			    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
			    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
					       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType, AssignmentName";
					       
					      
			       resultSet = sqlStatement.executeQuery(sql);
			       
			       Object [][] tempObject = new Object [99][4];
			       // loop through the result set
				  while (resultSet.next()) { 
					  int i = resultSet.getRow()-1;
					  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
					  String classNameResult = resultSet.getString("ClassName");
					  String assignmentNameResult = resultSet.getString("AssignmentName");
					  int submissionGradeResult = resultSet.getInt("Grade");
					  tempObject [i][0] = classNameResult;
					  tempObject [i][1] = assignmentNameResult;
					  tempObject [i][2] = assignmentTypeResult;
					  tempObject [i][3]= submissionGradeResult;
					  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + assignmentNameResult + ", " + submissionGradeResult);
					  }
			       resultsTableModel.setTableData(tempObject);
				   //updates the current student result label
				  lblCurrentStudentResult.setText(studentName);
		           sqlStatement.close();
		           
			       //SQL Get Student information (Name, and ID)
			       sqlStatement = sqlConnection.createStatement();
			       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, AVG(Submission.Grade) AS Average" + 
			    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
			    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
			    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
			    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
					       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType";
					       
					      
			       resultSet = sqlStatement.executeQuery(sql);
			       
			       Object [][] tempObjectAverages = new Object [99][3];
			       // loop through the result set
				  while (resultSet.next()) { 
					  int i = resultSet.getRow()-1;
					  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
					  String classNameResult = resultSet.getString("ClassName");
					  double calculatedAverageResult = resultSet.getDouble("Average");
					  tempObjectAverages [i][0] = classNameResult;
					  tempObjectAverages [i][1] = assignmentTypeResult;
					  tempObjectAverages [i][2] = calculatedAverageResult;
					  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + calculatedAverageResult);
					  }
				  averagesTableModel.setTableData(tempObjectAverages);
				   //updates the current student result label
				  lblCurrentStudentResult.setText(studentName);
				 
		           //Query & SQL close
		           sqlStatement.close();
		           
			       sqlConnection.close();
			       
			    } 
			   catch ( Exception e ) {
			       System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			       JOptionPane.showMessageDialog(null, "Student name is already present. Please select a different one.");
			    }
		}
	});
				
		btnChangeActive.setBounds(38, 71, 135, 41);
		panel_ChangeStudent.add(btnChangeActive);
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////   ADD / DELETE STUDENT FUNCTION UI ELEMENT   ///////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		//ui container for the add delete student function
		JPanel panel_AddDelete_Student = new JPanel();
		panel_AddDelete_Student.setBorder(new LineBorder(Color.GRAY, 1, true));
		panel_AddDelete_Student.setBounds(573, 434, 207, 123);
		frame.getContentPane().add(panel_AddDelete_Student);
		panel_AddDelete_Student.setLayout(null);
		
		//title label for the add delete student function
		JLabel lblAddDelete = new JLabel("Add / Delete Student");
		lblAddDelete.setBounds(30, 6, 147, 17);
		lblAddDelete.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel_AddDelete_Student.add(lblAddDelete);
		
		//label for the name of the student
		JLabel lblStudentName = new JLabel("Student Name:");
		lblStudentName.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblStudentName.setBounds(10, 34, 77, 26);
		panel_AddDelete_Student.add(lblStudentName);
		
		//textbox to add the name of the student
		txtDefaultStudent = new JTextField();
		txtDefaultStudent.setBounds(92, 34, 105, 26);
		txtDefaultStudent.setText("Default Student");
		txtDefaultStudent.setColumns(10);
		panel_AddDelete_Student.add(txtDefaultStudent);
		
		
		////*** Add btn for the add student function ***/////
		JButton btnNewButton_1 = new JButton("Add");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				   try {
					   //SQL-lite variables//
					   Connection sqlConnection = null; //connection object for SQL-Lite
					   Statement sqlStatement = null; //stmt sent to sqlLite
					   ResultSet resultSet;
					   String studentName;
					   int studentID = -1;
					   
					   //The following is a reg expression data validation for the studentName textbox //
						studentName = txtDefaultStudent.getText();
						if (studentName.matches("^[a-zA-Z0-9 _-]{1,15}$")) {
							System.out.println("Add Submitted: Data Valid");
						}
						else {
				 	       System.out.println("Data Not Valid Error");
				    	   throw new DataNotValid("Data Not Valid Error");
						}
					   
						
					   //tries to open a connection to sqlLite
				       Class.forName("org.sqlite.JDBC");
				       sqlConnection = DriverManager.getConnection("jdbc:sqlite:gradingApp.db");
				       
				      
				       //SQL Add student Statement
				       sqlStatement = sqlConnection.createStatement();   
				       String sql = "INSERT INTO Student ( StudentName )" + 
				       		"VALUES" + "('" + studentName + "');";
				       sqlStatement.executeUpdate(sql);
				       sqlStatement.close();
					   System.out.println("Completed add statement: " + studentName);
				       
				       //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql = "SELECT StudentID, StudentName FROM Student WHERE StudentName LIKE '" + studentName + "';";
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       // loop through the result set
					
					  while (resultSet.next()) { 
					  String studenResultName = resultSet.getString("StudentName"); 
					  studentID = resultSet.getInt("StudentID");
					  System.out.println("Added to Student: Student Name: " + studenResultName +
					  " StudentID: " + studentID); }
					  
					   //updates the student lists
				       allStudentsHashmap.put(studentName, studentID);
					   defaultComboBoxModel.addElement(studentName);
					 
			           //Query & SQL close
			           sqlStatement.close();

				       
				       //Asks User if they want to change to the added student
				       Object[] options = {"Yes",
	                    "No"};
						int optionChoice = JOptionPane.showOptionDialog(frame,
						    "Do you want to change to the new student: " + studentName + "?",
						    "Student Change",
						    JOptionPane.YES_NO_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,     //do not use a custom Icon
						    options,  //the titles of buttons
						    options[0]); //default button title
						if (optionChoice == 0) {
							//change ui to the new student
							String changeName = "";
							if (studentName.length() >= 16) {
								changeName = studentName.substring(0, 16);
								changeName = changeName + "...";
							}
							else {
								changeName = studentName;
							}
							lblCurrentStudentResult.setText(changeName);
							//SQL Get Student information (Name, and ID)
						       sqlStatement = sqlConnection.createStatement();
						       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, Assignment.AssignmentName AS AssignmentName, Submission.Grade AS Grade" + 
						    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
						    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
						    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
						    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
								       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType, AssignmentName";
								       
								      
						       resultSet = sqlStatement.executeQuery(sql);
						       
						       Object [][] tempObject = new Object [99][4];
						       // loop through the result set
							  while (resultSet.next()) { 
								  int i = resultSet.getRow()-1;
								  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
								  String classNameResult = resultSet.getString("ClassName");
								  String assignmentNameResult = resultSet.getString("AssignmentName");
								  int submissionGradeResult = resultSet.getInt("Grade");
								  tempObject [i][0] = classNameResult;
								  tempObject [i][1] = assignmentNameResult;
								  tempObject [i][2] = assignmentTypeResult;
								  tempObject [i][3]= submissionGradeResult;
								  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + assignmentNameResult + ", " + submissionGradeResult);
								  }
						       resultsTableModel.setTableData(tempObject);
							   //updates the current student result label
							  lblCurrentStudentResult.setText(studentName);
					           sqlStatement.close();
					           
						       //SQL Get Student information (Name, and ID)
						       sqlStatement = sqlConnection.createStatement();
						       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, AVG(Submission.Grade) AS Average" + 
						    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
						    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
						    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
						    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
								       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType";
								       
								      
						       resultSet = sqlStatement.executeQuery(sql);
						       
						       Object [][] tempObjectAverages = new Object [99][3];
						       // loop through the result set
							  while (resultSet.next()) { 
								  int i = resultSet.getRow()-1;
								  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
								  String classNameResult = resultSet.getString("ClassName");
								  double calculatedAverageResult = resultSet.getDouble("Average");
								  tempObjectAverages [i][0] = classNameResult;
								  tempObjectAverages [i][1] = assignmentTypeResult;
								  tempObjectAverages [i][2] = calculatedAverageResult;
								  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + calculatedAverageResult);
								  }
							  averagesTableModel.setTableData(tempObjectAverages);
							   //updates the current student result label
							  lblCurrentStudentResult.setText(studentName);
							 
					           //Query & SQL close
					           sqlStatement.close();
						       sqlConnection.close();
						}
				    } 
				   catch (DataNotValid e) {
					   JOptionPane.showMessageDialog(null, "Data Submitted is not valid, please only use the following characters: a-z, A-Z, 0-9, _ , - ");		   
				   }
				   catch ( Exception e ) {
				       System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				       JOptionPane.showMessageDialog(null, "Student name is already present. Please select a different one.");
				    }
			}
		});
		btnNewButton_1.setBounds(20, 71, 77, 41);
		panel_AddDelete_Student.add(btnNewButton_1);
		////*** End of Add btn for the add student function ***/////
		
		
		
		////*** Delete btn for the Delete student function ***/////
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					   //SQL-lite variables//
					   Connection sqlConnection = null; //connection object for SQL-Lite
					   Statement sqlStatement = null; //stmt sent to sqlLite
					   ResultSet resultSet;
					   String studentName = txtDefaultStudent.getText();
					   boolean studentThere = false;
					   
					   //The following is a reg expression data validation for the studentName textbox //
						studentName = txtDefaultStudent.getText();
						if (studentName.matches("^[a-zA-Z0-9 _-]{1,13}$")) {
							System.out.println("Delete Submitted: Data Valid");
						}
						else {
				 	       System.out.println("Data Not Valid Error");
				    	   throw new DataNotValid("Data Not Valid Error");
						}
					   
					   //tries to open a connection to sqlLite
				       Class.forName("org.sqlite.JDBC");
				       sqlConnection = DriverManager.getConnection("jdbc:sqlite:gradingApp.db");
				       
				       //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       String sql = "SELECT StudentID, StudentName FROM Student WHERE StudentName LIKE '" + studentName + "';";
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       // loop through the result set
					
					  while (resultSet.next()) { 
					  String studenResultName = resultSet.getString("StudentName"); 
					  int studentID = resultSet.getInt("StudentID");
					  System.out.println("Remaining students with that name: " + studenResultName +
					  " StudentID: " + studentID); 
					  studentThere = true;
					  }
					  
				      if (studentThere != true) {
				    	   //if the database does not have those 5 tables, it throws a custom Database not initialized exception
				 	       System.out.println("Student Not Found Error");
				    	   throw new StudentNotFound("Student Not In Database");
				      }
				       //SQL Delete student Statement
				       sqlStatement = sqlConnection.createStatement();   
				       sql = "DELETE FROM Student " + 
				       		"WHERE StudentName LIKE " + "'" + studentName + "';";
				       sqlStatement.executeUpdate(sql);
				       sqlStatement.close();
					   System.out.println("Completed delete statement: " + studentName);
					   
					   //updates the student lists
				       allStudentsHashmap.remove(studentName);
					   defaultComboBoxModel.removeElement(studentName);
				
					 
			           //Query & SQL close
			           sqlStatement.close();
			           
			           
			         //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, Assignment.AssignmentName AS AssignmentName, Submission.Grade AS Grade" + 
				    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
				    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
				    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
				    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
						       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType, AssignmentName";
						       
						      
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       Object [][] tempObject = new Object [99][4];
				       // loop through the result set
					  while (resultSet.next()) { 
						  int i = resultSet.getRow()-1;
						  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
						  String classNameResult = resultSet.getString("ClassName");
						  String assignmentNameResult = resultSet.getString("AssignmentName");
						  int submissionGradeResult = resultSet.getInt("Grade");
						  tempObject [i][0] = classNameResult;
						  tempObject [i][1] = assignmentNameResult;
						  tempObject [i][2] = assignmentTypeResult;
						  tempObject [i][3]= submissionGradeResult;
						  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + assignmentNameResult + ", " + submissionGradeResult);
						  }
				       resultsTableModel.setTableData(tempObject);
					   //updates the current student result label
					  lblCurrentStudentResult.setText(studentName);
			           sqlStatement.close();
			           
				       //SQL Get Student information (Name, and ID)
				       sqlStatement = sqlConnection.createStatement();
				       sql =    "SELECT Assignment.Type as AssignmentType, Class.ClassName AS ClassName, AVG(Submission.Grade) AS Average" + 
				    		    " FROM Student INNER JOIN Submission ON Student.StudentID = Submission.StudentID " 
				    		   +  " INNER JOIN Assignment ON Submission.AssignmentID = Assignment.AssignmentID "
				    		   +  " INNER JOIN Class ON Class.ClassID = Assignment.ClassID "
				    		   +  " INNER JOIN Enrollment ON Class.ClassID = Enrollment.ClassID "
						       +  "WHERE StudentName LIKE '" + studentName + "' GROUP BY ClassName, AssignmentType";
						       
						      
				       resultSet = sqlStatement.executeQuery(sql);
				       
				       Object [][] tempObjectAverages = new Object [99][3];
				       // loop through the result set
					  while (resultSet.next()) { 
						  int i = resultSet.getRow()-1;
						  String assignmentTypeResult = resultSet.getString("AssignmentType"); 
						  String classNameResult = resultSet.getString("ClassName");
						  double calculatedAverageResult = resultSet.getDouble("Average");
						  tempObjectAverages [i][0] = classNameResult;
						  tempObjectAverages [i][1] = assignmentTypeResult;
						  tempObjectAverages [i][2] = calculatedAverageResult;
						  System.out.println("Row" + i + ": " + assignmentTypeResult + ", " + classNameResult + ", " + calculatedAverageResult);
						  }
					  averagesTableModel.setTableData(tempObjectAverages);
					   //updates the current student result label
					  lblCurrentStudentResult.setText(studentName);
					 
			           //Query & SQL close
			           sqlStatement.close();
			           
			           
				       sqlConnection.close();
				    } 
				   catch (DataNotValid e) {
					   JOptionPane.showMessageDialog(null, "Data Submitted is not valid, please only use the following characters: a-z, A-Z, 0-9, _ , - ");		   
				   }
				   catch ( Exception e ) {
				       System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				       JOptionPane.showMessageDialog(null, "Student name is no longer present. Please select a different one.");
				    }
			}
		});
		btnDelete.setBounds(120, 71, 77, 41);
		panel_AddDelete_Student.add(btnDelete);
		////*** End of Delete btn for the Delete student function ***/////
		
		/*
		 * test for setTableData working
		 * Object[][] tempData = { {"TestClass", "Assignment1", "Test", new Integer (3),
		 * new Integer(0)}, {"TestClass", "Assignment2", "Test", new Integer (3), new
		 * Integer(0)}, {"TestClass", "Assignment3", "Test", new Integer (3), new
		 * Integer(0)} }; resultsTableModel.setTableData(tempData);
		 */
		
		
		/*
		 * try { //tries to open a connection to sqlLite
		 * Class.forName("org.sqlite.JDBC"); sqlConnection =
		 * DriverManager.getConnection("jdbc:sqlite:gradingApp.db");
		 * 
		 * //tries to access the initialized database sqlStatement =
		 * sqlConnection.createStatement();
		 * 
		 * //SQL Query Statement String sql = "To Do: SQL Statement";
		 * 
		 * //Query execution resultSet = sqlStatement.executeQuery(sql);
		 * 
		 * // loop through the result set while (resultSet.next()) {}
		 * 
		 * //Query & SQL close sqlStatement.close(); sqlConnection.close(); }
		 * 
		 * catch ( Exception e ) { System.err.println( e.getClass().getName() + ": " +
		 * e.getMessage() ); System.exit(0); }
		 */

	}
}
