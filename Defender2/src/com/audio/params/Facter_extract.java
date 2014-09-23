package com.audio.params;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


public class Facter_extract {
	
	 AudioInputStream currentSound;
	 byte[] audioData;  
	 //double QZProportion;
	/**
	 * @param args
	 * @return 
	 */
	public byte[] loadAudio(File file) throws Exception {   
        //�����Ƶ����...   
        currentSound = AudioSystem.getAudioInputStream(file);   
        AudioFormat format = currentSound.getFormat();   
        //����byte[]����   
       audioData = new byte[((int) currentSound.getFrameLength() * format.getFrameSize())];   
       // audioData=new byte[1000];
        //����Ƶ���ݶ��뵽byte[]����   
        currentSound.read(audioData); 
        return audioData;
    }   
	
//**********************************************************************************************
	//���������������
	protected void GetAValue (byte[] source)   
	{   
	    //��¼����Ƶ��   
	    double  CurrentCycle=HBaseCycle(source);   
	    System.out.println("����Ƶ��:"+CurrentCycle);
	    //Ƶ�ʱ仯��   
	    double  CurrentCycleRate=RBaseCycle(source);   
	    System.out.println("Ƶ�ʱ仯��:"+CurrentCycleRate);
	    //Ƶ�ʱ仯��Χ   
	    double[] CurrentChBase =ChBaseCycle(source);   
	    System.out.println("Ƶ�ʱ仯��Χ:"+CurrentChBase[0]+"---"+CurrentChBase[1]);
	    //����   
	    double CurrentBaseRange=(double)BaseRange(source); 
	    System.out.println("����:"+CurrentBaseRange);
	    //���ȱ仯��   
	    double CurrentRangeRate= ABaseRange(source);   
	    System.out.println("���ȱ仯��:"+CurrentRangeRate);
	    //��ʱ����   
	    double CurrentEnergy =(double)ASEnergy(source);   
	    System.out.println("��ʱ����:"+CurrentEnergy);
	    //��ʱƽ��������   
	    double CurrentPassZero=(double) APassZero(source);
	    System.out.println("��ʱƽ��������:"+CurrentPassZero);
	    //���������ȱ�   
	    //double Currentscale=QZProportion;
	    //System.out.println("���������ȱ�:"+Currentscale);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////
	  //��һ֡�����Ķ�ʱƽ�����Ȳ��,���Դ��ж���֡����������   
	//�ʺ�ԭʼ�������������ֱ������
	protected double BaseCycle(int index,byte[] source)   
	{   
	    int low=28;   
	    int high=200;   
	    int n=300;//30msΪһ����Ч֡   
	    int[] subArray= new int[n];//���ڴ�ŵ�i�����i+m(m=1,2,...n)�Ĳ�   
	    int[] compareArray=new int[high-low+1];//���ڴ��ÿ�αȽϺ����ֵ�ĺ�   
	    int[] compareindex=new int[high-low+1];   
	    int small=Integer.MAX_VALUE ;   
	    int s=0;   
	    int smallindex=0;   
	       
	    for (int t = low; t <=high; t++)   
	    {   
	        //����̶����ĵ�Ĳ�ֵ����ֵ   
	        for(int h=0;h<n;h++)   
	        {   
	            int temp=Math.abs(source[index*n+h]-source[index*n+h+t]);   
	            subArray[h]=temp;   
	        }   
	        int reslut=0;   
	        //����ù̶����ĵ�Ĳ�ֵ����ֵ�ĺ�    
	        for(int d=0;d<n;d++)   
	        {          
	           reslut=reslut+subArray[d];      
	        }   
	        compareArray[smallindex]=reslut;     
	        compareindex[smallindex]=t;   
	        smallindex++;   
	                          
	     }   
	     //�ҳ���С�Ĳ�ֵ�ĺ�   
	     for(int j=0;j<compareArray.length;j++)   
	     {   
	        if(small>compareArray[j])   
	        {   
	            small=compareArray[j];     
	            s=compareindex[j];   
	        }      
	     }   
	     double safter=(double)s;   
	     ////������s�����Ƶ��p   
	     double p =safter/10;   
	     p=1000/p;   
	     return p;     
	}   
/////// ////////////////////////////////////////////////////////////////////////////////////////////////////
	  //��������������ƽ��Ƶ��HΪwhole��д���ʺ�ԭʼ�������������ֱ������   
	    protected double HBaseCycle(byte[] source)   
	    {   
	            int z=0; //��֡������   
	            int n=300;//20msΪһ����Ч֡   
	            z=source.length/n;//����֡��������   
	            double newsource[]= new double[z];//��¼ÿһ֡��Ƶ��   
	            double totall=0;   
	            for(int i=0;i<z-3;i++)   
	            {   
	                newsource[i]=BaseCycle(i,source);   
	                totall=totall+newsource[i];   
	            }   
	            //����ƽ��Ƶ��   
	            totall=totall/(z-3);   
	            return totall;   
	    }   
///////////////////////////////////////////////////////////////////////////////////////////////////////
	  ///����Ƶ�ʱ仯��Χ���ʺ�ԭʼ�������������ֱ������
	    protected double[] ChBaseCycle(byte[] source)   
	    {   
	            int z=0; //��֡������   
	            int n=300;//20msΪһ����Ч֡   
	            z=source.length/n;//����֡��������   
	            double newsource[]= new double[z];//��¼ÿһ֡��Ƶ��   
	            double small;   
	            double large;   
	            double [] result=new double [2];//��¼��ߺ���͵�Ƶ��   
	            for(int i=0;i<z-3;i++)   
	            {   
	                newsource[i]=BaseCycle(i,source);   
	            }   
	            small=newsource[0];   
	            large=newsource[0];   
	            //�������Ƶ��   
	            for(int i=1;i<z-3;i++)   
	            {   
	                if(small>newsource[i])   
	                {   
	                    small=newsource[i];   
	                }   
	            }   
	            result[0]=small;   
	            //�������Ƶ��   
	            for(int i=1;i<z-3;i++)   
	            {   
	                if(large<newsource[i])   
	                {   
	                    large=newsource[i];   
	                }   
	            }   
	            result[1]=large;   
	            return result;   
	    }   
//////////////////////////////////////////////////////////////////////////////////////////////////////
	  ///����Ƶ�ʱ仯��//���ʺ�ԭʼ�������������ֱ������
	    protected double RBaseCycle(byte[] source)   
	    {   
	            int z=0; //��֡������   
	            int n=300;//20msΪһ����Ч֡   
	            z=source.length/n;//����֡��������   
	            double newsource[]= new double[z];//��¼ÿһ֡��Ƶ��   
	            double rate=0;//��¼�仯��   
	          //double av=HBaseCycle(source);//ƽ��Ƶ��   
	            for(int i=0;i<z-1;i++)   
	            {   
	                newsource[i]=BaseCycle(i,source);   
	            }   
	               
	            //����仯��   
	            for(int i=0;i<z-2;i++)   
	            {   
	                rate=rate+Math.abs(newsource[i]-newsource[i+1]);   
	            }   
	            //rate=Math.sqrt(rate);   
	            rate=rate/z;   
	            return rate;   
	    }   
///////////////////////////////////////////////////////////////////////////////////////////////////////
	  ///����ƽ������
	    protected int BaseRange(byte[] source)   
	    {   
	            int ra=0;   
	            for(int i=0;i<source.length;i++)   
	            {   
	                ra=ra+Math.abs(source[i]);   
	            }   
	            ra=(int)ra/source.length;   
	            return ra;   
	    }   
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  //����ƽ�����ȱ仯�� 
	    protected double ABaseRange(byte[] source)   
	    {   
	            double ra=0;   
	            BaseRange(source);
	            for(int i=0;i<source.length-1;i++)   
	            {   
	                ra=ra+Math.abs(source[i]-source[i+1]);   
	            }   
	            ra=ra/source.length;   
	            return ra;   
	    }        
////////////////////////////////////////////////////////////////////////////////////////////////////
	    //����һ֡�Ķ�ʱ����
	    protected long SEnergy(int index,byte[] source)   
	    {   
	        int n=300;//30msΪһ����Ч֡   
	        long result=0;   
	        //����һ֡������      
	        for(int m=0;m<n;m++)   
	        {   
	        	if(Math.abs(source[index*n+m])>=30){
	        		 result=result+(source[index*n+m]*source[index*n+m]);   
	        	}
	                   
	        }   
	            
	         return  result;       
	    }   
	       
	       
	    ///�������������ε�ƽ���Ķ�ʱ����   
	    protected long ASEnergy(byte[] source)   
	    {   
	        int z=0; //��֡������   
	        int n=300;//30msΪһ����Ч֡   
	        z=source.length/n;//����֡��������   
	          
	        long result=0;   
	        //�����ܵ�����       
	        for(int m=0;m<z;m++)   
	        {   
	            result=result+SEnergy(m,source);             
	        }   
	        //����ƽ������   
	         return  result/z;     
	    }   
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	  //��һ֡�����ʵĺ���
	    protected int PassZero(int index,byte[] source)   
	    {   
	        int n=300;//20msΪһ����Ч֡   
	        int result=0;//��¼һ֡�Ĺ�����   
	        //��һ֡�Ĺ�����   
	        for(int m=0;m<n-1;m++)   
	        {   
	           if(source[index*n+m]*source[index*n+m+1]<0)   
	           {   
	               result++;   
	           }   
	        }       
	        return result;   
	    }   
	       
	    //����������ƽ�������ʵĺ���   
	    protected int APassZero(byte[] source)   
	    {   
	        int z=0; //��֡������   
	        int n=300;//20msΪһ����Ч֡   
	        z=source.length/n;//����֡��������   
	        int result=0;//��¼ƽ���Ĺ�����   
	        //��ƽ���Ĺ�����   
	        for(int m=0;m<z;m++)   
	        {   
	           result=result+PassZero(m,source);   
	        }       
	        //����ƽ��������   
	        return result/z;   
	    }   
////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  //16λת��Ϊ8λ�ĺ���       
	    protected byte[] getByteArray(int[] source) {   
	           
	        byte[] toReturn = new byte[source.length * 2];   
	        int index = 0;   
	   
	        for (int t = 0; t < source.length; t++) {   
	            int sample = source[t];   
	               
	            toReturn[index] = (byte) sample;   
	            index++;   
	               
	            toReturn[index] = (byte) (sample >> 8);   
	            index++;   
	        }   
	   
	        return toReturn;   
	    }   
///////////////////////////////////////////////////////////////////////////////////////////////////
	  //�������ֱ�   
	    protected byte[] AudioIdentify(byte[] source)   
	    {   
	           
	        int z=0; //��֡������   
	        int n=300;//30msΪһ����Ч֡   
	        int move=150;   
	        int l=source.length;//��¼��������ĳ���   
	        z=source.length/n;//����֡��������   
	        //��������������   
	        //System.out.println(z);   
	        int[] chaoticArray = new int[source.length];   
	        //ȫ����ֵΪ0�����ں����ж϶��ಿ��   
	        for(int j=0;j<l;j++)   
	        {   
	            chaoticArray[j]=0;   
	        }   
	         //��������������   
	        int[] clearArray = new int[source.length];   
	        //����������±�   
	        int chindex=0;   
	        //����������±�   
	        int clindex=0;   
	       // boolean border=false;//��¼�����ж������Ƿ�����   
	        //int record=0;//��¼iֵ   
	        long etotal=ASEnergy(source);//���������εĶ�ʱ����   
	        //System.out.println(etotal);   
	        int passz=0;//��¼��ǰ֡�Ĺ�����   
	        long energy=0;//��¼��ǰ֡�Ķ�ʱ����   
	        //����˫���޷���֡���ӷ�����������������   
	        int count=-1;//������   
	        int i=0;//ѭ������   
	        while(i<l-1)   
	        {   
	            if(source[i]*source[i+1]<0)   
	            {   
	                passz++;   
	            }   
	            energy=energy+(source[i]*source[i]);   
	            count++;//����,300����Ϊһ֡   
	            if(count==n)   
	            {   
	                int temp=i-n;   
	                //���С��ƽ��������5/10��Ϊ����   
	                if(energy<=((etotal*3)/10))   
	                {   
	                    //���浽��������   
	                    for(int k=temp;k<i;k++)   
	                    {   
	                        clearArray[clindex]=source[k];   
	                        clindex++;   
	                    }   
	                    //������־����   
	                    count=0;   
	                    passz=0;   
	                    energy=0;   
	                    //i=i-move;   
	                    //��������ʴ���97����Ҳ�����������浽��������20ms��116��30msΪ174   
	                }else if(passz>=97)   
	                {   
	                    for(int k=temp;k<i;k++)   
	                    {   
	                        clearArray[clindex]=source[k];   
	                        clindex++;   
	                    }   
	                    //������־����   
	                    count=0;   
	                    passz=0;   
	                    energy=0;   
	                    //i=i-move;   
	                //����Ϊ���������浽��������   
	                }else   
	                {   
	                    for(int k=temp;k<i-move;k++)   
	                    {   
	                        chaoticArray[chindex]=source[k];   
	                        chindex++;   
	                    }   
	                    //������־����   
	                    count=0;   
	                    passz=0;   
	                    energy=0;    
	                    i=i-move;   
	                }   
	            }   
	        i++;   
	        }   
	        //����������������ʱ������δ֪������ȥ���������ĳ���       
	        int tempA=0;//��¼������������һ֡   
	        //����������������һ֡   
	        for(int j=0;j<z;j++)   
	        {   
	            if(SEnergy(j,getByteArray(chaoticArray))<=((etotal*3)/10))   
	            {   
	                tempA=j;   
	                break;   
	                   
	            }   
	        }   
	           
	        System.out.println(tempA);   
	        if(tempA!=0)   
	        //����Ϊ�µ����� �����������Ҫ��ȥ1֡    
	        {   
	            int [] RChaoticArray =new int[(tempA-1)*n];   
	        for(int j=0;j<RChaoticArray.length;j++)   
	        {   
	            RChaoticArray[j]=chaoticArray[j];   
	        }   
	        //QZProportion=rc;   
	         return getByteArray(RChaoticArray);   
	        }else return getByteArray(chaoticArray);   
	               
	    }   
	public static void main(String[] args) throws Exception {
		File file=new File("D:\\VOICE\\1\\1.wav");
		Facter_extract fe=new Facter_extract();
		fe.GetAValue(fe.loadAudio(file));
//		for(int i=0;i<fe.audioData.length;i++){
//		      System.out.println(fe.audioData[i]);
//		}
	}

}
