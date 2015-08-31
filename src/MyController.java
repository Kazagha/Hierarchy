import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.arcanesanctuary.Configuration.Conf;
import net.arcanesanctuary.Configuration.JAXBController;

public class MyController {

	private MyView view;
	private Conf conf;
	private SQLQuery sql;
	private JTable tableLHS;
	private JTable tableRHS;
	private MyTableModel modelRHS;
	private MyTableModel modelLHS;
	private JTree treeRHS;
	private JCheckBoxMenuItem manualEntryCheckBox;
	private JFileChooser fc;	
	private JTable jTableLHS;
	
	// JAXB Controller for loading/saving to XML
	JAXBController jaxb;
	
	// Status Bar
	private MyStatusBar statusBar;
	private int nodeCount;
	private int activeNodeCount;
	private int selectedNodeCount;
	
	private JLabel inactiveLabel = new JLabel("0");
	private JLabel activeLabel = new JLabel("0");
	private JLabel selectedLabel = new JLabel("0");
	private JLabel statusLabel = new JLabel(" ");
	
	// Search Panel
	private MySearchDialog searchDialog;
	
	// Setup Colors
	Color blackColor = new Color(51, 51, 51);
	Color greenColor = new Color(152, 251, 152);
	Color greyColor = new Color(205, 201, 201);
	Color orangeColor = new Color(244, 164, 96);
	
	private static enum UpdateMode { ACTIVE, SELECTED }
	private static enum Iterate { FORWARDS, BACKWARDS }
	
	public MyController(MyView view)
	{
		// Create the Action Listener for all actions in the controller
		MyActionListener actListener = new MyActionListener();
		// Set core MVC elements:
		// Create the View and set Actions
		this.view = view;
		this.view.setControllerActions(actListener);
		// Create the Model
		this.tableLHS = this.view.getTableLHS();
		this.modelLHS = ((MyTableModel) this.tableLHS.getModel());
		this.tableRHS = this.view.getTableRHS();
		this.modelRHS = ((MyTableModel) this.tableRHS.getModel()); 

		// Load connection settings, pass to SQL Query
		this.conf = loadConf("Shiv.conf");
		this.sql = new SQLQuery(conf);
		
		// Create the tree
		this.treeRHS = view.getJTree();
		
		// Tool tip manager for the JTree
		ToolTipManager.sharedInstance().registerComponent(treeRHS);
		
		// Create Listeners
		jTableLHS = view.getTableLHS();
		jTableLHS.	getSelectionModel().addListSelectionListener(new RowListener());
		manualEntryCheckBox = view.getManualEntryCheckBox();
		manualEntryCheckBox.addItemListener(new MyItemListener());
				
		// Setup the File Chooser
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new CSVFilter());
		
		// Set the status bar
		statusBar = view.getStatusBar();
		statusBar.addRightComponent(inactiveLabel, greyColor);
		statusBar.addRightComponent(activeLabel, orangeColor);
		statusBar.addRightComponent(selectedLabel, greenColor);
		statusBar.setLeftComponent(statusLabel);
		
