package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class AveragesTableModel extends AbstractTableModel {
    private String[] columnNames = {
			"Class", "Assignment Type", "Average Grade"
		};
    private Object[][] data = {
    	        };

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }


    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    public void setTableData(Object [][] dataInput) {
    	data = dataInput;
    	fireTableDataChanged();
    }
   
}
