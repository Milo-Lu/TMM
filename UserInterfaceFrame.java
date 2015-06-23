package se.liu.ifm.applphys.biorgel.TMM;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JProgressBar;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * 
 * @author minlu
 *
 */
//@SuppressWarnings("serial")
public class UserInterfaceFrame extends JFrame{

	private static final long serialVersionUID = 20141104L;
	private static final int FRAME_WIDTH = 1000;
	private static final int FRAME_HEIGHT = 450;

//	northPanel
	private JTextField angleField;
	private JTextField startwavelengthField;
	private JTextField wavelengthintervalField;
	private JTextField endwavelengthField;
	private JTextField numberofgridpointsField;
	
//	centerPanel
	private JPanel structurePanel;
	private ArrayList<JPanel> layerPanelList;
	private ArrayList<JTextField> layerNameList;
	private ArrayList<JTextField> startThicknessList;
	private ArrayList<JTextField> stepThicknessList;
	private ArrayList<JTextField> endThicknessList;
	private ArrayList<JTextField> nkFileFieldList;
	private ArrayList<JButton> readFileButtonList;
	private ArrayList<JCheckBox> isActiveLayerCheckBoxList;

//	southPanel
	private JTextField writefilepathField;
	private JTextField numberofwritefilesField;
	private String defaultPath;
	private JCheckBox currentSimulationCheckBox;
	private boolean calculateJsc;
	private JProgressBar progressBar;

// input parameters
	private double angle;
	private double startWavelength;
	private double wavelengthStep;
	private double endWavelength;
	private int NofGridPoints;
	private String[] layerName;
	private double[] startThickness;
	private double[] stepThickness;
	private double[] endThickness;
	private String[] nkFilePathName;
	private boolean[] isActiveLayer;
	private String wrtieFilePath;
	private int NofOutputFiles;
	
