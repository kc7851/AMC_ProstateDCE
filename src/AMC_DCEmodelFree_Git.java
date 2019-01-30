import ij.plugin.DragAndDrop;
import ij.plugin.PlugIn;
import ij.plugin.filter.*;
//import ij.plugin.frame.AMCSyncWindows;
import ij.*;
import ij.process.*;
import ij.util.DicomTools;
import ij.gui.*;
import ij.measure.*;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;



class AMC_Plot extends Plot{

	double xValueLocation;
	public AMC_Plot(String title, String xLabel, String yLabel,
			double[] xValues, double[] yValues) {
		super(title, xLabel, yLabel, xValues, yValues);

		xValueLocation=new Double(xValues.length);

		double xv = Double.NaN, yv = Double.NaN;
		//		System.out.println("LEFT_MARGIN "+this.LEFT_MARGIN);
		//		System.out.println("PlotWindow.plotWidth "+PlotWindow.plotWidth);


	}



}
//skc ij.gui.ImageWindow.java에 ImagePlus imp 를 public으로 바꿈
/** Implements the Image/Stack/Plot Z-axis Profile command. */
public class AMC_DCEmodelFree_Git implements PlugIn , Measurements, MouseListener, MouseMotionListener{
	public static int GAP = 0;
	private ImagePlus imp;
	AMC_Plot plotgraph; 
	PlotWindow plotWindow;
	AMC_Plot meanPlotgraph; 
	PlotWindow meanPlotWindow;
	Rectangle currentScrRect;
	ImageCanvas canvas;
	ImageCanvas plotcanvas;
	ImageCanvas iauc;
	ImageCanvas ve;
	ImageCanvas wash_in;
	ImageCanvas maxrelenh;
	ImagePlus auc1, auc2, auc3, auc4, washin, washout, pei, sub1, sub2;




	float[] imgPixels;
//	float[][][][] img4DArray; //img4DArray=new float[_Frame][_Slice][_W][_H];
	int _W;
	int _H;
	int _Slice;
	int _Frame;

	//	int plotwindowwidth;

	double currentMag;
	boolean isHyper;
	protected int oldSlice;


	AxisDialog1 axisDialog;

	//skc	
	ModelFreeMenu_Git modelfreemenu;
	float pixelMaxValue;
	int frameWhenMax;
	float sliceWhenMax;
	float xoffsetWhenMax;
	float yoffsetWhenMax;
	int baseStart;
	int baseEnd;
	int timeOfArrival;
	int initialbaseStart;
	int initialbaseEnd;
	int initialtimeOfArrival;
	int auc1Start;
	int auc2Start;
	int auc3Start;
	int auc4Start;
	int auc1End;
	int auc2End;
	int auc3End;
	int auc4End;
	int washinStart;
	int washinEnd;
	int washoutStart;
	int washoutEnd;
	int tsvalue;
	int lastArrayNum;
	int screenW;
	int screenH;
	int peakIndex;
	int taIndex;
	int blankIndex;
	float[] baseline;
	boolean normalizeOn;
	boolean auc1On;
	boolean auc2On;
	boolean auc3On;
	boolean auc4On;
	boolean washinOn;
	boolean washoutOn;
	boolean timetopeakOn;
	boolean timeofarrivalOn;
	boolean peiOn;
	boolean sliceonlyOn;
	boolean roionlyOn;
	boolean temposmt;
	boolean spatialsmt;
	boolean mouseEventRemove;
	boolean processOn;
	boolean outlierOn;
	boolean init3DArray;
	boolean subtraction1On;
	boolean subtraction2On;
	Label pauselb ;
	double[] xCoordi;
	double[] yCoordi;
	double yMinval;
	double yMaxval;
	double xScale;
	double yScale;

	float[][][] img3DArray; //img4DArray=new float[_Frame][_Slice][_W*_H];
	float[][][] outlier3D;

	public boolean done = false;
	protected final Object resourceSync = "resourceSync";//For synchronization.
	


	static final String NOT_GRAY32_MSG =
			"정확한 계산을 위해 32 bit Grayscale 로 변환합니다. \n "+
					"만약 정상적으로 동작 안할 시 "+	
					"수동으로 변환해주세요.";

