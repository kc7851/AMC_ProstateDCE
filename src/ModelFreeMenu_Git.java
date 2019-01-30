import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.NewImage;
import ij.gui.PlotWindow;
import ij.gui.Roi;
import ij.gui.TextRoi;
import ij.plugin.DragAndDrop;
import ij.plugin.Duplicator;
import ij.process.ImageProcessor;

import java.awt.EventQueue;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import java.awt.Button;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.Label;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;

import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;

public class ModelFreeMenu_Git extends JFrame implements WindowListener, Runnable {

	public static int GAP = 0;
	protected ImagePlus imp;
	protected PlotWindow plotWindow;
	public boolean done = false;
	protected Thread thread;
	public double currentMax;
	public double currentMin;
	public boolean autoRange;
	public double[] xxx;

	public TextField baserangenum1, baserangenum2, timeofarrivalnum, temporalsmoonum, auc1num1, auc1num2, auc2num1,
	auc2num2, washinnum1,
	washinnum2;
	public Button baserangebtn, timeofarrivalbtn, auc1btn, auc2btn, washinbtn, processbtn;
	public Checkbox baserangeAutocheck, timeofarrivalAutocheck, temporalsmoocheck, auc1check, auc2check, washincheck, peicheck;
	public Choice normalizationchoice;
	// public JCheckBox ;

	public int roiChoice;
	private int xcoordi;
	private int ycoordi;
	private int pwwidth;

	int _W;
	int _H;


	AMC_DCEmodelFree_Git amc_dce;
	AxisDialog1 axisDialog;
	// TemporalSmoothing ts;
	Roi baseRoi, taRoi, auc1Roi, auc2Roi, auc3Roi, auc4Roi, washinRoi,
	washoutRoi, sub1Roi, sub2Roi;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the application.
	 */
	public ModelFreeMenu_Git() {
		super("ModelFree Menu");
		initialize();
	}

	// public void setBaserangenum2(String _input){
	// baserangenum2.setText(""+_input);
	// }
	public ModelFreeMenu_Git(ImagePlus imp, AMC_DCEmodelFree_Git _amc_dce) {
		super("ModelFree Menu");
		this.amc_dce = _amc_dce;
		this.pwwidth = amc_dce.plotWindow.getWidth();
		this.imp = imp;
		_W=imp.getWidth();
		_H=imp.getHeight();

		initialize();
		roiChoice = 0;
		currentMax = Float.MIN_VALUE;
		currentMin = Float.MAX_VALUE;
		autoRange = true;
//		System.out.println(imp.getWindow().getLocation().x + " "+ imp.getWindow().getWidth());
		thread = new Thread(this, "MeasureStack");
		thread.start();

	}

