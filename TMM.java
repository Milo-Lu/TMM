package se.liu.ifm.applphys.biorgel.TMM;

//import java.awt.Font;
import java.io.FileNotFoundException;
import javax.swing.JFrame;

/**
 * 
 * @author Milo Lu
 * milolmt@gmail.com
 * 
 */

public class TMM {
	
	/**
	 * transfer matrix method
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException{
		JFrame frame = new UserInterfaceFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
//		frame.setFont(new Font("Arial", Font.PLAIN, 24));
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
	}
}
