/**
* Module 4 - Lab 2 - Audio Analysis Using a FFT - Task A
* Author: A. Stewart
* Current version written: May 2016
* Description: FT of a pure tone 
*/

// Import packages
import java.io.*;
//import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import org.math.plot.*;
import org.math.plot.plotObjects.*;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.*;
import javax.sound.sampled.*;
import java.util.Arrays;



public class FourierTermProject {

	// Start main method
	public static void main(String[] Args) {
		
		//*********************************************************************
        //Opening a file to store data ****************************************
        //*********************************************************************
        String filename = "powerAndFrequencyOutput.txt";
        PrintWriter outputFile = null;
        try
        {
            outputFile = new PrintWriter(new FileOutputStream(filename,false));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File error.  Program aborted.");
            System.exit(0);
        }

		
		System.out.print("Path and Name of Selected File is '");
        File FileChosen = getFile(); // Calling the method getFile(); A window opens and the user selects a file
        System.out.print(FileChosen.getAbsoluteFile()); // Prints the complete (absolute) path of the file
        System.out.println("'");
        System.out.print("File Size in bytes ");
        System.out.println(FileChosen.length()); // Prints the length of the file (long) (in bytes)
		/*
        try
        {
            AudioFileFormat audio = getAudioFileFormat(FileChosen); // Gives the audio file format of the file selected by the user
            System.out.println(audio.getFormat()); // Prints the "primary encoding format" of the key (info like ... Hz, ... bit, stereo, ... bytes/frame, etc.)
            //System.out.println(audio.getFormat().getFrameSize()); // Prints the frame size in bytes, but not necessary because given by the above print statement
        }
        catch (Exception ex2)
        {
            System.out.println("Something went wrong."); // Prints if the type of file selected is not the type of file wanted (ex.: pdf) (ex.: mp3 rather than wav)
        }
		*/
		// Initialize things outside the try/catch	
		double dt = 0.0;
		double fNyq = 0.0;
		double[] time = null;
		double[] signal = null;
		
		//////////////////////////////////////		
		// 1. Reading WAV file
		//////////////////////////////////////

		try {		
			// Open the WAV file
			WavFile wavFile = WavFile.openWavFile((FileChosen));
	

			// Display information about the WAV file	
			wavFile.display();
		
			// Calculate the time step and Nyquist frequency
			
			fNyq = (wavFile.getSampleRate())/2.;
			dt = 1/wavFile.getSampleRate();
			// Set the size of the arrays that hold the signal and time values
			//int lengthh = wavFile.getNumFrames()
			signal = new double[(int)wavFile.getNumFrames()];	    //do i need to divide by 2? jf said no
			time = new double[signal.length];
			
			System.out.println("signal length is "+signal.length);
			
			// Read the WAV file
			
			wavFile.readFrames(signal ,signal.length);
			

			// Create and fill the time array
			for (int n = 0; n < signal.length; n++) {
				time[n] = n*dt;
			}

			// Close the WAV file
			wavFile.close();
			
							//create a buffered reader that connects to the console, we use it so we can read lines
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

				//read a line from the console
				String lineFromInput = in.readLine();

			

		}
		catch (Exception e) {
			System.err.println(e);
		}

		
		//////////////////////////////////////		
		// 2. Padding
		//////////////////////////////////////

		// Calcuate the *next highest* power of 2 after the signal length
		
		int newsignallength=0;;
		int g = 0;
		
		while (signal.length>((int)Math.pow(2,g)))
		{
			newsignallength = (int)Math.pow(2,g+1);
			g++;
		}
		
		System.out.println("new signal length is "+newsignallength);
		
		double[] paddedSignal = new double[newsignallength];
		
		//int k = 0;
		// Loop to copy and pad the signal
		for (int i = 0; i < paddedSignal.length; i++) 
		{
			if (i<signal.length)
			{
				paddedSignal[i] = signal [i];
			
			}
			else
			{
				paddedSignal[i]=0;
			}
		}
		
		
		
		/*for (int i = 0; i <paddedSignal.length; i++)
		{
			System.out.println(paddedSignal[i]);
		}*/

		//////////////////////////////////////		
		// 3. FFT (DO NOT EDIT)
		//////////////////////////////////////

		// Create a FFT object and forward transform the signal
		FastFourierTransformer FFT = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] signalFT = FFT.transform(paddedSignal,TransformType.FORWARD);

		//////////////////////////////////////		
		// 4. Power spectrum (DO NOT EDIT)
		//////////////////////////////////////
			
		// Create arrays for frequency and power
		double[] frequency = new double[signalFT.length/2];
		double[] power = new double[signalFT.length/2];

