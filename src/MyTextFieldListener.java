import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

public class MyTextFieldListener implements DocumentListener {
	
	private ArrayList<String> userNameArray = new ArrayList<String>();
    public static final String COMMIT_ACTION = "commit";
	public static enum Mode { INSERT, COMPLETION };
	private Mode mode = Mode.INSERT;
	private JTextField textField = null;
	
	//public MyTextFieldListener(ArrayList<String> userNameList) {
	public MyTextFieldListener(JTextField textField) {
		InputMap im = textField.getInputMap();
		ActionMap am = textField.getActionMap();
		im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
		am.put(COMMIT_ACTION, new CommitAction());
		
		//Create some dummy names in the array
		userNameArray.add("anthony");
		userNameArray.add("chris");
		userNameArray.add("fred");	
	}

	@Override
	public void changedUpdate(DocumentEvent ev) {	
	}	

	@Override
	public void removeUpdate(DocumentEvent ev) {
	}

	@Override
	public void insertUpdate(DocumentEvent ev) {
		
		//What does this do?
		if (ev.getLength() != 1) {
			return;
		}
				
		
		Object owner = ev.getDocument().getProperty("owner");
		if(owner instanceof JTextField)
		{
			textField = (JTextField) owner;
		} else {
			return;
		}	
		
		
		//Find the position of the cursor
		int pos = ev.getOffset(); 
		String content = null;
		try {
			//Grab text from the beginning of the document to the cursor position
			content = textField.getText(0, pos + 1);
		} catch (BadLocationException e) { 
			e.printStackTrace();
		}
		
		// Set 'w' as the starting character of the word
		int w;
		for (w = pos; w >= 0; w--)
		{
			// Break on a non-letter character
			if (! Character.isLetter(content.charAt(w)))
			{
			break;
			}
		}
		
		// Not enough characters
		if ((pos - w) < 2)
		{
			return;
		}
		
		String prefix = content.substring(w + 1).toLowerCase();
		int n = Collections.binarySearch(userNameArray, prefix);
		if(n < 0 && -n <= userNameArray.size())
		{
			//System.out.println("Match?");
			String match = userNameArray.get(-n - 1);
			if (match.startsWith(prefix))
			{
				// Completion is found
				String completion = match.substring(pos - w);
				// Cannot modify the Document from within the notification, 
				// submit a task to do the change later
				SwingUtilities.invokeLater(
						new CompletionTask(completion, pos + 1));
			}
		} else {
			//Nothing Found
			mode = Mode.INSERT;
		}
		
		//System.out.println("n: " + n + " Array Size: " + userNameArray.size());
		//System.out.println(prefix);
	}	
	
	private class CompletionTask implements Runnable {
		String completion;
		int position;
		
		CompletionTask(String completion, int position)
		{
			this.completion = completion;
			this.position = position;
		}
		
		public void run() {
			textField.setText(insert(textField.getText(), completion, position));
			textField.setCaretPosition(position + completion.length());
			textField.moveCaretPosition(position);
			mode = Mode.COMPLETION;
		}
	}
	
	public class CommitAction extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			if(mode == Mode.COMPLETION) {
				int pos = textField.getSelectionEnd();
				textField.setText(insert(textField.getText()," ", pos));
				textField.setCaretPosition(pos + 1);
				mode = Mode.INSERT;
			} else {
				//Not required to go to a new line, a space will suffice
				textField.replaceSelection(" ");
			}
		}
	}
	
	private String insert(String text, String completion, int position)
	{
		StringBuilder sb = new StringBuilder(text);
		sb.insert(position, completion);
		return sb.toString();
	}
}
