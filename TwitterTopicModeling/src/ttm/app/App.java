package ttm.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ttm.twitter.TweetReceiver;
import ttm.utils.TextCleaner;
import vagueobjects.ir.lda.online.OnlineLDA;
import vagueobjects.ir.lda.online.Result;
import vagueobjects.ir.lda.tokens.Documents;
import vagueobjects.ir.lda.tokens.PlainVocabulary;
import vagueobjects.ir.lda.tokens.Vocabulary;

public class App {
 
	public static final int MAX_TWEETS_PER_DOC = 32;
    public static final int BATCH_SIZE  =  4;

    public static void main(String[] args) throws Exception
    { 
        int D=  10800;
        int K = 5;
        

        double tau =  1d;
        double kappa =  0.8d;

        double alpha = 1.d/K;
        double eta = 1.d/K;
        
        String vocabFilePath     = "data/vocab.txt";
        String stopWordsFilePath = "data/stopwords.txt";
        String docsDir           = "data/docs/";
        
        String stateFile 	     = "data/olda_state.bin" ;
        
        Vocabulary vocabulary    = new PlainVocabulary( vocabFilePath);
        OnlineLDA lda		     = null;
        
        try
        {
        	lda = readOLDAObject(stateFile);
        	
        }
        catch(Exception e)
        {
        	//e.printStackTrace();
        }
        if(lda == null)
        {	
        	System.out.println("starting afresh"); 
        	lda = new OnlineLDA(vocabulary.size(),K, D, alpha, eta, tau, kappa);
        }
        else
        {
        	System.out.println("loaded previous state.." ); 
        }
        
        List<String> documents = new ArrayList<String>(); 
        
        File dir = new File(docsDir);
    	String absDocsDir = dir.getAbsolutePath();
        
        long lastDocId          = getLastDocId(docsDir); 
        String tweetsFilePath   = absDocsDir+ File.separatorChar + "tweets_"+lastDocId+".txt";

    	Scanner sc = new Scanner(System.in);
        System.out.println("Continue for Live Tweets?");
        sc.next();
        lastDocId++;

        TweetReceiver tweetReceiver = new TweetReceiver(tweetsFilePath);
       
        TextCleaner cleaner = new TextCleaner(stopWordsFilePath);
    	tweetReceiver.setCleaner(cleaner); 
    	
    	tweetReceiver.getTweets();
    	BufferedReader tweetReader = new BufferedReader(new FileReader(tweetsFilePath));
    	
    	/// init reading buffer
    	for(int i = 0; i< BATCH_SIZE ;i++)
    	{
    		documents.add(null);
    	}
    	
        while(true)
        {
        	
        	
        	for( int i = 0; i< BATCH_SIZE;i++)
        	{
        		
        		documents.set(i,readTweets(tweetReader)); 
        		System.out.println("read doc #"+(i+1)); 
        		  
        	}
        	System.out.println("Done Reading.."); 
            Documents docs = new Documents(documents, vocabulary);
        	Result result = lda.workOn(docs);
        	System.out.println(result); 
        	
        	// update vocab..
        	// detect emerging topics
        	// label the topics
        	
        	System.out.println("press  key to continue"); 
        	sc.next();
        	try {
				
        		writeOLDAObject(lda, stateFile);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        }
        
   
    }
    
    static String readDoc(File doc) throws IOException
    {
    	String content = "";
        FileReader fr = new FileReader(doc);
        BufferedReader br = new BufferedReader(fr);
        String s = "";
        
        while((s= br.readLine())!= null)
        {
        	content += s+"\n";
        }
        
        br.close();
        return content;

    }
    static List<String> readDocs(String path) throws IOException
    {

        List<String> strings = new ArrayList<String>();
        File dir = new File(path);
        for(File f :  dir.listFiles())
        {
            
        	if(f.isDirectory()) continue;
            
            strings.add(readDoc(f)); 
        

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
    
    public static String readTweets( BufferedReader reader) throws IOException
    {
    	// MAX_TWEETS_PER_DOC tweets separated by \n .. serves as a document
    	String doc = "";
    	int lr     = 0;
    	String tmp = "";
    	while( lr < MAX_TWEETS_PER_DOC)
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
    			doc    = doc + tmp + "\n";
    			lr++;
    			System.out.print(lr+" ");
    			if(lr%20 == 0) System.out.println();
    			//System.out.print(lr*100/MAX_TWEETS_PER_DOC +"% \r");
    			 
    			
    		}
    	}
    	System.out.println();
    	return doc;
    }
    
    public static OnlineLDA readOLDAObject(String filePath) throws IOException, ClassNotFoundException
    {
    	FileInputStream fis   = new FileInputStream(filePath);
    	ObjectInputStream ois = new ObjectInputStream(fis);
    	Object obj     = ois.readObject();
    	OnlineLDA olda = null;
    	if(obj != null)
    	{
    		 olda = (OnlineLDA)ois.readObject();
    	}
    	ois.close();
    	
    	return olda;
    	
    	
    }
    public static void writeOLDAObject(OnlineLDA obj, String filePath) throws IOException
    {
    	FileOutputStream fos   = new FileOutputStream(filePath);
    	ObjectOutputStream oos = new ObjectOutputStream(fos);
    	
    	oos.writeObject(obj);
    	//fos.flush();
    	//fos.close();
    	oos.flush();
    	oos.close();
    	
    	
    }

}
