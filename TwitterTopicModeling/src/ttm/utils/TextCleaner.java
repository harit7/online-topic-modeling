package ttm.utils;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;


public class TextCleaner  
{
    private static HashSet<String> swlist;
    private static String stopWordsFilePath = "data/stopwords.txt";

    public TextCleaner( String stopWordsFilePath) throws IOException  
    {	
    	if(swlist == null)
    	{	
    		addStopWords(stopWordsFilePath);
    	}
    }
    
    public TextCleaner() throws IOException  
    {	
    	if(swlist == null)
    	{	
    		addStopWords(stopWordsFilePath);
    	}
    }
    
    public void addStopWords(String stopWordsFilePath) throws IOException
    {
        if(swlist == null)
        {
        	swlist            = new HashSet<String>();
        }
        
		FileReader fr     = new FileReader(stopWordsFilePath);
		BufferedReader br = new BufferedReader(fr);

		// build the set of stop words
		
		String word="";
		while((word=br.readLine())!= null)
		{
			swlist.add(word.trim());
		}
		br.close();
		
    }
    public String clean(String text) 
    {
		String cleantext = removeUrl(text);
		cleantext = cleantext.replaceAll("[^\\w\\s]", "").replaceAll(" +", " ");		
		cleantext  = removeStopWords( cleantext + "\n");
		
		return cleantext;
    }

    private String removeUrl(String commentstr)
    {
        /*String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return commentstr;*/
    	return commentstr.replaceAll("(https?|ftp|gopher|telnet|file|Unsure|http)://\\S+\\s?", "");
    }

    private String removeStopWords(String str)
    {    
    	String text="";
        
        StringTokenizer st = new StringTokenizer(str);
        int count = st.countTokens();
        
        for(int i=0;i<count;i++)
        {
            String word = st.nextToken();
            if(!swlist.contains(word.toLowerCase()))
            {
                text += word.toLowerCase() + " ";
            }
        }
        return text;
    }
}