	public UserInterfaceFrame(){
		super("TMM 2.72 Profile 	developed by Mingtao (Milo) Lu");
		defaultPath = "C:/Users/minlu/Downloads/Light Trapping/TMM/";
				
		layerPanelList = new ArrayList<JPanel>();
		layerNameList = new ArrayList<JTextField>();
		startThicknessList = new ArrayList<JTextField>();
		stepThicknessList = new ArrayList<JTextField>();
		endThicknessList = new ArrayList<JTextField>();
		nkFileFieldList = new ArrayList<JTextField>();
		readFileButtonList = new ArrayList<JButton>();
		isActiveLayerCheckBoxList = new ArrayList<JCheckBox>();
		
		createNorthPanel();
		createCenterPanel();
		createSouthPanel();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);	
	}
	
	/**
	 * north panel
	 */
	private void createNorthPanel(){
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(2, 5));
		
		JLabel angleLabel = new JLabel("Angle", JLabel.CENTER);
		JLabel startWLLable = new JLabel("Start Wavelength", JLabel.CENTER);
		JLabel wavelengthintervalLabel = new JLabel("Wavelength Interval", JLabel.CENTER);
		JLabel endWLLabel = new JLabel("End Wavelength", JLabel.CENTER);
		JLabel numberofgridpointsLabel = new JLabel("Number of Grid Points", JLabel.CENTER);
		
		final int FIELD_WIDTH = 10;
		final String DEFAULT_ANGLE = "0.0";
		final String DEFAULT_STARTWL = "400";
		final String DEFAULT_WAVELENGTHINTERVAL = "10.0";
		final String DEFAULT_ENDWL = "1000";
		final String DEFAULT_NUMBEROFGRIDPOINTS = "1000";
		
		angleField = new JTextField(FIELD_WIDTH);
		angleField.setText(DEFAULT_ANGLE);
		
		startwavelengthField = new JTextField(FIELD_WIDTH);
		startwavelengthField.setText(DEFAULT_STARTWL);
		
		wavelengthintervalField = new JTextField(FIELD_WIDTH);
		wavelengthintervalField.setText(DEFAULT_WAVELENGTHINTERVAL);
		
		endwavelengthField = new JTextField(FIELD_WIDTH);
		endwavelengthField.setText(DEFAULT_ENDWL);
		
		numberofgridpointsField = new JTextField(FIELD_WIDTH);
		numberofgridpointsField.setText(DEFAULT_NUMBEROFGRIDPOINTS);
		
		northPanel.add(angleLabel);
		northPanel.add(startWLLable);
		northPanel.add(wavelengthintervalLabel);
		northPanel.add(endWLLabel);
		northPanel.add(numberofgridpointsLabel);
		northPanel.add(angleField);
		northPanel.add(startwavelengthField);
		northPanel.add(wavelengthintervalField);
		northPanel.add(endwavelengthField);
		northPanel.add(numberofgridpointsField);
		
		add(northPanel, BorderLayout.NORTH);
	}
	
	/**
	 * center panel
	 */
	private void createCenterPanel(){
		JPanel titlePanel = createTitlePanel();
		JScrollPane scrollPane = new JScrollPane(updateLayerGroupPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel functionPanel = createFunctionPanel();
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(titlePanel, BorderLayout.NORTH);
		centerPanel.add(scrollPane, BorderLayout.CENTER);
		centerPanel.add(functionPanel, BorderLayout.SOUTH);
		centerPanel.setBorder(new TitledBorder(new EtchedBorder(), "Optical Layers"));
		
		add(centerPanel, BorderLayout.CENTER);
	}
	
	private JPanel createTitlePanel(){
		JLabel layerLabel = new JLabel("Layer Name", JLabel.CENTER);
		JLabel startLabel = new JLabel("Start", JLabel.CENTER);
		JLabel stepLabel = new JLabel("Step", JLabel.CENTER);
		JLabel endLabel = new JLabel("End", JLabel.CENTER);
		JLabel nkfileLabel = new JLabel("nk File", JLabel.CENTER);
		JLabel choosereadfileLabel = new JLabel(" ");
		JLabel chooseactivelayerLabel = new JLabel("Active Layer", JLabel.CENTER);
		
		JPanel tp = new JPanel();
		tp.setLayout(new GridLayout(1, 7));
		tp.add(layerLabel);
		tp.add(startLabel);
		tp.add(stepLabel);
		tp.add(endLabel);
		tp.add(nkfileLabel);
		tp.add(choosereadfileLabel);
		tp.add(chooseactivelayerLabel);
		
		return tp;
	}
	
	private JPanel createFunctionPanel(){
		JButton addlayerButton = createAddLayerButton();
		JButton removelayerButton = createRemoveLayerButton();
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		JPanel fp = new JPanel();
		fp.setLayout(new BorderLayout());
		
		fp.add(addlayerButton, BorderLayout.EAST);
		fp.add(progressBar, BorderLayout.CENTER);
		fp.add(removelayerButton, BorderLayout.WEST);
		
		return fp;
	}
	
	private JButton createAddLayerButton(){
		JButton addButton = new JButton("Add Layer");
		
		addButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						createLayerPanel();
						updateLayerGroupPanel();
					}
				}				
		);		
		return addButton;
	} 

	private JButton createRemoveLayerButton(){
		JButton removeButton = new JButton("Remove Layer");
		
		removeButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						removeLayerPanel();
						updateLayerGroupPanel();
					}
				}				
		);		
		return removeButton;
	}
	
	private void removeLayerPanel(){
		if(layerPanelList.size()==1){
			JOptionPane.showMessageDialog(null,"This is the last layer!","Warning",JOptionPane.WARNING_MESSAGE);
		}else{
			layerPanelList.remove(layerPanelList.size()-1);
			layerNameList.remove(layerNameList.size()-1);
			startThicknessList.remove(startThicknessList.size()-1);
			stepThicknessList.remove(stepThicknessList.size()-1);
			endThicknessList.remove(endThicknessList.size()-1);
			nkFileFieldList.remove(nkFileFieldList.size()-1);
			readFileButtonList.remove(readFileButtonList.size()-1);
			isActiveLayerCheckBoxList.remove(isActiveLayerCheckBoxList.size()-1);		
		}
	}
	
	private JPanel updateLayerGroupPanel(){
		if(layerPanelList.size()==0){
			createLayerGroupPanel();
		}else{
			structurePanel.removeAll();
			structurePanel.setLayout(new GridLayout(layerPanelList.size(), 1));
			
			for(int i=0; i<layerPanelList.size(); i++){
				structurePanel.add(layerPanelList.get(i));
			}			
		}
		structurePanel.revalidate();

		return structurePanel;
	}
	
	private void createLayerGroupPanel(){
		structurePanel = new JPanel();
		structurePanel.setLayout(new GridLayout(6, 1));
		
		for(int i=0; i<6; i++){		
			createLayerPanel();
			structurePanel.add(layerPanelList.get(i));
		}
	}
	
	private void createLayerPanel(){
		String default_LayerName = "Layer" + layerPanelList.size();
		JTextField layernameField = new JTextField(default_LayerName);
		layerNameList.add(layernameField);
		JTextField startthicknessField = new JTextField();
		startThicknessList.add(startthicknessField);
		JTextField stepthicknessField = new JTextField();
		stepThicknessList.add(stepthicknessField);
		JTextField endthicknessField = new JTextField();
		endThicknessList.add(endthicknessField);
		JTextField nkfileField = new JTextField();
		nkFileFieldList.add(nkfileField);
		
		JButton readfileButton = createReadFileButton();
		
		JCheckBox isactivelayerCheckBox = new JCheckBox();
		isActiveLayerCheckBoxList.add(isactivelayerCheckBox);
		
		JPanel lp = new JPanel();
		lp.setLayout(new GridLayout(1, 7));
		lp.setPreferredSize(new Dimension(FRAME_WIDTH, 40));
		
		lp.add(layernameField);
		lp.add(startthicknessField);
		lp.add(stepthicknessField);
		lp.add(endthicknessField);
		lp.add(nkfileField);
		lp.add(readfileButton);
		lp.add(isactivelayerCheckBox);
		
		layerPanelList.add(lp);
//		return lp;
	}

	private JButton createReadFileButton(){
		JButton rfb = new JButton("Choose File");
		readFileButtonList.add(rfb);

		rfb.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						try{
							int i = readFileButtonList.indexOf(event.getSource());
							nkFileFieldList.get(i).setText(chooseFile(false));
						}catch(FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}		
		);
		
		return rfb;
	}
	
	/**
	Choose the file manually
	*/
	private String chooseFile(boolean isWriteFile) throws FileNotFoundException{
		JFileChooser chooser = new JFileChooser(defaultPath);
		int status;
		
		if(isWriteFile){
			status = chooser.showSaveDialog(null);
		}else{
			status = chooser.showOpenDialog(null);
		}
		
		if(status == JFileChooser.APPROVE_OPTION){
			File selectedFile = chooser.getSelectedFile();
			String p = selectedFile.getPath();
			chooser.setCurrentDirectory(selectedFile);
			
			if(!isWriteFile) defaultPath = p;

			return p;
		}else{
			return null;
		}	
	}
	
	/**
	 * south panel
	 */
	private void createSouthPanel(){
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(2, 4));
		
		JLabel writefileLabel = new JLabel("Output File", JLabel.RIGHT);
		writefilepathField = new JTextField();
		JButton choosewritefileButton = createChooseWriteFileButton();
		
		JLabel numberofwritefilesLabel = new JLabel("Number of Written Files", JLabel.RIGHT);
		final String DEFAULT_NROFWRITEFILE = "1";
		numberofwritefilesField = new JTextField();
		numberofwritefilesField.setText(DEFAULT_NROFWRITEFILE);
		
		JButton runButton = createRunButton();
		
		JLabel currentSimulationLabel = new JLabel("Calculate Jsc");
		currentSimulationCheckBox = new JCheckBox();
		
		southPanel.add(writefileLabel);
		southPanel.add(writefilepathField);
		southPanel.add(choosewritefileButton);
		southPanel.add(currentSimulationLabel);
		southPanel.add(numberofwritefilesLabel);
		southPanel.add(numberofwritefilesField);
		southPanel.add(runButton);
		southPanel.add(currentSimulationCheckBox);
		
		add(southPanel, BorderLayout.SOUTH);
	}
	
	private JButton createChooseWriteFileButton(){
		JButton wfb = new JButton("Save as");
		
		wfb.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						try{
							writefilepathField.setText(chooseFile(true));
						}catch(FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}		
		);		
		return wfb;
	}
	
	private JButton createRunButton(){
		JButton runButton = new JButton("Run");
		
		runButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						
						try{
							angle = Double.parseDouble(angleField.getText());
						}catch(NumberFormatException e){
							JOptionPane.showMessageDialog(null,"Wrong angle value!","Warning",JOptionPane.WARNING_MESSAGE);
						}
						
						try{
							startWavelength = Double.parseDouble(startwavelengthField.getText());
						}catch(NumberFormatException e){
							JOptionPane.showMessageDialog(null,"Wrong start wavelength value!","Warning",JOptionPane.WARNING_MESSAGE);
						}
						
						try{
							wavelengthStep = Double.parseDouble(wavelengthintervalField.getText());
						}catch(NumberFormatException e){
							JOptionPane.showMessageDialog(null,"Wrong wavelength interval value!","Warning",JOptionPane.WARNING_MESSAGE);
						}
						
						try{
							endWavelength = Double.parseDouble(endwavelengthField.getText());
						}catch(NumberFormatException e){
							JOptionPane.showMessageDialog(null,"Wrong end wavelength value!","Warning",JOptionPane.WARNING_MESSAGE);
						}
						
						if(startWavelength>endWavelength){
							JOptionPane.showMessageDialog(null,"Start wavelength should be smaller than End wavelength!","Warning",JOptionPane.WARNING_MESSAGE);
						}
						
						try{
							NofGridPoints = Integer.parseInt(numberofgridpointsField.getText());
						}catch(NumberFormatException e){
							JOptionPane.showMessageDialog(null,"Wrong Number of Points value!","Warning",JOptionPane.WARNING_MESSAGE);
						}
						
						layerName = new String[layerPanelList.size()];
						startThickness = new double[layerPanelList.size()];
						stepThickness = new double[layerPanelList.size()];
						endThickness = new double[layerPanelList.size()];
						nkFilePathName = new String[layerPanelList.size()];
						isActiveLayer = new boolean[layerPanelList.size()];
						
						for(int i=0; i<layerPanelList.size(); i++){
							layerName[i] = layerNameList.get(i).getText();
							
							try{
								startThickness[i] = Double.parseDouble(startThicknessList.get(i).getText());
							}catch(NumberFormatException e){
								JOptionPane.showMessageDialog(null,"Wrong start thickness value!","Warning",JOptionPane.WARNING_MESSAGE);
							}
							
							try{
								stepThickness[i] = Double.parseDouble(stepThicknessList.get(i).getText());
							}catch(NumberFormatException e){
								JOptionPane.showMessageDialog(null,"Wrong step thickness value!","Warning",JOptionPane.WARNING_MESSAGE);
							}
							
							try{
								endThickness[i] = Double.parseDouble(endThicknessList.get(i).getText());
							}catch(NumberFormatException e){
								JOptionPane.showMessageDialog(null,"Wrong end thickness value!","Warning",JOptionPane.WARNING_MESSAGE);
							}
							
							nkFilePathName[i] = nkFileFieldList.get(i).getText();					
							isActiveLayer[i] = isActiveLayerCheckBoxList.get(i).isSelected();
						}
						wrtieFilePath = writefilepathField.getText();
						calculateJsc = currentSimulationCheckBox.isSelected();
						
						try{
							NofOutputFiles = Integer.parseInt(numberofwritefilesField.getText());
						}catch(NumberFormatException e){
							JOptionPane.showMessageDialog(null,"Wrong Number of write files!","Warning",JOptionPane.WARNING_MESSAGE);
						}
						start();
					}
				}		
		);		
		return runButton;
	}
	
	private void start(){
		try {
			Profile dataProfile = new Profile(angle, startWavelength, wavelengthStep, endWavelength, NofGridPoints, layerName, startThickness, stepThickness, endThickness, nkFilePathName, isActiveLayer, wrtieFilePath, NofOutputFiles, calculateJsc);
			dataProfile.addPropertyChangeListener(new PropertyChangeListener(){

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if("progress"==evt.getPropertyName()){
						progressBar.setIndeterminate(false);
						progressBar.setValue((Integer) evt.getNewValue());
					}
				}
			});
			dataProfile.execute();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
