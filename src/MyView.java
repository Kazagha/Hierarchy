import java.awt.Dimension;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.plaf.TableHeaderUI;

public class MyView extends JPanel {

	JTable tableLHS = new JTable(10, 2);
	JTable tableRHS = new JTable(10, 2);
	
	JSpinner spinnerLHS = new JSpinner();
	JSpinner spinnerRHS = new JSpinner();
	
	JLabel titleLabelLHS = new JLabel("User 1");
	JLabel titleLabelRHS = new JLabel("User 2");
	
	JButton swapButton = new JButton("Swap");
	
	public MyView()
	{
		JScrollPane scrollLHS = new JScrollPane(tableLHS);		
		JScrollPane scrollRHS = new JScrollPane(tableRHS);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				scrollLHS, scrollRHS);

		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(spinnerLHS)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(swapButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(spinnerRHS)
							.addContainerGap())
					.addGroup(Alignment.CENTER, layout.createSequentialGroup()
							.addContainerGap()
							//.addComponent(titleLabelLHS)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							//.addComponent(titleLabelRHS)
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
						.addComponent(spinnerLHS)
						.addComponent(spinnerRHS)
						.addComponent(swapButton))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						//.addComponent(titleLabelLHS)
						//.addComponent(titleLabelRHS)
						)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						//.addComponent(tableLHS)
						.addComponent(splitPane))
				.addContainerGap()
				);		
	}
	
	public void createAndShowGUI()
	{
		//Create frame setup Window
		JFrame frame = new JFrame("My View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(500, 300));
		
		//Add the content  
		frame.getContentPane().add(this);
		
		//Display the window
		frame.pack();
		frame.setVisible(true);		
	}
}