		// Set the search panel
		searchDialog = new MySearchDialog(view);
		searchDialog.setActionListener(actListener);
	}
	
	public Conf loadConf(String fileName)
	{
		// Create the JAXB controller
		jaxb = new JAXBController(new File(fileName));
		// Load the configuration file
		Conf conf = jaxb.load();	
		conf.setVar("Shiv");
		conf.setDesc("Set Credentials");
		
		// If the conf file has no children, create them
		if(conf.getChildCount() == 0) {
			// Create the following variables
			conf.appendChildren(new String[] {"Server", "Instance", "Database", "Domain", "Username", "Password"});
			// Password should be hidden (show '*' character instead of letters)
			conf.getNode("Password").setMask(true);
		}
		
		return conf;
	}
	
	/**
	 * Save the configuration file <br> 
	 * This will <code>null</code> the 'password' field, and delete the 'url' field. 
	 */
	public void saveConf()
	{
		// Blank the 'password' variable
		conf.setNullValues(new String[] {"Password"});
		// Delete the 'url' variable
		conf.removeChildren(new String[] {"url"});
		// Save the remaining config to file
		//conf.save();
		jaxb.save(conf);
		//TODO: Save the configuration
	}
	
	/**
	 * Create the 'help > about' screen.
	 * @return The completed JPanel
	 */
	private JPanel aboutPanel()
	{
		JPanel root = new JPanel(new BorderLayout());
		
		// Create the image
		BufferedImage aboutImage = null;
		try {
			aboutImage = ImageIO.read(new File("images/AboutShivImage.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JLabel aboutLabel = new JLabel(new ImageIcon(aboutImage));
		
		// Create the text using HTML formatting
		String aboutString = String.format(""
				+ "<html>"
				+ "Version: 1.01, 2014-11-24 <br>"
				+ "For more information visit: <br>"
				+ "https://github.com/Kazagha/Shiv"
				+ "</html>");
		
		// Add components to the root panel
		root.add(aboutLabel, BorderLayout.WEST);
		root.add(new JLabel(aboutString), BorderLayout.CENTER);
		
		return root;
	}
	
	/**
	 * Create the 'help > legend' screen
	 * @return The completed JPanel
	 */
	private JPanel legendPanel()
	{
		// Create Root Panel
		JPanel root = new JPanel(new BorderLayout());
		// Set Icons
		ImageIcon greenNodeIcon = new ImageIcon("images/GreenNodeIcon16.png");
		ImageIcon greyNodeIcon = new ImageIcon("images/GreyNodeIcon16.png");
		ImageIcon orangeNodeIcon = new ImageIcon("images/OrangeNodeIcon16.png");
		ImageIcon greenFolderIcon = new ImageIcon("images/GreenFolderIcon16.png");	
		ImageIcon greyFolderIcon = new ImageIcon("images/GreyFolderIcon16.png");
		ImageIcon orangeFolderIcon = new ImageIcon("images/OrangeFolderIcon16.png");
		
		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
		
		JLabel tempLabel;
		
		tempLabel = new JLabel("Inactive - The user permissions DO NOT grant access to this item");
		listPane.add(tempLabel);
		
		tempLabel = new JLabel("Inactive Folder");
		tempLabel.setIcon(greyFolderIcon);
		listPane.add(tempLabel);
		
		tempLabel = new JLabel("Inactive Node");
		tempLabel.setIcon(greyNodeIcon);
		listPane.add(tempLabel);
		
		listPane.add(Box.createRigidArea(new Dimension(50 ,10)));
		
		tempLabel = new JLabel("Active - The user permissions DO grant access to this item");
		listPane.add(tempLabel);
		
		tempLabel = new JLabel("Active Folder");
		tempLabel.setIcon(orangeFolderIcon);
		listPane.add(tempLabel);
		
		tempLabel = new JLabel("Active Node");
		tempLabel.setIcon(orangeNodeIcon);
		listPane.add(tempLabel);
		
		listPane.add(Box.createRigidArea(new Dimension(50 ,10)));
		
		tempLabel = new JLabel("Selected - The selected permission grants access to this item");
		listPane.add(tempLabel);		
		
		tempLabel = new JLabel("Selected Folder");
		tempLabel.setIcon(greenFolderIcon);
		listPane.add(tempLabel);
		
		tempLabel = new JLabel("Selected Node");
		tempLabel.setIcon(greenNodeIcon);
		listPane.add(tempLabel);
		
		listPane.add(Box.createRigidArea(new Dimension(50 ,10)));
		
		tempLabel = new JLabel("Hidden Nodes - Text highlighting indicates hidden items");
		listPane.add(tempLabel);	
		
		tempLabel = new JLabel("Hidden Active Menu");
		tempLabel.setBackground(orangeColor);
		tempLabel.setOpaque(true);
		listPane.add(tempLabel);
		
		tempLabel = new JLabel("Hidden Selected Menu");
		tempLabel.setBackground(greenColor);
		tempLabel.setOpaque(true);
		listPane.add(tempLabel);
		
		root.add(listPane);
		
		return root;
	}
	
	private class TreeNodeSearch 
	{
		TreePath path;
		String searchString;
		Iterate iterate;
		boolean fastForward = true;
		
		public TreeNodeSearch()	{	}
		
		/**
		 * Begin searching from the specified <code>searchPath<code>
		 * @param searchPath
		 */
		public void setPath(TreePath searchPath)
		{
			path = searchPath;
			fastForward = true;
		}
		
		/**
		 * Search for the specified <code>searchString</code>
		 * @param searchString
		 */
		public void setSearchString(String searchString)
		{
			// Change the search string to lower case and remove leading and trailing spaces 
			this.searchString = searchString.toLowerCase().trim();
		}
		
		/**
		 * Set the search direction to either <code>Iterate.FORWARDS</code> or <code>Iterate.BACKWARDS</code>
		 * @param searchDirection
		 */
		public void setSearchDirection(Iterate searchDirection)
		{
			iterate = searchDirection;
		}
		
		/**
		 * 
		 * @param newSelection
		 */
		public void setTreeSelection(TreePath newSelection)
		{
			// Check if the selection is valid
			if(newSelection != null)
			{
				// Selection is valid, set it in the tree
				
				treeRHS.scrollPathToVisible(newSelection);
				treeRHS.setSelectionPath(newSelection);
			} else { 
				// Selection is null, throw an error message
				
				// Prepare the error message
				String msgString;
				if(iterate == Iterate.FORWARDS)
				{
					msgString = String.format("No match found%nContinue searching from the first node in the Hierarchy?");
				} else {
					msgString = String.format("No match found%nContinue searching from the last node in the Hierarchy?");
				}
				
				// Prompt the user to search from the beginning of the Hierarchy
				int confirmVal = JOptionPane.showConfirmDialog(
						view, 
						msgString,
						"Search Error",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.ERROR_MESSAGE);
				
				if(confirmVal == JOptionPane.OK_OPTION)
				{
					// Begin another search from the root node. 
					// NOTE: This will not 'fast forward' 
					TreePath tempSelection = this.search((DefaultMutableTreeNode) treeRHS.getModel().getRoot());
					this.setTreeSelection(tempSelection);
				} else {
					// Set the selection back to the original path
					treeRHS.setSelectionPath(this.path);
				}
			}
		}
		
		/**
		 * Recursively search this <code>node</code>'s children for a <code>searchString</code> match
		 * @param node - The current node
		 * @return TreePath of the matching <code>node</code>
		 */
		TreePath search(DefaultMutableTreeNode node) 
		{			
			// Check if the current node is a match
			if((fastForward == false) && (node.toString().toLowerCase()).contains(searchString))
			{
				TreePath tp = new TreePath(node.getPath());
				return tp;
			}
			
			// Check if this 'node' is the last component in the 'path', 
			// or alternatively the 'path' is null
			// 	if true stop fast forwarding
			checkMatchesUserSelection(node);
			
			TreePath tempTreePath = null;
			
			// Check if there are any children nodes
			if(node.getChildCount() > 0)
			{		
				// Iterate forwards through the available nodes
				if(iterate == Iterate.FORWARDS)
				{
					// Begin iterating through child nodes
					for(int i = 0; i < node.getChildCount(); i++)
					{
						// Get the child node for this iteration 
						DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);

						// Fast forwarding through the array (skipping nodes) 
						if(fastForward)
						{	
							// From the 'path' find the component at this tier (node.getLevel()), 
							//  then do a string compare against the current node
							if((path.getPathComponent(childNode.getLevel()).toString()).equals(childNode.toString()))							
							{
								// If 'True' search further down the hierarchy
								tempTreePath = search(childNode);
							}   // If 'false' continue to fast forward without going down to the next tier							
						// No longer fast forwarding, search down the hierarchy
						} else if(!fastForward) {
							tempTreePath = search(childNode);
						}
						
						// If there is a valid selection this won't be NULL
						if(tempTreePath != null) 
						{
							return tempTreePath; 
						}
					}
				} else if(iterate == Iterate.BACKWARDS) {
										
					for(int i = node.getChildCount() - 1; i >= 0; i--)
					{
						DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);

						if(fastForward)
						{	
							if((path.getPathComponent(childNode.getLevel()).toString()).equals(childNode.toString()))							
							{
								tempTreePath = search(childNode);
							}   
						} else if(!fastForward) {
							tempTreePath = search(childNode);
						}
						
						if(tempTreePath != null) 
						{
							return tempTreePath; 
						}
					}
				}	
			}
			
			// No children match the search term
			return null;
		}
		
		/**
		 * Check if the specified <code>node</code> is the last component in the <code>path</code>.
		 * @param node - The specified node
		 * @return <code>true</code> if the specified node is the last in the path
		 */
		void checkMatchesUserSelection(DefaultMutableTreeNode node)
		{
			// Check if 'path' is null, or the last component in 'path' is the current node 
			if(path == null || path.getLastPathComponent() == node)
			{
				fastForward = false;
			}
		}
	}
	
	/**
	 *	This class implements <code>Runnable</code>, and can therefore be executed in a new 
	 *	thread. <br> 
	 *	This will fetch the hierarchy - permission data, and refresh the user 
	 *	name array used in the TextFieldListener. 
	 */
	private class fetchSQLData implements Runnable
	{
		public fetchSQLData() {
			// Nothing to setup
		}
		
		@Override
		public void run() {
			try {
				// Remove existing nodes
				//((DefaultMutableTreeNode) this.treeRHS.getModel().getRoot()).removeAllChildren();
				// TODO: This method is not clearing the nodes					
				
				// Reset total node count
				nodeCount = 0;
				activeNodeCount = 0;
				selectedNodeCount = 0;
				tableLHS.clearSelection();			
				
				statusLabel.setText("Fetching hierarchy");
				
				// Add nodes, expand the root node and then hide it.
				createHierarchyNodes((DefaultMutableTreeNode) treeRHS.getModel().getRoot());		
				treeRHS.expandRow(0);
				treeRHS.setRootVisible(false);
				treeRHS.setShowsRootHandles(true);
							
				// Set total inactive node count on the status bar
				inactiveLabel.setText(String.valueOf(nodeCount));
							
				statusLabel.setText("Fetching user names");
				
				// Create User Name array for the auto-complete function
				view.getTextFieldListener().setUserNameArray(userNameQuery());
				
				statusLabel.setText("Finished loading");
				Thread.sleep(2000);				
			} catch (Exception e) {
				JOptionPane.showMessageDialog(view, "Refreshing credentials has failed", "SQL Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} finally {
				statusLabel.setText("");
			}
		}
	}
	
	/**
	 * Convert the specified <code>array</code> into a comma-separated String.
	 * @param array - Array of user roles
	 * @return - Formatted comma-separated String
	 */
	public String roleDataCSV(ArrayList<RoleData> array)
	{
		String tempString = new String();
		
		if(array.isEmpty()) { return null; }

		// Array of RoleData
		if(array.get(0) instanceof RoleData)
		{
			// Transfer RoleData objects into Strings in tempString
			for(Object obj : array)
			{
				RoleData rd = (RoleData) obj;
				tempString += String.format("%s, %s%n", rd.getRole(), rd.getDescription());
			}
		} else {
			return null;
		}
			
		return tempString;	
	}
	
	/**
	 * Write <code>saveString</code> to the location specified in <code>file</code>.
	 * @param file - The specified file location 
	 * @param saveString - The string to save
	 */
	public void saveStringAs(String saveString)
	{
		int returnVal = fc.showSaveDialog(view);
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			// Find the file the user selected
			File fileSelection = fc.getSelectedFile();		
		
			try(FileOutputStream fos = new FileOutputStream(fileSelection)) {
				byte[] outputBytes = saveString.getBytes();
				fos.write(outputBytes);
			} catch (FileNotFoundException e) {
				String err = String.format("File not found: %n%s", fileSelection.getPath());					
				JOptionPane.showMessageDialog(view, err, "Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e)
			{
				String err = String.format("File not found: %n%s", e.getMessage());		
				JOptionPane.showMessageDialog(view, err, "Error", JOptionPane.ERROR_MESSAGE);
			}			
		} else {
			// Save Cancelled by User
		}
	}
	
	public class MyActionListener implements ActionListener
	{
		TreeNodeSearch tns = new TreeNodeSearch();
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String[] tempText = null;
			ArrayList<RoleData> dataLHS = null;
			ArrayList<RoleData> dataRHS = null;
			TreePath selection = null;
			
			switch(e.getActionCommand())
			{
			case "Load LHS":
				tempText = view.getLHSTextToString().split(" ");
				if(tempText.length == 2)
				{
					modelLHS.setArray(userQuery(tempText[0], tempText[1]));
					view.setLHSViewTitle(view.getLHSTextToString());
				}
				//TODO: Create Table Listener
				setActiveRoles(modelLHS.getArray());
				break;
			case "Load RHS":
				tempText = view.getRHSTextToString().split(" ");
				if(tempText.length == 2)
				{								
					modelRHS.setArray(userQuery(tempText[0], tempText[1]));
				view.setRHSViewTitle(view.getRHSTextToString());
				}
				break;
			case "Swap Sides":
				//Get table and title information 
				String titleStringLHS = view.getLHSViewTitle();
				String titleStringRHS = view.getRHSViewTitle();
				dataLHS = modelLHS.getArray();
				dataRHS = modelRHS.getArray();
				
				//Set table and title information
				view.setLHSViewTitle(titleStringRHS);
				view.setRHSViewTitle(titleStringLHS);
				modelLHS.setArray(dataRHS);				
				modelRHS.setArray(dataLHS);
				
				//TODO: Create a table listener
				setActiveRoles(modelLHS.getArray());
				break;
			case "Compare":
				dataLHS = modelLHS.getArray();
				dataRHS = modelRHS.getArray();
				ArrayList<RoleData> tempArray = new ArrayList<RoleData>();
				
				for(RoleData data: dataLHS)
				{
					if(dataRHS.contains(data))
					{
						tempArray.add(data);
					}
				}
				
				modelLHS.removeArray(tempArray);
				modelRHS.removeArray(tempArray);
				
				//TODO: Create Table Listener
				setActiveRoles(modelLHS.getArray());
				break;
			case "Search Dialog":					
				searchDialog.showDialog();		
				break;
			case "Search Next":
				// Find the current selection
				selection = treeRHS.getSelectionPath();
				
				// Set new search parameter
				tns.setPath(selection);
				tns.setSearchString(searchDialog.getTextField());
				tns.setSearchDirection(Iterate.FORWARDS);
				
				// Attempt to search for a new selection
				selection = tns.search((DefaultMutableTreeNode) treeRHS.getModel().getRoot());
				
				// Set the selection in the Hierarchy
				tns.setTreeSelection(selection);
				break;
			case "Search Prev":				
				// Find the current selection
				selection = treeRHS.getSelectionPath();
				
				// Set new search parameter
				tns.setPath(selection);
				tns.setSearchString(searchDialog.getTextField());
				tns.setSearchDirection(Iterate.BACKWARDS);
				
				// Attempt to search for a new selection
				selection = tns.search((DefaultMutableTreeNode) treeRHS.getModel().getRoot());
				
				// Set the selection in the Hierarchy
				tns.setTreeSelection(selection);
				break;
			case "View Hierarchy":
				view.setHierarchyPanel(true);				
				break;
			case "View List":
				view.setHierarchyPanel(false);
				break;
			case "Set Active Roles":
				setActiveRoles(modelLHS.getArray());				
				break;
			case "Remove Selected":
				removeSelectedRows(jTableLHS);
				break;
			case "Insert Roles":
				((MyTableModel) jTableLHS.getModel()).addExtraRows(5);
				break;
			case "Clear Array":
				modelLHS.clearArray();
				modelRHS.clearArray();
				break;
			case "Export Role":
				dataLHS = modelLHS.getArray();
				if(! dataLHS.isEmpty())
				{
					saveStringAs(roleDataCSV(dataLHS));
				} else {
					String err = String.format("Exporting permissions failed %nLeft hand side user roles are empty.");
					JOptionPane.showMessageDialog(view, err, "Export Error", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "Export Hierarchy":
				DefaultMutableTreeNode t = ((DefaultMutableTreeNode) treeRHS.getModel().getRoot());
				if(activeNodeCount > 0)
				{
					saveStringAs(hierarchyToString(t));
				} else {
					String err = String.format("Exporting Hierarchy failed %nOnly active nodes will be exported.");
					JOptionPane.showMessageDialog(view, err, "Export Error", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "Source":				
				// Remove existing 'URL' variable
				conf.removeChildren(new String[] {"url"});
				
				// Prompt the user for the variables
				conf.promptJOptionPane("Set Credentials", 
						new String[] {"Server", "Instance", "Database", "Domain", "Username", "Password"});
				
				// Set the 'URL' variable
				conf.appendChild(new Conf("url", null,  "jdbc:jtds:sqlserver://" + conf.get("Server")+ ";instance="+ conf.get("Instance") + ";DatabaseName=" + conf.get("Database") + ";Domain=" + conf.get("Domain")));
				
				// Using the credentials, fetch data from the specified SQL Server
				Thread fetchThread = new Thread(new fetchSQLData());
				fetchThread.start();
				
				break;
			case "About":
				JOptionPane.showMessageDialog(view, aboutPanel(), "About Shiv", JOptionPane.PLAIN_MESSAGE);				
				break;
			case "Legend":
				JOptionPane.showMessageDialog(view, legendPanel(), "Icon Legend", JOptionPane.PLAIN_MESSAGE);	
				break;
			case "Exit":
				// Save the current Conf
				saveConf();
				// Exit the program
				System.exit(0);
				break;				
			default:
				break;					
			}
		}		
	}
	
	private void createHierarchyNodes(DefaultMutableTreeNode rootNode) throws Exception
	{
		ArrayList<HierarchyData> hierarchyList = hierarchyQuery();
		// Sort the Hierarchy Data
		Collections.sort(hierarchyList, HierarchyData.Comparators.TIER_SEQ);
		this.view.setRHSHierarchyTitle("Hierarchy View");
		
		// Iterate though the Hierarchy List Array
		for(HierarchyData hd : hierarchyList)
		{
			// Set the first parent Node. Start searching from the root node.
			DefaultMutableTreeNode parentNode = rootNode;
			
			// Iterate though the tiers of the Hierarchy 
			for(int nodeNumber : hd.getNodeList())
			{
				// Find the number of children on the 'parentNode' 
				int childCount = parentNode.getChildCount();
				// Iterate through the children until a match is found
				for(int childIndex = 0; childIndex < childCount; childIndex++)
				{
					// Fetch the node at this child index.
					DefaultMutableTreeNode tempChild = (DefaultMutableTreeNode) parentNode.getChildAt(childIndex);
					// Fetch the Hierarchy Data from this node
					HierarchyData tempHierarchyNode = (HierarchyData) tempChild.getUserObject();
					// Fetch the node number from the Hierarchy Node
					int tempChildNodeNumber = tempHierarchyNode.getNodeNumber();					
					
					// Check if the node number (from hierarchy node list)
					// for this tier matches this child node
					if(nodeNumber == tempChildNodeNumber)
					{
						// Set the new parent node to this child node
						parentNode = (DefaultMutableTreeNode) parentNode.getChildAt(childIndex);
						break;
					}
				}
			}
			// Add the Hierarchy Data as a child to the specified parent node
			parentNode.add(new HierarchyTreeNode(hd));
			nodeCount++;
		}
	}
	
	private ArrayList<RoleData> userQuery(String firstName, String lastName)
	{
		ArrayList<RoleData> array = new ArrayList<RoleData>(); 
		
		try {
			array = sql.queryUserRoles(firstName, lastName);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(view, "Fetching user permissions has failed", "SQL Query Error", JOptionPane.ERROR_MESSAGE);			
		} 
		
		return array;
	}
	
	private ArrayList<HierarchyData> hierarchyQuery() throws Exception
	{
		ArrayList<HierarchyData> array = null;
		
		array = sql.queryHierarchy();
		
		return array;
	}
	
	private ArrayList<String> userNameQuery() throws Exception
	{
		ArrayList<String> array = null;
		
		array = sql.queryUserNames();		
		
		Collections.sort(array);
		
		return array;
	}
	
	private void setActiveRoles(ArrayList<RoleData> rdArray)
	{		
		activeNodeCount = 0;
		
		updateChildNodes((DefaultMutableTreeNode) treeRHS.getModel().getRoot(), 
        		UpdateMode.ACTIVE, rdArray);
		
		int inactiveCount = nodeCount - activeNodeCount;
		inactiveLabel.setText(String.valueOf(inactiveCount));
		activeLabel.setText(String.valueOf(activeNodeCount));		
		
		treeRHS.repaint();
	}
	
	private void setSelectedRoles(ArrayList<RoleData> rdArray)
	{		
		selectedNodeCount = 0;
		
		updateChildNodes((DefaultMutableTreeNode) treeRHS.getModel().getRoot(), 
        		UpdateMode.SELECTED, rdArray);
		
		selectedLabel.setText(String.valueOf(selectedNodeCount));		
		activeLabel.setText(String.valueOf(activeNodeCount - selectedNodeCount));		

		treeRHS.repaint();
	}	
	
	/**
	 * Check if the <code>node</code> contains the security roles specified in the
	 * <code>roleArrayList<code> then update the <code>node</code> according
	 * to the <code>mode</code>.
	 * <br><br>
	 * The mode can be set to either <code>updateMode.ACTIVE<code> or 
	 * <code>updateMode.SELECTED<code>
	 * <br> <br>
	 * For example updating with an 'active' mode will result in either an 
	 * 'ACTIVE' or 'INACTIVE' node. 
	 * @param node - The current node
	 * @param mode - The specified update mode
	 * @param roleArrayList - An array of <code>RoleData</code> permissions. 
	 */
	private void updateChildNodes(DefaultMutableTreeNode node, UpdateMode mode,
			ArrayList<RoleData> roleArrayList)
	{		
		if(node instanceof HierarchyTreeNode)
		{	
			if(mode == UpdateMode.ACTIVE)
			{
				if(nodeContainsRole(node, roleArrayList))
				{				
					((HierarchyTreeNode) node).setMode(HierarchyTreeNode.ActiveMode.ACTIVE);
					updateParentNodes(node, mode);

					activeNodeCount++;
				} else {
					((HierarchyTreeNode) node).setMode(HierarchyTreeNode.ActiveMode.INACTIVE);
				}

			} else if(mode == UpdateMode.SELECTED)
			{
				if(nodeContainsRole(node, roleArrayList))
				{
					((HierarchyTreeNode) node).setMode(HierarchyTreeNode.SelectedMode.SELECTED);
					updateParentNodes(node, mode);

					selectedNodeCount++;
				} else {
					((HierarchyTreeNode) node).setMode(HierarchyTreeNode.SelectedMode.NOT_SELECTED);
				}
			}
		}
		
		if(node.getChildCount() >= 0)
		{
			for(Enumeration e = node.children(); e.hasMoreElements();)
			{		
				DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) e.nextElement();
				updateChildNodes(nextNode, mode, roleArrayList);				
			}
		}
	}
	
	/**
	 * Set the 'parent node' of <code>node</code> to 'partial selection mode' 
	 * according to the specified <code>mode</code>.     
	 * 
	 * <br><br>
	 * The mode can be set to either <code>updateMode.ACTIVE<code> or 
	 * <code>updateMode.SELECTED<code>
	 * <br><br>
	 * For example when updating with an 'active' <code>mode</code>, check if the parent is 'active'.
	 * If <code>false</code> set the parent to 'partial_active'
	 * 
	 * @param node - The current node
	 * @param mode - The specified mode
	 */
	private void updateParentNodes(DefaultMutableTreeNode node, UpdateMode mode)
	{		
		Object objectNode = node.getParent();
		
		if(objectNode instanceof HierarchyTreeNode)			
		{
			HierarchyTreeNode parentNode = (HierarchyTreeNode) objectNode;
			
			if(mode == UpdateMode.ACTIVE)
			{
				if(parentNode.getActiveMode() != HierarchyTreeNode.ActiveMode.ACTIVE)
				{
					parentNode.setMode(HierarchyTreeNode.ActiveMode.PARTIAL_ACTIVE);
					updateParentNodes(parentNode, mode);
				}
			} else if(mode == UpdateMode.SELECTED) {
				if(parentNode.getSelectedMode() != HierarchyTreeNode.SelectedMode.SELECTED)
				{
					parentNode.setMode(HierarchyTreeNode.SelectedMode.PARTIAL_SELECTED);
					updateParentNodes(parentNode, mode);
				}
			}
		} 
	}
	
	/**
	 * Starting with the root of the Hierarchy, walk through the nodes adding the 'node path' of active nodes to the String. 
	 * @param node - The root <code>node</code>. 
	 * @return - Returns a string representing all active nodes. 
	 */
	private String hierarchyToString(DefaultMutableTreeNode node)
	{
		String tempString = new String();
		TreeNode[] nodePath = node.getPath();
		
		// Check if the node is an instance of HierarchyTreeNode
		if(node instanceof HierarchyTreeNode)
		{			
			// Check that the node is Active
			if(((HierarchyTreeNode) node).getActiveMode() == HierarchyTreeNode.ActiveMode.ACTIVE)
			{
				// Iterate through the node path excluding the root node in position 0
				for(int i = 1; i < nodePath.length; i++)
				{	
					// Is this the first element in the node path 
					if (i == 1)
					{
						tempString = String.format("%s", nodePath[i].toString());
					} else {
						tempString += String.format(" > %s", nodePath[i].toString());
					}
				}
			}
		}
		
		// Check if this node has children
		if(node.getChildCount() >= 0)
		{
			// Iterate through children
			for(Enumeration e = node.children(); e.hasMoreElements();)
			{		
				DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) e.nextElement();
				String child = hierarchyToString(nextNode);
				
				// If child contains text, but tempString is empty
				if((! child.isEmpty()) && (tempString.isEmpty()))
				{
					tempString = child;
				// If child contains text, insert new line
				} else if (! child.isEmpty()) {
					tempString = String.format("%s%n%s", tempString, child);
				}
			}
		}
		
		return tempString;
	}
	
	/**
	 * Check if the <code>obj</code> node, contains the roles specified in <code>roleArrayList</code> 
	 * @param obj - The specified node object. 
	 * @param roleArrayList - A list of <code>RoleData</code> permissions. 
	 * @return
	 */
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
	
	public void removeSelectedRows(JTable selectedJTable)
	{
		ArrayList<RoleData> tempArray = new ArrayList<RoleData>();
		int[] rows = selectedJTable.getSelectedRows();
		
		// Convert view index to model index, then find the role
		for(int i : rows)
		{
			int rowModel = selectedJTable.convertRowIndexToModel(i);
			RoleData tempRole = ((MyTableModel) selectedJTable.getModel()).getRoleAt(rowModel);
			tempArray.add(tempRole);
		}

		// Clear the 'selection' of the rows
		selectedJTable.clearSelection();
		// Remove the selected rows from the table
		((MyTableModel) selectedJTable.getModel()).removeArray(tempArray);
	}
	
    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {  	
        	// If the selection is still adjusting return
        	if (event.getValueIsAdjusting()) {
                return;
            }
        	
            // Create a temporary Role Data array
            ArrayList<RoleData> tempArray = new ArrayList<RoleData>();
           
            // Iterate through all selected rows
            for(int i : jTableLHS.getSelectedRows())
            {
            	// Convert the user selection in the view, to the underlying model
            	int modelIndex = jTableLHS.convertRowIndexToModel(i);
            	RoleData tempRD = modelLHS.getArray().get(modelIndex);
            	// Add specified roles to the array
            	tempArray.add(tempRD);
            }
            
            setSelectedRoles(tempArray);
        }
    }
    
    private class MyItemListener implements ItemListener
    {
		@Override
		public void itemStateChanged(ItemEvent e) {
			// Find the source that triggered the event
	    	Object source = e.getItemSelectable(); 
	    			
	    	// Check if the source was the 'Manual Entry' checkbox
			if (source == manualEntryCheckBox)
			{
				if (e.getStateChange() == ItemEvent.DESELECTED)
				{				
					view.setManualEntry(false);
					modelLHS.setEditMode(false);
				} else {
					view.setManualEntry(true);
					modelLHS.setEditMode(true);
				}
			}
		}  	
    }
}
