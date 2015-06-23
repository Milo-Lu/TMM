package se.liu.ifm.applphys.biorgel.TMM;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Milo
 * generate thickness set for all optical layers recursively
 *
 */
public class ThicknessPermutation {
	
	private List<double[]> Layers;
	
	public ThicknessPermutation(List<double[]> layers){
		Layers = new ArrayList<double[]>(layers);
	}

	public List<List<Double>> generatePermutation(){
		List<List<Double>> permutations = new ArrayList<List<Double>>();
		
		if(Layers.size()==1){		
			
			for(int t=0; t<Layers.get(0).length; t++){
				List<Double> thicknessList = new ArrayList<Double>();
				thicknessList.add(Layers.get(0)[t]);
				permutations.add(thicknessList);
			}
			return permutations;		
		}else{			
			List<double[]> subLayers = new ArrayList<double[]>(Layers);
			subLayers.remove(Layers.size()-1);
			ThicknessPermutation shorterPermutation = new ThicknessPermutation(subLayers);
			List<List<Double>> shorterList = shorterPermutation.generatePermutation();
			
			for(int t=0; t<Layers.get(Layers.size()-1).length; t++){
				
				for(int p=0; p<shorterList.size(); p++){	
					List<Double> thicknessList = new ArrayList<Double>(shorterList.get(p));
					thicknessList.add(Layers.get(Layers.size()-1)[t]);
					permutations.add(thicknessList);
				}
			}	
			return permutations;
		}
	}
}