	public void run() {
		try {
			this.setVisible(true);
			this.setResizable(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (!done) {
			xcoordi = imp.getWindow().getLocation().x;
			ycoordi = imp.getWindow().getLocation().y
					+ imp.getWindow().getHeight();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
			// this.toFront();
			if (WindowManager.getWindow(imp.getTitle()) == null) {
				shutDown();
			} else {
				// frame.setLocation(imp.getWindow().getLocation().x,imp.getWindow().getLocation().y+imp.getWindow().getHeight());
				//				this.setBounds(imp.getWindow().getLocation().x + imp.getWindow().getWidth() , imp.getWindow().getLocation().y, 268, 309);
//				this.setBounds(imp.getWindow().getLocation().x + imp.getWindow().getWidth() , amc_dce.plotWindow.getLocation().y-this.getHeight(), 268, 340); //skc
				this.setBounds(imp.getWindow().getLocation().x + imp.getWindow().getWidth() , imp.getWindow().getLocation().y+amc_dce.plotWindow.getHeight(), 550, 190); //Sab
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setBounds(100, 100, 550, 192);
		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);

		baserangebtn = new Button("Base range");
		baserangebtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (baserangebtn.getForeground() == Color.red) {
					baserangebtn.setForeground(Color.black);
					amc_dce.plotWindow.imp.killRoi();
					baseRoi = null;
				} else {

					roiChoice = 1;
					baserangebtn.setForeground(Color.RED);
					timeofarrivalbtn.setForeground(Color.black);
					auc1btn.setForeground(Color.black);
					auc2btn.setForeground(Color.black);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.black);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);

					float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.baseStart));
					float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.baseEnd - amc_dce.baseStart));
					Rectangle rect = new Rectangle((int) startxoff, 15,
							(int) roiwidth, 200);
					// 120frame=450width //1-20=72 1frame = 3.6 width //21-40=75
					// 1frame = 3.75 //41-60=76 1frame = 3.8 //61-80=76 1frame =
					// 3.8 //81-100=75 1frame = 3.75 //101-120=76 1frame = 3.8
					baseRoi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					// baseRoi= new Roi(rect);
					// baseRoi.setImage(amc_dce.plotWindow.imp);
					// baseRoi.setNonScalable(false);
					// System.out.println("width: "+baseRoi.getBounds().getWidth()+" x :"+baseRoi.getXBase()+" "+baseRoi.getBounds().getX());
					// System.out.println("start:"+(int)startxoff+" width :"+(int)roiwidth);
					// System.out.println("start:"+startxoff+" width :"+roiwidth);
					// System.out.println("bouns:"+baseRoi);
					amc_dce.plotWindow.imp.setRoi(baseRoi);

					// amc_dce.imp.setRoi(baseRoi,true);
				}
			}
		});
		baserangebtn.setForeground(Color.black);
		baserangebtn.setBounds(10, 10, 109, 23);
		this.getContentPane().add(baserangebtn);

		baserangenum1 = new TextField();
		baserangenum1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					amc_dce.baseStart = Integer.parseInt(baserangenum1
							.getText()) - 1;
					if (amc_dce.baseStart < 0) {
						amc_dce.baseStart = 0;
						baserangenum1.setText("" + (amc_dce.baseStart + 1));
					}
					if (amc_dce.baseStart > amc_dce._Frame - 1) {
						amc_dce.baseStart = amc_dce._Frame - 1;
						baserangenum1.setText("" + amc_dce._Frame);
					}
					baserangeAutocheck.setState(false);

					float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.baseStart));
					float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.baseEnd - amc_dce.baseStart));
					baseRoi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(baseRoi);

					roiChoice = 1;
					baserangebtn.setForeground(Color.RED);
					timeofarrivalbtn.setForeground(Color.black);
					auc1btn.setForeground(Color.black);
					auc2btn.setForeground(Color.black);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.black);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);

				} catch (NumberFormatException e1) {
				}
				;

			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((int) e.getKeyChar() == 48 || (int) e.getKeyChar() == 49
						|| (int) e.getKeyChar() == 50
						|| (int) e.getKeyChar() == 51
						|| (int) e.getKeyChar() == 52
						|| (int) e.getKeyChar() == 53
						|| (int) e.getKeyChar() == 54
						|| (int) e.getKeyChar() == 55
						|| (int) e.getKeyChar() == 56
						|| (int) e.getKeyChar() == 57) {

				} else {
					e.consume();
				}

			}
		});
		baserangenum1.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent arg0) {
			}
		});
		baserangenum1.setBounds(125, 10, 24, 23);

		this.getContentPane().add(baserangenum1);

		Label label = new Label("~");
		label.setBounds(153, 10, 9, 24);
		this.getContentPane().add(label);

		baserangenum2 = new TextField();
		baserangenum2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					amc_dce.baseEnd = Integer.parseInt(baserangenum2.getText()) - 1;
					if (amc_dce.baseEnd < 0) {
						amc_dce.baseEnd = 0;
						baserangenum2.setText("" + (amc_dce.baseEnd + 1));
					}
					if (amc_dce.baseEnd > amc_dce._Frame - 1) {
						amc_dce.baseEnd = amc_dce._Frame - 1;
						baserangenum2.setText("" + amc_dce._Frame);
					}
					baserangeAutocheck.setState(false);

					float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.baseStart));
					float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.baseEnd - amc_dce.baseStart));
					baseRoi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(baseRoi);

					roiChoice = 1;
					baserangebtn.setForeground(Color.RED);
					timeofarrivalbtn.setForeground(Color.black);
					auc1btn.setForeground(Color.black);
					auc2btn.setForeground(Color.black);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.black);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);
				} catch (NumberFormatException e1) {
				}
				;

				// if(amc_dce.baseEnd<0)amc_dce.baseEnd=0;
				// baserangenum2.setText(""+(amc_dce.baseEnd+1));
				// if(amc_dce.baseEnd>amc_dce._Frame-1)amc_dce.baseEnd=amc_dce._Frame-1;
				// baserangenum2.setText(""+amc_dce._Frame);
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((int) e.getKeyChar() == 48 || (int) e.getKeyChar() == 49
						|| (int) e.getKeyChar() == 50
						|| (int) e.getKeyChar() == 51
						|| (int) e.getKeyChar() == 52
						|| (int) e.getKeyChar() == 53
						|| (int) e.getKeyChar() == 54
						|| (int) e.getKeyChar() == 55
						|| (int) e.getKeyChar() == 56
						|| (int) e.getKeyChar() == 57) {

				} else {
					e.consume();
				}

			}
		});
		baserangenum2.setBounds(170, 10, 24, 23);
		this.getContentPane().add(baserangenum2);

		baserangeAutocheck = new Checkbox("Auto", true);
		baserangeAutocheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					baserangenum1.setText("" + (amc_dce.initialbaseStart + 1));
					baserangenum2.setText("" + (amc_dce.initialbaseEnd + 1));
					amc_dce.baseStart = amc_dce.initialbaseStart;
					amc_dce.baseEnd = amc_dce.initialbaseEnd;
					baserangebtn.setForeground(Color.red);
					float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.baseStart));
					float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.baseEnd - amc_dce.baseStart));
					baseRoi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(baseRoi);
				} else {
					baserangenum1.setText("");
					baserangenum2.setText("");
				}
			}
		});
		baserangeAutocheck.setBounds(200, 10, 52, 23);
		this.getContentPane().add(baserangeAutocheck);
		// baserangeAutocheck.setSelected(true);

		timeofarrivalAutocheck = new Checkbox("Auto", true);
		timeofarrivalAutocheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					timeofarrivalnum.setText(""
							+ (amc_dce.initialtimeOfArrival + 1));
					amc_dce.timeOfArrival = amc_dce.initialtimeOfArrival;
					timeofarrivalbtn.setForeground(Color.red);
					float startxoff = (float) (60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.timeOfArrival - 0.5)));
					float roiwidth = (450.0f / (amc_dce._Frame - 1));
					taRoi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(taRoi);
				} else {
					timeofarrivalnum.setText("");

				}
			}
		});
		timeofarrivalAutocheck.setBounds(200, 39, 52, 23);
		this.getContentPane().add(timeofarrivalAutocheck);
		// timeofarrivalAutocheck.setSelected(true);

		timeofarrivalnum = new TextField();
		timeofarrivalnum.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					amc_dce.timeOfArrival = Integer.parseInt(timeofarrivalnum
							.getText()) - 1;
					if (amc_dce.timeOfArrival < 0) {
						amc_dce.timeOfArrival = 0;
						timeofarrivalnum.setText(""
								+ (amc_dce.timeOfArrival + 1));
					}
					if (amc_dce.timeOfArrival > amc_dce._Frame - 1) {
						amc_dce.timeOfArrival = amc_dce._Frame - 1;
						timeofarrivalnum.setText("" + amc_dce._Frame);
					}
					timeofarrivalAutocheck.setState(false);

					float startxoff = (float) (60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.timeOfArrival - 0.5)));
					float roiwidth = (450.0f / (amc_dce._Frame - 1));
					taRoi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(taRoi);

					roiChoice = 2;
					baserangebtn.setForeground(Color.black);
					timeofarrivalbtn.setForeground(Color.red);
					auc1btn.setForeground(Color.black);
					auc2btn.setForeground(Color.black);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.black);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);
				} catch (NumberFormatException e1) {
				}
				;

			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((int) e.getKeyChar() == 48 || (int) e.getKeyChar() == 49
						|| (int) e.getKeyChar() == 50
						|| (int) e.getKeyChar() == 51
						|| (int) e.getKeyChar() == 52
						|| (int) e.getKeyChar() == 53
						|| (int) e.getKeyChar() == 54
						|| (int) e.getKeyChar() == 55
						|| (int) e.getKeyChar() == 56
						|| (int) e.getKeyChar() == 57) {

				} else {
					e.consume();
				}

			}
		});
		timeofarrivalnum.setBounds(170, 39, 24, 23);
		this.getContentPane().add(timeofarrivalnum);

		timeofarrivalbtn = new Button("Time of Arrival");
		timeofarrivalbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (timeofarrivalbtn.getForeground() == Color.red) {
					timeofarrivalbtn.setForeground(Color.black);
					amc_dce.plotWindow.imp.killRoi();
					taRoi = null;
				} else {

					roiChoice = 2;
					baserangebtn.setForeground(Color.black);
					timeofarrivalbtn.setForeground(Color.RED);
					auc1btn.setForeground(Color.black);
					auc2btn.setForeground(Color.black);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.black);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);
					float startxoff = (float) (60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.timeOfArrival - 0.25)));
					float roiwidth = (450.0f / (amc_dce._Frame - 1) / 2);
					// 120frame=450width //1-20=72 1frame = 3.6 width //21-40=75
					// 1frame = 3.75 //41-60=76 1frame = 3.8 //61-80=76 1frame =
					// 3.8 //81-100=75 1frame = 3.75 //101-120=76 1frame = 3.8
					taRoi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					// taRoi.setNonScalable(false);

					// System.out.println("start:"+Math.round(startxoff)+" width :"+Math.round(roiwidth));
					amc_dce.plotWindow.imp.setRoi(taRoi);
					taRoi.setNonScalable(false);
				}
			}
		});
		timeofarrivalbtn.setForeground(Color.black);
		timeofarrivalbtn.setBounds(10, 39, 109, 23);
		this.getContentPane().add(timeofarrivalbtn);

		normalizationchoice = new Choice();
		normalizationchoice.setBounds(10, 68, 184, 21);
		this.getContentPane().add(normalizationchoice);
		normalizationchoice.add("rel SI (%)");
		normalizationchoice.add("--Select Normalize--");

		// normalizationchoice.set
		normalizationchoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int Selection;
				Selection = normalizationchoice.getSelectedIndex();
				if (Selection == 0) {
					amc_dce.normalizeOn = true;
					// amc_dce.normalize();
					if (amc_dce.lastArrayNum != 0) {
						amc_dce.plotMousePoint_smoothing(amc_dce.lastArrayNum);
					}

				} else if (Selection == 1) {
					amc_dce.normalizeOn = false;
					if (amc_dce.lastArrayNum != 0) {
						amc_dce.plotMousePoint_smoothing(amc_dce.lastArrayNum);
					}

				}
			}
		});
		// normalizationchoice.set

		temporalsmoocheck = new Checkbox("Temporal smoothing",true);
		temporalsmoocheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					amc_dce.temposmt = true;
					temporalsmoonum.setText("" + (amc_dce.tsvalue));
					if (amc_dce.tsvalue > 0) {
						amc_dce.plotMousePoint_smoothing(amc_dce.lastArrayNum);
					}
				} else {
					amc_dce.temposmt = false;
					amc_dce.plotMousePoint_smoothing(amc_dce.lastArrayNum);
					temporalsmoonum.setText("");
				}
			}
		});
		temporalsmoocheck.setBounds(10, 95, 153, 23);
		this.getContentPane().add(temporalsmoocheck);

		temporalsmoonum = new TextField();
		temporalsmoonum.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					amc_dce.tsvalue = Integer.parseInt(temporalsmoonum
							.getText());

					if (amc_dce.tsvalue < 0) {
						amc_dce.tsvalue = 0;
						temporalsmoonum.setText("" + (amc_dce.tsvalue));
					}
					if (amc_dce.tsvalue > amc_dce._Frame - 1) {
						amc_dce.tsvalue = amc_dce._Frame - 1;
						temporalsmoonum.setText("" + amc_dce._Frame);
					}
					if (amc_dce.tsvalue % 2 == 0) {
						amc_dce.tsvalue = amc_dce.tsvalue + 1;
						temporalsmoonum.setText("" + (amc_dce.tsvalue));
					}
					if (temporalsmoocheck.getState() == true) {
						amc_dce.plotMousePoint_smoothing(amc_dce.lastArrayNum);
					}
				} catch (NumberFormatException e1) {
				}
				;

			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((int) e.getKeyChar() == 48 || (int) e.getKeyChar() == 49
						|| (int) e.getKeyChar() == 50
						|| (int) e.getKeyChar() == 51
						|| (int) e.getKeyChar() == 52
						|| (int) e.getKeyChar() == 53
						|| (int) e.getKeyChar() == 54
						|| (int) e.getKeyChar() == 55
						|| (int) e.getKeyChar() == 56
						|| (int) e.getKeyChar() == 57) {

				} else {
					e.consume();
				}

			}
		});
		temporalsmoonum.setBounds(170, 95, 24, 23);
		this.getContentPane().add(temporalsmoonum);

		auc1check = new Checkbox("",true);
		auc1check.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					amc_dce.auc1On = true;
					auc1num1.setText("" + (amc_dce.auc1Start + 1));
					auc1num2.setText("" + (amc_dce.auc1End + 1));
					roiChoice = 3;
					baserangebtn.setForeground(Color.black);
					timeofarrivalbtn.setForeground(Color.black);
					auc1btn.setForeground(Color.RED);
					auc2btn.setForeground(Color.black);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.black);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);
					float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc1Start));
					float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc1End - amc_dce.auc1Start));
					Rectangle rect = new Rectangle((int) startxoff, 15,
							(int) roiwidth, 200);
					// 120frame=450width //1-20=72 1frame = 3.6 width //21-40=75
					// 1frame = 3.75 //41-60=76 1frame = 3.8 //61-80=76 1frame =
					// 3.8 //81-100=75 1frame = 3.75 //101-120=76 1frame = 3.8
					auc1Roi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(auc1Roi);
				} else {
					amc_dce.auc1On = false;
					auc1num1.setText("");
					auc1num2.setText("");
					auc1btn.setForeground(Color.black);
					amc_dce.plotWindow.imp.killRoi();
					auc1Roi = null;
				}

			}
		});
		auc1check.setBounds(293, 11, 19, 23);
		this.getContentPane().add(auc1check);

		auc1btn = new Button("iAUC");
		auc1btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (auc1btn.getForeground() == Color.red) {
					auc1btn.setForeground(Color.black);
					amc_dce.plotWindow.imp.killRoi();
					auc1Roi = null;
				} else {
					auc1check.setState(true);
					amc_dce.auc1On = true;
					auc1num1.setText("" + (amc_dce.auc1Start + 1));
					auc1num2.setText("" + (amc_dce.auc1End + 1));
					roiChoice = 3;
					baserangebtn.setForeground(Color.black);
					timeofarrivalbtn.setForeground(Color.black);
					auc1btn.setForeground(Color.RED);
					auc2btn.setForeground(Color.black);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.black);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);
					float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc1Start));
					float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc1End - amc_dce.auc1Start));
					Rectangle rect = new Rectangle((int) startxoff, 15,
							(int) roiwidth, 200);
					// 120frame=450width //1-20=72 1frame = 3.6 width //21-40=75
					// 1frame = 3.75 //41-60=76 1frame = 3.8 //61-80=76 1frame =
					// 3.8 //81-100=75 1frame = 3.75 //101-120=76 1frame = 3.8
					auc1Roi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(auc1Roi);
				}
			}
		});
		auc1btn.setForeground(Color.black);
		auc1btn.setBounds(316, 11, 114, 23);
		this.getContentPane().add(auc1btn);

		auc1num1 = new TextField();
		auc1num1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (auc1num1.getText() != null
						&& auc1num1.getText().length() > 0)
					try {
						amc_dce.auc1Start = Integer.parseInt(auc1num1.getText()) - 1;
						amc_dce.washinStart = Integer.parseInt(auc1num1.getText()) - 1;
						washinnum1.setText(""+Integer.parseInt(auc1num1.getText()));
//						System.out.println("amc_dce.washinStart"+amc_dce.washinStart);
						if (amc_dce.auc1Start < 0) {
							amc_dce.auc1Start = 0;
							auc1num1.setText("" + (amc_dce.auc1Start + 1));
							amc_dce.washinStart = 0;
							washinnum1.setText("" + (amc_dce.auc1Start + 1));
						}
						if (amc_dce.auc1Start > amc_dce._Frame - 1) {
							amc_dce.auc1Start = amc_dce._Frame - 1;
							auc1num1.setText("" + amc_dce._Frame);
							amc_dce.washinStart = amc_dce._Frame - 1;
							washinnum1.setText("" + amc_dce._Frame);
						}
						if (amc_dce.auc1Start > amc_dce.auc1End) {
							amc_dce.auc1End = amc_dce.auc1Start;
							auc1num2.setText("" + (amc_dce.auc1End + 1));
							amc_dce.washinEnd = amc_dce.auc1Start;
							washinnum2.setText("" + (amc_dce.auc1End + 1));
						}
						if (amc_dce.auc1On) {
							float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc1Start));
							float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc1End - amc_dce.auc1Start));
							auc1Roi = new Roi((int) Math.round(startxoff),
									(int) 15, (int) Math.round(roiwidth),
									(int) 200);
							amc_dce.plotWindow.imp.setRoi(auc1Roi);

							roiChoice = 3;
							baserangebtn.setForeground(Color.black);
							timeofarrivalbtn.setForeground(Color.black);
							auc1btn.setForeground(Color.red);
							auc2btn.setForeground(Color.black);
							//							auc3btn.setForeground(Color.black);
							//							auc4btn.setForeground(Color.black);
							washinbtn.setForeground(Color.black);
							//							washoutbtn.setForeground(Color.black);
							//							subtraction1.setForeground(Color.black);
							//							subtraction2.setForeground(Color.black);
						}

					} catch (NumberFormatException e1) {
					}
				;

			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((int) e.getKeyChar() == 48 || (int) e.getKeyChar() == 49
						|| (int) e.getKeyChar() == 50
						|| (int) e.getKeyChar() == 51
						|| (int) e.getKeyChar() == 52
						|| (int) e.getKeyChar() == 53
						|| (int) e.getKeyChar() == 54
						|| (int) e.getKeyChar() == 55
						|| (int) e.getKeyChar() == 56
						|| (int) e.getKeyChar() == 57) {

				} else {
					e.consume();
				}

			}
		});
		auc1num1.setBounds(437, 11, 30, 23);
		this.getContentPane().add(auc1num1);

		Label label_4 = new Label("~");
		label_4.setBounds(475, 10, 9, 24);
		this.getContentPane().add(label_4);

		auc1num2 = new TextField();
		auc1num2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (auc1num2.getText() != null
						&& auc1num2.getText().length() > 0)
					try {
						amc_dce.auc1End = Integer.parseInt(auc1num2.getText()) - 1;
						amc_dce.washinEnd = Integer.parseInt(auc1num2.getText()) - 1;
						washinnum2.setText(""+Integer.parseInt(auc1num2.getText()));
						if (amc_dce.auc1End < 0) {
							amc_dce.auc1End = 0;
							auc1num2.setText("" + (amc_dce.auc1End + 1));
						}
						if (amc_dce.auc1End > amc_dce._Frame - 1) {
							amc_dce.auc1End = amc_dce._Frame - 1;
							auc1num2.setText("" + amc_dce._Frame);
							amc_dce.washinEnd = amc_dce._Frame - 1;
							washinnum2.setText("" + amc_dce._Frame);
						}
						if (amc_dce.auc1On) {
							float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc1Start));
							float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc1End - amc_dce.auc1Start));
							auc1Roi = new Roi((int) Math.round(startxoff),
									(int) 15, (int) Math.round(roiwidth),
									(int) 200);
							amc_dce.plotWindow.imp.setRoi(auc1Roi);
							roiChoice = 3;
							baserangebtn.setForeground(Color.black);
							timeofarrivalbtn.setForeground(Color.black);
							auc1btn.setForeground(Color.red);
							auc2btn.setForeground(Color.black);
							//							auc3btn.setForeground(Color.black);
							//							auc4btn.setForeground(Color.black);
							washinbtn.setForeground(Color.black);
							//							washoutbtn.setForeground(Color.black);
							//							subtraction1.setForeground(Color.black);
							//							subtraction2.setForeground(Color.black);
						}
					} catch (NumberFormatException e1) {
					}
				;

			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((int) e.getKeyChar() == 48 || (int) e.getKeyChar() == 49
						|| (int) e.getKeyChar() == 50
						|| (int) e.getKeyChar() == 51
						|| (int) e.getKeyChar() == 52
						|| (int) e.getKeyChar() == 53
						|| (int) e.getKeyChar() == 54
						|| (int) e.getKeyChar() == 55
						|| (int) e.getKeyChar() == 56
						|| (int) e.getKeyChar() == 57) {

				} else {
					e.consume();
				}

			}
		});
		auc1num2.setBounds(494, 11, 30, 23);
		this.getContentPane().add(auc1num2);

		auc2num1 = new TextField();
		auc2num1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					amc_dce.auc2Start = Integer.parseInt(auc2num1.getText()) - 1;
					if (amc_dce.auc2Start < 0) {
						amc_dce.auc2Start = 0;
						auc2num1.setText("" + (amc_dce.auc2Start + 1));
					}
					if (amc_dce.auc2Start > amc_dce._Frame - 1) {
						amc_dce.auc2Start = amc_dce._Frame - 1;
						auc2num1.setText("" + amc_dce._Frame);
					}
					if (amc_dce.auc2Start > amc_dce.auc2End) {
						amc_dce.auc2End = amc_dce.auc2Start;
						auc2num2.setText("" + (amc_dce.auc2End + 1));
					}
					if (amc_dce.auc2On) {
						float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * amc_dce.auc2Start);
						float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc2End - amc_dce.auc2Start));
						// 120frame=450width //1-20=72 1frame = 3.6 width
						// //21-40=75 1frame = 3.75 //41-60=76 1frame = 3.8
						// //61-80=76 1frame = 3.8 //81-100=75 1frame = 3.75
						// //101-120=76 1frame = 3.8
						auc2Roi = new Roi((int) Math.round(startxoff),
								(int) 15, (int) Math.round(roiwidth), (int) 200);
						amc_dce.plotWindow.imp.setRoi(auc2Roi);
						roiChoice = 4;
						baserangebtn.setForeground(Color.black);
						timeofarrivalbtn.setForeground(Color.black);
						auc1btn.setForeground(Color.black);
						auc2btn.setForeground(Color.red);
						//						auc3btn.setForeground(Color.black);
						//						auc4btn.setForeground(Color.black);
						washinbtn.setForeground(Color.black);
						//						washoutbtn.setForeground(Color.black);
						//						subtraction1.setForeground(Color.black);
						//						subtraction2.setForeground(Color.black);
					}
				} catch (NumberFormatException e1) {
				}
				;

			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((int) e.getKeyChar() == 48 || (int) e.getKeyChar() == 49
						|| (int) e.getKeyChar() == 50
						|| (int) e.getKeyChar() == 51
						|| (int) e.getKeyChar() == 52
						|| (int) e.getKeyChar() == 53
						|| (int) e.getKeyChar() == 54
						|| (int) e.getKeyChar() == 55
						|| (int) e.getKeyChar() == 56
						|| (int) e.getKeyChar() == 57) {

				} else {
					e.consume();
				}

			}
		});
		auc2num1.setBounds(437, 40, 30, 23);
		this.getContentPane().add(auc2num1);

		Label label_5 = new Label("~");
		label_5.setBounds(475, 40, 9, 24);
		this.getContentPane().add(label_5);

		auc2num2 = new TextField();
		auc2num2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					amc_dce.auc2End = Integer.parseInt(auc2num2.getText()) - 1;
					if (amc_dce.auc2End < 0) {
						amc_dce.auc2End = 0;
						auc2num2.setText("" + (amc_dce.auc2End + 1));
					}
					if (amc_dce.auc2End > amc_dce._Frame - 1) {
						amc_dce.auc2End = amc_dce._Frame - 1;
						auc2num2.setText("" + amc_dce._Frame);
					}
					if (amc_dce.auc2On) {
						float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * amc_dce.auc2Start);
						float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc2End - amc_dce.auc2Start));
						// 120frame=450width //1-20=72 1frame = 3.6 width
						// //21-40=75 1frame = 3.75 //41-60=76 1frame = 3.8
						// //61-80=76 1frame = 3.8 //81-100=75 1frame = 3.75
						// //101-120=76 1frame = 3.8
						auc2Roi = new Roi((int) Math.round(startxoff),
								(int) 15, (int) Math.round(roiwidth), (int) 200);
						amc_dce.plotWindow.imp.setRoi(auc2Roi);
						roiChoice = 4;
						baserangebtn.setForeground(Color.black);
						timeofarrivalbtn.setForeground(Color.black);
						auc1btn.setForeground(Color.black);
						auc2btn.setForeground(Color.red);
						//						auc3btn.setForeground(Color.black);
						//						auc4btn.setForeground(Color.black);
						washinbtn.setForeground(Color.black);
						//						washoutbtn.setForeground(Color.black);
						//						subtraction1.setForeground(Color.black);
						//						subtraction2.setForeground(Color.black);
					}
				} catch (NumberFormatException e1) {
				}
				;

			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((int) e.getKeyChar() == 48 || (int) e.getKeyChar() == 49
						|| (int) e.getKeyChar() == 50
						|| (int) e.getKeyChar() == 51
						|| (int) e.getKeyChar() == 52
						|| (int) e.getKeyChar() == 53
						|| (int) e.getKeyChar() == 54
						|| (int) e.getKeyChar() == 55
						|| (int) e.getKeyChar() == 56
						|| (int) e.getKeyChar() == 57) {

				} else {
					e.consume();
				}

			}
		});
		auc2num2.setBounds(494, 41, 30, 23);
		this.getContentPane().add(auc2num2);

		auc2btn = new Button("VE");
		auc2btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (auc2btn.getForeground() == Color.red) {
					auc2btn.setForeground(Color.black);
					amc_dce.plotWindow.imp.killRoi();
					auc2Roi = null;
				} else {
					auc2check.setState(true);
					amc_dce.auc2On = true;
					auc2num1.setText("" + (amc_dce.auc2Start + 1));
					auc2num2.setText("" + (amc_dce.auc2End + 1));
					roiChoice = 4;
					baserangebtn.setForeground(Color.black);
					timeofarrivalbtn.setForeground(Color.black);
					auc1btn.setForeground(Color.black);
					auc2btn.setForeground(Color.RED);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.black);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);
					float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * amc_dce.auc2Start);
					float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc2End - amc_dce.auc2Start));
					// 120frame=450width //1-20=72 1frame = 3.6 width //21-40=75
					// 1frame = 3.75 //41-60=76 1frame = 3.8 //61-80=76 1frame =
					// 3.8 //81-100=75 1frame = 3.75 //101-120=76 1frame = 3.8
					auc2Roi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(auc2Roi);
				}
			}
		});
		auc2btn.setForeground(Color.black);
		auc2btn.setBounds(316, 40, 114, 23);
		this.getContentPane().add(auc2btn);

		auc2check = new Checkbox("",true);
		auc2check.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					amc_dce.auc2On = true;
					auc2num1.setText("" + (amc_dce.auc2Start + 1));
					auc2num2.setText("" + (amc_dce.auc2End + 1));
					roiChoice = 4;
					baserangebtn.setForeground(Color.black);
					timeofarrivalbtn.setForeground(Color.black);
					auc1btn.setForeground(Color.black);
					auc2btn.setForeground(Color.RED);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.black);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);
					float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * amc_dce.auc2Start);
					float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.auc2End - amc_dce.auc2Start));
					// 120frame=450width //1-20=72 1frame = 3.6 width //21-40=75
					// 1frame = 3.75 //41-60=76 1frame = 3.8 //61-80=76 1frame =
					// 3.8 //81-100=75 1frame = 3.75 //101-120=76 1frame = 3.8
					auc2Roi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(auc2Roi);
				} else {
					amc_dce.auc2On = false;
					auc2num1.setText("");
					auc2num2.setText("");
					auc2btn.setForeground(Color.black);
					amc_dce.plotWindow.imp.killRoi();
					auc2Roi = null;
				}
			}
		});
		auc2check.setBounds(293, 40, 19, 23);
		this.getContentPane().add(auc2check);

		washincheck = new Checkbox("",true);
		washincheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					amc_dce.washinOn = true;
					washinnum1.setText("" + (amc_dce.washinStart + 1));
					washinnum2.setText("" + (amc_dce.washinEnd + 1));
					roiChoice = 7;
					baserangebtn.setForeground(Color.black);
					timeofarrivalbtn.setForeground(Color.black);
					auc1btn.setForeground(Color.black);
					auc2btn.setForeground(Color.black);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.RED);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);

					float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * amc_dce.washinStart);
					float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.washinEnd - amc_dce.washinStart));
					Rectangle rect = new Rectangle((int) startxoff, 15,
							(int) roiwidth, 200);
					// 120frame=450width //1-20=72 1frame = 3.6 width //21-40=75
					// 1frame = 3.75 //41-60=76 1frame = 3.8 //61-80=76 1frame =
					// 3.8 //81-100=75 1frame = 3.75 //101-120=76 1frame = 3.8
					washinRoi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(washinRoi);
				} else {
					amc_dce.washinOn = false;
					washinnum1.setText("");
					washinnum2.setText("");
					washinbtn.setForeground(Color.black);
					amc_dce.plotWindow.imp.killRoi();
					washinRoi = null;
				}
			}
		});
		washincheck.setBounds(293, 69, 19, 23);
		this.getContentPane().add(washincheck);

		peicheck = new Checkbox("   Max Rel Enh",true);
		peicheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					amc_dce.peiOn = true;
				} else {
					amc_dce.peiOn = false;

				}
			}
		});
		peicheck.setBounds(293, 98, 101, 23);
		this.getContentPane().add(peicheck);

		processbtn = new Button("Process");
		processbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (amc_dce.processOn == true) {
					// amc_dce.processOn=false;
					// processbtn.removeActionListener(this);
					amc_dce.processResult();
				}
			}
		});
		processbtn.setBounds(10, 124, 106, 23);
		this.getContentPane().add(processbtn);

		washinbtn = new Button("Wash-In");
		washinbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (washinbtn.getForeground() == Color.red) {
					washinbtn.setForeground(Color.black);
					amc_dce.plotWindow.imp.killRoi();
					washinRoi = null;
				} else {
					washincheck.setState(true);
					amc_dce.washinOn = true;
					washinnum1.setText("" + (amc_dce.washinStart + 1));
					washinnum2.setText("" + (amc_dce.washinEnd + 1));
					roiChoice = 7;
					baserangebtn.setForeground(Color.black);
					timeofarrivalbtn.setForeground(Color.black);
					auc1btn.setForeground(Color.black);
					auc2btn.setForeground(Color.black);
					//					auc3btn.setForeground(Color.black);
					//					auc4btn.setForeground(Color.black);
					washinbtn.setForeground(Color.RED);
					//					washoutbtn.setForeground(Color.black);
					//					subtraction1.setForeground(Color.black);
					//					subtraction2.setForeground(Color.black);

					float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * amc_dce.washinStart);
					float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.washinEnd - amc_dce.washinStart));
					Rectangle rect = new Rectangle((int) startxoff, 15,
							(int) roiwidth, 200);
					// 120frame=450width //1-20=72 1frame = 3.6 width //21-40=75
					// 1frame = 3.75 //41-60=76 1frame = 3.8 //61-80=76 1frame =
					// 3.8 //81-100=75 1frame = 3.75 //101-120=76 1frame = 3.8
					washinRoi = new Roi((int) Math.round(startxoff), (int) 15,
							(int) Math.round(roiwidth), (int) 200);
					amc_dce.plotWindow.imp.setRoi(washinRoi);
				}
			}
		});
		washinbtn.setForeground(Color.black);
		washinbtn.setBounds(316, 69, 114, 23);
		getContentPane().add(washinbtn);

		washinnum1 = new TextField();
		washinnum1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					amc_dce.washinStart = Integer.parseInt(washinnum1.getText()) - 1;
					if (amc_dce.washinStart < 0) {
						amc_dce.washinStart = 0;
						washinnum1.setText("" + (amc_dce.washinStart + 1));
					}
					if (amc_dce.washinStart > amc_dce._Frame - 1) {
						amc_dce.washinStart = amc_dce._Frame - 1;
						washinnum1.setText("" + amc_dce._Frame);
					}
					if (amc_dce.washinStart > amc_dce.washinEnd) {
						amc_dce.washinEnd = amc_dce.washinStart;
						washinnum2.setText("" + (amc_dce.washinEnd + 1));
					}
					if (amc_dce.washinOn) {
						float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * amc_dce.washinStart);
						float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.washinEnd - amc_dce.washinStart));
						Rectangle rect = new Rectangle((int) startxoff, 15,
								(int) roiwidth, 200);
						// 120frame=450width //1-20=72 1frame = 3.6 width
						// //21-40=75 1frame = 3.75 //41-60=76 1frame = 3.8
						// //61-80=76 1frame = 3.8 //81-100=75 1frame = 3.75
						// //101-120=76 1frame = 3.8
						washinRoi = new Roi((int) Math.round(startxoff),
								(int) 15, (int) Math.round(roiwidth), (int) 200);
						amc_dce.plotWindow.imp.setRoi(washinRoi);
						roiChoice = 7;
						baserangebtn.setForeground(Color.black);
						timeofarrivalbtn.setForeground(Color.black);
						auc1btn.setForeground(Color.black);
						auc2btn.setForeground(Color.black);
						//						auc3btn.setForeground(Color.black);
						//						auc4btn.setForeground(Color.black);
						washinbtn.setForeground(Color.red);
						//						washoutbtn.setForeground(Color.black);
						//						subtraction1.setForeground(Color.black);
						//						subtraction2.setForeground(Color.black);
					}
				} catch (NumberFormatException e1) {
				}
				;
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((int) e.getKeyChar() == 48 || (int) e.getKeyChar() == 49
						|| (int) e.getKeyChar() == 50
						|| (int) e.getKeyChar() == 51
						|| (int) e.getKeyChar() == 52
						|| (int) e.getKeyChar() == 53
						|| (int) e.getKeyChar() == 54
						|| (int) e.getKeyChar() == 55
						|| (int) e.getKeyChar() == 56
						|| (int) e.getKeyChar() == 57) {

				} else {
					e.consume();
				}

			}
		});
		washinnum1.setBounds(437, 69, 30, 23);
		getContentPane().add(washinnum1);

		Label label_1 = new Label("~");
		label_1.setBounds(475, 70, 9, 24);
		getContentPane().add(label_1);

		washinnum2 = new TextField();
		washinnum2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					amc_dce.washinEnd = Integer.parseInt(washinnum2.getText()) - 1;
					if (amc_dce.washinEnd < 0) {
						amc_dce.washinEnd = 0;
						washinnum2.setText("" + (amc_dce.washinEnd + 1));
					}
					if (amc_dce.washinEnd > amc_dce._Frame - 1) {
						amc_dce.washinEnd = amc_dce._Frame - 1;
						washinnum2.setText("" + amc_dce._Frame);
					}
					if (amc_dce.washinOn) {
						float startxoff = 60 + (450.0f / (amc_dce._Frame - 1) * amc_dce.washinStart);
						float roiwidth = (450.0f / (amc_dce._Frame - 1) * (amc_dce.washinEnd - amc_dce.washinStart));
						Rectangle rect = new Rectangle((int) startxoff, 15,
								(int) roiwidth, 200);
						// 120frame=450width //1-20=72 1frame = 3.6 width
						// //21-40=75 1frame = 3.75 //41-60=76 1frame = 3.8
						// //61-80=76 1frame = 3.8 //81-100=75 1frame = 3.75
						// //101-120=76 1frame = 3.8
						washinRoi = new Roi((int) Math.round(startxoff),
								(int) 15, (int) Math.round(roiwidth), (int) 200);
						amc_dce.plotWindow.imp.setRoi(washinRoi);
						roiChoice = 7;
						baserangebtn.setForeground(Color.black);
						timeofarrivalbtn.setForeground(Color.black);
						auc1btn.setForeground(Color.black);
						auc2btn.setForeground(Color.black);
						//						auc3btn.setForeground(Color.black);
						//						auc4btn.setForeground(Color.black);
						washinbtn.setForeground(Color.red);
						//						washoutbtn.setForeground(Color.black);
						//						subtraction1.setForeground(Color.black);
						//						subtraction2.setForeground(Color.black);
					}
				} catch (NumberFormatException e1) {
				}
				;
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((int) e.getKeyChar() == 48 || (int) e.getKeyChar() == 49
						|| (int) e.getKeyChar() == 50
						|| (int) e.getKeyChar() == 51
						|| (int) e.getKeyChar() == 52
						|| (int) e.getKeyChar() == 53
						|| (int) e.getKeyChar() == 54
						|| (int) e.getKeyChar() == 55
						|| (int) e.getKeyChar() == 56
						|| (int) e.getKeyChar() == 57) {

				} else {
					e.consume();
				}
			}
		});
		washinnum2.setBounds(494, 69, 30, 23);
		getContentPane().add(washinnum2);

		Button saveallbtn = new Button("Save all");
		saveallbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean savedSomething = false;
				int[] imageIDs = WindowManager.getIDList();
				if (imageIDs != null) {
					for (int m=0; m<imageIDs.length; ++m) {
						ImagePlus cimp = WindowManager.getImage(imageIDs[m]);

						if(cimp.getTitle()=="ADC"){


							WindowManager.setCurrentWindow(cimp.getWindow());
							int cSlice = cimp.getCurrentSlice();
							//skc
//							IJ.run(cimp, "Size...", "width="+cimp.getWidth()*3+" height="+cimp.getHeight()*3+" depth="+cimp.getNSlices()+" constrain average interpolation=Bicubic");


							TextRoi textroi = new TextRoi(512*0.4, 512*0.9, " ADC color map");
							WindowManager.getImage("ADC").setRoi(textroi);
							for(int i = 1;i<WindowManager.getImage("ADC").getNSlices();i++){
								WindowManager.getImage("ADC").setSlice(i);
								WindowManager.getImage("ADC").setRoi(textroi);
								IJ.runPlugIn(WindowManager.getImage("ADC") , "ij.plugin.OverlayCommands", "add");
							}//skc
							IJ.runPlugIn(WindowManager.getImage("ADC") ,"ij.plugin.CalibrationBar","ADC1"); //skc
							IJ.runPlugIn("ij.plugin.StackWriter","noDialog"); //Sab. for saving without dialog
							WindowManager.getImage("ADC").setSlice(cSlice);


							savedSomething = true;

						}

						if(cimp.getTitle()=="ADC_overlay"){
							WindowManager.setCurrentWindow(cimp.getWindow());
							int cSlice = cimp.getCurrentSlice();
							cimp.folderpath = DragAndDrop.folderpath;

							TextRoi textroi = new TextRoi(512*0.4, 512*0.9, " fusion image");
							WindowManager.getImage("ADC_overlay").setRoi(textroi);
							for(int i = 1;i<WindowManager.getImage("ADC_overlay").getNSlices();i++){
								WindowManager.getImage("ADC_overlay").setSlice(i);
								WindowManager.getImage("ADC_overlay").setRoi(textroi);
								IJ.runPlugIn(WindowManager.getImage("ADC_overlay") , "ij.plugin.OverlayCommands", "add");
							}//skc

							IJ.runPlugIn("ij.plugin.StackWriter","noDialog"); //Sab. for saving without dialog
							WindowManager.getImage("ADC_overlay").setSlice(cSlice);
							savedSomething = true;
						}

						if(cimp.getTitle()=="b1500"){
							WindowManager.setCurrentWindow(cimp.getWindow());
							int cSlice = cimp.getCurrentSlice();
							//skc
//							IJ.run(cimp, "Size...", "width="+cimp.getWidth()*3+" height="+cimp.getHeight()*3+" depth="+cimp.getNSlices()+" constrain average interpolation=Bicubic");


							TextRoi textroi = new TextRoi(512*0.45, 512*0.9, " b = 1500 ");
							WindowManager.getImage("b1500").setRoi(textroi);
							for(int i = 1;i<WindowManager.getImage("b1500").getNSlices();i++){
								WindowManager.getImage("b1500").setSlice(i);
								WindowManager.getImage("b1500").setRoi(textroi);
								IJ.runPlugIn(WindowManager.getImage("b1500") , "ij.plugin.OverlayCommands", "add");
							}//skc
							//						IJ.runPlugIn(WindowManager.getImage("b1500") ,"ij.plugin.CalibrationBar","ADC1"); //skc
							IJ.runPlugIn("ij.plugin.StackWriter","noDialog"); //Sab. for saving without dialog
							WindowManager.getImage("b1500").setSlice(cSlice);
							savedSomething = true;

						}
						
						if(cimp.getTitle()=="iAUC"){
							WindowManager.setCurrentWindow(cimp.getWindow());
							int cSlice = cimp.getCurrentSlice();
							TextRoi textroi = new TextRoi(512*0.45, 512*0.88, " iAUC ");
							WindowManager.getImage("iAUC").setRoi(textroi);
							for(int i = 1;i<WindowManager.getImage("iAUC").getNSlices();i++){
								WindowManager.getImage("iAUC").setSlice(i);
								WindowManager.getImage("iAUC").setRoi(textroi);
								IJ.runPlugIn(WindowManager.getImage("iAUC") , "ij.plugin.OverlayCommands", "add");
							}
							IJ.runPlugIn("ij.plugin.StackWriter","noDialog"); //Sab. for saving without dialog
							WindowManager.getImage("iAUC").setSlice(cSlice);
							savedSomething = true;

						}
						if(cimp.getTitle()=="VE"){
							WindowManager.setCurrentWindow(cimp.getWindow());
							int cSlice = cimp.getCurrentSlice();
							TextRoi textroi = new TextRoi(512*0.47, 512*0.88, " VE ");
							WindowManager.getImage("VE").setRoi(textroi);
							for(int i = 1;i<WindowManager.getImage("VE").getNSlices();i++){
								WindowManager.getImage("VE").setSlice(i);
								WindowManager.getImage("VE").setRoi(textroi);
								IJ.runPlugIn(WindowManager.getImage("VE") , "ij.plugin.OverlayCommands", "add");
							}
							IJ.runPlugIn("ij.plugin.StackWriter","noDialog"); //Sab. for saving without dialog
							WindowManager.getImage("VE").setSlice(cSlice);
							savedSomething = true;
						}
						if(cimp.getTitle()=="Wash-In"){
							WindowManager.setCurrentWindow(cimp.getWindow());
							int cSlice = cimp.getCurrentSlice();
							TextRoi textroi = new TextRoi(512*0.42, 512*0.88, " Wash-In ");
							WindowManager.getImage("Wash-In").setRoi(textroi);
							for(int i = 1;i<WindowManager.getImage("Wash-In").getNSlices();i++){
								WindowManager.getImage("Wash-In").setSlice(i);
								WindowManager.getImage("Wash-In").setRoi(textroi);
								IJ.runPlugIn(WindowManager.getImage("Wash-In") , "ij.plugin.OverlayCommands", "add");
							}
							IJ.runPlugIn("ij.plugin.StackWriter","noDialog"); //Sab. for saving without dialog
							WindowManager.getImage("Wash-In").setSlice(cSlice);
							savedSomething = true;
						}
						if(cimp.getTitle()=="MaxRelEnh"){
							WindowManager.setCurrentWindow(cimp.getWindow());
							int cSlice = cimp.getCurrentSlice();
							TextRoi textroi = new TextRoi(512*0.40, 512*0.88, " MaxRelEnh ");
							WindowManager.getImage("MaxRelEnh").setRoi(textroi);
							for(int i = 1;i<WindowManager.getImage("MaxRelEnh").getNSlices();i++){
								WindowManager.getImage("MaxRelEnh").setSlice(i);
								WindowManager.getImage("MaxRelEnh").setRoi(textroi);
								IJ.runPlugIn(WindowManager.getImage("MaxRelEnh") , "ij.plugin.OverlayCommands", "add");
							}
							IJ.runPlugIn("ij.plugin.StackWriter","noDialog"); //Sab. for saving without dialog
							WindowManager.getImage("MaxRelEnh").setSlice(cSlice);
							savedSomething = true;
						}
					}
				}
				if (savedSomething) {
//					JOptionPane.showMessageDialog(null, "Save Finished.", "Save all", JOptionPane.INFORMATION_MESSAGE);
//					IJ.runPlugIn("ij.plugin.Commands","close-all");
					//Sab. close every images after save
					ImagePlus cimp = WindowManager.getImage("MaxRelEnh");
					if (cimp!=null) {
						cimp.changes = false;
						cimp.close();
					}
					cimp = WindowManager.getImage("Wash-In");
					if (cimp!=null) {
						cimp.changes = false;
						cimp.close();
					}
					cimp = WindowManager.getImage("VE");
					if (cimp!=null) {
						cimp.changes = false;
						cimp.close();
					}
					cimp = WindowManager.getImage("iAUC");
					if (cimp!=null) {
						cimp.changes = false;
						cimp.close();
					}
					if (imp!=null) {
						imp.changes = false;
						imp.close();
					}
					
					cimp = WindowManager.getImage("ADC");
					if (cimp!=null) {
						cimp.changes = false;
						cimp.close();
					}
					if (WindowManager.getImage("b1500")!=null) {
						WindowManager.getImage("b1500").changes = false;
						WindowManager.getImage("b1500").close();
					}
					if (WindowManager.getImage("T2")!=null) {
						WindowManager.getImage("T2").changes = false;
						WindowManager.getImage("T2").close();
					}
					if (WindowManager.getImage("ADC_overlay")!=null) {
						WindowManager.getImage("ADC_overlay").changes = false;
						WindowManager.getImage("ADC_overlay").close();
					}
					cimp = WindowManager.getImage("DWI");
					if (cimp!=null) {
						cimp.changes = false;
						cimp.close();
					}
					
					amc_dce.img3DArray=null;
					System.gc();
					//Sab. close every images after save.. end.
				}
			}
		});
		saveallbtn.setBounds(135, 124, 106, 23);
//		getContentPane().add(saveallbtn); //skc 20121222

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

	public void shutDown() {
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
}
