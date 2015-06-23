package se.liu.ifm.applphys.biorgel.TMM;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

/**
 * 
 * @author Milo
 * Layers in an Object Array
 * 
 * each layer contains two parts: Layer Matrix & Interface Matrix
 */
public class Layer {
	private double Thickness;
	private Complex[] theta;	
	private Complex[] beta;
	private Matrix[] LM;						//Layer matrix
	private Matrix[] IpM;						//Interface matrix p-polarized
	private Matrix[] IsM;						//Interface matrix s-polarized
	private double[] ReadWavelength;
	private Complex[] N;
	private double[] ReadNReal;
	private double[] ReadNImaginary;

	/**
	 * N Complex refractive index
	 * LM Layer matrix
	 * IpM Interface matrix for p-polarized light
	 * IsM Interface matrix for s-polarized light
	 */
	public Layer(){
		N = null;
		LM = null;
		IpM = null;
		IsM = null;
	}
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
	/**
	 * this constructor is used for the extra layer on the back, which is required by the "last layer / air" interface matrix
	 * 
	 * @param NofWL
	 */
	public Layer(int NofWL){
		//declare arrays according to the length of wavelength array
		theta = new Complex[NofWL];
		beta = new Complex[NofWL];					
		IpM = new Matrix[NofWL];						
		IsM = new Matrix[NofWL];
	}

	/**
	 * 
	 * @throws FileNotFoundException
	 * scan the file and store the data in an arraylist - readOpticalConstants as Strings
	 */
	public void openFile(String fpn) throws FileNotFoundException{
		String FilePathName = fpn;
		readFile(FilePathName);
	}
	
	/**
	 * read the data file and store 3 parameters: wavelength, n, k
	 */
	private void readFile(String fpn) throws FileNotFoundException{
		FileReader reader = new FileReader(fpn);
		Scanner in = new Scanner(reader);
		ArrayList<String> readOpticalConstants = new ArrayList<String>();
		int ReadFileLength = 0;

		try{
			while(in.hasNext()){				
				readOpticalConstants.add(in.next());
				readOpticalConstants.add(in.next());
				readOpticalConstants.add(in.next());
				ReadFileLength += 1;
			}
			
			if(readOpticalConstants.size() > 0){	
				ReadWavelength = new double[ReadFileLength];
				ReadNReal = new double[ReadFileLength];
				ReadNImaginary = new double[ReadFileLength];
				int m = 0;
				
				for(int i = 0; i < ReadFileLength; i++){
					m = i * 3;
					ReadWavelength[i] = Double.parseDouble(readOpticalConstants.get(m));
					ReadNReal[i] = Double.parseDouble(readOpticalConstants.get(m+1));
					ReadNImaginary[i] = Double.parseDouble(readOpticalConstants.get(m+2));
				}
			}else{
				System.out.println("No file has been read");
			}
		}finally{
			in.close();
		}
	}
	
	/**
	 * 
	 * @param wl interpolate N at each wavelength
	 */
	public void InterpolatedData(double[] wl) throws FileNotFoundException{
		if(ReadWavelength!=null && ReadNReal!=null && ReadNImaginary!=null){
			SplineInterpolator splineInterp = new SplineInterpolator();
			PolynomialSplineFunction polySplineFReal =  splineInterp.interpolate(ReadWavelength, ReadNReal);
			PolynomialSplineFunction polySplineFImaginary = splineInterp.interpolate(ReadWavelength, ReadNImaginary);

			double[] NReal = new double[wl.length];
			double[] NImaginary = new double[wl.length];
			N = new Complex[wl.length];
			theta = new Complex[wl.length];
			beta = new Complex[wl.length];
			LM = new Matrix[wl.length];						
			IpM = new Matrix[wl.length];						
			IsM = new Matrix[wl.length];
			
			for(int j=0; j<=wl.length-1; j++){
				NReal[j] = polySplineFReal.value(wl[j]);
				NImaginary[j] = polySplineFImaginary.value(wl[j]);
				N[j] = new Complex(NReal[j], NImaginary[j]);
			}
		}
	}
		
