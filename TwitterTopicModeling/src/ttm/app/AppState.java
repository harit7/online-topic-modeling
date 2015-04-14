package ttm.app;



import java.io.Serializable;

import vagueobjects.ir.lda.online.OnlineLDA;
import vagueobjects.ir.lda.online.Result;
import vagueobjects.ir.lda.tokens.PlainVocabulary;

public class AppState implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 414042751959035080L;
	long lastDocId;
	long lastBatchId;
	OnlineLDA curOlda;
	OnlineLDA prevOlda;
	Result   curResult;
	Result   prevResult;
	PlainVocabulary vocab;

	public long getLastDocId() {
		return lastDocId;
	}
	public void setLastDocId(long lastDocId) {
		this.lastDocId = lastDocId;
	}
	public long getLastBatchId() {
		return lastBatchId;
	}
	public void setLastBatchId(long lastBatchId) {
		this.lastBatchId = lastBatchId;
	}
	public AppState(long lastDocId, long lastBatchId) {
		super();
		this.lastDocId = lastDocId;
		this.lastBatchId = lastBatchId;
	}
	public OnlineLDA getCurOlda() {
		return curOlda;
	}
	public void setCurOlda(OnlineLDA curOlda) {
		this.curOlda = curOlda;
	}
	public OnlineLDA getPrevOlda() {
		return prevOlda;
	}
	public void setPrevOlda(OnlineLDA prevOlda) {
		this.prevOlda = prevOlda;
	}
	public Result getCurResult() {
		return curResult;
	}
	public void setCurResult(Result curResult) {
		this.curResult = curResult;
	}
	public Result getPrevResult() {
		return prevResult;
	}
	public void setPrevResult(Result prevResult) {
		this.prevResult = prevResult;
	}
	public PlainVocabulary getVocab() {
		return vocab;
	}
	public void setVocab(PlainVocabulary vocab) {
		this.vocab = vocab;
	}
	
	
	
	
	
}
