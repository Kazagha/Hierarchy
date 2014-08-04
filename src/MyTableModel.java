import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

	class MyTableModel extends AbstractTableModel
	{
		ArrayList<RoleData> dataArray = new ArrayList<RoleData>();
		String [] columnName = {"Role Num", "Role Description"};
		private boolean editMode = false;
	
		@Override
		public int getColumnCount() {
			return columnName.length;
		}

		@Override
		public int getRowCount() {
			return dataArray.size();
		}
		
		/**
		 * Returns the name of the column appearing in the view at the column position <code>col</code>.
		 * @param col - The column in the view being queried
		 * @return The column's name
		 */
		public String getColumnName(int col)
		{
			return columnName[col];
		}
		
		/**
		 * The JTable uses this method to determine the data class 
		 * of the specified column for the JTable editor/renderer
		 */
		public Class getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}		

		/**
		 * Return <code>true</code> if the specified cell can be edited <br>
		 * @return boolean - True if the cell can be edited 
		 */
		public boolean isCellEditable(int row, int col)
		{
			return editMode;
		}
		
		/**
		 * Fetch the entire RoleData array from <code>this</code>. 
		 * @return RoleData array
		 */
		public ArrayList<RoleData> getArray()
		{
			return dataArray;
		}

		/**
		 * Return the value of the cell at the specified row and column
		 * @param row - The row whose value is to be queried
		 * @param col - The column whose value is to be queried
		 * @return - The String value of the specified cell
		 */
		@Override
		public String getValueAt(int row, int col) {
			RoleData rowData = dataArray.get(row);
			if(col == 0)
			{
				return String.valueOf(rowData.getRole());
			} else {
				return rowData.getDescription();
			}
		}
		
		public  void setValueAt(Object value, int row, int col)
		{
			try {
				if(col == 0)
				{
					dataArray.get(row).setRole(Integer.valueOf((String)value));
				} else if (col == 1)
				{
					dataArray.get(row).setDescription((String) value);
				}
			} catch (ClassCastException err) {
				System.out.println("Unable to convert " + value);
			}
		}
		
		/**
		 * Fetch the RoleData element at the specified index
		 * @param row - Specified row index
		 * @return A RoleData element 
		 */
		public RoleData getRoleAt(int row) {
			return dataArray.get(row);
		}
		
		/**
		 * Add a new role to <code>this</code> with the specified parameters 
		 * @param roleNumber - Unique ID of the role
		 * @param RoleDesc - Description of the role
		 */
		public void addRow(int roleNumber, String RoleDesc)
		{
			dataArray.add(new RoleData(roleNumber, RoleDesc));
			//this.fireTableRowsInserted(dataArray.size() - 1, dataArray.size() - 1);
			int rowNumber = getRowCount() - 1;
			this.fireTableRowsInserted(rowNumber, rowNumber);
		}
		
		/**
		 * Clear all data from <code>this</code> table's model array <br>
		 * then refresh the table
		 */
		public void clearArray()
		{
			int lastRow = dataArray.size() - 1;
			//Attempt to clear data only if the array contains data
			if(lastRow > 0)
			{
				dataArray.clear();
				//Specify the rows that have been removed
				this.fireTableRowsDeleted(0, lastRow);
			}
		}
		
		/**
		 * Replace <code>this</code> table's model array, with the specified array.
		 * @param inputArray - The specified array
		 */
		public void setArray(ArrayList<RoleData> inputArray)
		{
			int lastRow = dataArray.size() - 1;
			dataArray = inputArray;
			
			if(inputArray.isEmpty() && lastRow > 0)
			{
				//No data; delete rows
				this.fireTableRowsDeleted(0, lastRow);
			} else {
				//Data present; fire table changed
				this.fireTableDataChanged();
			}
		}
		
		/**
		 * Set this to enable/disable editing on the table.
		 * @param editMode - True to enable editing
		 */
		public void setEditMode(boolean editMode)
		{
			this.editMode = editMode;
		}
		
		/**
		 * Add the specified number of additional blank rows to the table.
		 * @param numOfRows - Specified number of rows
		 */
		public void addExtraRows(int numOfRows)
		{
			for(int i = 0; i < numOfRows; i++)
			{
				addRow(0, "");
			}
		}
		
		/**
		 * Remove the specified row from <code>this</code>
		 * @param row - The specified row's index
		 */
		public void removeRow(int row)
		{
			dataArray.remove(row);
			this.fireTableRowsDeleted(row, row);
		}
		
		/**
		 * Remove the specified array of roles from <code>this</code>
		 * @param removeArray - The specified array
		 */
		public void removeArray(ArrayList<RoleData> removeArray)
		{
			//Find the last row in the current array
			int lastRow = dataArray.size() - 1;
			
			// Remove the specified roles from the array
			dataArray.removeAll(removeArray);
			
			// If the array is now empty, but previously contained data
			if(dataArray.isEmpty() && lastRow > 0)
			{
				this.fireTableRowsDeleted(0, lastRow);
			// If the array is not empty
			} else if(! dataArray.isEmpty()) {
				this.fireTableDataChanged();
			}
		}
	}