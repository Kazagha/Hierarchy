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
	ImageIcon redNodeIcon = new ImageIcon("images/RedNodeIcon16.png");
	ImageIcon violetNodeIcon = new ImageIcon("images/VioletNodeIcon16.png");
	ImageIcon yellowNodeIcon = new ImageIcon("images/YellowNodeIcon16.png");
	//Setup Colors
	Color blackColor = new Color(51, 51, 51);
	Color greenColor = new Color(34, 139, 34);
	Color greyColor = new Color(105, 105, 105);
	Color orangeColor = new Color(255, 140, 0);	
	
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
		
		if(value instanceof HierarchyTreeNode)
		{
			HierarchyTreeNode tempTreeNode = (HierarchyTreeNode) value;
			
			setFont(getFont().deriveFont(Font.PLAIN));
			setForeground(blackColor);
			
			switch(tempTreeNode.getActiveMode())
			{						
			case INACTIVE:					
				if(!leaf)
				{
					setIcon(greyFolderIcon);
				} else {
					setIcon(greyNodeIcon);
				}				
				break;
			case PARTIAL_ACTIVE:
				setForeground(orangeColor);
				
				if(!leaf)
				{
					setIcon(greyFolderIcon);
				} else {
					setIcon(greyNodeIcon);
				}	
				break;
			case ACTIVE:				
				if(!leaf)
				{
					setIcon(orangeFolderIcon);
				} else {
					setIcon(orangeNodeIcon);
				}	
			default:
				break;
			}
			
			switch(tempTreeNode.getSelectedMode())
			{						
			case NOT_SELECTED:
				break;
			case PARTIAL_SELECTED:
				setForeground(greenColor);
				break;
			case SELECTED:
				setFont(getFont().deriveFont(Font.BOLD));
				
				if(!leaf)
				{
					setIcon(greenFolderIcon);
				} else {
					setIcon(greenNodeIcon);
				}
				break;
			default:
				break;
			}
		}	
		
		// TODO: Tool tips are not quick enough, need another solution for displaying roles
		setToolTipText(getToolTip(value));
		
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
				s += String.format("%s <br>", rd.getDescription());
			}
			s += "</html>";
			return s;
		} else {
			return null;
		}
	}
}