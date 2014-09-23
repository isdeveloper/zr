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
        //获得音频输入...   
        currentSound = AudioSystem.getAudioInputStream(file);   
        AudioFormat format = currentSound.getFormat();   
        //创造byte[]数组   
       audioData = new byte[((int) currentSound.getFrameLength() * format.getFrameSize())];   
       // audioData=new byte[1000];
        //把音频数据读入到byte[]数组   
        currentSound.read(audioData); 
        return audioData;
    }   
	
//**********************************************************************************************
	//计算各项特征参数
	protected void GetAValue (byte[] source)   
	{   
	    //记录基音频率   
	    double  CurrentCycle=HBaseCycle(source);   
	    System.out.println("基音频率:"+CurrentCycle);
	    //频率变化率   
	    double  CurrentCycleRate=RBaseCycle(source);   
	    System.out.println("频率变化率:"+CurrentCycleRate);
	    //频率变化范围   
	    double[] CurrentChBase =ChBaseCycle(source);   
	    System.out.println("频率变化范围:"+CurrentChBase[0]+"---"+CurrentChBase[1]);
	    //幅度   
	    double CurrentBaseRange=(double)BaseRange(source); 
	    System.out.println("幅度:"+CurrentBaseRange);
	    //幅度变化率   
	    double CurrentRangeRate= ABaseRange(source);   
	    System.out.println("幅度变化率:"+CurrentRangeRate);
	    //短时能量   
	    double CurrentEnergy =(double)ASEnergy(source);   
	    System.out.println("短时能量:"+CurrentEnergy);
	    //短时平均过零率   
	    double CurrentPassZero=(double) APassZero(source);
	    System.out.println("短时平均过零率:"+CurrentPassZero);
	    //清浊音长度比   
	    //double Currentscale=QZProportion;
	    //System.out.println("清浊音长度比:"+Currentscale);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////
	  //求一帧语音的短时平均幅度差函数,并以此判断这帧的语音周期   
	//适合原始语音和清浊音分辨后语音
	protected double BaseCycle(int index,byte[] source)   
	{   
	    int low=28;   
	    int high=200;   
	    int n=300;//30ms为一个有效帧   
	    int[] subArray= new int[n];//用于存放第i点与第i+m(m=1,2,...n)的差   
	    int[] compareArray=new int[high-low+1];//用于存放每次比较后各差值的和   
	    int[] compareindex=new int[high-low+1];   
	    int small=Integer.MAX_VALUE ;   
	    int s=0;   
	    int smallindex=0;   
	       
	    for (int t = low; t <=high; t++)   
	    {   
	        //计算固定差距的点的差值绝对值   
	        for(int h=0;h<n;h++)   
	        {   
	            int temp=Math.abs(source[index*n+h]-source[index*n+h+t]);   
	            subArray[h]=temp;   
	        }   
	        int reslut=0;   
	        //计算该固定差距的点的差值绝对值的和    
	        for(int d=0;d<n;d++)   
	        {          
	           reslut=reslut+subArray[d];      
	        }   
	        compareArray[smallindex]=reslut;     
	        compareindex[smallindex]=t;   
	        smallindex++;   
	                          
	     }   
	     //找出最小的差值的和   
	     for(int j=0;j<compareArray.length;j++)   
	     {   
	        if(small>compareArray[j])   
	        {   
	            small=compareArray[j];     
	            s=compareindex[j];   
	        }      
	     }   
	     double safter=(double)s;   
	     ////将周期s换算成频率p   
	     double p =safter/10;   
	     p=1000/p;   
	     return p;     
	}   
/////// ////////////////////////////////////////////////////////////////////////////////////////////////////
	  //计算整个语音的平均频率H为whole简写，适合原始语音和清浊音分辨后语音   
	    protected double HBaseCycle(byte[] source)   
	    {   
	            int z=0; //分帧的数量   
	            int n=300;//20ms为一个有效帧   
	            z=source.length/n;//计算帧的总数量   
	            double newsource[]= new double[z];//记录每一帧的频率   
	            double totall=0;   
	            for(int i=0;i<z-3;i++)   
	            {   
	                newsource[i]=BaseCycle(i,source);   
	                totall=totall+newsource[i];   
	            }   
	            //计算平均频率   
	            totall=totall/(z-3);   
	            return totall;   
	    }   
///////////////////////////////////////////////////////////////////////////////////////////////////////
	  ///计算频率变化范围，适合原始语音和清浊音分辨后语音
	    protected double[] ChBaseCycle(byte[] source)   
	    {   
	            int z=0; //分帧的数量   
	            int n=300;//20ms为一个有效帧   
	            z=source.length/n;//计算帧的总数量   
	            double newsource[]= new double[z];//记录每一帧的频率   
	            double small;   
	            double large;   
	            double [] result=new double [2];//记录最高和最低的频率   
	            for(int i=0;i<z-3;i++)   
	            {   
	                newsource[i]=BaseCycle(i,source);   
	            }   
	            small=newsource[0];   
	            large=newsource[0];   
	            //查找最低频率   
	            for(int i=1;i<z-3;i++)   
	            {   
	                if(small>newsource[i])   
	                {   
	                    small=newsource[i];   
	                }   
	            }   
	            result[0]=small;   
	            //查找最高频率   
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
	  ///计算频率变化率//，适合原始语音和清浊音分辨后语音
	    protected double RBaseCycle(byte[] source)   
	    {   
	            int z=0; //分帧的数量   
	            int n=300;//20ms为一个有效帧   
	            z=source.length/n;//计算帧的总数量   
	            double newsource[]= new double[z];//记录每一帧的频率   
	            double rate=0;//记录变化率   
	          //double av=HBaseCycle(source);//平均频率   
	            for(int i=0;i<z-1;i++)   
	            {   
	                newsource[i]=BaseCycle(i,source);   
	            }   
	               
	            //计算变化率   
	            for(int i=0;i<z-2;i++)   
	            {   
	                rate=rate+Math.abs(newsource[i]-newsource[i+1]);   
	            }   
	            //rate=Math.sqrt(rate);   
	            rate=rate/z;   
	            return rate;   
	    }   
///////////////////////////////////////////////////////////////////////////////////////////////////////
	  ///计算平均幅度
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
	  //计算平均幅度变化率 
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
	    //计算一帧的短时能量
	    protected long SEnergy(int index,byte[] source)   
	    {   
	        int n=300;//30ms为一个有效帧   
	        long result=0;   
	        //计算一帧的能量      
	        for(int m=0;m<n;m++)   
	        {   
	        	if(Math.abs(source[index*n+m])>=30){
	        		 result=result+(source[index*n+m]*source[index*n+m]);   
	        	}
	                   
	        }   
	            
	         return  result;       
	    }   
	       
	       
	    ///计算整个语音段的平均的短时能量   
	    protected long ASEnergy(byte[] source)   
	    {   
	        int z=0; //分帧的数量   
	        int n=300;//30ms为一个有效帧   
	        z=source.length/n;//计算帧的总数量   
	          
	        long result=0;   
	        //计算总的能量       
	        for(int m=0;m<z;m++)   
	        {   
	            result=result+SEnergy(m,source);             
	        }   
	        //返回平均能量   
	         return  result/z;     
	    }   
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	  //求一帧过零率的函数
	    protected int PassZero(int index,byte[] source)   
	    {   
	        int n=300;//20ms为一个有效帧   
	        int result=0;//记录一帧的过零率   
	        //求一帧的过零率   
	        for(int m=0;m<n-1;m++)   
	        {   
	           if(source[index*n+m]*source[index*n+m+1]<0)   
	           {   
	               result++;   
	           }   
	        }       
	        return result;   
	    }   
	       
	    //求整个语音平均过零率的函数   
	    protected int APassZero(byte[] source)   
	    {   
	        int z=0; //分帧的数量   
	        int n=300;//20ms为一个有效帧   
	        z=source.length/n;//计算帧的总数量   
	        int result=0;//记录平均的过零率   
	        //求平均的过零率   
	        for(int m=0;m<z;m++)   
	        {   
	           result=result+PassZero(m,source);   
	        }       
	        //返回平均过零率   
	        return result/z;   
	    }   
////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  //16位转换为8位的函数       
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
	  //清浊音分辨   
	    protected byte[] AudioIdentify(byte[] source)   
	    {   
	           
	        int z=0; //分帧的数量   
	        int n=300;//30ms为一个有效帧   
	        int move=150;   
	        int l=source.length;//记录语音数组的长度   
	        z=source.length/n;//计算帧的总数量   
	        //保存浊音的数组   
	        //System.out.println(z);   
	        int[] chaoticArray = new int[source.length];   
	        //全部赋值为0，用于后面判断多余部分   
	        for(int j=0;j<l;j++)   
	        {   
	            chaoticArray[j]=0;   
	        }   
	         //保存清音的数组   
	        int[] clearArray = new int[source.length];   
	        //浊音数组的下标   
	        int chindex=0;   
	        //清音数组的下标   
	        int clindex=0;   
	       // boolean border=false;//记录两次判断区域是否相邻   
	        //int record=0;//记录i值   
	        long etotal=ASEnergy(source);//整个语音段的短时能量   
	        //System.out.println(etotal);   
	        int passz=0;//记录当前帧的过零率   
	        long energy=0;//记录当前帧的短时能量   
	        //利用双门限法和帧叠加法来区分清音和浊音   
	        int count=-1;//计数器   
	        int i=0;//循环变量   
	        while(i<l-1)   
	        {   
	            if(source[i]*source[i+1]<0)   
	            {   
	                passz++;   
	            }   
	            energy=energy+(source[i]*source[i]);   
	            count++;//计数,300个点为一帧   
	            if(count==n)   
	            {   
	                int temp=i-n;   
	                //如果小于平均能量的5/10，为清音   
	                if(energy<=((etotal*3)/10))   
	                {   
	                    //保存到清音数组   
	                    for(int k=temp;k<i;k++)   
	                    {   
	                        clearArray[clindex]=source[k];   
	                        clindex++;   
	                    }   
	                    //各个标志清零   
	                    count=0;   
	                    passz=0;   
	                    energy=0;   
	                    //i=i-move;   
	                    //如果过零率大于97，则也是清音，保存到浊音数组20ms是116，30ms为174   
	                }else if(passz>=97)   
	                {   
	                    for(int k=temp;k<i;k++)   
	                    {   
	                        clearArray[clindex]=source[k];   
	                        clindex++;   
	                    }   
	                    //各个标志清零   
	                    count=0;   
	                    passz=0;   
	                    energy=0;   
	                    //i=i-move;   
	                //否则为浊音，保存到浊音数组   
	                }else   
	                {   
	                    for(int k=temp;k<i-move;k++)   
	                    {   
	                        chaoticArray[chindex]=source[k];   
	                        chindex++;   
	                    }   
	                    //各个标志清零   
	                    count=0;   
	                    passz=0;   
	                    energy=0;    
	                    i=i-move;   
	                }   
	            }   
	        i++;   
	        }   
	        //声明保存浊音数组时，长度未知，现在去掉后面多余的长度       
	        int tempA=0;//记录浊音数组的最后一帧   
	        //查找浊音数组的最后一帧   
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
	        //保存为新的数组 如果有浊音则要减去1帧    
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
