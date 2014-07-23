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
		
		public boolean isCellEditable(int row, int col)
		{
			//return editMode && col == 0;
			return editMode;
		}
		
		public ArrayList<RoleData> getArray()
		{
			return dataArray;
		}

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
		
		public void addRow(int roleNumber, String RoleDesc)
		{
			dataArray.add(new RoleData(roleNumber, RoleDesc));
			//this.fireTableRowsInserted(dataArray.size() - 1, dataArray.size() - 1);
			int rowNumber = getRowCount() - 1;
			this.fireTableRowsInserted(rowNumber, rowNumber);
		}
		
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
		
		public void setArray(ArrayList<RoleData> inputArray)
		{
			dataArray = inputArray;
			this.fireTableDataChanged();
		}
		
		public void refreshArray()
		{
			//TODO: If there is no data in the array this will cause an error
			this.fireTableDataChanged();
		}
		
		public void setEditMode(boolean editMode)
		{
			this.editMode = editMode;
		}
		
		public void addExtraRows(int numOfRows)
		{
			for(int i = 0; i < numOfRows; i++)
			{
				addRow(0, "");
			}
		}
		
		public void removeRow(int row)
		{
			dataArray.remove(row);
			this.fireTableRowsDeleted(row, row);
		}
		
		public void removeArray(ArrayList<RoleData> removeArray)
		{
			int lastRow = dataArray.size() - 1;
			
			//Check that both dataArray and removeArray contain data 
			if(!dataArray.isEmpty() && !removeArray.isEmpty())
			{
				dataArray.removeAll(removeArray);
				this.fireTableRowsDeleted(getRowCount(), lastRow);
			}
		}
	}