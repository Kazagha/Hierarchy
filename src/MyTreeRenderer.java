import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MyTreeRenderer extends DefaultTreeCellRenderer 
{
	ArrayList<RoleData> activeRoleDataArray = new ArrayList<RoleData>();
	
	public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		if(leaf && isTest(value))
		{
			setForeground(new Color(34, 139, 34));
		}
		
		return this;
	}
	
	public void setActiveRoles(ArrayList<RoleData> roleDataArray)
	{
		activeRoleDataArray = roleDataArray;
	}
	
	public boolean isTest(Object obj)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
		//Check if the user object is a HierarchyData node
		if(node.getUserObject() instanceof HierarchyData)
		{
			HierarchyData hd = (HierarchyData) node.getUserObject();
			//Iterate through all active roles
			for(RoleData rd : activeRoleDataArray)
			{
				//Check for a match
				if(hd.contains(rd))
				{
					return true;
				} 
				//Else continue to search for a match
			} 
		}
		//Failing finding a match, return false 
		return false;
	}
}