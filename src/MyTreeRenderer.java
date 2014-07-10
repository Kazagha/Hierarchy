import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
	ArrayList<RoleData> selectedRoleDataArray = new ArrayList<RoleData>();
	//Folder Icons
	ImageIcon greenFolderIcon = new ImageIcon("images/GreenFolderIcon16.png");	
	ImageIcon greyFolderIcon = new ImageIcon("images/GreyFolderIcon16.png");
	ImageIcon orangeFolderIcon = new ImageIcon("images/OrangeFolderIcon16.png");
	ImageIcon redFolderIcon = new ImageIcon("images/RedFolderIcon16.png");
	ImageIcon violetFolderIcon = new ImageIcon("images/VioletFolderIcon16.png");
	ImageIcon yellowFolderIcon = new ImageIcon("images/YellowFolderIcon16.png");
	//Node Icons
	ImageIcon greenNodeIcon = new ImageIcon("images/GreenNodeIcon16.png");
	ImageIcon greyNodeIcon = new ImageIcon("images/GreyNodeIcon16.png");
	ImageIcon orangeNodeIcon = new ImageIcon("images/OrangeNodeIcon16.png");
	
	Color greenColor = new Color(34,139,34);
	Color greyColor = new Color(105, 105, 105);
	
	
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
		
		if(!leaf && nodeContainsRole(value, activeRoleDataArray))
		{
			setIcon(orangeFolderIcon);
		} else if (!leaf) {
			setIcon(greyFolderIcon);
			//setForeground(greyColor);
		}
		
		if(leaf && nodeContainsRole(value, activeRoleDataArray))
		{
			setIcon(orangeNodeIcon);
			//setForeground(greenColor);
		} else if(leaf) {
			setIcon(greyNodeIcon);
			//setForeground(greyColor);
		}
		/*
		if(!leaf && nodeContainsRole(value, selectedRoleDataArray))
		{
			setIcon(greenFolderIcon);
			setFont(getFont().deriveFont(Font.BOLD));
		} else if (leaf && nodeContainsRole(value, selectedRoleDataArray)) {
			setIcon(greenNodeIcon);
			setFont(getFont().deriveFont(Font.BOLD));
		} else {
			setFont(getFont().deriveFont(Font.PLAIN));
		}
		*/
		if(nodeContainsRole(value, selectedRoleDataArray))
		{
			setFont(getFont().deriveFont(Font.BOLD));
			
			if(!leaf)
			{
				setIcon(greenFolderIcon);
			} else {
				setIcon(greenNodeIcon);
			}
		} else {
			setFont(getFont().deriveFont(Font.PLAIN));
		}
				
		/**
		 * Consider using the following
		 * 	Border blackline = BorderFactory.createLineBorder(Color.black);
		 *	setBorder(blackline);
		 *  setForeground(greenColor);
		 */
				
		//TODO: Tool tips are not quick enough, need another solution
		//setToolTipText(getToolTip(value));		
		
		return this;
	}
	
	public void setActiveRoles(ArrayList<RoleData> roleDataArray)
	{
		activeRoleDataArray = roleDataArray;
	}
	
	public void setSelectedRoleData(ArrayList<RoleData> roleDataArray)
	{
		selectedRoleDataArray = roleDataArray;
	}
	
	public boolean nodeContainsRole(Object obj, ArrayList<RoleData> roleArrayList)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
		//Check if the user object is a HierarchyData node
		if(node.getUserObject() instanceof HierarchyData)
		{
			HierarchyData hd = (HierarchyData) node.getUserObject();
			//Iterate through all active roles
			for(RoleData rd : roleArrayList)
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
	
	public String getToolTip(Object obj)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
		//Check if the user object is a HierarchyData node
		if(node.getUserObject() instanceof HierarchyData)
		{
			String s = "<html>";
			HierarchyData hd = (HierarchyData) node.getUserObject();
			for(RoleData rd : hd.getPermissionList())
			{
				s += String.format("%s <br>%n", rd.getDescription());
			}
			s += "</html>";			
			return s;
		} else {
			return null;
		}
	}
}