		// Calculate frequency and power and fill the arrays
		for (int m = 0; m < frequency.length; m++) {
			frequency[m] = /*m/dt/signalFT.length*/ m*fNyq/frequency.length;
			power[m] = Math.pow(signalFT[m].getReal(),2)+Math.pow(signalFT[m].getImaginary(),2);
		}
		
		//************************************************************************************************
		//open this part to get the power data in the file and close it to when getting the frequency data
		double p = 0;
		for (int i = 0; i < frequency.length; i++){
		//System.out.println (power[i]);
		p = power[i] ;
		
		outputFile.printf("%6.6f\n",p);
		}
		
		//************************************************************************************************
		//open this part to get the frequency data in the file and close it to when getting the power data
		/*double f = 0;
		for (int i = 0; i < frequency.length; i++){
		f = frequency[i] ;
		
		outputFile.printf("%6.6f\n",f);
		}*/
		
		outputFile.close();
		
		
		
		//////////////////////////////////////		
		// 5. Plotting (DO NOT EDIT)
		//////////////////////////////////////

		//Font plotFont = new Font(Font.MONOSPACED,Font.PLAIN,12);

		// Signal plot
		/*
		// Create a PlotPanel
  		Plot2DPanel plot1 = new Plot2DPanel();
		
		// Add a line plot and labels to the PlotPanel
		plot1.addLinePlot("Signal",Color.magenta,time,signal);
		plot1.setFixedBounds(0,0,signal.length*dt); //Manually set bounds on x-axis	
		plot1.setAxisLabels("Time", "Amplitude");
		plot1.getAxis(0).setLabelPosition(0.5, -0.1);
		plot1.getAxis(0).setLabelFont(plotFont);
		plot1.getAxis(1).setLabelPosition(-0.15,0.5);
		plot1.getAxis(1).setLabelFont(plotFont);
		BaseLabel title1 = new BaseLabel("Signal from " + FileChosen,Color.BLACK,0.5, 1.1);
		title1.setFont(plotFont);
       		plot1.addPlotable(title1);
			
		// Put the PlotPanel in a JFrame as a JPanel
		JFrame frame1 = new JFrame("Output 1");
		frame1.setSize(1024,576);
		frame1.setContentPane(plot1);
		frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame1.setVisible(true);

		// Power spectrum plot

		// Find maximum power
		double PMax = 0.0;
		for (int j = 0; j < power.length; j++) {
			if (power[j] > PMax) {
				PMax = power[j];
			}
		} 
		
		// Create a PlotPanel (you can use it as a JPanel)
  		Plot2DPanel plot2 = new Plot2DPanel();
				
		// Add a bar plot and labels to the PlotPanel
		plot2.addLinePlot("Power", frequency, power);
		plot2.setFixedBounds(0,0,fNyq);	//Manually set bounds on x-axis
		plot2.setFixedBounds(1,0,1.01*PMax); // Manually set bounds on y-axis
		plot2.setAxisLabels("Frequency", "Power");
		plot2.getAxis(0).setLabelPosition(0.5,-0.1);
		plot2.getAxis(0).setLabelFont(plotFont);
		plot2.getAxis(1).setLabelPosition(-0.15,0.5);
		plot2.getAxis(1).setLabelFont(plotFont);
		BaseLabel title2 = new BaseLabel("Power spectrum of " + FileChosen, Color.BLACK, 0.5, 1.1);
		title2.setFont(plotFont);
       		plot2.addPlotable(title2);
		
		// Put the PlotPanel in a JFrame, as a JPanel
		JFrame frame2 = new JFrame("Output 2");
		frame2.setSize(1024,576);
		frame2.setLocationRelativeTo(null);
		frame2.setContentPane(plot2);
		frame2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame2.setVisible(true);*/
	}
	
	private static File getFile()
    // The purpose of this method is that the user can select the file (wav or mp3) that the user will chose
    // Since our code will not be tested on our own computer, it is important that we allow the teacher to chose it himself from our folder
    // (Trying to get the file from a specific directory would result problematic)
    {
        JFileChooser fc = new JFileChooser(); // Empty constructor that points to user’s default directory
        int result = fc.showOpenDialog(null); // Pops up an "Open File" file chooser dialog; "null" means displayed at the center
        File file = null; // Declare "file"; "null" means empty object file
        if (result == JFileChooser.APPROVE_OPTION) // APPROVE_OPTION: Return value if approve (yes, ok) is chosen.
            file = fc.getSelectedFile(); // Then "file" will be the file selected by the user.
        return file; // The method returns the file selected by the user
    }
}