package ttm.topiclabel;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.EncryptedPrivateKeyInfo;

import ttm.app.Config;

public class TopicLabel
{
	List<String> listWords;
	List<Integer> counts;
	int labelCount;
	double score;
	
	List<Double> Pw_given_l; // pr of word i

	
	public TopicLabel(List<String> listWords, List<Integer> counts,
			int labelCount) {
		super();
		this.listWords = listWords;
		this.counts = counts;
		this.labelCount = labelCount;
		double sum = 0;
		for(int wc: this.counts)
		{
			sum += wc; 
		}
		
		Pw_given_l = new ArrayList<Double>();
		for(int wc: this.counts)
		{
			Pw_given_l.add((wc/sum));
		}
	
		
	}
	public List<String> getListWords() {
		return listWords;
	}
	public void setListWords(List<String> listWords) {
		this.listWords = listWords;
	}
	public List<Integer> getCounts() {
		return counts;
	}
	public void setCounts(List<Integer> counts) {
		this.counts = counts;
	}
	public int getLabelCount() {
		return labelCount;
	}
	public void setLabelCount(int labelCount) {
		this.labelCount = labelCount;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	} 
	
	public List<Double> getPw_given_l() {
		return Pw_given_l;
	}
	public void setPw_given_l(List<Double> pw_given_l) {
		Pw_given_l = pw_given_l;
	}
	public String toString()
	{
		String out = "";
		
		for(String s: listWords)
		{
			out = out + s + " ";
			
		}
		
		return out + " : " + score +"\t"+ labelCount;
		
	}
	
	@Override
	public boolean equals(Object label)
	{
		if(label == this) return true;
		if(! (label instanceof TopicLabel)) return false;
		
		TopicLabel l = (TopicLabel) label;
		
		boolean x = true;
		for(String w: l.listWords)
		{
			for(String w2: this.listWords)
			{
				if(!w.equals(w2))
				{
					x = false;
				}
				
			}
		}
		return x;
	}

	/*
	 *  sim(l,l') = -D(l'||l) = - sigma_over_w( p(w|l')* log(p(w|l') / p(w|l) )
	 * */
	public static  double similarity(TopicLabel l1,TopicLabel l2)
	{
		double sim = 0;
		int i = 0;
		for(String w: l2.listWords)
		{
			if(l1.listWords.contains(w))
			{
				int j = l1.listWords.indexOf(w);
				sim +=  l2.Pw_given_l.get(i) * Math.log( l2.Pw_given_l.get(i)/ l1.Pw_given_l.get(j));
			}
			else
			{
				sim +=  l2.Pw_given_l.get(i) * Math.log( l2.Pw_given_l.get(i)/Config.EPSELON);
			}
			i++;
		}
		return sim;
	}
}
