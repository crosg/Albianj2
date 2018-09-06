package org.albianj.runtime;

import org.albianj.comment.Comments;
import org.albianj.comment.SpecialWarning;
import org.albianj.datetime.AlbianDateTime;
import org.albianj.verify.Validate;
import sun.misc.resources.Messages_de;

/**
 * Created by xuhaifeng on 17/2/14.
 */
public class AlbianRuntimeException extends RuntimeException {
//    private IStackTrace currentStackTrace = null;
    private String filename;
    private String methodName;
    private int lineNumber;
    private String innerMessage;
    private String openMessage;
    private AlbianModuleType module;

    /** Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public AlbianRuntimeException() {
        super();
    }

    /** Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public AlbianRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public AlbianRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public AlbianRuntimeException(Throwable cause) {
        super(cause);
    }


    public AlbianRuntimeException(AlbianModuleType module,
                                  IStackTrace currentStackTrace,
                                  String innerMessage,
                                  String openMessage
                                  ){
        super(openMessage);
        this.module = module;
        this.filename = currentStackTrace.getFileName();
        this.lineNumber = currentStackTrace.getLineNumber();
        this.methodName = currentStackTrace.getMethodName();
        this.innerMessage = innerMessage;
        this.openMessage = openMessage;
    }

    public AlbianRuntimeException(AlbianModuleType module,
                                  String filename,
                                  int lineNumber,
                                  String methodName,
                                  String innerMessage,
                                  String openMessage
    ){
        super(openMessage);
        this.module = module;
        this.filename = filename;
        this.lineNumber = lineNumber;
        this.methodName = methodName;
        this.innerMessage = innerMessage;
        this.openMessage = openMessage;
    }

    public AlbianRuntimeException(AlbianModuleType module,
                                  String filename,
                                  int lineNumber,
                                  String methodName,
                                  String innerMessage,
                                  Throwable e
    ){
        super();
        if(null != e) {
            this.initCause(e);
        }
        this.module = module;
        this.filename = filename;
        this.lineNumber = lineNumber;
        this.methodName = methodName;
        this.innerMessage = innerMessage;
    }



    @Override
    public String toString() {
        if(Validate.isNullOrEmptyOrAllSpace(this.filename))
            return super.toString();
        return openMessage;
    }

    @Comments("内部异常信息，注意：只供内部记录日志用，千万不能泄露给用户，否则你就等着死吧！")
    @SpecialWarning("调用的时候千万要注意")
    public String toInnerString(){
        if(Validate.isNullOrEmptyOrAllSpace(this.filename))
            return super.toString();
        return new  StringBuilder("File:").append(this.filename).append(",")
                .append("Line:").append(this.lineNumber).append(",")
                .append("Method:").append(this.methodName).append(",")
                .append("Message:").append(innerMessage)
                .toString();
    }
}