	public void run(String arg) {
		this.imp = IJ.getImage();

		if (imp.isHyperStack()) //if hyperstack
		{
			//Sab. magnify original image 150% and set current slice to middle
			imp.setZ(Math.round((float)(imp.getNSlices())/2)); //Sab. change to middle of total slice
			IJ.runPlugIn(imp ,"ij.plugin.Animator","next");
			IJ.runPlugIn(imp ,"ij.plugin.Animator","previous");
//			IJ.runPlugIn(imp ,"ij.plugin.Zoom","in" );	
			//Sab. magnify original image 150% and set current slice to middle.. end.
			
			//skc. set current imp directory path
//			imp.folderpath = DragAndDrop.folderpath;
			//skc. set current imp directory path.. end.
			
			isHyper=true;
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			screenW = gd.getDisplayMode().getWidth();
			screenH = gd.getDisplayMode().getHeight();

//			if(IJ.livePlotMap.get(imp.getTitle())==null){
//				IJ.livePlotMap.put(imp.getTitle(),this);
//			}else {
//				IJ.livePlotMap.remove(imp.getTitle());
//				IJ.livePlotMap.put(imp.getTitle(),this);
//			}

			this.imp = checkThatImageIsGray32(imp);

			
			if (imp.isHyperStack()){
				isHyper=true;
			}
			else{
				isHyper=false;
			}

			_W=imp.getWidth();
			_H=imp.getHeight();

			ImageProcessor ip = imp.getProcessor();

			pixelMaxValue=0;
			frameWhenMax=0;
			sliceWhenMax=0;
			xoffsetWhenMax=0;
			yoffsetWhenMax=0;
			baseStart=2;
			baseEnd=5;
			timeOfArrival=5;
			normalizeOn = true;
			auc1On = true;
			auc2On = true;
			auc3On = false;
			auc4On = false;
			washinOn = true;
			washoutOn = false;
			timetopeakOn = false;
			timeofarrivalOn = false;
			peiOn = true;
			sliceonlyOn = false;
			roionlyOn = false;
			temposmt= true;
			spatialsmt= false;
			mouseEventRemove = true;
			processOn = true;
			outlierOn = false;
			init3DArray = true;
			subtraction1On = false;
			subtraction2On = false;

			initialbaseStart=0;
			initialbaseEnd=0;
			initialtimeOfArrival=0;

			yMinval=0;
			yMaxval=1;

			tsvalue=5;
			lastArrayNum=0;
			//temp plotting
			double[] temp = new double[5]; 
			plotgraph = new AMC_Plot("_Plot_"+imp.getTitle(), "x", "Y", temp, temp);

			plotWindow = plotgraph.show();

			plotWindow.setResizable(false);
			plotWindow.setLocation(imp.getWindow().getLocation().x+imp.getWindow().getWidth()+GAP,imp.getWindow().getLocation().y );
			plotWindow.setTitle("Live Point View");

			pauselb = new Label("Live!");
			//		lb.setLocation(70, 300);
			plotWindow.add(pauselb);

			axisDialog = new AxisDialog1(imp);
//			axisDialog.setLocation(imp.getWindow().getLocation().x+imp.getWindow().getWidth()+GAP,imp.getWindow().getLocation().y-axisDialog.getHeight()-GAP);
//			axisDialog.setLocation(plotWindow.getX()+plotWindow.getWidth(),plotWindow.getY());
			axisDialog.xxx = new double[imp.getNFrames()];
			for (int i=0; i<axisDialog.xxx.length; i++)
				axisDialog.xxx[i] = i+1;
			
			ImageStack stack1 = imp.getStack(); 

			_Slice=imp.getNSlices();
			_Frame=imp.getNFrames();
			blankIndex=0;
//			if(_Slice==15){
//				this.blankIndex=5;
//			}else if(_Slice==20){
//				this.blankIndex=3;
//			}
			//			img4DArray=new float[_Frame][_Slice][_W][_H];
			img3DArray=new float[_Frame][_Slice][_W*_H];

			for (int indexF=0;indexF<_Frame;indexF++){
				for (int indexS=0;indexS<_Slice;indexS++){
					//Slice number starts from 1 not 0;
					float[] tmpPixels = (float[]) stack1.getPixels(indexF*_Slice+(indexS+1));
					for(int indexH=0;indexH<_H;indexH++){
						for(int indexW=0;indexW<_W;indexW++){
							//							img4DArray[indexF][indexS][indexW][indexH]=tmpPixels[indexH*_W+indexW];
							img3DArray[indexF][indexS][indexH*_W+indexW]=tmpPixels[indexH*_W+indexW];
							if(2<indexF&&indexF<30){
								float subMax = img3DArray[indexF][indexS][indexH*_W+indexW]-((img3DArray[0][indexS][indexH*_W+indexW]+img3DArray[1][indexS][indexH*_W+indexW]+img3DArray[2][indexS][indexH*_W+indexW])/3);
								if(pixelMaxValue<subMax){
									pixelMaxValue=subMax;
									frameWhenMax=indexF;
									sliceWhenMax=indexS;
									xoffsetWhenMax=indexW;
									yoffsetWhenMax=indexH;
								}
							}	
						}
					}
				}
			}

			baseline=new float[frameWhenMax+5];

			for(int ii=0;ii<frameWhenMax+5;ii++){
				baseline[ii] = img3DArray[(int)ii][(int) sliceWhenMax][(int) xoffsetWhenMax+(int) yoffsetWhenMax*_W];
			}
			for(int i=2;i<frameWhenMax;i++){
				if(baseline[i+1]-baseline[i]>0){
					if(((Math.abs(baseline[i-1]-baseline[i-2])+Math.abs(baseline[i]-baseline[i-1]))/2)*4<Math.abs(baseline[i+1]-baseline[i])){
						baseEnd=i-3;
						timeOfArrival=i-1;
						if((baseline[i-2]+baseline[i-1]+baseline[i])/3*1.5<baseline[i+2]){

							if(baseline[i+2]>baseline[i+1]){
								baseEnd=i-3;
								timeOfArrival=i-1;

							}
						}
					}
				}
			}

			auc1Start=19;
			auc2Start=_Frame-1;
			auc3Start=timeOfArrival;
			auc4Start=timeOfArrival;
			auc1End=auc1Start+19;
			auc2End=_Frame-1;
			auc3End=timeOfArrival+40;
			auc4End=timeOfArrival+70;
			washinStart=19;
			washinEnd=washinStart+19;
			washoutStart=washinEnd;
			washoutEnd=_Frame-1;
			baseEnd=14;
			peakIndex = timeOfArrival+3;
			taIndex = timeOfArrival;
			
			if(timeOfArrival>10){
				auc1Start = timeOfArrival;
				auc1End=auc1Start+19;
				washinStart=timeOfArrival;
				washinEnd=washinStart+19;
			}
			initialbaseStart=baseStart;
			initialbaseEnd=baseEnd;
			initialtimeOfArrival=timeOfArrival;
			
			modelfreemenu = new ModelFreeMenu_Git(imp,this);
			modelfreemenu.xxx = new double[imp.getNFrames()];
			for (int i=0; i<modelfreemenu.xxx.length; i++)
				modelfreemenu.xxx[i] = i+1;

			modelfreemenu.baserangenum1.setText(""+(baseStart+1));
			modelfreemenu.baserangenum2.setText(""+(baseEnd+1));
			modelfreemenu.timeofarrivalnum.setText(""+(timeOfArrival+1));

			modelfreemenu.temporalsmoonum.setText(""+(this.tsvalue));
			modelfreemenu.auc1num1.setText(""+(this.auc1Start+1));
			modelfreemenu.auc1num2.setText(""+(auc1End+1));
			modelfreemenu.auc2num1.setText(""+(auc2Start+1));
			modelfreemenu.auc2num2.setText(""+(auc2End+1));
			modelfreemenu.washinnum1.setText(""+(this.washinStart+1));
			modelfreemenu.washinnum2.setText(""+(this.washinEnd+1));

			ImageWindow tmpWin=imp.getWindow();
			canvas=tmpWin.getCanvas();
			canvas.addMouseListener(this);
			canvas.addMouseMotionListener(this);
			currentMag=canvas.getMagnification();

			plotcanvas=plotWindow.getCanvas();
			plotcanvas.addMouseListener(this);
			plotcanvas.addMouseMotionListener(this);

			currentScrRect= new Rectangle(canvas.getSrcRect()); 

			plotROIAVE();

			boolean paused=false;

//			IJ.runPlugIn("ij.plugin.frame.AMCSyncWindows",""); //skc 20171207 추가
//			IJ.runPlugIn("ij.plugin.frame.ContrastAdjuster","skc");
			
			while (!done) {
				try {Thread.sleep(20);}
				catch(InterruptedException e) {}
				synchronized(resourceSync){//Obtain a lock to aviod interfering with doMeasurements.
					if(paused)
						;
					else
						checkSlice();				
				}


				//If there is no imp, close all.
				if (WindowManager.getWindow(imp.getTitle())==null){
					if(plotWindow!=null){					
						plotgraph=null;
						plotWindow.setVisible(false);	
						plotWindow.close();
						if(meanPlotWindow!=null){
							if(!meanPlotWindow.isClosed()){
								meanPlotgraph=null;
								meanPlotWindow.setVisible(false);
								meanPlotWindow.close();
							}

						}
					}
					System.gc();
					img3DArray=null;
					plotWindow=null;
					meanPlotWindow=null;
					done=true;
					axisDialog.shutDown();
					if(WindowManager.getImage("iAUC")!=null){WindowManager.getImage("iAUC").close();}
					if(WindowManager.getImage("VE")!=null){WindowManager.getImage("VE").close();}
					if(WindowManager.getImage("AUC3 "+(auc3Start+1)+"-"+(auc3End+1)+" "+imp.getTitle()+" Result")!=null){auc3.close(); auc3=null;}
					if(WindowManager.getImage("AUC4 "+(auc4Start+1)+"-"+(auc4End+1)+" "+imp.getTitle()+" Result")!=null){auc4.close(); auc4=null;}
					if(WindowManager.getImage("Wash-In")!=null){WindowManager.getImage("Wash-In").close();}
					if(WindowManager.getImage("Wash-Out"+" "+imp.getTitle()+" Result")!=null){washout.close(); washout=null;}
					if(WindowManager.getImage("Subtraction1"+" "+imp.getTitle()+" Result")!=null){sub1.close(); sub1=null;}
					if(WindowManager.getImage("Subtraction2"+" "+imp.getTitle()+" Result")!=null){sub2.close(); sub2=null;}
					if(WindowManager.getImage("MaxRelEnh")!=null){WindowManager.getImage("MaxRelEnh").close();}
					
					return;
				}else{
					if(IJ.escapePressed()){
						if (paused){
							//						IJ.resetEscape();
							paused=false;
							canvas.addMouseListener(this);
							canvas.addMouseMotionListener(this);
							//						System.out.println("ESC key pressed! resume!");
						}
						else {
							//						IJ.resetEscape();
							paused=true;
							canvas.removeMouseListener(this);	
							canvas.removeMouseMotionListener(this);
							//						System.out.println("ESC key pressed! paused");
						}
						IJ.resetEscape();

					}
					if(IJ.spaceBarDown()){
						axisDialog.autoRange=true;
						axisDialog.currentMax=-Float.MAX_VALUE;
						axisDialog.currentMin=Float.MAX_VALUE;
					}

					if(imp.getWindow().isAlwaysOnTop()!=axisDialog.isAlwaysOnTop()){
						axisDialog.setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
					}
					if(imp.getWindow().isAlwaysOnTop()!=modelfreemenu.isAlwaysOnTop()){
						modelfreemenu.setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
					}
					if(meanPlotWindow!=null&&imp.getWindow().isAlwaysOnTop()!=meanPlotWindow.isAlwaysOnTop()){
						meanPlotWindow.setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
					}

					if(WindowManager.getImage("iAUC")!=null){
						if(imp.getWindow().isAlwaysOnTop()!=WindowManager.getImage("iAUC").getWindow().isAlwaysOnTop()){
							WindowManager.getImage("iAUC").getWindow().setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
						}
					}
					if(WindowManager.getImage("VE")!=null){
						if(imp.getWindow().isAlwaysOnTop()!=WindowManager.getImage("VE").getWindow().isAlwaysOnTop()){
							WindowManager.getImage("VE").getWindow().setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
						}
					}
					if(WindowManager.getImage("AUC3 "+(auc3Start+1)+"-"+(auc3End+1)+" "+imp.getTitle()+" Result")!=null){
						if(imp.getWindow().isAlwaysOnTop()!=auc3.getWindow().isAlwaysOnTop()){
							auc3.getWindow().setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
						}
					}
					if(WindowManager.getImage("AUC4 "+(auc4Start+1)+"-"+(auc4End+1)+" "+imp.getTitle()+" Result")!=null){
						if(imp.getWindow().isAlwaysOnTop()!=auc4.getWindow().isAlwaysOnTop()){
							auc4.getWindow().setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
						}
					}
					if(WindowManager.getImage("Wash-In")!=null){
						if(imp.getWindow().isAlwaysOnTop()!=WindowManager.getImage("Wash-In").getWindow().isAlwaysOnTop()){
							WindowManager.getImage("Wash-In").getWindow().setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
						}
					}
					if(WindowManager.getImage("Wash-Out"+" "+imp.getTitle()+" Result")!=null){
						if(imp.getWindow().isAlwaysOnTop()!=washout.getWindow().isAlwaysOnTop()){
							washout.getWindow().setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
						}
					}
					if(WindowManager.getImage("Subtraction1"+" "+imp.getTitle()+" Result")!=null){
						if(imp.getWindow().isAlwaysOnTop()!=sub1.getWindow().isAlwaysOnTop()){
							sub1.getWindow().setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
						}
					}
					if(WindowManager.getImage("Subtraction2"+" "+imp.getTitle()+" Result")!=null){
						if(imp.getWindow().isAlwaysOnTop()!=sub2.getWindow().isAlwaysOnTop()){
							sub2.getWindow().setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
						}
					}
					if(WindowManager.getImage("MaxRelEnh")!=null){
						if(imp.getWindow().isAlwaysOnTop()!=WindowManager.getImage("MaxRelEnh").getWindow().isAlwaysOnTop()){
							WindowManager.getImage("MaxRelEnh").getWindow().setAlwaysOnTop(imp.getWindow().isAlwaysOnTop());
						}
					}

					if(!plotWindow.isClosed()){
						plotWindow.setLocation(imp.getWindow().getLocation().x+imp.getWindow().getWidth()+GAP,imp.getWindow().getLocation().y );
						if(plotWindow.isFocused()&&axisDialog.getState()==java.awt.Frame.NORMAL){
							if(!axisDialog.isShowing()){
								axisDialog.setVisible(true);
							}
						}
						if(imp.getWindow().isFocused()){
							if(!axisDialog.isShowing()){
								axisDialog.setVisible(true);
							}
							if(!plotWindow.isShowing()){
								plotWindow.setVisible(true);
							}
							if(meanPlotWindow!=null){
								if(!meanPlotWindow.isClosed()){
								}
							}
							if(!modelfreemenu.isShowing()){
								modelfreemenu.setVisible(true);
							}
							if(WindowManager.getImage("iAUC")!=null){
								if(!WindowManager.getImage("iAUC").getWindow().isShowing()){
									WindowManager.getImage("iAUC").getWindow().setVisible(true);
								}
							}
							if(WindowManager.getImage("VE")!=null){
								if(!WindowManager.getImage("VE").getWindow().isShowing()){
									WindowManager.getImage("VE").getWindow().setVisible(true);
								}
							}
							if(WindowManager.getImage("AUC3 "+(auc3Start+1)+"-"+(auc3End+1)+" "+imp.getTitle()+" Result")!=null){
								if(!WindowManager.getImage("AUC3 "+(auc3Start+1)+"-"+(auc3End+1)+" "+imp.getTitle()+" Result").getWindow().isShowing()){
									WindowManager.getImage("AUC3 "+(auc3Start+1)+"-"+(auc3End+1)+" "+imp.getTitle()+" Result").getWindow().setVisible(true);
								}
							}
							if(WindowManager.getImage("AUC4 "+(auc4Start+1)+"-"+(auc4End+1)+" "+imp.getTitle()+" Result")!=null){
								if(!WindowManager.getImage("AUC4 "+(auc4Start+1)+"-"+(auc4End+1)+" "+imp.getTitle()+" Result").getWindow().isShowing()){
									WindowManager.getImage("AUC4 "+(auc4Start+1)+"-"+(auc4End+1)+" "+imp.getTitle()+" Result").getWindow().setVisible(true);
								}
							}
							if(WindowManager.getImage("Wash-In")!=null){
								if(!WindowManager.getImage("Wash-In").getWindow().isShowing()){
									WindowManager.getImage("Wash-In").getWindow().setVisible(true);
								}
							}
							if(WindowManager.getImage("Wash-Out"+" "+imp.getTitle()+" Result")!=null){
								if(!WindowManager.getImage("Wash-Out"+" "+imp.getTitle()+" Result").getWindow().isShowing()){
									WindowManager.getImage("Wash-Out"+" "+imp.getTitle()+" Result").getWindow().setVisible(true);
								}
							}
							if(WindowManager.getImage("Subtraction1"+" "+imp.getTitle()+" Result")!=null){
								if(!WindowManager.getImage("Subtraction1"+" "+imp.getTitle()+" Result").getWindow().isShowing()){
									WindowManager.getImage("Subtraction1"+" "+imp.getTitle()+" Result").getWindow().setVisible(true);
								}
							}
							if(WindowManager.getImage("Subtraction2"+" "+imp.getTitle()+" Result")!=null){
								if(!WindowManager.getImage("Subtraction2"+" "+imp.getTitle()+" Result").getWindow().isShowing()){
									WindowManager.getImage("Subtraction2"+" "+imp.getTitle()+" Result").getWindow().setVisible(true);
								}
							}
							if(WindowManager.getImage("MaxRelEnh")!=null){
								if(!WindowManager.getImage("MaxRelEnh").getWindow().isShowing()){
									WindowManager.getImage("MaxRelEnh").getWindow().setVisible(true);
								}
							}
						}

						if(imp.getWindow().isVisible()&&imp.getWindow().getState()==java.awt.Frame.ICONIFIED){
							plotWindow.setVisible(false);
							axisDialog.setVisible(false);
							modelfreemenu.setVisible(false);
							if(WindowManager.getImage("iAUC")!=null){
								auc1.getWindow().setVisible(false);
							}
							if(WindowManager.getImage("VE")!=null){
								auc2.getWindow().setVisible(false);
							}
							if(WindowManager.getImage("AUC3 "+(auc3Start+1)+"-"+(auc3End+1)+" "+imp.getTitle()+" Result")!=null){
								auc3.getWindow().setVisible(false);
							}
							if(WindowManager.getImage("AUC4 "+(auc4Start+1)+"-"+(auc4End+1)+" "+imp.getTitle()+" Result")!=null){
								auc4.getWindow().setVisible(false);
							}
							if(WindowManager.getImage("Wash-In")!=null){
								washin.getWindow().setVisible(false);
							}
							if(WindowManager.getImage("Wash-Out"+" "+imp.getTitle()+" Result")!=null){
								washout.getWindow().setVisible(false);
							}
							if(WindowManager.getImage("Subtraction1"+" "+imp.getTitle()+" Result")!=null){
								sub1.getWindow().setVisible(false);
							}
							if(WindowManager.getImage("Subtraction2"+" "+imp.getTitle()+" Result")!=null){
								sub2.getWindow().setVisible(false);
							}
							if(WindowManager.getImage("MaxRelEnh")!=null){
								pei.getWindow().setVisible(false);
							}


						}else if(imp.getWindow().getState()==java.awt.Frame.NORMAL){
						}
					}
					if(meanPlotWindow!=null&&!meanPlotWindow.isClosed()){
						if(!plotWindow.isClosed()){
							meanPlotWindow.setLocation(plotWindow.getLocation().x,plotWindow.getLocation().y+plotWindow.getHeight() +GAP);
							//앞으로 올리기
							if(imp.getWindow().isFocused()){
								if(!meanPlotWindow.isShowing()){
									meanPlotWindow.setVisible(true);
									//								imp.getWindow().toFront();
								}

							}
							if(imp.getWindow().isVisible()&&imp.getWindow().getState()==java.awt.Frame.ICONIFIED){
								meanPlotWindow.setVisible(false);
							}
						}
					}

					if (plotWindow.isClosed()){
						//					System.out.println("Live Point View Window Closed");
						canvas.removeMouseListener(this);
						canvas.removeMouseMotionListener(this);
						if(plotWindow!=null){
							plotWindow.setVisible(false);
						}
						if(meanPlotWindow!=null){
							meanPlotWindow.setVisible(false);

						}
						meanPlotWindow=null;
//						img4DArray=null; // out of memory error 방지
						img3DArray=null;
						axisDialog.setVisible(false);
						modelfreemenu.setVisible(false);
						if(WindowManager.getImage("iAUC")!=null){auc1.close(); auc1=null;}
						if(WindowManager.getImage("VE")!=null){auc2.close(); auc2=null;}
						if(WindowManager.getImage("AUC3 "+(auc3Start+1)+"-"+(auc3End+1)+" "+imp.getTitle()+" Result")!=null){auc3.close(); auc3=null;}
						if(WindowManager.getImage("AUC4 "+(auc4Start+1)+"-"+(auc4End+1)+" "+imp.getTitle()+" Result")!=null){auc4.close(); auc4=null;}
						if(WindowManager.getImage("Wash-In")!=null){washin.close(); washin=null;}
						if(WindowManager.getImage("Wash-Out"+" "+imp.getTitle()+" Result")!=null){washout.close(); washout=null;}
						if(WindowManager.getImage("Subtraction1"+" "+imp.getTitle()+" Result")!=null){sub1.close(); sub1=null;}
						if(WindowManager.getImage("Subtraction2"+" "+imp.getTitle()+" Result")!=null){sub2.close(); sub2=null;}
						if(WindowManager.getImage("MaxRelEnh")!=null){pei.close(); pei=null;}

						done=true;

						return;
					}
				}
			}
			
			
		}else{
			IJ.error("Hyper Stack이 아닙니다.");
		}
	}

