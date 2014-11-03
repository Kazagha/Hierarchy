
public class Shiv {
	
	public static void main(String args[])
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Create the View
				MyView v = new MyView();
				// Create the controller, with a reference to the View
				new MyController(v);
				// Show the GUI on screen 
				v.createAndShowGUI();
			}			
		});
	}
}
