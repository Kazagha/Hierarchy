import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MyTreeRenderer extends DefaultTreeCellRenderer 
{
	ArrayList<RoleData> activeRoleDataArray = new ArrayList<RoleData>();
	ImageIcon greenIcon = new ImageIcon("images/GreenFolderIcon16.png");
	Color greenColor = new Color(34,139,34);	
	ImageIcon greyIcon = new ImageIcon("images/GreyFolderIcon16.png");
	
	ImageIcon orangeIcon = new ImageIcon("images/OrangeFolderIcon16.png");
	
	
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
		
		if(!leaf && isTest(value))
		{
			setIcon(greenIcon);
		} else if (!leaf) {
			setIcon(orangeIcon);
		}
		
		if(leaf && isTest(value))
		{
			setIcon(greenIcon);
			setForeground(greenColor);
		} else if(leaf) {
			setIcon(greyIcon);
		}
		
		/**
		 * Consider using the following
		 * 	Border blackline = BorderFactory.createLineBorder(Color.black);
		 *	setBorder(blackline);
		 *  setForeground(greenColor);
		 */
		
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
	
	public String getToolTip()
	{
		return "";
	}
}