	public void checkSlice() {
		int slice = imp.getCurrentSlice();
		if (slice != oldSlice){
			oldSlice = slice;
			if (meanPlotWindow!=null)
				plotROIAVE();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		Object b = arg0.getSource();

		if(b == canvas){

			if(normalizeOn==true){
				plotMousePoint_Normal(arg0);
			}else{
				plotMousePoint(arg0);
				if (meanPlotWindow!=null){
					//					plotROIAVE();
				}
			}		
		}else if(b == plotcanvas){
			if(modelfreemenu.roiChoice==1){
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
					modelfreemenu.baserangebtn.setForeground(Color.red);
					float avc = (float) ((plotWindow.imp.getRoi().getBounds().getX()-60)*_Frame/450);
					float abc = (float) ((plotWindow.imp.getRoi().getBounds().getX()+plotWindow.imp.getRoi().getBounds().getWidth()-60)*_Frame/450);
					if (avc < 0)
						avc =0;
					if(abc > 119)
						abc = 119;
					baseStart = (int)Math.round(avc);
					baseEnd = (int)Math.round(abc);
					modelfreemenu.baserangenum1.setText(""+(baseStart+1));
					modelfreemenu.baserangenum2.setText(""+(baseEnd+1));
					modelfreemenu.baserangeAutocheck.setState(false);
				}
			}else if(modelfreemenu.roiChoice==2){
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){


					float abc = (float) ((plotWindow.imp.getRoi().getBounds().getX()+(plotWindow.imp.getRoi().getBounds().getWidth()/2)-60)*_Frame/450);
					modelfreemenu.timeofarrivalbtn.setForeground(Color.red);
					timeOfArrival =(int)Math.round(abc);
					modelfreemenu.timeofarrivalnum.setText(""+(timeOfArrival+1));
					modelfreemenu.timeofarrivalAutocheck.setState(false);


				}
			}else if(modelfreemenu.roiChoice==3){
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
					modelfreemenu.auc1btn.setForeground(Color.red);
					float avc = (float) ((plotWindow.imp.getRoi().getBounds().getX()-60)*_Frame/450);
					float abc = (float) ((plotWindow.imp.getRoi().getBounds().getX()+plotWindow.imp.getRoi().getBounds().getWidth()-60)*_Frame/450);
					if (avc < 0) avc =0;
					if (abc > _Frame-1) abc = _Frame-1; 
					auc1Start = (int)Math.round(avc);
					auc1End = (int)Math.round(abc);
					this.washinStart=(int)Math.round(avc);
					this.washinEnd=	(int)Math.round(abc);	
					modelfreemenu.auc1num1.setText(""+(auc1Start+1));
					modelfreemenu.auc1num2.setText(""+(auc1End+1));
					modelfreemenu.washinnum1.setText(""+(auc1Start+1));
					modelfreemenu.washinnum2.setText(""+(auc1End+1));
				}

			}else if(modelfreemenu.roiChoice==4){
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
					modelfreemenu.auc2btn.setForeground(Color.red);
					float avc = (float) ((plotWindow.imp.getRoi().getBounds().getX()-60)*_Frame/450);
					float abc = (float) ((plotWindow.imp.getRoi().getBounds().getX()+plotWindow.imp.getRoi().getBounds().getWidth()-60)*_Frame/450);
					if (avc < 0) avc =0;
					if (abc > _Frame-1) abc = _Frame-1; 
					auc2Start = (int)Math.round(avc);
					auc2End = (int)Math.round(abc);
					modelfreemenu.auc2num1.setText(""+(auc2Start+1));
					modelfreemenu.auc2num2.setText(""+(auc2End+1));
				}

			}
			else if(modelfreemenu.roiChoice==7){
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
					modelfreemenu.washinbtn.setForeground(Color.red);
					float avc = (float) ((plotWindow.imp.getRoi().getBounds().getX()-60)*_Frame/450);
					float abc = (float) ((plotWindow.imp.getRoi().getBounds().getX()+plotWindow.imp.getRoi().getBounds().getWidth()-60)*_Frame/450);
					if (avc < 0) avc =0;
					if (abc > _Frame-1) abc = _Frame-1; 
					washinStart = (int)Math.round(avc);
					washinEnd = (int)Math.round(abc);
					modelfreemenu.washinnum1.setText(""+(washinStart+1));
					modelfreemenu.washinnum2.setText(""+(washinEnd+1));
				}
			}	
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		Object b = arg0.getSource();
		if(b == canvas){
			if(normalizeOn==true){
				if(mouseEventRemove==true)plotMousePoint_Normal(arg0);
				//				System.out.println("ddd");
			}else{
				if(mouseEventRemove==true)plotMousePoint(arg0);

			}

		}else if(b == plotcanvas){

		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		Object b = arg0.getSource();
		if(b == canvas){
		}else if(b == plotcanvas){
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//		System.out.println("mouse entered");
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//				System.out.println("mouse exited");

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		Object b = arg0.getSource();
		if(b == canvas){
			if(mouseEventRemove==true){
				mouseEventRemove=false;
				pauselb.setText("Pause!");
			}else{
				mouseEventRemove=true;
				pauselb.setText("Live!");
			}

		}else if(b == plotcanvas){

			if(modelfreemenu.roiChoice==1){
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
					modelfreemenu.baserangebtn.setForeground(Color.red);
				}
			}else if(modelfreemenu.roiChoice==2&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
				if(plotWindow.imp.getRoi()!=null){
					modelfreemenu.timeofarrivalbtn.setForeground(Color.red);
				}
			}else if(modelfreemenu.roiChoice==3&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
				if(plotWindow.imp.getRoi()!=null){
					modelfreemenu.auc1btn.setForeground(Color.red);
				}
			}else if(modelfreemenu.roiChoice==4&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
				if(plotWindow.imp.getRoi()!=null){
					modelfreemenu.auc2btn.setForeground(Color.red);
				}
			}else if(modelfreemenu.roiChoice==7&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
				if(plotWindow.imp.getRoi()!=null){
					modelfreemenu.washinbtn.setForeground(Color.red);
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		Object b = arg0.getSource();
		if(b == canvas){
			if(normalizeOn==true){
				plotMousePoint_Normal(arg0);
			}else{
				plotMousePoint(arg0);
			}	
			plotROIAVE();
		}else if(b == plotcanvas){
			if(modelfreemenu.roiChoice==1){
				if(plotWindow.imp.getRoi()==null)
					modelfreemenu.baserangebtn.setForeground(Color.black);
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
					modelfreemenu.baserangebtn.setForeground(Color.red);
					float avc = (float) ((plotWindow.imp.getRoi().getBounds().getX()-60)*_Frame/450);
					float abc = (float) ((plotWindow.imp.getRoi().getBounds().getX()+plotWindow.imp.getRoi().getBounds().getWidth()-60)*_Frame/450);
					if (avc < 0) avc =0;
					if (abc > _Frame-1) abc = _Frame-1; 
					baseStart = (int)Math.round(avc);
					baseEnd = (int)Math.round(abc);
					modelfreemenu.baserangenum1.setText(""+(baseStart+1));
					modelfreemenu.baserangenum2.setText(""+(baseEnd+1));
				}
			}else if(modelfreemenu.roiChoice==2){
				if(plotWindow.imp.getRoi()==null)
					modelfreemenu.timeofarrivalbtn.setForeground(Color.black);
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
					modelfreemenu.timeofarrivalbtn.setForeground(Color.red);
					float abc = (float) ((plotWindow.imp.getRoi().getBounds().getX()+(plotWindow.imp.getRoi().getBounds().getWidth()/2)-60)*_Frame/450);
					timeOfArrival =(int)Math.round(abc);
					modelfreemenu.timeofarrivalnum.setText(""+(timeOfArrival+1));
				}
			}else if(modelfreemenu.roiChoice==3){
				if(plotWindow.imp.getRoi()==null)
					modelfreemenu.auc1btn.setForeground(Color.black);
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
					modelfreemenu.auc1btn.setForeground(Color.red);
					float avc = (float) ((plotWindow.imp.getRoi().getBounds().getX()-60)*_Frame/450);
					float abc = (float) ((plotWindow.imp.getRoi().getBounds().getX()+plotWindow.imp.getRoi().getBounds().getWidth()-60)*_Frame/450);
					if (avc < 0) avc =0;
					if (abc > _Frame-1) abc = _Frame-1; 
					auc1Start = (int)Math.round(avc);
					auc1End = (int)Math.round(abc);
					modelfreemenu.auc1num1.setText(""+(auc1Start+1));
					modelfreemenu.auc1num2.setText(""+(auc1End+1));
				}

			}else if(modelfreemenu.roiChoice==4){
				if(plotWindow.imp.getRoi()==null)
					modelfreemenu.auc2btn.setForeground(Color.black);
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
					modelfreemenu.auc2btn.setForeground(Color.red);
					float avc = (float) ((plotWindow.imp.getRoi().getBounds().getX()-60)*_Frame/450);
					float abc = (float) ((plotWindow.imp.getRoi().getBounds().getX()+plotWindow.imp.getRoi().getBounds().getWidth()-60)*_Frame/450);
					if (avc < 0) avc =0;
					if (abc > _Frame-1) abc = _Frame-1; 
					auc2Start = (int)Math.round(avc);
					auc2End = (int)Math.round(abc);
					modelfreemenu.auc2num1.setText(""+(auc2Start+1));
					modelfreemenu.auc2num2.setText(""+(auc2End+1));
				}

			} else if(modelfreemenu.roiChoice==7){
				if(plotWindow.imp.getRoi()==null)
					modelfreemenu.washinbtn.setForeground(Color.black);
				if(plotWindow.imp.getRoi()!=null&&plotWindow.imp.getRoi().getBounds().getHeight()==200){
					modelfreemenu.washinbtn.setForeground(Color.red);
					float avc = (float) ((plotWindow.imp.getRoi().getBounds().getX()-60)*_Frame/450);
					float abc = (float) ((plotWindow.imp.getRoi().getBounds().getX()+plotWindow.imp.getRoi().getBounds().getWidth()-60)*_Frame/450);
					if (avc < 0) avc =0;
					if (abc > _Frame-1) abc = _Frame-1; 
					washinStart = (int)Math.round(avc);
					washinEnd = (int)Math.round(abc);
					modelfreemenu.washinnum1.setText(""+(washinStart+1));
					modelfreemenu.washinnum2.setText(""+(washinEnd+1));
				}

			}
		}
	}

	private ImagePlus checkThatImageIsGray32(ImagePlus i) {
		if (i.getType() != ImagePlus.GRAY32)
		{
			//IJ.showMessage(NOT_GRAY32_MSG);
			StackConverter ic = new StackConverter(i);
			ic.convertToGray32();
		}
		return i;
	}

	// getting the maximum value
	public static double getMaxValue(double[] aveY){  
		double maxValue = aveY[0];  
		for(int i=1;i < aveY.length;i++){  
			if(aveY[i] > maxValue){  
				maxValue = aveY[i];  
			}  
		}  
		return maxValue;  
	}  

	public static float getMaxValuefloat(float[] aveY){  
		float maxValue = aveY[0];  
		for(int i=1;i < aveY.length;i++){  
			if(aveY[i] > maxValue){  
				maxValue = aveY[i];  
			}  
		}  
		return maxValue;  
	}

	// getting the miniumum value
	public static double getMinValue(double[] y){  
		double minValue = y[0];  
		for(int i=1;i<y.length;i++){  
			if(y[i] < minValue){  
				minValue = y[i];  
			}  
		}  
		return minValue;  
	}  

	void plotMousePoint_Normal(MouseEvent arg0){


		if (imp.isHyperStack()) //if hyperstack
		{
			currentMag=canvas.getMagnification();
			currentScrRect= new Rectangle(canvas.getSrcRect()); 
			lastArrayNum=(int)(currentScrRect.x+arg0.getX()/currentMag)+((int)(currentScrRect.y+arg0.getY()/currentMag)*_W);
			double[] y= new double[_Frame];

			for (int indexF=0; indexF<_Frame; indexF++){
//				y[indexF]= img3DArray[indexF][imp.getZ()-1][(int)(currentScrRect.x+arg0.getX()/currentMag+((currentScrRect.y+arg0.getY()/currentMag)*_W))];
				try{
					float threebythreeAve = img3DArray[indexF][imp.getZ()-1][lastArrayNum]; //skc 20171207 3x3 평균값으로 보여주기
					if(arg0.getX()>1 && arg0.getX()<_W && arg0.getY()>1 && arg0.getY()<_H){
						float sumValue = img3DArray[indexF][imp.getZ()-1][lastArrayNum-_H-1]; //위왼 
						sumValue = sumValue + img3DArray[indexF][imp.getZ()-1][lastArrayNum-_H];; //위중간
						sumValue = sumValue + img3DArray[indexF][imp.getZ()-1][lastArrayNum-_H+1];; //위오른
						sumValue = sumValue + img3DArray[indexF][imp.getZ()-1][lastArrayNum-1]; //중간왼
						sumValue = sumValue + img3DArray[indexF][imp.getZ()-1][lastArrayNum]; //중간중간
						sumValue = sumValue + img3DArray[indexF][imp.getZ()-1][lastArrayNum+1]; //중간오른
						sumValue = sumValue + img3DArray[indexF][imp.getZ()-1][lastArrayNum+_H-1]; //아래왼
						sumValue = sumValue + img3DArray[indexF][imp.getZ()-1][lastArrayNum+_H]; //아래중간
						sumValue = sumValue + img3DArray[indexF][imp.getZ()-1][lastArrayNum+_H+1]; //아래오른
						
						threebythreeAve = sumValue/9;
					}
					
					y[indexF]= threebythreeAve;
				}catch(Exception eeee){};
			}

			if (temposmt==true&&tsvalue>0){
				double[] tempoY= new double[_Frame];
				int quota = tsvalue/2;
				for(int indexF=0;indexF<quota;indexF++){
					double avg =0;
					for(int i = 0;i<=quota;i++){
						double tmp = y[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/(quota+1);
					tempoY[indexF]=avg;
				}
				for(int indexF=quota;indexF<_Frame-quota;indexF++){
					double avg =0;
					for(int i = -(quota);i<=quota;i++){
						double tmp = y[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/tsvalue;
					tempoY[indexF]=avg;
				}
				for(int indexF=_Frame-quota;indexF<_Frame;indexF++){
					double avg =0;
					for(int i = -quota;i<=0;i++){
						double tmp = y[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/(quota+1);
					tempoY[indexF]=avg;
				}
				for(int i=0;i<_Frame;i++){
					y[i]=tempoY[i];
				}
			}

			double baselineAVG=0;
			for(int i=baseStart;i<baseEnd+1;i++){
				double baselineAVG1 = y[i];
				baselineAVG = baselineAVG + baselineAVG1;
			}

			baselineAVG=baselineAVG/(baseEnd-baseStart+1);

			for (int indexF=0; indexF<_Frame; indexF++){
				y[indexF]= y[indexF]/baselineAVG-1;
			}



			double[] x = new double[y.length];
			for (int i=0; i<x.length; i++)
				x[i] = i+1;


			//String title;
			//title = imp.getTitle()+"-0-0";
			String xAxisLabel = "Frame";

			if (axisDialog.autoRange){
				axisDialog.currentMax=1;
				axisDialog.currentMin=-0.1;
				if(axisDialog.currentMax<getMaxValue(y)){
					axisDialog.currentMax=getMaxValue(y);
				}
				if(axisDialog.currentMin>getMinValue(y)){
					axisDialog.currentMin=getMinValue(y);
				}
				yMinval=axisDialog.currentMin;
				yMaxval=axisDialog.currentMax;

			}
			xScale = (double)450/(_Frame-1);
			yScale = (double)(200/(yMaxval-yMinval));

			xCoordi = new double[_Frame];
			yCoordi = new double[_Frame];
			for(int i = 0;i<_Frame;i++){
				xCoordi[i]= 60 + xScale*i;
				yCoordi[i]=	((y[i]-yMinval)*yScale-215)*-1;	

			}

			if (axisDialog.fitterChoice==-1){

				plotgraph = new AMC_Plot("_Average_Plot_"+imp.getTitle(), xAxisLabel, "rel SI (%)", axisDialog.xxx, y);
				plotgraph.setLimits(axisDialog.xxx[0], axisDialog.xxx[x.length-1], axisDialog.currentMin, axisDialog.currentMax);
				//plotWindow.removeAll();
				plotWindow.drawPlot(plotgraph);	
				plotWindow.addPoints(axisDialog.xxx, y, PlotWindow.X );
				//					System.out.print("d");
			}

		}

	}

	void plotMousePoint_smoothing(int arrayNum){


		if (imp.isHyperStack()) //if hyperstack
		{
			currentMag=canvas.getMagnification();
			currentScrRect= new Rectangle(canvas.getSrcRect()); 
			double[] y= new double[_Frame];
			String xAxisLabel = "Frame";
			String yAxisLabel = "";
			for(int indexF=0;indexF<_Frame;indexF++){
				y[indexF]=img3DArray[indexF][imp.getZ()-1][arrayNum];
			}

			if (temposmt==true&&tsvalue>0){
				double[] tempoY= new double[_Frame];
				int quota = tsvalue/2;
				//				System.out.println("quota : "+quota);
				for(int indexF=0;indexF<quota;indexF++){
					double avg =0;
					for(int i = 0;i<=quota;i++){
						double tmp = y[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/(quota+1);
					//					System.out.println("avg : "+avg);
					tempoY[indexF]=avg;
				}
				for(int indexF=quota;indexF<_Frame-quota;indexF++){
					double avg =0;
					for(int i = -(quota);i<=quota;i++){
						double tmp = y[indexF+i];
						//						System.out.println("[] : "+(indexF+i));
						avg = avg+tmp;
					}
					//					System.out.println();
					avg=avg/tsvalue;
					//					System.out.println("avg : "+avg);
					tempoY[indexF]=avg;
				}
				for(int indexF=_Frame-quota;indexF<_Frame;indexF++){
					double avg =0;
					for(int i = -quota;i<=0;i++){
						double tmp = y[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/(quota+1);
					tempoY[indexF]=avg;
				}

				for(int i=0;i<_Frame;i++){
					y[i]=tempoY[i];
				}

			}

			if(normalizeOn==true){
				double baselineAVG=0;
				for(int i=baseStart;i<baseEnd+1;i++){
					double baselineAVG1 = y[i];
					baselineAVG = baselineAVG + baselineAVG1;
				}

				baselineAVG=baselineAVG/(baseEnd-baseStart+1);

				for (int indexF=0; indexF<_Frame; indexF++){
					//need to do -1 for Slice#  
					//					y[indexF]= img4DArray[indexF][imp.getZ()-1][(int)(currentScrRect.x+arg0.getX()/currentMag)][(int)(currentScrRect.y+arg0.getY()/currentMag)];
					y[indexF]= y[indexF]/baselineAVG-1;
				}


				if (axisDialog.autoRange){
					axisDialog.currentMax=1;
					axisDialog.currentMin=-0.1;
					if(axisDialog.currentMax<getMaxValue(y)){
						axisDialog.currentMax=getMaxValue(y);
					}
					if(axisDialog.currentMin>getMinValue(y)){
						axisDialog.currentMin=getMinValue(y);
					}
					yMinval=axisDialog.currentMin;
					yMaxval=axisDialog.currentMax;
				}
				yAxisLabel="rel SI (%)";


			}else{


				if (axisDialog.autoRange){
					if(axisDialog.currentMax<getMaxValue(y)){
						axisDialog.currentMax=getMaxValue(y);
					}
					if(axisDialog.currentMin>getMinValue(y)){
						axisDialog.currentMin=getMinValue(y);
					}
					yMinval=axisDialog.currentMin;
					yMaxval=axisDialog.currentMax;
				}

				yAxisLabel="value";
			}

			xScale = (double)450/(_Frame-1);
			yScale = (double)(200/(yMaxval-yMinval));

			xCoordi = new double[_Frame];
			yCoordi = new double[_Frame];
			for(int i = 0;i<_Frame;i++){
				xCoordi[i]= 60 + xScale*i;
				yCoordi[i]=	((y[i]-yMinval)*yScale-215)*-1;	

			}

			double[] x = new double[y.length];
			for (int i=0; i<x.length; i++)
				x[i] = i+1;

			plotgraph = new AMC_Plot("_Average_Plot_"+imp.getTitle(), xAxisLabel, yAxisLabel, axisDialog.xxx, y);
			plotgraph.setLimits(axisDialog.xxx[0], axisDialog.xxx[x.length-1], axisDialog.currentMin, axisDialog.currentMax);
			plotWindow.drawPlot(plotgraph);	
			plotWindow.addPoints(axisDialog.xxx, y, PlotWindow.X );
		}

	}

	void plotMousePoint(MouseEvent arg0){
		if (imp.isHyperStack()) //if hyperstack
		{
			currentMag=canvas.getMagnification();
			currentScrRect= new Rectangle(canvas.getSrcRect()); 
			lastArrayNum=(int)(currentScrRect.x+arg0.getX()/currentMag)+((int)(currentScrRect.y+arg0.getY()/currentMag)*_W);
			double[] y= new double[_Frame];
			for (int indexF=0; indexF<_Frame; indexF++){
				//need to do -1 for Slice#  
//				y[indexF]= img3DArray[indexF][imp.getZ()-1][(int)(currentScrRect.x+arg0.getX()/currentMag)+((int)(currentScrRect.y+arg0.getY()/currentMag)*_W)];
				try{y[indexF]= img3DArray[indexF][imp.getZ()-1][lastArrayNum];}catch(Exception eeee){};
			}

			double[] x = new double[y.length];
			for (int i=0; i<x.length; i++)
				x[i] = i+1;

			if (temposmt==true&&tsvalue>0){
				double[] tempoY= new double[_Frame];
				int quota = tsvalue/2;
				for(int indexF=0;indexF<quota;indexF++){
					double avg =0;
					for(int i = 0;i<=quota;i++){
						double tmp = y[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/(quota+1);
					tempoY[indexF]=avg;
				}
				for(int indexF=quota;indexF<_Frame-quota;indexF++){
					double avg =0;
					for(int i = -(quota);i<=quota;i++){
						double tmp = y[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/tsvalue;
					tempoY[indexF]=avg;
				}
				for(int indexF=_Frame-quota;indexF<_Frame;indexF++){
					double avg =0;
					for(int i = -quota;i<=0;i++){
						double tmp = y[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/(quota+1);
					tempoY[indexF]=avg;
				}
				for(int i=0;i<_Frame;i++){
					y[i]=tempoY[i];
				}
			}

			String xAxisLabel = "Frame";

			if (axisDialog.autoRange){
				if(axisDialog.currentMax<getMaxValue(y)){
					axisDialog.currentMax=getMaxValue(y);
				}
				if(axisDialog.currentMin>getMinValue(y)){
					axisDialog.currentMin=getMinValue(y);

				}
				yMinval=axisDialog.currentMin;
				yMaxval=axisDialog.currentMax;

			}

			xScale = (double)450/(_Frame-1);
			yScale = (double)(200/(yMaxval-yMinval));

			xCoordi = new double[_Frame];
			yCoordi = new double[_Frame];
			for(int i = 0;i<_Frame;i++){
				xCoordi[i]= 60 + xScale*i;
				yCoordi[i]=	((y[i]-yMinval)*yScale-215)*-1;	

			}

			LiveCurveFitter1 cv = new LiveCurveFitter1(axisDialog.xxx, y);
			if (axisDialog.fitterChoice==-1){

				plotgraph = new AMC_Plot("_Average_Plot_"+imp.getTitle(), xAxisLabel, "Value", axisDialog.xxx, y);
				plotgraph.setLimits(axisDialog.xxx[0], axisDialog.xxx[x.length-1], axisDialog.currentMin, axisDialog.currentMax);

				plotWindow.drawPlot(plotgraph);	
				plotWindow.addPoints(axisDialog.xxx, y, PlotWindow.X );
			}
		}

	}

	//ROI AVE plotting
	void plotROIAVE(){

	}

	public void processResult(){
		//		float[][][] final3DArray = new float[_Frame][_Slice][_W*_H];
		if(temposmt==true){
			//			img3DArray = temporalSmoothing(img3DArray);
			temporalSmoothing1();
		}

		if(normalizeOn==true){normalize();}

		int sliceStart,sliceEnd;
		if(sliceonlyOn==true){
			sliceStart=imp.getZ()-1;
			sliceEnd=imp.getZ();
		}else{
			sliceStart=0;
			sliceEnd=_Slice;
		}

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double monitorwidth = screenSize.getWidth();
		double monitorheight = screenSize.getHeight();

		int t2Width = 512;
		int t2Height = 512;
//		if(WindowManager.getImage("T2")!=null){
//			t2Width = WindowManager.getImage("T2").getWidth();
//			t2Height = WindowManager.getImage("T2").getHeight();
//		}
		
		int xloca1, xloca2, xloca3, xloca4, xloca5, xloca6, xloca7, yloca1, yloca2, yloca3, yloca4, yloca5, yloca6, yloca7;
		xloca1 = 10;
		xloca2 = imp.getWindow().getLocation().x-10+218;
		xloca3 = imp.getWindow().getLocation().x-10+218+218;
		xloca4 = imp.getWindow().getLocation().x-10+218+218+218;
		xloca5 = imp.getWindow().getLocation().x-10+218+218+218+218;
		xloca6 = imp.getWindow().getLocation().x-10+218+218+218+218+218;
		xloca7 = imp.getWindow().getLocation().x-10+218+218+218+218+218+218;
		
		yloca1 = imp.getWindow().getLocation().y+550;
		yloca2 = imp.getWindow().getLocation().y+850;

		if(auc1On==true&&auc1Start<=auc1End){
			if(auc1Start<0)auc1Start=0;
			if(auc1Start>_Frame-1)auc1Start=_Frame-1;
			if(auc1End<0)auc1End=0;
			if(auc1End>_Frame-1)auc1End=_Frame-1;
			//			System.out.println(" auc1Start "+auc1Start+ " auc1End "+auc1End );
			float[][] img2DArray = new float[_Slice][_W*_H];
			for(int indexS=sliceStart;indexS<sliceEnd;indexS++){
				Roi roi = null;
				if(roionlyOn){
					imp.setZ(indexS+1);
					try{Thread.sleep(35);}catch(Exception eeee){};
					roi= imp.getRoi();
					if(roi==null)
						continue;
				}
				float[] pixelres = new float[_W*_H];
				float imsipixel[] = new float[_W*_H];
				for(int indexF=auc1Start;indexF<auc1End+1;indexF++){
					imsipixel=img3DArray[indexF][indexS];
					if(roionlyOn==true){
						pixelres = arraySumForRoi(imsipixel,pixelres,roi,indexS);
					}else{
						pixelres = arraySum(imsipixel,pixelres,indexS, "iAUC");
					}
				}
				for(int i=0;i<pixelres.length;i++){
					if(pixelres[i]<0)pixelres[i]=0;
				}
				img2DArray[indexS]=pixelres;
			}

			auc1 = NewImage.createFloatImage("iAUC", _W, _H,  sliceEnd-sliceStart+(blankIndex*2), NewImage.FILL_BLACK);

			ImageStack result_win2 = auc1.getStack();
			
//			auc1.folderpath = imp.folderpath;
			
			if(sliceonlyOn==false){
				auc1.getProcessor().setPixels(img2DArray[0]);
				for (int p=sliceStart; p<sliceEnd; p++) {
					result_win2.setPixels(img2DArray[p],p+1+blankIndex);
				}
				auc1.setSlice(Math.round((float)(auc1.getNSlices())/2)); //Sab. change to middle of total slice
				auc1.show();
//				IJ.runPlugIn(WindowManager.getImage("iAUC") ,"ij.plugin.Zoom","in" );	
//				IJ.runPlugIn(WindowManager.getImage("iAUC") ,"ij.plugin.Zoom","in" );	
			}else{
				auc1.getProcessor().setPixels(img2DArray[sliceStart]);
				auc1.show();
			}
			setPropertyInfo(imp,auc1);

		}
		if(auc2On==true&&auc2Start<=auc2End){
			if(auc2Start<0)auc2Start=0;
			if(auc2Start>_Frame-1)auc2Start=_Frame-1;
			if(auc2End<0)auc2End=0;
			if(auc2End>_Frame-1)auc2End=_Frame-1;

			float[][] img2DArray = new float[_Slice][_W*_H];
			for(int indexS=sliceStart;indexS<sliceEnd;indexS++){
				Roi roi = null;
				if(roionlyOn){
					imp.setZ(indexS+1);
					try{Thread.sleep(35);}catch(Exception eeee){};
					roi= imp.getRoi();
					if(roi==null)
						continue;
				}
				float[] pixelres = new float[_W*_H];
				float imsipixel[] = new float[_W*_H];
				for(int indexF=auc2Start;indexF<auc2End+1;indexF++){
					imsipixel=img3DArray[indexF][indexS];
					if(roionlyOn==true){
						pixelres = arraySumForRoi(imsipixel,pixelres,roi,indexS);
					}else{
						pixelres = arraySum(imsipixel,pixelres,indexS,"VE");
					}
				}
				for(int i=0;i<pixelres.length;i++){
					if(pixelres[i]<0)pixelres[i]=0;
				}
				img2DArray[indexS]=pixelres;
			}

			auc2 = NewImage.createFloatImage("VE", _W, _H,  sliceEnd-sliceStart+(blankIndex*2), NewImage.FILL_BLACK);

			ImageStack result_win2 = auc2.getStack();
//			auc2.folderpath = imp.folderpath;
			if(sliceonlyOn==false){
				auc2.getProcessor().setPixels(img2DArray[0]);
				for (int p=sliceStart; p<sliceEnd; p++) {
					result_win2.setPixels(img2DArray[p],p+1+(blankIndex));
				}
				auc2.setSlice(Math.round((float)(auc2.getNSlices())/2)); //Sab. change to middle of total slice
				
				auc2.show();
//				IJ.runPlugIn(WindowManager.getImage("VE") ,"ij.plugin.Zoom","in" );
//				IJ.runPlugIn(WindowManager.getImage("VE") ,"ij.plugin.Zoom","in" );

			}else{
				auc2.getProcessor().setPixels(img2DArray[sliceStart]);
				auc2.show();
			}
			setPropertyInfo(imp,auc2);
			
		}
	
		



		if(washinOn==true){

			float[][] img2DArray = new float[_Slice][_W*_H];

			for(int indexS=sliceStart;indexS<sliceEnd;indexS++){
				IJ.showStatus("Wash-In progress: "+indexS+"/"+_Slice);
				Roi roi = null;
				ImageProcessor ip = null;
				ImageProcessor mask = null;
				Rectangle r = null;
				if(roionlyOn){
					imp.setZ(indexS+1);
					try{Thread.sleep(35);}catch(Exception eeee){};
					roi= imp.getRoi();
					if(roi==null)
						continue;
					if (roi!=null && !roi.isArea()) roi = null;
					ip = imp.getProcessor();
					mask = roi!=null?roi.getMask():null;
					r = roi!=null?roi.getBounds():new Rectangle(0,0,ip.getWidth(),ip.getHeight());
				}
				for(int indexP=0;indexP<img2DArray[indexS].length;indexP++){
					float washinValue = 0;
					int x=(int)indexP%_W;
					int y=(int)indexP/_W;
					
					washinValue=img3DArray[washinEnd][indexS][indexP]-img3DArray[washinStart][indexS][indexP];
					washinValue=washinValue/(washinEnd-washinStart);	
					if(washinValue<0)washinValue=0;

					float maxValue = 0;
					for(int indexF=0;indexF<_Frame;indexF++){
						if(maxValue<img3DArray[indexF][indexS][indexP]){
							maxValue=img3DArray[indexF][indexS][indexP];
						}
					}
					if(maxValue<0.5)washinValue=0; //skc 20171207 maxRelEhn 0.5미만이면 날리기
						
					if(roionlyOn==true){
						if(roi!=null){
							if (x>=r.x && y>=r.y && x<=r.x+r.width && y<=r.y+r.height&& (mask==null||mask.getPixel(x-r.x,y-r.y)!=0) ) {
								img2DArray[indexS][indexP]=washinValue;
							}else{
								img2DArray[indexS][indexP]=(float) 0.0;
							}
						}else{
							img2DArray[indexS][indexP]=(float) 0.0;
						}
					}else{
						img2DArray[indexS][indexP]=washinValue;
					}	

				}
			}	
			washin = NewImage.createFloatImage("Wash-In", _W, _H,  sliceEnd-sliceStart+(blankIndex*2), NewImage.FILL_BLACK);

			ImageStack result_win2 = washin.getStack();
//			washin.folderpath = imp.folderpath;
			if(sliceonlyOn==false){
				washin.getProcessor().setPixels(img2DArray[0]);
				for (int p=sliceStart; p<sliceEnd; p++) {
					result_win2.setPixels(img2DArray[p],p+1+(blankIndex));
				}
				washin.setSlice(Math.round((float)(washin.getNSlices())/2)); //Sab. change to middle of total slice
				
				washin.show();
//				IJ.runPlugIn(WindowManager.getImage("Wash-In") ,"ij.plugin.Zoom","in" );
//				IJ.runPlugIn(WindowManager.getImage("Wash-In") ,"ij.plugin.Zoom","in" );

			}else{
				washin.getProcessor().setPixels(img2DArray[sliceStart]);
				washin.show();
			}
			setPropertyInfo(imp,washin);
			
		}

		if(peiOn==true){

			float[][] img2DArray = new float[_Slice][_W*_H];

			for(int indexS=sliceStart;indexS<sliceEnd;indexS++){
				IJ.showStatus("MaxRelEnh progress: "+indexS+"/"+_Slice);
				Roi roi = null;
				ImageProcessor ip = null;
				ImageProcessor mask = null;
				Rectangle r = null;
				if(roionlyOn){
					imp.setZ(indexS+1);
					try{Thread.sleep(35);}catch(Exception eeee){};
					roi= imp.getRoi();
					if(roi==null)
						continue;
					if (roi!=null && !roi.isArea()) roi = null;
					ip = imp.getProcessor();
					mask = roi!=null?roi.getMask():null;
					r = roi!=null?roi.getBounds():new Rectangle(0,0,ip.getWidth(),ip.getHeight());
				}
				for(int indexP=0;indexP<img2DArray[indexS].length;indexP++){
					float maxValue = 0;
					int x=(int)indexP%_W;
					int y=(int)indexP/_W;
					for(int indexF=0;indexF<_Frame;indexF++){

						if(maxValue<img3DArray[indexF][indexS][indexP]){
							maxValue=img3DArray[indexF][indexS][indexP];
						}
						
						if(roionlyOn==true){
							if(roi!=null){
								if (x>=r.x && y>=r.y && x<=r.x+r.width && y<=r.y+r.height&& (mask==null||mask.getPixel(x-r.x,y-r.y)!=0) ) {
									if(maxValue<0.5)maxValue=0;
									img2DArray[indexS][indexP]=maxValue;
								}else{
									img2DArray[indexS][indexP]=(float) 0.0;
								}
							}else{
								img2DArray[indexS][indexP]=(float) 0.0;
							}
						}else{
							if(maxValue<0.5)maxValue=0;
							img2DArray[indexS][indexP]=maxValue;
						}	
					}
				}
			}	
			pei = NewImage.createFloatImage("MaxRelEnh", _W, _H,  sliceEnd-sliceStart+(blankIndex*2), NewImage.FILL_BLACK);

			ImageStack result_win2 = pei.getStack();
//			pei.folderpath = imp.folderpath;
			if(sliceonlyOn==false){
				pei.getProcessor().setPixels(img2DArray[0]);
				for (int p=sliceStart; p<sliceEnd; p++) {
					result_win2.setPixels(img2DArray[p],p+1+(blankIndex));
				}
				pei.setSlice(Math.round((float)(pei.getNSlices())/2)); //Sab. change to middle of total slice
				
				pei.show();
//				IJ.runPlugIn(WindowManager.getImage("MaxRelEnh") ,"ij.plugin.Zoom","in" );
//				IJ.runPlugIn(WindowManager.getImage("MaxRelEnh") ,"ij.plugin.Zoom","in" );

			}else{
				pei.getProcessor().setPixels(img2DArray[sliceStart]);
				pei.show();
			}
			setPropertyInfo(imp,pei);
			
		}

//		AMCSyncWindows syncWindow = AMCSyncWindows.getInstance();
//		if (syncWindow!=null) {
//			syncWindow.allSelect();
//		}
		
		ImageStack stack1 = imp.getStack(); 

		for (int indexF=0;indexF<_Frame;indexF++){
			for (int indexS=0;indexS<_Slice;indexS++){
				float[] tmpPixels = (float[]) stack1.getPixels(indexF*_Slice+(indexS+1));
				for(int indexH=0;indexH<_H;indexH++){
					for(int indexW=0;indexW<_W;indexW++){
						img3DArray[indexF][indexS][indexH*_W+indexW]=tmpPixels[indexH*_W+indexW];
					}
				}
			}
		}
	}
	
	void setPropertyInfo(ImagePlus Parent,ImagePlus Child){

		Calibration oriCal = Parent.getCalibration(); 
		Calibration newCal = Child.getCalibration(); 
		newCal.pixelWidth=oriCal.pixelWidth; 
		newCal.pixelHeight=oriCal.pixelHeight; 
		newCal.pixelDepth=oriCal.pixelDepth; 
		newCal.setXUnit(oriCal.getXUnit()); 
		newCal.setYUnit(oriCal.getYUnit()); 
		newCal.setZUnit(oriCal.getZUnit()); 
		newCal.frameInterval=oriCal.frameInterval;
		newCal.setTimeUnit(oriCal.getTimeUnit());
	}
	
	float[] arraySum(float[] a,float[] b){
		float[] tmparr = new float[_W*_H];
		for(int i=0;i<a.length;i++){
			
			tmparr[i]=a[i]+b[i];
		}

		return tmparr;
	}
	
	float[] arraySum(float[] a,float[] b, int indexS, String arg){ //skc 20171207 maxRelEhn 0.5미만이면 날리기
		float[] tmparr = new float[_W*_H];
		for(int i=0;i<a.length;i++){
			float maxValue = 0;
			for(int indexF=0;indexF<_Frame;indexF++){
				if(maxValue<img3DArray[indexF][indexS][i]){
					maxValue=img3DArray[indexF][indexS][i];
				}
			}
			tmparr[i]=a[i]+b[i];
//			if(tmparr[i]<0)tmparr[i]=0;
			if(maxValue<0.5)tmparr[i]=0;
			
		}
		IJ.showStatus(arg+" progress: "+indexS+"/"+_Slice);
		return tmparr;
	}

	float[] arraySumForRoi(float[] a,float[] b,Roi roi, int indexS){//skc 20171207 maxRelEhn 0.5미만이면 날리기
		float[] tmparr = new float[_W*_H];
		for(int i=0;i<a.length;i++){
			if (roi!=null && !roi.isArea()) roi = null;
			ImageProcessor ip = imp.getProcessor();
			ImageProcessor mask = roi!=null?roi.getMask():null;
			Rectangle r = roi!=null?roi.getBounds():new Rectangle(0,0,ip.getWidth(),ip.getHeight());

			int x=(int)i%_W;
			int y=(int)i/_W;
			if(roi!=null){
				if (x>=r.x && y>=r.y && x<=r.x+r.width && y<=r.y+r.height&& (mask==null||mask.getPixel(x-r.x,y-r.y)!=0) ) {
					float maxValue = 0;
					for(int indexF=0;indexF<_Frame;indexF++){
						if(maxValue<img3DArray[indexF][indexS][i]){
							maxValue=img3DArray[indexF][indexS][i];
						}
					}
					tmparr[i]=a[i]+b[i];
					if(tmparr[i]<0)tmparr[i]=0;
					if(maxValue<0.5)tmparr[i]=0;
				}else{
					tmparr[i]=(float) 0.0;
				}
			}else{
				tmparr[i]=(float) 0.0;
			}

		}
		IJ.showStatus("progress: "+indexS+"/"+_Slice);
		return tmparr;
	}

	float[] baseAVG(float[] a){
		float[] tmparr = new float[_W*_H];
		float tmparr1 = 0;
		for(int i=0;i<a.length;i++){
			tmparr[i]=a[i]/(baseEnd-baseStart+1);
		}
		return tmparr;
	}


	float[] arrayDevide(float[] a,float[] b){
		float[] tmparr = new float[_W*_H];
		for(int i=0;i<a.length;i++){
			if (b[i]==0){
				tmparr[i]=0;
			}else{
				tmparr[i]=a[i]/b[i];
			}

		}
		return tmparr;
	}
//skc bg제거
	float[] arrayNormalize(float[] a,float[] b){
		float[] tmparr = new float[_W*_H];
		for(int i=0;i<a.length;i++){
			if (b[i]==0){
				tmparr[i]=0;
			}else{
				tmparr[i]=a[i]/b[i]-1;
			}

		}
		return tmparr;
	}

	float[] arraySubtract(float[] a,float[] b){
		float[] tmparr = new float[_W*_H];
		for(int i=0;i<a.length;i++){
			tmparr[i]=a[i]-b[i];

		}
		return tmparr;
	}

	float[] normalizefinal(float[] a,float[] b,float[] c){
		float[] tmparr = new float[_W*_H];
		for(int i=0;i<a.length;i++){
			if (b[i]==0){
				tmparr[i]=0;
			}else{
				tmparr[i]=a[i]/b[i]-c[i];
			}

		}
		return tmparr;
	}

	float[][][] temporalSmoothing(float[][][] abc){
		float[][][] tmparr =new float[_Frame][_Slice][_W*_H];

		for(int indexS = 0;indexS<_Slice;indexS++){
			for(int indexP=0;indexP<_W*_H;indexP++){

				int quota = tsvalue/2;

				for(int indexF=0;indexF<quota;indexF++){
					float avg =0;
					for(int i = 0;i<=quota;i++){
						float tmp = abc[indexF+i][indexS][indexP];
						avg = avg+tmp;
					}
					avg=avg/(quota+1);
					tmparr[indexF][indexS][indexP]=avg;
				}
				for(int indexF=quota;indexF<_Frame-quota;indexF++){

					float avg =0;
					for(int i = -(quota);i<=quota;i++){
						float tmp = abc[indexF+i][indexS][indexP];
						avg = avg+tmp;
					}
					avg=avg/tsvalue;
					tmparr[indexF][indexS][indexP]=avg;
				}
				for(int indexF=_Frame-quota;indexF<_Frame;indexF++){
					float avg =0;
					for(int i = -quota;i<=0;i++){
						float tmp = abc[indexF+i][indexS][indexP];
						avg = avg+tmp;
					}
					avg=avg/(quota+1);
					tmparr[indexF][indexS][indexP]=avg;
				}
			}
		}

		return tmparr;
	}

	void temporalSmoothing1(){


		for(int indexS = 0;indexS<_Slice;indexS++){
			for(int indexP=0;indexP<_W*_H;indexP++){

				int quota = tsvalue/2;
				float[] tmparr =new float[_Frame];
				float[] tmparr2 =new float[_Frame];

				for(int i=0;i<_Frame;i++){
					tmparr2[i]=img3DArray[i][indexS][indexP];
				}

				for(int indexF=0;indexF<quota;indexF++){
					float avg =0;
					for(int i = 0;i<=quota;i++){
						float tmp = tmparr2[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/(quota+1);
					tmparr[indexF]=avg;
				}
				for(int indexF=quota;indexF<_Frame-quota;indexF++){

					float avg =0;
					for(int i = -(quota);i<=quota;i++){
						float tmp = tmparr2[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/tsvalue;
					tmparr[indexF]=avg;
				}
				for(int indexF=_Frame-quota;indexF<_Frame;indexF++){
					float avg =0;
					for(int i = -quota;i<=0;i++){
						float tmp = tmparr2[indexF+i];
						avg = avg+tmp;
					}
					avg=avg/(quota+1);
					tmparr[indexF]=avg;
				}

				for(int i=0;i<_Frame;i++){
					img3DArray[i][indexS][indexP]=tmparr[i];

				}
			}
		}
	}

	public void normalize(){
		//모든 slice의 pixel에 대해 원본 소스 time series의 baseline 평균값 (2중 array)
		float[][] baseAVG2DArray=new float[_Slice][_W*_H];
		for(int indexS=0;indexS<_Slice;indexS++){
			float[] baselineAVG= new float[_W*_H];
			for(int indexF=baseStart;indexF<baseEnd+1;indexF++){	
				float imsipixel[]=img3DArray[indexF][indexS];
				baselineAVG = arraySum(imsipixel,baselineAVG);
			}
			baselineAVG = baseAVG(baselineAVG);
			baseAVG2DArray[indexS]=baselineAVG;
		}

		//		finalnormalize =new float[_Frame][_Slice][_W*_H];
		//모든 slice와 time series에 대해 원본 소스값에서 baseline 평균값(baseAVG2DArray[][])을 나눈 값(3중 array)
//		float[][][] baseDevide3D = new float[_Frame][_Slice][_W*_H];		
		for(int indexS=0;indexS<_Slice;indexS++){
			float[] pixelres = new float[_W*_H];
			for(int indexF=0;indexF<_Frame;indexF++){	
				float imsipixel[]=img3DArray[indexF][indexS];
				pixelres=arrayNormalize(imsipixel,baseAVG2DArray[indexS]);
				img3DArray[indexF][indexS]=pixelres;
				IJ.showStatus("Normalization: "+indexS+"/"+_Slice);
			}
		}
	}

	public void outlier(){

		init3DArray = false;
		//		outlier3D=img3DArray;
		outlierOn=true;
		Roi roi = plotWindow.imp.getRoi();
		if (roi!=null && !roi.isArea()) roi = null;
		ImageProcessor ip = plotWindow.imp.getProcessor();
		ImageProcessor mask = roi!=null?roi.getMask():null;
		Rectangle r = roi!=null?roi.getBounds():new Rectangle(0,0,ip.getWidth(),ip.getHeight());
		for(int i = 0;i<_Frame;i++){
			if(xCoordi[i]>=r.x&&xCoordi[i]<=r.x+r.width&&yCoordi[i]>=r.y&&yCoordi[i]<=r.y+r.height&&(mask==null)){
				for(int indexS=0;indexS<_Slice;indexS++){
					for(int indexP=0;indexP<_W*_H;indexP++){
						if(i>=2&&i<=117){
							img3DArray[i][indexS][indexP]=(img3DArray[i-2][indexS][indexP]+img3DArray[i-1][indexS][indexP]+img3DArray[i+1][indexS][indexP]+img3DArray[i+2][indexS][indexP])/4;
						}else if(i<2){
							img3DArray[i][indexS][indexP]=(img3DArray[i+2][indexS][indexP]+img3DArray[i+3][indexS][indexP])/2;
						}else if(i>117){
							img3DArray[i][indexS][indexP]=(img3DArray[i-2][indexS][indexP]+img3DArray[i-3][indexS][indexP])/2;
						}
					}
				}
			}
		}

		plotMousePoint_smoothing(lastArrayNum);
	}

	public void initData(){
		outlier3D=new float[_Frame][_Slice][_W*_H];
		for(int indexF=0;indexF<_Frame;indexF++){
			for(int indexS=0;indexS<_Slice;indexS++){
				for(int indexP=0;indexP<_W*_H;indexP++){
					outlier3D[indexF][indexS][indexP]=img3DArray[indexF][indexS][indexP];

				}
			}
		}
	}

	public void outlierReset(){
		ImageStack stack1 = imp.getStack(); 

		for (int indexF=0;indexF<_Frame;indexF++){
			for (int indexS=0;indexS<_Slice;indexS++){
				float[] tmpPixels = (float[]) stack1.getPixels(indexF*_Slice+(indexS+1));
				for(int indexH=0;indexH<_H;indexH++){
					for(int indexW=0;indexW<_W;indexW++){
						img3DArray[indexF][indexS][indexH*_W+indexW]=tmpPixels[indexH*_W+indexW];
					}
				}
			}
		}
		plotMousePoint_smoothing(lastArrayNum);
	}

	String filePath(){
		URL url = getClass().getResource("/ij/IJ.class");
		String cPath = url == null ? null : url.toString().replaceAll("%20", " ");
		int firstIdx = cPath.indexOf("file:/");
		int lastIdx = cPath.indexOf("ij.jar!");
		String lut_path;
		if (lastIdx == -1 || lastIdx >= cPath.length()) {
			lastIdx = cPath.indexOf("/bin/ij/IJ.class");
			lut_path = ""+cPath.substring(firstIdx+6,lastIdx)+"\\16_Colors.lut";
		} else {
			lut_path = ""+cPath.substring(firstIdx+6,lastIdx-1)+"\\luts\\16_Colors.lut";
		}
		return lut_path;
	}

}

class LiveCurveFitter1 {
	public static final int STRAIGHT_LINE=0,POLY2=1,POLY3=2,POLY4=3,
			EXPONENTIAL=4,POWER=5,LOG=6,RODBARD=7,GAMMA_VARIATE=8;
	public static final int T1_SAT_RELAX = 9;
	public static final int T2_DEPHASE = 10;
	public static final int DIFFUSION = 11;
	public static final int IVIM = 12;
	public static final int IterFactor = 2000;

	public static final String[] fitList = {"Straight Line","2nd Degree Polynomial",
		"3rd Degree Polynomial", "4th Degree Polynomial","Exponential","Power",
		"log","Rodbard", "Gamma Variate"};

	public static final String[] fList = {"y = a+bx","y = a+bx+cx^2",
		"y = a+bx+cx^2+dx^3", "y = a+bx+cx^2+dx^3+ex^4","y = a*exp(bx)","y = ax^b",
		"y = a*ln(bx)","y = c*((a-x)/(x-d))^(1/b)", "y = a*(x-b)^c*exp(-(x-b)/d)", "y=a*(1-exp(-x/b))"};

	private static final double alpha = -1.0;     // reflection coefficient
	private static final double beta = 0.5;   // contraction coefficient
	private static final double gamma = 2.0;      // expansion coefficient
	private static final double root2 = 1.414214; // square root of 2

	private int fit;                // Number of curve type to fit
	private double[] xData, yData;  // x,y data to fit
	private int numPoints;          // number of data points
	private int numParams;          // number of parametres
	private int numVertices;        // numParams+1 (includes sumLocalResiduaalsSqrd)
	private int worst;          // worst current parametre estimates
	private int nextWorst;      // 2nd worst current parametre estimates
	private int best;           // best current parametre estimates
	private double[][] simp;        // the simplex (the last element of the array at each vertice is the sum of the square of the residuals)
	private double[] next;      // new vertex to be tested
	private int numIter;        // number of iterations so far
	private int maxIter;    // maximum number of iterations per restart
	private int restarts;   // number of times to restart simplex after first soln.
	private double maxError;     // maximum error tolerance

	//SWH
	private int _fitType=0;

	/** Construct a new T1T2CurveFitter. */
	public LiveCurveFitter1 (double[] xData, double[] yData) {
		this.xData = xData;
		this.yData = yData;
		numPoints = xData.length;
	}

	/**  Perform curve fitting with the simplex method
	 *          doFit(fitType) just does the fit
	 *          doFit(fitType, true) pops up a dialog allowing control over simplex parameters
	 *      alpha is reflection coefficient  (-1)
	 *      beta is contraction coefficient (0.5)
	 *      gamma is expansion coefficient (2)
	 */
	public void doFit(int fitType) {
		_fitType=fitType;
		doFit(fitType, false);
	}

	public void doFit(int fitType, boolean showSettings) {
		_fitType=fitType;
		if (fitType < STRAIGHT_LINE || fitType > IVIM)
			throw new IllegalArgumentException("Invalid fit type");
		fit = fitType;
		initialize();
		if (showSettings) settingsDialog();
		restart(0);

		numIter = 0;
		boolean done = false;
		double[] center = new double[numParams];  // mean of simplex vertices
		while (!done) {
			numIter++;
			for (int i = 0; i < numParams; i++) center[i] = 0.0;
			// get mean "center" of vertices, excluding worst
			for (int i = 0; i < numVertices; i++)
				if (i != worst)
					for (int j = 0; j < numParams; j++)
						center[j] += simp[i][j];
			// Reflect worst vertex through centre
			for (int i = 0; i < numParams; i++) {
				center[i] /= numParams;
				next[i] = center[i] + alpha*(simp[worst][i] - center[i]);
			}
			sumResiduals(next);
			// if it's better than the best...
			if (next[numParams] <= simp[best][numParams]) {
				newVertex();
				// try expanding it
				for (int i = 0; i < numParams; i++)
					next[i] = center[i] + gamma * (simp[worst][i] - center[i]);
				sumResiduals(next);
				// if this is even better, keep it
				if (next[numParams] <= simp[worst][numParams])
					newVertex();
			}
			// else if better than the 2nd worst keep it...
			else if (next[numParams] <= simp[nextWorst][numParams]) {
				newVertex();
			}
			// else try to make positive contraction of the worst
			else {
				for (int i = 0; i < numParams; i++)
					next[i] = center[i] + beta*(simp[worst][i] - center[i]);
				sumResiduals(next);
				// if this is better than the second worst, keep it.
				if (next[numParams] <= simp[nextWorst][numParams]) {
					newVertex();
				}
				// if all else fails, contract simplex in on best
				else {
					for (int i = 0; i < numVertices; i++) {
						if (i != best) {
							for (int j = 0; j < numVertices; j++)
								simp[i][j] = beta*(simp[i][j]+simp[best][j]);
							sumResiduals(simp[i]);
						}
					}
				}
			}
			order();

			double rtol = 2 * Math.abs(simp[best][numParams] - simp[worst][numParams]) /
					(Math.abs(simp[best][numParams]) + Math.abs(simp[worst][numParams]) + 0.0000000001);

			if (numIter >= maxIter) done = true;
			else if (rtol < maxError) {
				//System.out.print(getResultString());
				restarts--;
				if (restarts < 0) {
					done = true;
				}
				else {
					restart(best);
				}
			}
		}
	}

	/** Initialise the simplex
	 */
	void initialize() {
		// Calculate some things that might be useful for predicting parametres
		numParams = getNumParams();
		numVertices = numParams + 1;      // need 1 more vertice than parametres,
		simp = new double[numVertices][numVertices];
		next = new double[numVertices];

		double firstx = xData[0];
		double firsty = yData[0];
		double lastx = xData[numPoints-1];
		double lasty = yData[numPoints-1];
		double xmean = (firstx+lastx)/2.0;
		double ymean = (firsty+lasty)/2.0;

		//SWH
		double _Dinit=0.001;
		double _DSinit=0.01;
		for (int i=0;i<xData.length;i++){
			if(xData[i]>30){
				if(yData[0] <=0 && yData[yData.length-1]<=0 && yData[i]<=0 )
					break;
				_Dinit= -1*(Math.log(yData[yData.length-1])-Math.log(yData[i]))/(xData[xData.length-1]-xData[i]);
				_DSinit= -1*(Math.log(yData[i-1])-Math.log(yData[0]))/(xData[i-1]-xData[0]);

				if (_Dinit<0||_DSinit<0||_Dinit>_DSinit){
					_Dinit=0.001;
					_DSinit=0.01;
				}

				break;
			}
		}

		if (_fitType ==IVIM){

		}

		double slope;
		if ((lastx - firstx) != 0.0)
			slope = (lasty - firsty)/(lastx - firstx);
		else
			slope = 1.0;
		double yintercept = firsty - slope * firstx;
		maxIter = IterFactor * numParams * numParams;  // Where does this estimate come from?
		restarts = 1;
		maxError = 1e-9;
		switch (fit) {
		case STRAIGHT_LINE:
			simp[0][0] = yintercept;
			simp[0][1] = slope;
			break;
		case POLY2:
			simp[0][0] = yintercept;
			simp[0][1] = slope;
			simp[0][2] = 0.0;
			break;
		case POLY3:
			simp[0][0] = yintercept;
			simp[0][1] = slope;
			simp[0][2] = 0.0;
			simp[0][3] = 0.0;
			break;
		case POLY4:
			simp[0][0] = yintercept;
			simp[0][1] = slope;
			simp[0][2] = 0.0;
			simp[0][3] = 0.0;
			simp[0][4] = 0.0;
			break;
		case EXPONENTIAL:
			simp[0][0] = 0.1;
			simp[0][1] = 0.01;
			break;
		case POWER:
			simp[0][0] = 0.0;
			simp[0][1] = 1.0;
			break;
		case LOG:
			simp[0][0] = 0.5;
			simp[0][1] = 0.05;
			break;
		case RODBARD:
			simp[0][0] = firsty;
			simp[0][1] = 1.0;
			simp[0][2] = xmean;
			simp[0][3] = lasty;
			break;
		case T1_SAT_RELAX:
			simp[0][0] = firsty;
			simp[0][1] = 1.0;
			break;
		case T2_DEPHASE:
			simp[0][0] = firsty;
			simp[0][1] = 1.0;
			break;
		case DIFFUSION:
			simp[0][0] = firsty;
			simp[0][1] = 0.0005;
			break;
		case IVIM:
			simp[0][0] = firsty;
			simp[0][1] = 0.2;
			simp[0][2] = _Dinit;
			simp[0][3] = _DSinit;
			break;
		case GAMMA_VARIATE:
			//  First guesses based on following observations:
			//  t0 [b] = time of first rise in gamma curve - so use the user specified first limit
			//  tm = t0 + a*B [c*d] where tm is the time of the peak of the curve
			//  therefore an estimate for a and B is sqrt(tm-t0)
			//  K [a] can now be calculated from these estimates
			simp[0][0] = firstx;
			double ab = xData[getMax(yData)] - firstx;
			simp[0][2] = Math.sqrt(ab);
			simp[0][3] = Math.sqrt(ab);
			simp[0][1] = yData[getMax(yData)] / (Math.pow(ab, simp[0][2]) * Math.exp(-ab/simp[0][3]));
			break;
		}
	}

	/** Pop up a dialog allowing control over simplex starting parameters */
	private void settingsDialog() {
		GenericDialog gd = new GenericDialog("Simplex Fitting Options", IJ.getInstance());
		gd.addMessage("Function name: " + fitList[fit] + "\n" +
				"Formula: " + fList[fit]);
		char pChar = 'a';
		for (int i = 0; i < numParams; i++) {
			gd.addNumericField("Initial "+(new Character(pChar)).toString()+":", simp[0][i], 2);
			pChar++;
		}
		gd.addNumericField("Maximum iterations:", maxIter, 0);
		gd.addNumericField("Number of restarts:", restarts, 0);
		gd.addNumericField("Error tolerance [1*10^(-x)]:", -(Math.log(maxError)/Math.log(10)), 0);
		gd.showDialog();
		if (gd.wasCanceled() || gd.invalidNumber()) {
			IJ.error("Parameter setting canceled.\nUsing default parameters.");
		}
		// Parametres:
		for (int i = 0; i < numParams; i++) {
			simp[0][i] = gd.getNextNumber();
		}
		maxIter = (int) gd.getNextNumber();
		restarts = (int) gd.getNextNumber();
		maxError = Math.pow(10.0, -gd.getNextNumber());
	}

	/** Restart the simplex at the nth vertex */
	void restart(int n) {
		// Copy nth vertice of simplex to first vertice
		for (int i = 0; i < numParams; i++) {
			simp[0][i] = simp[n][i];
		}
		sumResiduals(simp[0]);          // Get sum of residuals^2 for first vertex
		double[] step = new double[numParams];
		for (int i = 0; i < numParams; i++) {
			step[i] = simp[0][i] / 2.0;     // Step half the parametre value
			if (step[i] == 0.0)             // We can't have them all the same or we're going nowhere
				step[i] = 0.01;
		}
		// Some kind of factor for generating new vertices
		double[] p = new double[numParams];
		double[] q = new double[numParams];
		for (int i = 0; i < numParams; i++) {
			p[i] = step[i] * (Math.sqrt(numVertices) + numParams - 1.0)/(numParams * root2);
			q[i] = step[i] * (Math.sqrt(numVertices) - 1.0)/(numParams * root2);
		}
		// Create the other simplex vertices by modifing previous one.
		for (int i = 1; i < numVertices; i++) {
			for (int j = 0; j < numParams; j++) {
				simp[i][j] = simp[i-1][j] + q[j];
			}
			simp[i][i-1] = simp[i][i-1] + p[i-1];
			sumResiduals(simp[i]);
		}
		// Initialise current lowest/highest parametre estimates to simplex 1
		best = 0;
		worst = 0;
		nextWorst = 0;
		order();
	}

	// Display simplex [Iteration: s0(p1, p2....), s1(),....] in ImageJ window
	void showSimplex(int iter) {
		ij.IJ.write("" + iter);
		for (int i = 0; i < numVertices; i++) {
			String s = "";
			for (int j=0; j < numVertices; j++)
				s += "  "+ ij.IJ.d2s(simp[i][j], 6);
			ij.IJ.write(s);
		}
	}

	/** Get number of parameters for current fit function */
	public int getNumParams() {
		switch (fit) {
		case STRAIGHT_LINE: return 2;
		case POLY2: return 3;
		case POLY3: return 4;
		case POLY4: return 5;
		case EXPONENTIAL: return 2;
		case POWER: return 2;
		case LOG: return 2;
		case T1_SAT_RELAX: return 2;
		case T2_DEPHASE: return 2;
		case DIFFUSION: return 2;
		case IVIM: return 4;
		case RODBARD: return 4;
		case GAMMA_VARIATE: return 4;
		}
		return 0;
	}

	/** Returns "fit" function value for parametres "p" at "x" */
	public static double f(int fit, double[] p, double x) {
		switch (fit) {
		case STRAIGHT_LINE:
			return p[0] + p[1]*x;
		case POLY2:
			return p[0] + p[1]*x + p[2]* x*x;
		case POLY3:
			return p[0] + p[1]*x + p[2]*x*x + p[3]*x*x*x;
		case POLY4:
			return p[0] + p[1]*x + p[2]*x*x + p[3]*x*x*x + p[4]*x*x*x*x;
		case EXPONENTIAL:
			return p[0]*Math.exp(p[1]*x);
		case T1_SAT_RELAX:
			return p[0]*(1 - Math.exp(-(x / p[1])));
		case T2_DEPHASE:
			return p[0]*Math.exp(-(x / p[1]));
		case DIFFUSION:
			return p[0]*Math.exp(-x * p[1]);
		case IVIM:
			return p[0]*((1-p[1])*Math.exp(-x * p[2])+ p[1]*Math.exp(-x*p[3]));
		case POWER:
			if (x == 0.0)
				return 0.0;
			else
				return p[0]*Math.exp(p[1]*Math.log(x)); //y=ax^b
		case LOG:
			if (x == 0.0)
				x = 0.5;
			return p[0]*Math.log(p[1]*x);
		case RODBARD:
			double ex;
			if (x == 0.0)
				ex = 0.0;
			else
				ex = Math.exp(Math.log(x/p[2])*p[1]);
			double y = p[0]-p[3];
			y = y/(1.0+ex);
			return y+p[3];
		case GAMMA_VARIATE:
			if (p[0] >= x) return 0.0;
			if (p[1] <= 0) return -100000.0;
			if (p[2] <= 0) return -100000.0;
			if (p[3] <= 0) return -100000.0;

			double pw = Math.pow((x - p[0]), p[2]);
			double e = Math.exp((-(x - p[0]))/p[3]);
			return p[1]*pw*e;
		default:
			return 0.0;
		}
	}

	/** Get the set of parameter values from the best corner of the simplex */
	public double[] getParams() {
		order();
		return simp[best];
	}

	/** Returns residuals array ie. differences between data and curve */
	public double[] getResiduals() {
		double[] params = getParams();
		double[] residuals = new double[numPoints];
		for (int i = 0; i < numPoints; i++)
			residuals[i] = yData[i] - f(fit, params, xData[i]);
		return residuals;
	}

	/* Last "parametre" at each vertex of simplex is sum of residuals
	 * for the curve described by that vertex
	 */
	public double getSumResidualsSqr() {
		double sumResidualsSqr = (getParams())[getNumParams()];
		return sumResidualsSqr;
	}

	/**  SD = sqrt(sum of residuals squared / number of params+1)
	 */
	public double getSD() {
		double sd = Math.sqrt(getSumResidualsSqr() / numVertices);
		return sd;
	}

	/**  Get a measure of "goodness of fit" where 1.0 is best.
	 *
	 */
	public double getFitGoodness() {
		double sumY = 0.0;
		for (int i = 0; i < numPoints; i++) sumY += yData[i];
		double mean = sumY / numVertices;
		double sumMeanDiffSqr = 0.0;
		int degreesOfFreedom = numPoints - getNumParams();
		double fitGoodness = 0.0;
		for (int i = 0; i < numPoints; i++) {
			sumMeanDiffSqr += sqr(yData[i] - mean);
		}
		if (sumMeanDiffSqr > 0.0 && degreesOfFreedom != 0)
			fitGoodness = 1.0 - (getSumResidualsSqr() / degreesOfFreedom) * ((numParams) / sumMeanDiffSqr);

		return fitGoodness;
	}

	/** Get a string description of the curve fitting results
	 * for easy output.
	 */
	public String getResultString() {
		StringBuffer results = new StringBuffer("\nNumber of iterations: " + getIterations() +
				"\nMaximum number of iterations: " + getMaxIterations() +
				"\nSum of residuals squared: " + getSumResidualsSqr() +
				"\nStandard deviation: " + getSD() +
				"\nGoodness of fit: " + getFitGoodness() +
				"\nParameters:");
		char pChar = 'a';
		double[] pVal = getParams();
		for (int i = 0; i < numParams; i++) {
			results.append("\n" + pChar + " = " + pVal[i]);
			pChar++;
		}
		return results.toString();
	}

	double sqr(double d) { return d * d; }

	/** Adds sum of square of residuals to end of array of parameters */
	void sumResiduals (double[] x) {
		x[numParams] = 0.0;
		for (int i = 0; i < numPoints; i++) {
			x[numParams] = x[numParams] + sqr(f(fit,x,xData[i])-yData[i]);
			//        if (IJ.debugMode) ij.IJ.log(i+" "+x[n-1]+" "+f(fit,x,xData[i])+" "+yData[i]);
		}
	}

	/** Keep the "next" vertex */
	void newVertex() {
		for (int i = 0; i < numVertices; i++)
			simp[worst][i] = next[i];
	}

	/** Find the worst, nextWorst and best current set of parameter estimates */
	void order() {
		for (int i = 0; i < numVertices; i++) {
			if (simp[i][numParams] < simp[best][numParams]) best = i;
			if (simp[i][numParams] > simp[worst][numParams]) worst = i;
		}
		nextWorst = best;
		for (int i = 0; i < numVertices; i++) {
			if (i != worst) {
				if (simp[i][numParams] > simp[nextWorst][numParams]) nextWorst = i;
			}
		}
	}

	/** Get number of iterations performed */
	public int getIterations() {
		return numIter;
	}

	/** Get maximum number of iterations allowed */
	public int getMaxIterations() {
		return maxIter;
	}

	/** Set maximum number of iterations allowed */
	public void setMaxIterations(int x) {
		maxIter = x;
	}

	/** Get number of simplex restarts to do */
	public int getRestarts() {
		return restarts;
	}

	/** Set number of simplex restarts to do */
	public void setRestarts(int x) {
		restarts = x;
	}

	/**
	 * Gets index of highest value in an array.
	 * 
	 * @param              Double array.
	 * @return             Index of highest value.
	 */
	public static int getMax(double[] array) {
		double max = array[0];
		int index = 0;
		for(int i = 1; i < array.length; i++) {
			if(max < array[i]) {
				max = array[i];
				index = i;
			}
		}
		return index;
	}

}

class AxisDialog1 extends Frame implements  ActionListener, Runnable, WindowListener{ //, ItemListener{
	public static int GAP = 0;
	protected ImagePlus imp;
	protected Button axisBtn, fittingBtn, loadBtn;
	protected Choice equList;
	public boolean done = false;
	public int fitterChoice;
	protected Thread thread;
	public double currentMax;
	public double currentMin;
	public boolean  autoRange;
	public  double [] xxx;

	public AxisDialog1(ImagePlus imp){
		super("LivePlot Menu");
		addWindowListener(this);
		this.imp = imp;
		setup();
		fitterChoice=-1;
		currentMax=Float.MIN_VALUE;
		currentMin=Float.MAX_VALUE;
		autoRange=true;
		this.setResizable(false);

		thread = new Thread(this, "MeasureStack");
		thread.start();

	}


	public void setup() {
		setLayout(new FlowLayout(FlowLayout.CENTER));
		//setLayout(new BorderLayout(3,3));


		//		equList=new Choice();
		//		equList.add("-- Select Equation --");
		//		equList.add("ADC: a*EXP(-B*b)");
		//		equList.add("T1: a(1-EXP(-TR/b)");
		//		equList.add("T2/T2*: aEXP(-TE/b)");
		//		equList.add("IVIM");

		axisBtn = new Button("Axis range");
		axisBtn.addActionListener(this);



		fittingBtn=new Button("Fitting model");
		fittingBtn.addActionListener(this);

		//review = new Button("Review areas");
		//review.addActionListener(this);
		loadBtn = new Button("Load X vals");
		loadBtn.addActionListener(this);

		//		equList.addItemListener (this);

		//		add(equList);
		//		add(loadBtn);
		add(axisBtn);
		pack();
		show();
	}



	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		shutDown();
	}
	public void shutDown(){
		done = true;
		setVisible(false);
		dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		while (!done) {
			try {Thread.sleep(20);}
			catch(InterruptedException e) {}
			//			this.toFront();			
			if (WindowManager.getWindow(imp.getTitle())==null){
				shutDown();
			}else{
//				this.setLocation(imp.getWindow().getLocation().x+imp.getWindow().getWidth()+GAP,imp.getWindow().getLocation().y-this.getHeight()-GAP);
				this.setLocation(WindowManager.getWindow("_Plot_"+imp.getTitle()).getLocation().x+WindowManager.getWindow("_Plot_"+imp.getTitle()).getWidth(), this.imp.getWindow().getY());
			}

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		Object b = e.getSource();
		if (b == axisBtn){

			String tmpxxxString=""+xxx[0];
			for (int i=1;i<xxx.length;i++)
				tmpxxxString+=" "+xxx[i];
			GenericDialog gd = new GenericDialog("Axis Setting");
			gd.addMessage("X values:");
			gd.addStringField("           ",tmpxxxString, 30);
			gd.addMessage("Y min:");
			gd.addNumericField("          ", currentMin, 10);
			gd.addMessage("Y max:");
			gd.addNumericField("          ", currentMax, 10);
			gd.addCheckbox("Auto Axis", false);

			gd.showDialog();
			if (gd.wasCanceled())
				return;

			String xValues = gd.getNextString();
			double ymin = gd.getNextNumber();
			double ymax = gd.getNextNumber();
			currentMax=ymax;
			currentMin=ymin;
			if (gd.getNextBoolean())
			{
				autoRange=true;
			}
			else
			{
				autoRange=false;
			}

		} else if (b == loadBtn){
			FileDialog fileOpen= new FileDialog(this,"Open Preset",FileDialog.LOAD);
			fileOpen.setDirectory("c:\\Preset");
			fileOpen.setFile("*.txt");
			fileOpen.setVisible(true);


			String filename = fileOpen.getFile();
			if (filename == null){

			}
			else{
				try{
					String xvalStr = readFile(fileOpen.getDirectory()+fileOpen.getFile());
					StringTokenizer xvalsTokenize = new StringTokenizer (xvalStr, " ");
					Vector xvalVec = new Vector();

					while (xvalsTokenize.hasMoreTokens()) {
						try {
							xvalVec.addElement(new Float(xvalsTokenize.nextToken()));
						} catch (Exception eTok) { 
							JOptionPane.showMessageDialog(null, "Parsing Error");
						}
					}
					if(xvalVec.size()==xxx.length){
						imp.xvalStr=xvalStr;
						for(int i=0;i<xxx.length;i++){
							xxx[i]=((Float)xvalVec.get(i)).doubleValue();
						}
					}
					else{

						JOptionPane.showMessageDialog(null, "X-length's not matched with img's length");
					}
				}
				catch(Exception fileExc){
					JOptionPane.showMessageDialog(null, "Error");
				}
			}
		} 
	}

	String readFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}


	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
}



