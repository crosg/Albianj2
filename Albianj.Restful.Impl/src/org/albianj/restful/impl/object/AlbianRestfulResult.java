package org.albianj.restful.impl.object;

import org.albianj.restful.object.IAlbianRestfulResult;
import org.albianj.verify.Validate;

public class AlbianRestfulResult implements IAlbianRestfulResult {
	int returnCode = 0;
	String returnMsg = null;
	Object rc = null;
	
	public AlbianRestfulResult(){
		
	}
	
	public AlbianRestfulResult(Object rc){
		this.returnCode = 0;
		this.returnMsg = "Success";
		this.rc = rc;
	}
	
	public AlbianRestfulResult(int no,String  msg){
		this.returnCode = no;
		this.returnMsg = msg;
	}

	@Override
	public int getReturnCode() {
		// TODO Auto-generated method stub
		return this.returnCode;
	}

	@Override
	public void setReturnCode(int no) {
		// TODO Auto-generated method stub
		this.returnCode = no;
	}

	@Override
	public String getReturnMsg() {
		// TODO Auto-generated method stub
		if(Validate.isNullOrEmptyOrAllSpace(returnMsg))
			return "Success";
		return this.returnMsg;
	}

	@Override
	public void setReturnMsg(String msg) {
		// TODO Auto-generated method stub
		this.returnMsg = msg;
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return this.rc;
	}

	@Override
	public void setResult(Object rc) {
		// TODO Auto-generated method stub
		this.rc = rc;
	}

}
