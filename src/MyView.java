import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class MyView extends JPanel {
	
	//Static Strings
	public final static String ENTER_ACTION = "enter";

	//Frame Width and Height
	final int FRAME_WIDTH = 300;
	final int FRAME_HEIGHT = 650;
	
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
	JTextField inputTextFieldLHS = new JTextField();
	JTextField inputTextFieldRHS = new JTextField();
	//Buttons
	JButton swapButton;
	JButton loadButtonLHS;
	JButton loadButtonRHS;
	
	//Menu Items	
	JMenuItem compareMenuItem = new JMenuItem();
	JMenuItem swapMenuItem = new JMenuItem();
	JCheckBoxMenuItem manualEntryCheckBox = new JCheckBoxMenuItem();
	JMenuItem importMenuItem = new JMenuItem();
	JMenuItem exportRoleMenuItem = new JMenuItem();
	JMenuItem exportHierarchyMenuItem = new JMenuItem();
	JMenuItem loadConfigMenuItem = new JMenuItem();
	JMenuItem exitMenuItem = new JMenuItem();
	JMenuItem hierarchyViewMenuItem = new JMenuItem();
	JMenuItem listViewMenuItem = new JMenuItem();
	JMenuItem insertRolesMenuItem = new JMenuItem();
	JMenuItem removeRolesMenuItem = new JMenuItem();
	JMenuItem dataSourceMenuItem = new JMenuItem();
	JMenuItem searchMenuItem = new JMenuItem();
	JMenuItem statsMenuItem = new JMenuItem();
		
	MyTextFieldListener textFieldlistener;
	
	ComponentWithTitle contentLHSView = new ComponentWithTitle(tableLHS);
	ComponentWithTitle contentRHSView = new ComponentWithTitle(tableRHS);
	ComponentWithTitle contentRHSHierarchy = new ComponentWithTitle(treeRHS);
	
	// Status Bar
	MyStatusBar statusBar = new MyStatusBar();
	
	public MyView()
	{
		initComponents();
		
		setColumnWidth(tableLHS);
		setColumnWidth(tableRHS);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contentLHSView, contentRHSView);
		splitPane.setResizeWeight(.5);
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup()			
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addGroup(layout.createParallelGroup(Alignment.CENTER)
									.addComponent(inputTextFieldLHS)
									.addComponent(loadButtonLHS)
									)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(Alignment.CENTER)
									.addComponent(inputTextFieldRHS)
									.addComponent(loadButtonRHS)
									)
							.addContainerGap()
							)
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addGroup(layout.createParallelGroup(Alignment.CENTER)
									.addComponent(splitPane)
									.addComponent(swapButton)
							)
							.addContainerGap()
						)
					.addComponent(statusBar)
				);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(inputTextFieldLHS)
						.addComponent(inputTextFieldRHS)
						)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(loadButtonLHS)
						.addComponent(loadButtonRHS)
						.addComponent(swapButton)
						)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(splitPane)
						)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(statusBar, 25, 25, 25)
				);		
	}
	
	class ComponentWithTitle extends JPanel
	{
		JLabel titleLabel = new JLabel(" - ");
		ComponentWithTitle(JComponent component)
		{
			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);
			
			JScrollPane scrollPane = new JScrollPane(component);
			scrollPane.setMinimumSize(new Dimension(300,0));			
			
			layout.setHorizontalGroup(
					layout.createParallelGroup()
							.addComponent(titleLabel, GroupLayout.Alignment.CENTER)
							.addComponent(scrollPane)
					);
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addComponent(titleLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scrollPane)
					);	
			}
		
		public String getTitle()
		{
			return titleLabel.getText();
		}
		
		public void setTitle(String s)
		{
			titleLabel.setText(s);
		}
	}
	
	protected JMenuBar createMenu()
	{	
		// Set Menu Text
		compareMenuItem.setText("Compare");		
		swapMenuItem.setText("Swap Sides");
		manualEntryCheckBox.setText("Manual Entry");
		importMenuItem.setText("Import");
		exportRoleMenuItem.setText("Permissions");
		exportHierarchyMenuItem.setText("Hierarchy");
		loadConfigMenuItem.setText("Load Conf");
		exitMenuItem.setText("Exit");
		hierarchyViewMenuItem.setText("Hierarchy");
		listViewMenuItem.setText("Permissions");
		insertRolesMenuItem.setText("Insert");
		removeRolesMenuItem.setText("Delete");
		dataSourceMenuItem.setText("Data Source");
		searchMenuItem.setText("Search");
		statsMenuItem.setText("Statistics");
		
		// Manual Entry Mode is disabled by default
		setManualEntry(false);
		
		// Disable unimplemented Menu Items
		importMenuItem.setEnabled(false);
		exportRoleMenuItem.setEnabled(true);
		exportHierarchyMenuItem.setEnabled(false);
		dataSourceMenuItem.setEnabled(true);
		searchMenuItem.setEnabled(false);
		statsMenuItem.setEnabled(false);
		
		// Create Menu Bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
				
		// Create Root Menu
		JMenu menu;
		
		// 'File' Top Menu
		menu = new JMenu("File");
		menu.add(importMenuItem);
		// 'Export' Sub Menu
		JMenu exportSubMenu = new JMenu("Export");
		exportSubMenu.add(exportRoleMenuItem);
		exportSubMenu.add(exportHierarchyMenuItem);		
		menu.add(exportSubMenu);
		
		menu.addSeparator();
		menu.add(dataSourceMenuItem);
		menu.add(statsMenuItem);
		menu.addSeparator();
		menu.add(exitMenuItem);		
		menuBar.add(menu);
		
		// 'Edit' Top Menu
		menu = new JMenu("Edit");
		menu.add(swapMenuItem);
		menu.add(compareMenuItem);
		menu.addSeparator();
		menu.add(manualEntryCheckBox);
		menu.add(insertRolesMenuItem);
		menu.add(removeRolesMenuItem);
		menu.addSeparator();
		menu.add(searchMenuItem);
		menuBar.add(menu);
		
		// 'View' Top Menu
		menu = new JMenu("View");
		menu.add(listViewMenuItem);
		menu.add(hierarchyViewMenuItem);
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
		t.setAutoCreateRowSorter(true);
	}
	
	public void setManualEntry(boolean isEnabled)
	{
		removeRolesMenuItem.setEnabled(isEnabled);
		insertRolesMenuItem.setEnabled(isEnabled);
	}
	
	public String getLHSTextToString()
	{
		return inputTextFieldLHS.getText();
	}
	
	public String getRHSTextToString()
	{
		return inputTextFieldRHS.getText();
	}	
	
	public JTextField getLHSTextField()
	{
		return inputTextFieldLHS;
	}
	public JTextField getRHSTextField() 
	{
		return inputTextFieldRHS;
	}
	
	public String getLHSViewTitle()
	{
		return contentLHSView.getTitle();
	}
	
	public String getRHSViewTitle()
	{
		return contentRHSView.getTitle();
	}
	
	public String getRHSHierarchyTitle()
	{
		return contentRHSHierarchy.getTitle();
	}
	
	public MyTableModel getLHSTableModel()
	{
		return (MyTableModel) tableLHS.getModel();
	}
	
	public MyTableModel getRHSTableModel()
	{
		return (MyTableModel) tableRHS.getModel();
	}
	
	public JTable getTableLHS()
	{
		return tableLHS;
	}
	
	public JTable getTableRHS()
	{
		return tableRHS;
	}
	
	public JCheckBoxMenuItem getManualEntryCheckBox()
	{
		return manualEntryCheckBox;
	}
	
	public MyStatusBar getStatusBar()
	{
		return statusBar;
	}	
	
	/*
	 * This can't be used without importing the CompentWithTitle into MyController
	public ComponentWithTitle getLHSContent()
	{
		return contentLHSView;
	}
	*/
	
	public DefaultMutableTreeNode getRootNode()
	{
		return rootNode;
	}
	
	public JTree getJTree()
	{	
		return treeRHS;
	}
	
	public MyTextFieldListener getTextFieldListener()
	{
		return textFieldlistener;
	}
	
	public void setControllerActions(ActionListener controllerActionListener)
	{		
		//Setup Menu Actions
		compareMenuItem.setActionCommand("Compare");
		compareMenuItem.addActionListener(controllerActionListener);
		
		swapMenuItem.setActionCommand("Swap Sides");
		swapMenuItem.addActionListener(controllerActionListener);
		
		hierarchyViewMenuItem.setActionCommand("View Hierarchy");
		hierarchyViewMenuItem.addActionListener(controllerActionListener);
		
		listViewMenuItem.setActionCommand("View List");
		listViewMenuItem.addActionListener(controllerActionListener);
		
		exitMenuItem.setActionCommand("Exit");
		exitMenuItem.addActionListener(controllerActionListener);
		
		removeRolesMenuItem.setActionCommand("Remove Selected");
		removeRolesMenuItem.addActionListener(controllerActionListener);
		
		exportRoleMenuItem.setActionCommand("Export Role");
		exportRoleMenuItem.addActionListener(controllerActionListener);
		
		dataSourceMenuItem.setActionCommand("Source");
		dataSourceMenuItem.addActionListener(controllerActionListener);
		
		//Setup Button Actions
		loadButtonLHS.setActionCommand("Load LHS");
		loadButtonLHS.addActionListener(controllerActionListener);
		
		loadButtonRHS.setActionCommand("Load RHS");
		loadButtonRHS.addActionListener(controllerActionListener);
		
		swapButton.setActionCommand("Swap Sides");
		swapButton.addActionListener(controllerActionListener);
		
		insertRolesMenuItem.setActionCommand("Insert Roles");
		insertRolesMenuItem.addActionListener(controllerActionListener);
		
		/*
		 * 	InputMap im = textField.getInputMap();
		 *	ActionMap am = textField.getActionMap();
		 *	im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
		 *	am.put(COMMIT_ACTION, new CommitAction());
		 */
	}
	
	public void initComponents()
	{
		//Dummy Icons
		ImageIcon redNodeIcon = new ImageIcon("images/RedNodeIcon16.png");
		ImageIcon violetNodeIcon = new ImageIcon("images/VioletNodeIcon16.png");
		ImageIcon yellowNodeIcon = new ImageIcon("images/YellowNodeIcon16.png");
		
		swapButton = new JButton();
		swapButton.setIcon(yellowNodeIcon);
		swapButton.setHorizontalTextPosition(JButton.CENTER);
		swapButton.setVerticalTextPosition(JButton.BOTTOM);
		
		loadButtonLHS = new JButton("Load");
		loadButtonRHS = new JButton("Load");
		
		//Setup the Tree Cell Renderer
		treeRHS.setCellRenderer(new MyTreeRenderer());		
		
		//Setup the listener on input text fields
		textFieldlistener = new MyTextFieldListener();
		inputTextFieldLHS.getDocument().putProperty("owner", inputTextFieldLHS);
		inputTextFieldLHS.getDocument().addDocumentListener(textFieldlistener);
		textFieldlistener.setKeyMapping(inputTextFieldLHS);
		inputTextFieldRHS.getDocument().putProperty("owner", inputTextFieldRHS);
		inputTextFieldRHS.getDocument().addDocumentListener(textFieldlistener);
		textFieldlistener.setKeyMapping(inputTextFieldRHS);	
	}
	
	public void setLHSViewTitle(String s)
	{
		contentLHSView.setTitle(s);	
	}
	
	public void setRHSViewTitle(String s)
	{
		contentRHSView.setTitle(s);
	}
	
	public void setRHSHierarchyTitle(String s)
	{
		contentRHSHierarchy.setTitle(s);
	}
	
	public void setHierarchyPanel(boolean isHierarchy)
	{
		if(isHierarchy)
		{
			splitPane.setRightComponent(contentRHSHierarchy);
		} else {			
			splitPane.setRightComponent(contentRHSView);
		}
	}	
	
	public void createAndShowGUI()
	{
		//Create frame setup Window
		JFrame frame = new JFrame("Shiv: Security HIerarchy Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(FRAME_HEIGHT, FRAME_WIDTH));
		
		//Add the content  
		frame.getContentPane().add(this);
		
		//Add the Menu
		frame.setJMenuBar(createMenu());
		
		//Display the window
		frame.pack();
		frame.setVisible(true);		
	}
}
