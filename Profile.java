package se.liu.ifm.applphys.biorgel.TMM;

import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * the progress of the main thread is monitored using SwingWorker
 * @author minlu
 *
 */
public class Profile extends SwingWorker<Void, Void>{

	private double angle;
	private String[] layerName;
	private double[] startThickness;
	private double[] stepThickness;
	private double[] endThickness;
	private String writeFilePath;
	private int NofOutputFiles;
	private Simulation sim;
	private double[] Qmax;
	private boolean Jsc1DPlot;
	private boolean Jsc2DPlot;
	private boolean[] isActiveLayer; 
	private double[] activeLayerThicknessArray;
	private double[] Jsc;
	private String activeLayer1Name;
	
	public Profile(double angle, double startWL, double wavelengthStep, double endWL, int NofGridPoints, String[] layerName, double[] startThickness, double[] stepThickness, double[] endThickness, String[] nkFilePathName, boolean[] isActiveLayer, String writeFilePath, int NofOutputFiles, boolean calculateJsc) throws FileNotFoundException{
		this.angle = angle;
		this.layerName = layerName;
		this.startThickness = startThickness;
		this.stepThickness = stepThickness;
		this.endThickness = endThickness;
		this.NofOutputFiles = NofOutputFiles;
		this.writeFilePath = writeFilePath;	
		this.isActiveLayer = isActiveLayer;
		Jsc1DPlot = false;
		Jsc2DPlot = false;
		int activeLayerCount = 0;
		
		if(calculateJsc){
			for(int i=0; i<layerName.length; i++){
				if(isActiveLayer[i]) activeLayerCount++;
			}
				
			switch(activeLayerCount){
				case 0:
					JOptionPane.showMessageDialog(null,"No active layer is chosen!","Warning",JOptionPane.WARNING_MESSAGE);
					break;
				case 1:
					Jsc1DPlot = true;
					break;
				case 2:
					Jsc2DPlot = true;
					JOptionPane.showMessageDialog(null,"A 3D plot will be added in the next version","Warning",JOptionPane.WARNING_MESSAGE);
				default:
					JOptionPane.showMessageDialog(null,"More than two active layers are chosen, no Jsc plot will be generated.","Warning",JOptionPane.WARNING_MESSAGE);
					break;
			}
		}
		sim = new Simulation(angle, startWL, endWL, wavelengthStep, NofGridPoints, layerName.length, nkFilePathName, isActiveLayer);
		Qmax = new double[NofOutputFiles+1];
	}

	/**
	 * sort out the structures with the highest power dissipation in the active layers
	 * 
	 * before the permutation
	 * 
	 * ArrayList Layer
	 * <Layer0>: layerThickness[0], layerThickness[1], layerThickness[2]...
	 * <Layer1>: layerThickness[0], layerThickness[1], layerThickness[2]...
	 * ...
	 * 
	 * after the permutation
	 * 
	 * ArrayList thicknessList
	 * <thicknessList0>:  <layer0 thickness>, <layer1 thickness>, <layer2 thickness>...
	 * <thicknessList1>:  <layer0 thickness>, <layer1 thickness>, <layer2 thickness>...
	 * <thicknessList2>:  <layer0 thickness>, <layer1 thickness>, <layer2 thickness>...
	 * 
	 * @throws FileNotFoundException
	 */
	
