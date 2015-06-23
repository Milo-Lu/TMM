package se.liu.ifm.applphys.biorgel.TMM;

import java.awt.Color;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;

public class Jsc1DChart extends ApplicationFrame{
	private static final long serialVersionUID = 20140522L;
	private static String activeLayerName;

	public Jsc1DChart(String activeLayerName) {
		super("Jsc - Active Layer Thickness plot");
		Jsc1DChart.activeLayerName = activeLayerName;
	}

	private static XYDataset createDataset(double[] activeLayerThicknessArray, double[] Jsc){
		assert activeLayerThicknessArray.length == Jsc.length;
		XYSeries series1 = new XYSeries(activeLayerName);
		
		for(int nofPoints=0; nofPoints<Jsc.length; nofPoints++){
			series1.add(activeLayerThicknessArray[nofPoints], Jsc[nofPoints]);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		return dataset;
	}

	private static JFreeChart createChart(XYDataset dataset){
		JFreeChart chart = ChartFactory.createXYLineChart(
			"",
			"Active Layer Thickness (nm)",
			"Jsc-max (A/m^2)",
			dataset,
			PlotOrientation.VERTICAL,
			true,
			true,
			false
		);
		chart.setBackgroundPaint(Color.white);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		
		XYLineAndShapeRenderer render = (XYLineAndShapeRenderer) plot.getRenderer();
		render.setShapesVisible(true);
		render.setShapesFilled(true);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		return chart;
	}
	
	public JPanel create1DJscPlot(double[] activeLayerThicknessArray, double Jsc[]){
		JFreeChart chart = createChart(createDataset(activeLayerThicknessArray, Jsc));
		return new ChartPanel(chart);
	}
}
