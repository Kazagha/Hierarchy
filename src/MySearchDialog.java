import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class MySearchDialog extends JPanel {

	//public MySearchDialog(String title, Object[] content, JButton[] options)
	public MySearchDialog(String titleString)
	{
		//Frame Width and Height
		final int FRAME_WIDTH = 150;
		final int FRAME_HEIGHT = 150;
		
		JFrame searchFrame = new JFrame(titleString);		
		searchFrame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		
		JPanel contentPanel = new JPanel();
		contentPanel.add(new JLabel("THis is a test"));
		
		searchFrame.setContentPane(contentPanel);
		searchFrame.pack();			
		searchFrame.setVisible(true);	
	}
	
}
