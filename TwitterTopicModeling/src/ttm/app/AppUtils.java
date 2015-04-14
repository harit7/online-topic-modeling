package ttm.app;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import ttm.utils.TextCleaner;
import vagueobjects.ir.lda.online.OnlineLDA;
import jnsp.NSPOption;

public class AppUtils
{
	private static 	int a [] = new int[10];
	 static{ 
	
		for(int i = 1; i< 11;i++)
		{
			int p = (Config.MAX_TWEETS_PER_DOC*i)/10;
			a[i-1] = p;
			
		}
	 }
	  static String readDoc(File doc, TextCleaner cleaner) throws IOException
	    {
	    	String content = "";
	        FileReader fr = new FileReader(doc);
	        BufferedReader br = new BufferedReader(fr);
	        String s = "";
	        
	        while((s= br.readLine())!= null)
	        {
	        	if(cleaner != null)
	        		s= cleaner.clean(s); 
	        	
	        	content += s+"\n";
	        }
	        
	        br.close();
	        return content;

	    }
	    static List<String> readDocs(String path, TextCleaner cleaner) throws IOException
	    {

	        List<String> strings = new ArrayList<String>();
	        File dir = new File(path);
	        for(File f :  dir.listFiles())
	        {
	            
	        	if(f.isDirectory()) continue;
	            
	            strings.add(readDoc(f,cleaner)); 
	        

	        }
	        return strings;
	    }
	    
	    static long getLastDocId(String docsDir) 
	    {
	    	long max = -1;
	    	try
	        {
		        File dir = new File(docsDir);
		        
		        for(File f :  dir.listFiles()){
		        	if(f.isDirectory()) continue;
		        	
		        	String fname = f.getName();
		        	int st = fname.indexOf("_");
		        	String n = fname.substring(st+1,fname.length()-4) ; // -4 for .txt e.g. tweets_1001.txt 
		        	Long num =  Long.parseLong(n);
		        	if(num > max) max = num;
		        }
	        }
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	        return max;
	            	
	    }
	    
	    public static String readTweets( BufferedReader reader,TextCleaner cleaner) throws IOException
	    {
	    	// MAX_TWEETS_PER_DOC tweets separated by \n .. serves as a document
	    	String doc = "";
	    	int lr     = 0;
	    	String tmp = "";
	    	while( lr < Config.MAX_TWEETS_PER_DOC)
	    	{
	    		tmp = reader.readLine();
	    		 
	    		if(tmp == null ) {
	    			try{
	    				Thread.sleep(500);
	    			}
	    			catch(InterruptedException e)
	    			{
	    				e.printStackTrace();
	    			}
	    			continue; 
	    		}
	    		else
	    		{
	    			if(cleaner != null) tmp = cleaner.clean(tmp);
	    			doc    = doc + tmp + "\n";
	    			lr++;
	    			
	    			//if(lr%100 == 0) System.out.print(lr+" ");
	    			//if(lr%1000 ==0) System.out.println();
	    			
	    			for(int i = 0;i< a.length;i++)
	    			{
	    				if(a[i] == lr) System.out.print((i+1)*10+"% "); 
	    			}
	    			
	    			//System.out.print(lr*100/MAX_TWEETS_PER_DOC +"% \r");
	    			 
	    			
	    		}
	    	}
	    	System.out.println();
	    	return doc;
	    }
	    
	    public static AppState readAppState(String filePath) throws IOException, ClassNotFoundException
	    {	
	    	AppState appState = null;
	    	try{
	    	FileInputStream fis   = new FileInputStream(filePath);
	    	ObjectInputStream ois = new ObjectInputStream(fis);
	    	Object obj     = ois.readObject();
	    
	    	if(obj != null)
	    	{
	    		 appState = (AppState)obj;
	    	}
	    	ois.close();
	    	fis.close();
	    	}
	    	catch(EOFException e)
	    	{
	    		System.out.println(e.getMessage()); 
	    	}
	    	return appState;
	    	
	    	
	    }
	    public static void writeAppState(AppState obj, String filePath) throws IOException
	    {
	    	FileOutputStream fos   = new FileOutputStream(filePath);
	    	ObjectOutputStream oos = new ObjectOutputStream(fos);
	    	
	    	oos.writeObject(obj);

	    	oos.close();

	
	    }
	    
	    public static OnlineLDA readOLDAObject(String filePath) throws IOException, ClassNotFoundException
	    {
	    	FileInputStream fis   = new FileInputStream(filePath);
	    	ObjectInputStream ois = new ObjectInputStream(fis);
	    	Object obj            = ois.readObject();
	    	OnlineLDA lda = null;
	    	if(obj != null)
	    	{
	    		 lda = (OnlineLDA)obj;
	    	}
	    	ois.close();
	    	return lda;
	    	
	    	
	    }
	    public static void writeOLDAObject(OnlineLDA obj, String filePath) throws IOException
	    {
	    	FileOutputStream fos   = new FileOutputStream(filePath);
	    	ObjectOutputStream oos = new ObjectOutputStream(fos);
	    	
	    	oos.writeObject(obj);
	    	
	    	oos.close();
	
	    }
	    
	    public static NSPOption getNSPOption()
	    {   
	    	
	    	String absRootDataDir = new File(Config.ROOT_DATA_DIR).getAbsolutePath();
	    	
	    	NSPOption option = new NSPOption();
	    	option.window    = 2;
			option.stopFile  = new File(absRootDataDir + File.separatorChar + Config.STOP_WORDS_FILE);
			option.cntFile   = absRootDataDir + File.separatorChar + "tmp"+File.separatorChar+"ngram_out";
			option.agressiveCount = false;
			option.freqCutOff  = -1;
			option.rareCutOff  = -1;
			//option.statFile    = "";
			option.statlib     = "TScore2D";
			option.ngramCache  = true;
			
			return option;
					
	    }
	    
	    public static void writeCurBatchDocs(List<String> documents, String batchFilePath) throws IOException
	    {
	    	FileOutputStream fos   = new FileOutputStream(batchFilePath);
	    	ObjectOutputStream oos = new ObjectOutputStream(fos);
	    	
	    	oos.writeObject(documents);

	    	oos.close();
	    }
	    
	    public static List<String> readPrevBatchDocs(String filePath) throws IOException, ClassNotFoundException
	    {
	    	FileInputStream fis   = new FileInputStream(filePath);
	    	ObjectInputStream ois = new ObjectInputStream(fis);
	    	Object obj            = ois.readObject();
	    	List<String> docs     = null;
	    	if(obj != null)
	    	{
	    		 docs = (List<String>)obj;
	    	}
	    	ois.close();
	    	
	    	return docs;
	    	
	    	
	    }
	    
	    public static String getBatchFileName( long tweetsDocId, long batchId)
	    {
	        String batchFileName    = Config.BATCH_DOC_FILE.replace("$tid", ""+tweetsDocId)
					.replace("$bid", ""+batchId); 
	        return batchFileName;
	    }
}
