import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

	class MyTableModel extends AbstractTableModel
	{
		ArrayList<RoleData> dataArray = new ArrayList<RoleData>();
		String [] columnName = {"Role Num", "Role Description"};
	
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
			dataArray = new ArrayList<RoleData>();
			this.fireTableDataChanged();
		}
		
		public void setArray(ArrayList<RoleData> inputArray)
		{
			dataArray = inputArray;
			this.fireTableDataChanged();
		}
		
		public void refreshArray()
		{
			this.fireTableDataChanged();
		}
	}