	@Override
	protected Void doInBackground() throws Exception {
		List<List<Double>> thicknessList = new ArrayList<List<Double>>();
		List<double[]> Layers = new ArrayList<double[]>();
		activeLayerThicknessArray = null;
		Jsc = null;
		
		if(Jsc1DPlot){
			for(int i=0; i<layerName.length; i++){
				double[] thicknessArray;
				
				if(!isActiveLayer[i]){
					thicknessArray = new double[1];
					thicknessArray[0] = startThickness[i];		//to generate 1D Jsc-active layer thickness plot, thickness all the non-active layers will be set as the starting thickness  
				}else{
					int NofTndummy = (int)Math.round((endThickness[i] - startThickness[i]) / stepThickness[i]);
					int NofTn = (endThickness[i] - startThickness[i]) % stepThickness[i] == 0 ? NofTndummy + 1 : NofTndummy;	// number of thickness for the layer
					thicknessArray = new double[NofTn];
					activeLayerThicknessArray = new double[NofTn];
					
					for(int lt=0; lt<NofTn; lt++){
						thicknessArray[lt] = startThickness[i] + stepThickness[i] * lt;
					}
					Jsc = new double[NofTn];
					activeLayerThicknessArray = thicknessArray.clone();
					activeLayer1Name = layerName[i];
				}
				Layers.add(thicknessArray);
			}
		}else{
			for(int i=0; i<layerName.length; i++){			
				double[] thicknessArray;
				
				if(stepThickness[i]!=0){
					int NofTndummy = (int)Math.round((endThickness[i] - startThickness[i]) / stepThickness[i]);
					int NofTn = (endThickness[i] - startThickness[i]) % stepThickness[i] == 0 ? NofTndummy + 1 : NofTndummy;
					thicknessArray = new double[NofTn];
					
					for(int lt=0; lt<NofTn; lt++){
						thicknessArray[lt] = startThickness[i] + stepThickness[i] * lt;
					}				
				}else{
					thicknessArray = new double[1];
					thicknessArray[0] = startThickness[i];
				}
				Layers.add(thicknessArray);
			}
		}
		ThicknessPermutation generator = new ThicknessPermutation(Layers);
		thicknessList = new ArrayList<List<Double>>(generator.generatePermutation());
		
		if(NofOutputFiles>thicknessList.size()) NofOutputFiles = thicknessList.size();
		
		
		LinkedList<ArrayList<double[]>> resultList = new LinkedList<ArrayList<double[]>>();
		
		for(int ts=0; ts<thicknessList.size(); ts++){
			int progress = ts * 100 / (thicknessList.size() - 1);
			setProgress(progress);
			double Qtotal = sim.Compute(thicknessList.get(ts));
			int count = 0;
			ListIterator<ArrayList<double[]>> iter = resultList.listIterator();		
			
			int lastPosition = resultList.size() < NofOutputFiles ? resultList.size():NofOutputFiles;
			
			while(Qtotal<Qmax[count] && count<lastPosition){
				count++;
				iter.next();
			}
						
			for(int sort=lastPosition; sort>count; sort--) Qmax[sort] = Qmax[sort-1];		
			
			Qmax[count] = Qtotal;
			iter.add(sim.getARTQZone());
			
			while(resultList.size()>NofOutputFiles) resultList.removeLast();
			
			if(Jsc1DPlot){
				Jsc[ts] = sim.getJsc();
			}
		}
		final int LASTCOLUMN = 7;		//LASTCOLUMN stors the thickness of each layer
		final int WAVELENTHCOLUMNS = 5; 	//WAVELENTHCOLUMNS stores datum in ART files. 0, wavelength; 1, A; 2, R; 3, T; 4, EQE.
		
		for(int nof=0; nof<NofOutputFiles; nof++){
			double[] layerThicknessArray = new double[resultList.get(nof).get(LASTCOLUMN).length];
			layerThicknessArray = resultList.get(nof).get(LASTCOLUMN);
					
			ArrayList<double[]> ARTList = new ArrayList<double[]>();

			for(int f=0; f<WAVELENTHCOLUMNS; f++) ARTList.add(resultList.get(nof).get(f));
						
			String ARTFile = writeFilePath + "ART" + nof;
			String[] title1 = {"wavelength", "A", "R", "T" , "EQE"};
			WriteFile(Qmax[nof], ARTList, ARTFile, layerThicknessArray, title1);
			
			ArrayList<double[]> ProfileList = new ArrayList<double[]>(); 
			
			for(int f=WAVELENTHCOLUMNS; f<LASTCOLUMN; f++) ProfileList.add(resultList.get(nof).get(f)); 
					
			String ProfileFile = writeFilePath + "Profile" + nof;
			String[] title2 = {"x", "Qprofile"};
			WriteFile(Qmax[nof], ProfileList, ProfileFile, layerThicknessArray, title2);
		}
		return null;
	}
	
	@Override
	protected void done(){
		Toolkit.getDefaultToolkit().beep();
		if(Jsc1DPlot)
			try {
				generate1DJscPlot();
				writeFileJsc();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		super.done();
	}

	/**
	 * the results will be saved in two files: ART & Q profile
	 * 
	 * @param writtenList
	 * @param fpn
	 * @param lta
	 * @param title
	 * @throws FileNotFoundException
	 */
	private void WriteFile(double Qm, ArrayList<double[]> writtenList, String fpn, double[] lta, String[] title) throws FileNotFoundException{
		PrintWriter out = new PrintWriter(fpn + ".txt");
		
		for(int i=0; i<layerName.length; i++) out.print(layerName[i] + " / ");
		out.println();
		for(int i=0; i<layerName.length; i++) out.printf("%.1f / ", lta[i]);	//lta represents the layerThicknessArray, the thickness of each layer
		out.println();
		out.printf("Angle %.1f", angle);
		out.println();
		out.printf("Power dissipation in active layers %.2f W/m^2", Qm);
		out.println();
		for(int t=0; t<title.length; t++) out.print(title[t] + "\t");
		out.println();
		
		for(int ofl=0; ofl<writtenList.get(0).length; ofl++){		//the leng of the output file
			for(int al=0; al<writtenList.size(); al++){				//the number of arrays
				out.printf("%.4f", writtenList.get(al)[ofl]);
				out.print("\t");
			}
			out.println();
		}
		out.close();
	}
	
	private void generate1DJscPlot() throws FileNotFoundException{
		Jsc1DChart chart = new Jsc1DChart(activeLayer1Name);
		JFrame chartFrame = new JFrame("Jsc - Active Layer Thickness plot");
		chartFrame.add(chart.create1DJscPlot(activeLayerThicknessArray, Jsc));
		chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chartFrame.setResizable(false);
		chartFrame.pack();
		chartFrame.setLocationRelativeTo(null);
		chartFrame.setVisible(true);
	}
	
	private void writeFileJsc() throws FileNotFoundException{
		PrintWriter out = new PrintWriter(writeFilePath + "Jsc.txt");
		
		for(int i=0; i<layerName.length; i++) out.print(layerName[i] + " / ");
		out.println();
		out.printf("Angle %.1f", angle);
		out.println();
		out.print("Thickness(nm) \t Jsc(A/m^2)");
		out.println();

		for(int t=0; t<activeLayerThicknessArray.length; t++){
			out.printf("%.1f", activeLayerThicknessArray[t]);
			out.print('\t');
			out.printf("%.2f", Jsc[t]);
			out.println();
		}
		out.close();
	}
}
