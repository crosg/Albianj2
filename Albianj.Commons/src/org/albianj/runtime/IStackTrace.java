package org.albianj.runtime;

public interface IStackTrace {
	public String getFileName();

	public void setFileName(String fileName);

	public String getMethodName();

	public void setMethodName(String methodName);

	public String getClassName();

	public void setClassName(String className);

	public int getLineNumber();

	public void setLineNumber(int lineNumber);
}
