import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MyTreeRenderer extends DefaultTreeCellRenderer 
{
	// Folder Icons
	ImageIcon greenFolderIcon = new ImageIcon("images/GreenFolderIcon16.png");	
	ImageIcon greyFolderIcon = new ImageIcon("images/GreyFolderIcon16.png");
	ImageIcon orangeFolderIcon = new ImageIcon("images/OrangeFolderIcon16.png");
	ImageIcon redFolderIcon = new ImageIcon("images/RedFolderIcon16.png");
	ImageIcon violetFolderIcon = new ImageIcon("images/VioletFolderIcon16.png");
	ImageIcon yellowFolderIcon = new ImageIcon("images/YellowFolderIcon16.png");
	ImageIcon blueFolderIcon = new ImageIcon("images/BlueFolderIcon16.png");
	// Node Icons
	ImageIcon greenNodeIcon = new ImageIcon("images/GreenNodeIcon16.png");
	ImageIcon greyNodeIcon = new ImageIcon("images/GreyNodeIcon16.png");
	ImageIcon orangeNodeIcon = new ImageIcon("images/OrangeNodeIcon16.png");
	ImageIcon redNodeIcon = new ImageIcon("images/RedNodeIcon16.png");
	ImageIcon violetNodeIcon = new ImageIcon("images/VioletNodeIcon16.png");
	ImageIcon yellowNodeIcon = new ImageIcon("images/YellowNodeIcon16.png");
	ImageIcon blueNodeIcon = new ImageIcon("images/BlueNodeIcon16.png");
	// Alert Icons
	ImageIcon orangeAlertIcon = new ImageIcon("images/OrangeAlert16.png");
	ImageIcon greenAlertIcon = new ImageIcon("images/GreenAlert16.png");
	ImageIcon blueAlertIcon = new ImageIcon("images/BlueAlert16.png");
	// Compound Icons	
	CompoundIcon orangeAlertGreyFolderIcon = new CompoundIcon(CompoundIcon.Axis.X_AXIS, 4, greyFolderIcon, orangeAlertIcon);
	CompoundIcon greenAlertGreyFolderIcon = new CompoundIcon(CompoundIcon.Axis.X_AXIS, 4, greyFolderIcon, greenAlertIcon);
	CompoundIcon blueAlertGreyFolderIcon = new CompoundIcon(CompoundIcon.Axis.X_AXIS, 4, greyFolderIcon, blueAlertIcon);
	CompoundIcon greenAlertOrangeFolderIcon = new CompoundIcon(CompoundIcon.Axis.X_AXIS, 4, orangeFolderIcon, greenAlertIcon);
	CompoundIcon greenAlertBlueFolderIcon = new CompoundIcon(CompoundIcon.Axis.X_AXIS, 4, blueFolderIcon, greenAlertIcon);
	// Setup Colors
	Color blackColor = new Color(51, 51, 51);
	Color greenColor = new Color(152, 251, 152);
	Color greyColor = new Color(105, 105, 105);
	Color orangeColor = new Color(244, 164, 96);
	Color whiteColor = new Color(255, 255, 255);
	Color blueColor = new Color(193, 237, 255);
	
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
				
				if(!leaf)
				{
					setIcon(blueAlertGreyFolderIcon);
				} else {
					setIcon(greyNodeIcon);
				}	
				break;
			case ACTIVE:				
				if(!leaf)
				{
					setIcon(blueFolderIcon);
				} else {
					setIcon(blueNodeIcon);
				}	
			default:
				break;
			}
			
			switch(tempTreeNode.getSelectedMode())
			{						
			case NOT_SELECTED:
				break;
			case PARTIAL_SELECTED:
				// Check the icon on this node and swap it for the alert icon
				if(getIcon() == greyFolderIcon || getIcon() == blueAlertGreyFolderIcon)	{
					setIcon(greenAlertGreyFolderIcon);
				} else if (getIcon() == blueFolderIcon) {
					setIcon(greenAlertBlueFolderIcon);
				}
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
		
		setToolTipText(getToolTip(value));
		
		return this;
	}
	
	/**
	 * Return a list of permissions (HTML String) for the specified node <code>obj</code>
	 * @param obj The specified node
	 * @return - A String of permissions
	 */
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