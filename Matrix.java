package se.liu.ifm.applphys.biorgel.TMM;

import org.apache.commons.math3.complex.Complex;

/**
 * 
 * @author Milo
 * Multiplication of 2*2 matrices
 */

public class Matrix {	
	Complex[][] matrix;
	
	public Matrix(){
		matrix = new Complex[2][2];
	}
	
	public void init(){
		for(int i=0; i<matrix.length; i++){
			for(int j=0; j<matrix[i].length;j++){
				matrix[i][j] = Complex.ONE;
			}
		}
	}
	
	public Matrix(Complex[][] array){
		assert array.length == 2;
		assert array[0].length == 2;
		matrix = array;
	}
	
	public Complex[][] getComplexArray(){
		return matrix;
	}
	
	public void setMatrix(Matrix mat){
		matrix = mat.getComplexArray();
	}
	
	public Matrix multiply(Matrix mat){
		Complex[][] product = new Complex[2][2];
		Complex[][] array = mat.getComplexArray();
		
		for(int i=0; i<2; i++){
			for(int j=0; j<2; j++){
				product[i][j] = matrix[i][0].multiply(array[0][j]).add(matrix[i][1].multiply(array[1][j]));
			}
		}
		Matrix production = new Matrix(product);
		return production;
	}
	
	public Complex getElement(int i, int j){
		assert i<2;
		assert j<2;
		return matrix[i][j];
	}	
}
