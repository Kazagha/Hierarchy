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
	InputMap im;
	ActionMap am;
	
	public MyTextFieldListener() {}

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
				
		//Locate the JTextField that fired the insertUpdate event
		//using the 'owner' property field. 
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
		
		// This is not required as the prefix will always use the entire string
		int w = -1;
		// Set 'w' as the starting character of the word
		/*
		int w;
		for (w = pos; w >= 0; w--)
		{
			// Break on a non-letter character
			if (! Character.isLetter(content.charAt(w)))
			{
			break;
			}
		}
		*/
		
		// Not enough characters
		if ((pos - w) < 2)
		{
			return;
		}
		
		// Find the beginning of the word 
		String prefix = content.substring(w + 1).toLowerCase();
		// Search the array for the 'insertion point' (negative number)
		int n = Collections.binarySearch(userNameArray, prefix);
		// Check the number is an 'insertion point', and not outside the existing array
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
	
	/**
	 * CommitAction
	 * This class checks if the 'Enter' action is a mode.COMPLETION
	 * action and responds differently in each case.
	 */
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
	
	/**
	 * Set Key Mapping <br>
	 * This method sets the required InputMapping and ActionMapping <br>
	 * on the specified JTextField
	 * @param tempTextField
	 */
	public void setKeyMapping(JTextField textField)
	{
		InputMap im = textField.getInputMap();
		ActionMap am = textField.getActionMap();
		im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION);
		am.put(COMMIT_ACTION, new CommitAction());
	}
	
	/**
	 * Set User Name Array <br>
	 * Use the specified array as the 'autocomplete' list 
	 * @param array - Specified array
	 */
	public void setUserNameArray(ArrayList<String> array)
	{
		userNameArray = array;
	}
	
	/**
	 * Insert <br>
	 * Insert the specified text at the specified position, into the existing String
	 * @param existing - The existing text
	 * @param completion - The text to insert
	 * @param position - The position at which to insert >= 0
	 * @return
	 */
	private String insert(String existing, String completion, int position)
	{
		StringBuilder sb = new StringBuilder(existing);
		sb.insert(position, completion);
		return sb.toString();
	}
}
