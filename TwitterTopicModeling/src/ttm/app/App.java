package ttm.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jnsp.Counter;
import ttm.topiclabel.TopicLabel;
import ttm.topiclabel.TopicLabeler;
import ttm.twitter.TweetReceiver;
import ttm.utils.TextCleaner;
import vagueobjects.ir.lda.online.OnlineLDA;
import vagueobjects.ir.lda.online.Result;
import vagueobjects.ir.lda.tokens.Documents;
import vagueobjects.ir.lda.tokens.PlainVocabulary;

public class App 
{
	
	
    public static void main(String[] args) throws Exception
    { 
        int D  =  Config.BATCH_SIZE;
        int K  =  Config.K;

        double tau =  1d;
        double kappa =  0.8d;

        double alpha = 1.d/K;
        double eta = 1.d/K;
        
        final char slash = Config.slash;
        boolean startAfresh = true;
        
        boolean live = false; // if true it will fetch live tweets and work on them.
         
        File dir 				 = new File(Config.ROOT_DATA_DIR);
    	String absDataDir        = dir.getAbsolutePath();
    	String absDocsDir 		 = absDataDir + slash + "docs";
    	String stopWordsFilePath = absDataDir + slash + Config.STOP_WORDS_FILE;
    	
        PlainVocabulary vocab    = null;
        
        String stateFilesDir     = absDataDir    + slash + "state";
        String appStateFilePath  = stateFilesDir + slash + "app_state.bin";
      
        
        OnlineLDA prevLda	= null;
        Result prevResult	= null;
        OnlineLDA curLda	= null;
        Result curResult	= null;
        AppState appState 	= new AppState(0,0); 
        //PlainVocabulary vocab	=	null;		
        
        Counter ngramCounter  = new Counter(AppUtils.getNSPOption());
        TopicLabeler labeler  = new TopicLabeler(ngramCounter);
        List<String> documents = new ArrayList<String>(); 
        
        long lastDocId    = 0;
        long batchId      = 0;
       
        try{
        	
        	appState      = AppUtils.readAppState(appStateFilePath);
        	prevLda	      = appState.getPrevOlda();
        	prevResult	  = appState.getPrevResult();
        	vocab 		  = appState.getVocab();
        	if(prevResult == null) 
        	{
        		prevResult = appState.getCurResult();
        	}
        	lastDocId = appState.getLastDocId();
        	batchId   = appState.getLastBatchId();
        	String lastBatchDoc = absDocsDir + slash + 
        						   AppUtils.getBatchFileName(appState.lastDocId, appState.lastBatchId);
        	
        	System.out.println("reading from : "+ lastBatchDoc); 
			documents   = AppUtils.readPrevBatchDocs(lastBatchDoc);
	
			System.out.println("loaded previous state.." );
			
			List<TopicLabel> listLabels = labeler.getTopicLabels(prevResult, documents, vocab);
			for(TopicLabel label: listLabels){
				System.out.println(label); 
			}
			
			curLda = appState.getCurOlda();
  
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        if(vocab == null)
        {
        	vocab = new PlainVocabulary( Config.vocabFilePath);
        }
        if(curLda == null || appState == null || startAfresh )
        {	
        	System.out.println("starting afresh"); 
        	
        	curLda   = new OnlineLDA(vocab.size(),K, D, alpha, eta, tau, kappa);
        	appState = new AppState(0, 0);
      
        	
        }
        if(documents == null || documents.size() < Config.BATCH_SIZE)
        {	
        	documents  = new ArrayList<String>();
        	for(int i = 0; i< Config.BATCH_SIZE ;i++){
        		documents.add(null);
        	}
        }
        
    	Scanner sc = new Scanner(System.in);
        System.out.println("Continue for Live Tweets?");
        sc.next();
      
        // data/docs/tweets_0.txt
        
        lastDocId++;
        batchId = 0;
       
       
        String tweetsFilePath  =   absDocsDir+ slash + "clubbeddata.txt";
        
        TextCleaner cleaner = new TextCleaner(stopWordsFilePath);
        
        if(live)
        {
        	tweetsFilePath   = absDocsDir + slash + "tweets_"+(lastDocId)+".txt";
	        String batchFileName    = AppUtils.getBatchFileName(lastDocId, batchId);
	        			
	        String batchFilePath    = absDocsDir + slash + batchFileName;
	        
	        TweetReceiver tweetReceiver = new TweetReceiver(tweetsFilePath);
	       
	        
	    	tweetReceiver.setCleaner(cleaner);    
	        
	    	tweetReceiver.getTweets();
        }
    	
         
    	
    	BufferedReader tweetReader = new BufferedReader(new FileReader(tweetsFilePath));

        while(true){ 
        	
        	prevLda  = curLda;
        	prevResult = curResult;
        	
        	for( int i = 0; i< Config.BATCH_SIZE;i++){
        		
        		documents.set(i,AppUtils.readTweets(tweetReader,cleaner)); 
        		System.out.println("read doc #"+(i+1)); 
        		  
        	}
        	System.out.println("Done Reading..");
        	
            updateVocab(documents, vocab);
            
            Documents docs = new Documents(documents, vocab);
            
          
            // create lda instance
        	curLda   = new OnlineLDA(vocab.size(),K, D, alpha, eta, tau, kappa);
        	
        	curResult = curLda.workOn(docs);
        	System.out.println(curResult); 
        	
       
        	
        	// detect emerging topics
        	// label the topics
        	
        	List<TopicLabel> listLabels = labeler.getTopicLabels(curResult, documents, vocab);
        	for(TopicLabel label: listLabels)
        	{
        		System.out.println(label); 
        	}
        	
        	listLabels = labeler.getTopicLabelsZeroOrder(curResult, documents, vocab);
        	for(TopicLabel label: listLabels)
        	{
        		System.out.println(label); 
        	}
        	
        	listLabels = labeler.getTopicLabelsBaseline(curResult, documents, vocab);
        	for(TopicLabel label: listLabels){
        		System.out.println(label); 
        	}
        	
        	
        	try {
				appState.setCurOlda(curLda);
				appState.setCurResult(curResult);
				appState.setPrevOlda(prevLda);
				appState.setPrevResult(prevResult);
				appState.setVocab(vocab);
				//appState.setDocuments(documents); 
				appState.setLastBatchId(batchId); 
				appState.setLastDocId(lastDocId); 
				
        		AppUtils.writeAppState(appState, appStateFilePath);
        		String batchFilePath = absDocsDir + slash + AppUtils.getBatchFileName(lastDocId, batchId);
        	
        		AppUtils.writeCurBatchDocs(documents, batchFilePath); 
        		
        		System.out.println("saved current state..");
        		batchId++;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        	System.out.println("press  key to continue"); 
        	//sc.next();
        	//System.exit(0);
        	//break;
        	
        }
    }
    private static void updateVocab(List<String> docs, PlainVocabulary vocab )
    {
    	for(String doc: docs)
    	{
    		
    		vocab.addWords(doc.split(" "));   
    	}
    }
}
