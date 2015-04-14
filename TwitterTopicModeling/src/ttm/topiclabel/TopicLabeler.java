package ttm.topiclabel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import jnsp.Counter;
import ttm.app.Config;
import vagueobjects.ir.lda.online.Result;
import vagueobjects.ir.lda.tokens.Vocabulary;

public class TopicLabeler 
{
	Counter ngramCounter ;
	
	public TopicLabeler(Counter ngramCounter)
	{
		this.ngramCounter = ngramCounter;
	}
	
	public List<TopicLabel> getTopicLabelsZeroOrder(Result result, List<String> documents,Vocabulary vocab) throws IOException
	{
		List<TopicLabel> listLabels = new ArrayList<TopicLabel>(); 
		List<TopicLabel> candLabels = getCandidateLabels(documents);

		List<HashMap<String,Double>> topicModels = result.getTopicModels();
		int totalWC = 0;
		for(String doc: documents)
		{
			totalWC += doc.split(" ").length;
		}
		
		int i = 0;
		for(HashMap<String,Double> topicModel: topicModels)
		{
			
			listLabels.add(null);
			
			double max = -1;
			
			for(TopicLabel label: candLabels)
			{
				if(listLabels.contains(label)) continue;
				//double s = zeroOrderScore(label, topicModel, vocab);
				//double  s = score(label, topicModel, totalWC);
				double s  = discScore(topicModels, label, topicModel, Config.mu, totalWC);
				label.setScore(s); 
				s = s*Math.pow(label.labelCount, 1);
				
				if(s > max)
				{
					max = s;
					
					listLabels.set(i, label);
				}
				
			}
			i++;
		}
		
		return listLabels;
	}

	public List<TopicLabel> getTopicLabelsBaseline(Result result, List<String> documents,Vocabulary vocab) throws IOException
	{
		List<TopicLabel> listLabels = new ArrayList<TopicLabel>(); 
		List<TopicLabel> candLabels = getCandidateLabels(documents);

		List<HashMap<String,Double>> topicModels = result.getTopicModels();
		int totalWC = 0;
		for(String doc: documents)
		{
			totalWC += doc.split(" ").length;
		}
		
		int i = 0;
		for(HashMap<String,Double> topicModel: topicModels)
		{
			
			listLabels.add(null);
			
			double max = -1;
			
			for(TopicLabel label: candLabels)
			{
				if(listLabels.contains(label)) continue;
				//double s = zeroOrderScore(label, topicModel, vocab);
				//double  s = score(label, topicModel, totalWC);
				double s  = discScore(topicModels, label, topicModel, Config.mu, totalWC);
				label.setScore(s); 
				s = s*Math.pow(label.labelCount, 1);
				
				if(s > max)
				{
					max = s;
					
					listLabels.set(i, label);
				}
				
			}
			i++;
		}
		
		return listLabels;
	}

	public List<TopicLabel> getTopicLabels(Result result, List<String> documents,Vocabulary vocab) throws IOException
	{
		List<TopicLabel> listLabels = new ArrayList<TopicLabel>(); 
		List<TopicLabel> candLabels = getCandidateLabels(documents);

		List<HashMap<String,Double>> topicModels = result.getTopicModels();
		int totalWC = 0;
		for(String doc: documents)
		{
			totalWC += doc.split(" ").length;
		}
		
		int i = 0;
		for(HashMap<String,Double> topicModel: topicModels)
		{
			
			listLabels.add(null);
			
			double max = -1;
			
			for(TopicLabel label: candLabels)
			{
				if(listLabels.contains(label)) continue;
				//double s = zeroOrderScore(label, topicModel, vocab);
				//double  s = score(label, topicModel, totalWC);
				double s  = discScore(topicModels, label, topicModel, Config.mu, totalWC);
				label.setScore(s); 
				s = s*Math.pow(label.labelCount, 1);
				
				if(s > max)
				{
					max = s;
					
					listLabels.set(i, label);
				}
				
			}
			i++;
		}
		
		return listLabels;
	}
	
	private double discScore(List<HashMap<String, Double>> topicModels, TopicLabel l, HashMap<String,Double> theta, double mu, int totalWordsInDoc)
	{
		
		double b = 0;
		double k = topicModels.size();
		
		double a = ExpectedPMI(theta, l, totalWordsInDoc);
		
		for(HashMap<String,Double> theta_j: topicModels)
		{
			b = b + ExpectedPMI(theta_j, l, totalWordsInDoc);
		}
		
		if(k>1)
			return (1 + mu/(k-1) )*a  - (mu/(k-1)) * b;
		else
			return a;
	}
	private double ExpectedPMI(HashMap<String,Double> theta, TopicLabel l, int totalWordsInDoc)
	{
		double Epmi = 0;
		int i =0;
		for(String w: l.listWords)
		{
			if(theta.containsKey(w)) 
			{
				Epmi = Epmi + theta.get(w)* pmi(i, l, (double)l.counts.get(i)/totalWordsInDoc);
				//score = score +	0 - (theta.get(w) * ( Math.log(theta.get(w) )));
				
			}
			i++;
		    
		}

		return Epmi;
	}
	private double zeroOrderScore(TopicLabel label, HashMap<String,Double> theta, int totalWordsInDoc)
	{
		double score = 0;
		int n  = label.listWords.size();
		for(String w: label.listWords)
		{
			if(theta.containsKey(w)) 
			{
				//double wgl = 
				score = score + Math.log(n*theta.get(w));
			}
			
		}   
		
		return  0-score;
	}
	
	private double score(TopicLabel label, HashMap<String,Double> theta ,int totalWordsInDoc)
	{
		double score = 0;
		int i =0;
		for(String w: label.listWords)
		{
			if(theta.containsKey(w)) 
			{
				score = score + theta.get(w)* pmi(i, label, (double)label.counts.get(i)/totalWordsInDoc);
				//score = score +	0 - (theta.get(w) * ( Math.log(theta.get(w) )));
				
			}
			i++;
		    
		}

		return score * label.labelCount;
		
		// KL divergence
		
	}
	// pointwise mutual information
	private double pmi(int wordIdInLabel, TopicLabel label, double Pw_given_C)
	{
		// pmi
				// p(w,l)/p(w)*p(l) = p(w|l)*p(l)/ p(w)*p(l) = p(w|l)/p(w)
		return label.getPw_given_l().get(wordIdInLabel)/Pw_given_C;

	}
	
	
	private  List<TopicLabel> getCandidateLabels(List<String> docs) throws IOException
	{
		// use n-grams

		ngramCounter.count1(docs);
		//read output..
		String outFile = ngramCounter.getOption().cntFile;
		BufferedReader reader = new BufferedReader(new FileReader(outFile));
		String s = "";
		reader.readLine();// skip first line .
		//plays<>german<>2 11 5
		
		List<TopicLabel> listTopicLabels  = new ArrayList<TopicLabel>();
		while( (s= reader.readLine())!=null )
		{
			String toks[] = s.split("<>");
			String counts[] = toks[toks.length-1].split(" ");
			
			List<String> listWords = new ArrayList<String>();
			Collections.addAll(listWords, toks);
			
			listWords.remove(listWords.size()-1);
			
			List<Integer> listCounts = new ArrayList<Integer>();
			int labelCount   = Integer.parseInt(counts[0]);
			for(int i = 1; i<counts.length;i++)
			{
				listCounts.add(Integer.parseInt(counts[i]));
			}
			TopicLabel label = new TopicLabel(listWords, listCounts, labelCount);
			listTopicLabels.add(label);
		}
		reader.close();
		
		return listTopicLabels;

		
	}
}

