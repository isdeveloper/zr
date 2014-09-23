package com.audio.mfcc;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class Distance {

	public static final double[] Lib ={
		-2.201978847211832,2.3277179201607723,-3.076637329069093,1.3655801094342512,
		-3.3766482578147734,0.38311265367736463,-1.7237467995639053,0.10804789543084604,
		-2.1672096863089867,0.8240904930100761,-2.19932160375255,-0.07447006592221878};
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 */
	public double calculate(byte[] audioData) throws UnsupportedAudioFileException, IOException {
		// TODO Auto-generated method stub
		int nnumberofFilters = 24;
        int nlifteringCoefficient = 22;
        boolean oisLifteringEnabled = true;
        boolean oisZeroThCepstralCoefficientCalculated = false;
        int nnumberOfMFCCParameters = 12; //without considering 0-th
        double dsamplingFrequency = 8000.0;
        int nFFTLength = 512;
        
        double distance_sum=0;//距离返回值
        
        if (oisZeroThCepstralCoefficientCalculated)
          //take in account the zero-th MFCC
          nnumberOfMFCCParameters = nnumberOfMFCCParameters + 1;
        
        MFCC mfcc = new MFCC(nnumberOfMFCCParameters,
                             dsamplingFrequency,
                             nnumberofFilters,
                             nFFTLength,
                             oisLifteringEnabled, 
                             nlifteringCoefficient,
                             oisZeroThCepstralCoefficientCalculated);
        
        double[] av_parameters=mfcc.get_mfcc(audioData);
        
        System.out.println("MFCC parameters:");
        for (int i = 0; i < 12; i++) {
        	System.out.println(av_parameters[i]);
	    }
        
        //计算距离
        for(int i=0;i<nnumberOfMFCCParameters;i++){
			distance_sum=distance_sum+Math.abs(av_parameters[i]-Lib[i])*Math.abs(av_parameters[i]-Lib[i]);
		}
		System.out.println("几何距离为："+distance_sum);
		
		return distance_sum;
	}
}
