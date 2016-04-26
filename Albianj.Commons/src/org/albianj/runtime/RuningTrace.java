package org.albianj.runtime;

public class RuningTrace {

	public static IStackTrace getTraceInfo() {
		StackTraceElement[] stacks = new Throwable().getStackTrace();
		int stacksLen = stacks.length;
		IStackTrace trace = new StackTrace();
		trace.setFileName(stacks[stacksLen - 1].getFileName());
		trace.setClassName(stacks[stacksLen - 1].getClassName());
		trace.setMethodName(stacks[stacksLen - 1].getMethodName());
		trace.setLineNumber(stacks[stacksLen - 1].getLineNumber());
		return trace;
	}

	public static IStackTrace getTraceInfo(Exception e) {

		StackTraceElement[] stacks = e.getStackTrace();
		IStackTrace trace = new StackTrace();
		trace.setFileName(stacks[0].getFileName());
		trace.setClassName(stacks[0].getClassName());
		trace.setMethodName(stacks[0].getMethodName());
		trace.setLineNumber(stacks[0].getLineNumber());
		return trace;
	}

	public static IStackTrace getThreadTraceInfo() {
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		int stacksLen = stacks.length;
		IStackTrace trace = new StackTrace();
		trace.setFileName(stacks[stacksLen - 1].getFileName());
		trace.setClassName(stacks[stacksLen - 1].getClassName());
		trace.setMethodName(stacks[stacksLen - 1].getMethodName());
		trace.setLineNumber(stacks[stacksLen - 1].getLineNumber());
		return trace;
	}
}
