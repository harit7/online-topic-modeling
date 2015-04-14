package ttm.app;

import java.io.File;

public class Config 
{
	public static final int MAX_TWEETS_PER_DOC = 4096 ;
    public static final int BATCH_SIZE  =  4;
    
    public static final int K 			= 5;
    
	public static final char slash               = File.separatorChar;
	public static final String ROOT_DATA_DIR     = "data";
	
	public static final String STOP_WORDS_FILE   = "stop-words_english_3_en.txt";
	
	public static final String BATCH_DOC_FILE    = "tweets_$tid_batch_$bid.txt";
	
    public static final String vocabFilePath     = "data/vocab.txt";
    public static final String stopWordsFilePath = "data/stopwords.txt";
    public static final String docsDir           = "data/docs/";
    
    public static final String OLDA_STATE_FILE   = "olda_state.bin" ;
    public static final String APP_STATE_FILE    = "app_state.bin" ;
    
    public static final double EPSELON           = 0.00001;
    public static final double mu				 = 0.07;
    
	
	
}
