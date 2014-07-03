import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MyTreeRenderer extends DefaultTreeCellRenderer 
{
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
	
	public boolean isTest(Object obj)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
		if(node.getUserObject() instanceof HierarchyData)
		{
			HierarchyData hd = (HierarchyData) node.getUserObject();
			return hd.contains(new RoleData(328, "CM_00"));
		} else {
			return false;
		}
	}
}
