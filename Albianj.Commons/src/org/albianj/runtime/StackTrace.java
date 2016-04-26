package org.albianj.runtime;

public class StackTrace implements IStackTrace {

	private String fileName = null;
	private String methodName = null;
	private String className = null;
	private int lineNumber = 0;

	public String getMethodName() {
		// TODO Auto-generated method stub
		return this.methodName;
	}

	public void setMethodName(String methodName) {
		// TODO Auto-generated method stub
		this.methodName = methodName;
	}

	public String getClassName() {
		// TODO Auto-generated method stub
		return this.className;
	}

	public void setClassName(String className) {
		// TODO Auto-generated method stub
		this.className = className;
	}

	public int getLineNumber() {
		// TODO Auto-generated method stub
		return this.lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		// TODO Auto-generated method stub
		this.lineNumber = lineNumber;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(" File:").append(this.fileName).append(", ").append("Class:")
				.append(this.className).append(", ").append("Method:")
				.append(this.methodName).append(", ").append("Line:")
				.append(this.lineNumber).append(". ");
		return sb.toString();
	}

}
