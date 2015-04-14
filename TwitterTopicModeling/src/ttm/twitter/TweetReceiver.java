package ttm.twitter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import ttm.app.App;
import ttm.utils.TextCleaner;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class TweetReceiver 
{
    private FileWriter  writer;
    private TextCleaner cleaner ;
    
    private static int count = 0;
    private static long lastDocId  = -1;
    private static String rootDir;
    private static int docswritten = 0;
    public static  final String DEFAULT_OUT_FILE_PATH = "";
    
    
    private List<File> filesWritten;
    
    private Object batchLock;
    
    public TweetReceiver(String filePath) throws IOException 
    {
    	
    	
        this.writer  = new FileWriter(filePath);
        
        filesWritten = new ArrayList<File>();
        
        batchLock = new Object();
        
    }
    
   

   public void getTweets() throws TwitterException, IOException
   {
	   
        	
	    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	    twitterStream.addListener(listener);
	
	    FilterQuery filter = new FilterQuery();
	    filter.language(new String[]{"en"});
	    filter.locations(new double[][] { {57,5 },{ 101,39 }  }); //for indian subcontinent
	   // filter.locations(new double[][] { {-180,-90 },{ 180,90 }  }); // for the whole world
	    twitterStream.filter(filter);
	   // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
	    //twitterStream.sample();
	    

    }

 
    
    public  FileWriter getWriter() {
		return writer;
	}

	public void setWriter(FileWriter writer) {
		this.writer = writer;
	}
	
	public TextCleaner getCleaner() {
		return cleaner;
	}
	public void setCleaner(TextCleaner cleaner) {
		this.cleaner = cleaner;
	}
	
	StatusListener listener = new StatusListener()
    {
        public void onStatus(Status status) 
        {
            String text = status.getText(); 
           
            try 
            {

            	   text = text.toLowerCase();
            	   
				   if(cleaner != null)
				   {
					   
					   text = cleaner.clean(text);
				   }
				   
				   writer.append(text+"\n");
				   writer.flush();
			       count++;
			       
			       /*
				   if(count % MAX_TWEETS_PER_DOC == MAX_TWEETS_PER_DOC -1 )
				   {
					   writer.flush();
					   writer.close();
					
					   lastDocId++;
					   String newDocPath  = rootDir+ File.separatorChar+ "tweets_"+lastDocId+".txt";
					   writer      = new FileWriter(newDocPath);
					   setWriter(writer);
					   filesWritten.add(new File(newDocPath));
					   
					}
            	   }
				   if(filesWritten.size() == BATCH_SIZE)
				   {
//					   synchronized (filesWritten) {
//						   filesWritten.notifyAll();
//						   
//					   }
					   App.semaphore.release();
					   
					   System.out.println("notified filesWritten lock");
					   
					   App.semaphore.acquire();
					   System.out.println("waiting for batchlock");
					   
//					   synchronized (batchLock) 
//					   {
//						   batchLock.wait();
//					   }
					 
					   
					   
					   // clear
					
					   //Thread.sleep(60*1000); // sleep for 1 minute
					    * */
					   
				
				  
				  // Thread.sleep(200); // max 10 tweets per second
         

            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
        public void onException(Exception ex)
        {
            ex.printStackTrace();
        }
        public void onScrubGeo(long userId, long upToStatusId){}
        public void onStallWarning(StallWarning warning){}
    };

	public static int getCount() {
		return count;
	}

	public Object getBatchLock() {
		return batchLock;
	}



	public List<File> getFilesWritten() {
		return filesWritten;
	}
	
	public void clearBuffer()
	{
		filesWritten = new ArrayList<File>();
	}
}

