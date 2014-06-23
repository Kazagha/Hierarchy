import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class MyView extends JPanel {

	JPanel panelLHS;
	JPanel panelRHS;
	//Lower half split pane
	JSplitPane splitPane;
	//Permissions Tables
	JTable tableLHS = new JTable(new MyTableModel());
	JTable tableRHS = new JTable(new MyTableModel());
	//Hierarchy Nodes
	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Hierarchy");
	JTree  treeRHS  = new JTree(rootNode);
	//User Input Fields
	JTextField userTextFieldLHS = new JTextField();
	JTextField userTextFieldRHS = new JTextField();
	//Title Lables
	JLabel titleLabelLHS = new JLabel(" - ");
	JLabel titleLabelRHS = new JLabel(" - ");
	//Buttons
	JButton swapButton = new JButton();
	JButton loadButtonLHS = new JButton("Load LHS");
	JButton loadButtonRHS = new JButton("Load RHS");
	//Menu Items
	JMenuItem compareMenuItem = new JMenuItem("Compare");
	JMenuItem swapMenuItem = new JMenuItem("Swap Sides");
	JMenuItem manualEntryMenuItem = new JMenuItem("Manual Entry");
	JMenuItem saveLHSMenuItem = new JMenuItem("Left");
	JMenuItem saveRHSMenuItem = new JMenuItem("Right");
	JMenuItem exitMenuItem = new JMenuItem("Exit");
	
	ComponentWithTitle contentLHS = new ComponentWithTitle(titleLabelLHS, tableLHS);
	ComponentWithTitle contentRHSView = new ComponentWithTitle(titleLabelRHS, tableRHS);
	ComponentWithTitle contentRHSHierarchy = new ComponentWithTitle(titleLabelLHS, treeRHS);
	
	public MyView()
	{		
		setColumnWidth(tableLHS);
		setColumnWidth(tableRHS);
		//JPanel panelLHS = new ComponentWithTitle(titleLabelLHS, tableLHS);
		//JPanel panelRHS = new ComponentWithTitle(titleLabelRHS, tableRHS);
		panelLHS = contentLHS;
		panelRHS = contentRHSView;
		
		Icon swapIcon = new ImageIcon("images/arrow-repeat.png");
		if(swapIcon != null)
		{
			swapButton.setIcon(swapIcon);	
		}
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLHS, panelRHS);
		splitPane.setResizeWeight(0);
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(userTextFieldLHS)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(swapButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(userTextFieldRHS)
							.addContainerGap())
					.addGroup(Alignment.CENTER, layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(loadButtonLHS)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(loadButtonRHS)
							.addContainerGap())
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							//.addComponent(tableLHS)
							.addComponent(splitPane)
							.addContainerGap())
				);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(userTextFieldLHS)
						.addComponent(userTextFieldRHS)
						.addComponent(swapButton))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(loadButtonLHS)
						.addComponent(loadButtonRHS)
						)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						//.addComponent(tableLHS)
						.addComponent(splitPane))
				.addContainerGap()
				);		
	}
	
	class ComponentWithTitle extends JPanel
	{
		ComponentWithTitle(JLabel title, JComponent component)
		{
			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);
			
			JScrollPane scrollPane = new JScrollPane(component);
			scrollPane.setMinimumSize(new Dimension(300,0));			
			
			layout.setHorizontalGroup(
					layout.createParallelGroup()
							.addComponent(title, GroupLayout.Alignment.CENTER)
							.addComponent(scrollPane)
					);
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addComponent(title)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scrollPane)
					);		
		}	
	}
	
	protected JMenuBar createMenu()
	{		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		
		JMenu menu;
		
		//'File' Top Menu
		menu = new JMenu("File");
		//'Save' Sub Menu
		JMenu saveJMenu = new JMenu("Save");
		saveJMenu.add(saveLHSMenuItem);
		saveJMenu.add(saveRHSMenuItem);
		menu.add(saveJMenu);
		//'Exit' Sub Menu
		menu.add(exitMenuItem);
		menuBar.add(menu);
		
		//'Edit Top Menu'
		menu = new JMenu("Edit");
		menu.add(new JMenuItem("List?"));
		menu.add(compareMenuItem);
		menu.addSeparator();
		menu.add(swapMenuItem);
		menu.addSeparator();
		menu.add(manualEntryMenuItem);
		menuBar.add(menu);
		
		return menuBar;
	}
	
	/*
	 * This is a short term solution and needs to be implements in a 
	 * TableColumnModel properly. Including centering text. 
	 */
	public void setColumnWidth(JTable t)
	{
		t.getColumnModel().getColumn(0).setMaxWidth(80);
		t.getColumnModel().getColumn(0).setMinWidth(80);
	}
	
	public String getLHSTextField()
	{
		return userTextFieldLHS.getText();
	}
	
	public String getRHSTextField()
	{
		return userTextFieldRHS.getText();
	}
	
	public String getLHSTitleLabel()
	{
		return titleLabelLHS.getText();
	}
	
	public String getRHSTitleLable()
	{
		return titleLabelRHS.getText();
	}
	
	public MyTableModel getLHSTableModel()
	{
		return (MyTableModel) tableLHS.getModel();
	}
	
	public MyTableModel getRHSTableModel()
	{
		return (MyTableModel) tableRHS.getModel();
	}
	
	public void setControllerActions(ActionListener controllerActionListener)
	{		
		//Setup Menu Actions
		compareMenuItem.setActionCommand("Compare");
		swapMenuItem.setActionCommand("Swap Hierarchy");
		exitMenuItem.setActionCommand("Exit");
		//Setup Button Actions
		loadButtonLHS.setActionCommand("Load LHS");
		loadButtonRHS.setActionCommand("Load RHS");
		swapButton.setActionCommand("Swap Sides");
		//Setup Action Listener
		compareMenuItem.addActionListener(controllerActionListener);
		swapMenuItem.addActionListener(controllerActionListener);
		exitMenuItem.addActionListener(controllerActionListener);
		loadButtonLHS.addActionListener(controllerActionListener);
		loadButtonRHS.addActionListener(controllerActionListener);
		swapButton.addActionListener(controllerActionListener);
	}
	
	public void setLHSTitle(String s)
	{
		titleLabelLHS.setText(s);		
	}
	
	public void setRHSTitle(String s)
	{
		titleLabelRHS.setText(s);
	}
	
	public void setRHSPanel(boolean b)
	{
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Test");
		rootNode.add(node);
		splitPane.setRightComponent(new ComponentWithTitle(titleLabelRHS, treeRHS));
	}	
	
	public void createAndShowGUI()
	{
		//Create frame setup Window
		JFrame frame = new JFrame("My View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(650, 300));
		
		//Add the content  
		frame.getContentPane().add(this);
		
		//Add the Menu
		frame.setJMenuBar(createMenu());
		
		//Display the window
		frame.pack();
		frame.setVisible(true);		
	}
}