	/**
	 * 
	 * @param j				Index of the wavelength
	 * @param AngleP		Angle of the incident beam -- Layer[i-1]
	 * @param Np			Refractive index of Layer[i-1]
	 * @param wavelength
	 * air   /   layer0 / layer1 / ... / layerN / air
	 *       ^      ^
	 *       |      |
	 *   IpM & IsM  L
	 */
	public void setLayerParameter(int j, Complex AngleP, Complex Np, double wavelength){
/*		theta[j] = Math.asin(Np.getReal() * Math.sin(AngleP) / N[j].getReal());
		beta[j] = N[j].multiply(2*Math.PI*Thickness).multiply(Math.cos(theta[j])).divide(wavelength);
		Complex rs = (Np.multiply(Math.cos(AngleP)).subtract(N[j].multiply(Math.cos(theta[j])))).divide(Np.multiply((Math.cos(AngleP))).add(N[j].multiply(Math.cos(theta[j]))));
		Complex rp = (N[j].multiply(Math.cos(AngleP)).subtract(Np.multiply(Math.cos(theta[j])))).divide(N[j].multiply((Math.cos(AngleP))).add(Np.multiply(Math.cos(theta[j]))));
		Complex ts = Np.multiply(Math.cos(AngleP)).multiply(2.0).divide(Np.multiply(Math.cos(AngleP)).add(N[j].multiply(Math.cos(theta[j]))));
		Complex tp = Np.multiply(Math.cos(AngleP)).multiply(2.0).divide(N[j].multiply(Math.cos(AngleP)).add(Np.multiply(Math.cos(theta[j]))));
*/
		theta[j] = (Np.multiply(AngleP.sin()).divide(N[j])).asin();
		beta[j] = N[j].multiply(2*Math.PI*Thickness).multiply(theta[j].cos()).divide(wavelength);
		Complex rs = (Np.multiply(AngleP.cos()).subtract(N[j].multiply(theta[j].cos()))).divide(Np.multiply((AngleP.cos())).add(N[j].multiply(theta[j].cos())));
		Complex rp = (N[j].multiply(AngleP.cos()).subtract(Np.multiply(theta[j].cos()))).divide(N[j].multiply((AngleP.cos())).add(Np.multiply(theta[j].cos())));
		Complex ts = Np.multiply(AngleP.cos()).multiply(2.0).divide(Np.multiply(AngleP.cos()).add(N[j].multiply(theta[j].cos())));
		Complex tp = Np.multiply(AngleP.cos()).multiply(2.0).divide(N[j].multiply(AngleP.cos()).add(Np.multiply(theta[j].cos())));

		Complex[][] L = {{(beta[j].multiply(Complex.I).negate()).exp(), Complex.ZERO}, {Complex.ZERO, (beta[j].multiply(Complex.I)).exp()}};
	 	Complex[][] Ip = {{tp.reciprocal(), rp.divide(tp)}, {rp.divide(tp), tp.reciprocal()}};
		Complex[][] Is = {{ts.reciprocal(), rs.divide(ts)}, {rs.divide(ts), ts.reciprocal()}};
				
		LM[j] = new Matrix(L);
		IpM[j] = new Matrix(Ip);
		IsM[j] = new Matrix(Is);
	}
	
	/**
	 * 
	 * @param j
	 * air   /   layer0 / layer1 / ... / layerN  / air
	 *           								 ^
	 *          						 	     |
	 *           							 IpM & IsM
	 * for air, n[j] is 1
	 */
	public void setBackInterfaceParameter(int j, Complex AngleP, Complex Np, double wavelength){
/*		theta[j] = Math.asin(Np.getReal()*Math.sin(AngleP));		//the interface on the back is contact with the air, n is 1.
		final Complex N_Air = Complex.ONE;
		Complex rs = (Np.multiply(Math.cos(AngleP)).subtract(N_Air.multiply(Math.cos(theta[j])))).divide(Np.multiply(N_Air.multiply(Math.cos(AngleP))).add(Math.cos(theta[j])));
		Complex rp = (N_Air.multiply(Math.cos(AngleP)).subtract(Np.multiply(Math.cos(theta[j])))).divide(N_Air.multiply(Math.cos(AngleP)).add(Np.multiply(Math.cos(theta[j]))));
		Complex ts = Np.multiply(Math.cos(AngleP)).multiply(2.0).divide(Np.multiply(Math.cos(AngleP)).add(N_Air.multiply(Math.cos(theta[j]))));
		Complex tp = Np.multiply(Math.cos(AngleP)).multiply(2.0).divide(N_Air.multiply(Math.cos(AngleP)).add(Np.multiply(Math.cos(theta[j]))));
*/			
		
		theta[j] = (Np.multiply(AngleP.sin())).asin();				//the interface on the back is contact with the air, n is 1.
		final Complex N_Air = Complex.ONE;
		Complex rs = (Np.multiply((AngleP).cos()).subtract(N_Air.multiply((theta[j]).cos()))).divide(Np.multiply(N_Air.multiply((AngleP).cos())).add((theta[j]).cos()));
		Complex rp = (N_Air.multiply((AngleP).cos()).subtract(Np.multiply((theta[j]).cos()))).divide(N_Air.multiply((AngleP).cos()).add(Np.multiply((theta[j]).cos())));
		Complex ts = Np.multiply((AngleP).cos()).multiply(2.0).divide(Np.multiply((AngleP).cos()).add(N_Air.multiply((theta[j]).cos())));
		Complex tp = Np.multiply((AngleP).cos()).multiply(2.0).divide(N_Air.multiply((AngleP).cos()).add(Np.multiply((theta[j]).cos())));
		
	 	Complex[][] Ip = {{tp.reciprocal(), rp.divide(tp)}, {rp.divide(tp), tp.reciprocal()}};
		Complex[][] Is = {{ts.reciprocal(), rs.divide(ts)}, {rs.divide(ts), ts.reciprocal()}};

		IpM[j] = new Matrix(Ip);
		IsM[j] = new Matrix(Is);
	}
	
	public double getminWavelength(){
		return ReadWavelength[0];
	}
	
	public double getmaxWavelength(){
		return ReadWavelength[ReadWavelength.length-1];
	}
	
	public Complex getN(int j){
		return N[j];
	}
	
	public Complex getAngle(int j){
		return theta[j];
	}
	
	public Complex getBeta(int j){
		return beta[j];
	}
	
	public Matrix getLM(int j){
		return LM[j];
	}
	
	public Matrix getIsM(int j){
		return IsM[j];
	}
	
	public Matrix getIpM(int j){
		return IpM[j];
	}
	
	public void setThickness(double tn){
		Thickness = tn;
	}
	
	public double getThickness(){
		return Thickness;
	}	
}
