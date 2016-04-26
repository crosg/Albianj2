package org.albianj.restful.object;

public interface IAlbianRestfulResult {
	int getReturnCode();
	void setReturnCode(int no);
	
	String getReturnMsg();
	void setReturnMsg(String msg);
	
	Object getResult();
	void setResult(Object rc);
}
