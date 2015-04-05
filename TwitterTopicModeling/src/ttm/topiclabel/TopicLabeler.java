package ttm.topiclabel;

import java.util.List;

import vagueobjects.ir.lda.online.Result;

public class TopicLabeler 
{
	public static String getTopicLabel(Result topicModel, List<String> documents)
	{
		
	}
	private static List<String> getCandidateLabels(List<String> docs)
	{
		// use n-grams
	}
	
	private double score(String label, double theta[])
	{
		// KL divergence
	}
}
