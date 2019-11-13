package org.albianj.persistence.impl.db.localize;

import org.albianj.persistence.db.localize.IDBClientSection;
import org.albianj.verify.Validate;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.sql.Types;

public class MysqlClientSection implements IDBClientSection {
    /**
     * mysql的注入问题解决
     * @param argVal
     * @param escapeDoubleQuotes
     * @param charsetName
     * @return
     */
    public static String injectionArgumentEscaper(String argVal,
                                                   boolean escapeDoubleQuotes,
                                                   String charsetName){
        int len = argVal.length();
        StringBuilder buf = new StringBuilder((int) (len * 1.1));

        buf.append('\'');

        //
        // Note: buf.append(char) is _faster_ than appending in blocks, because the block append requires a System.arraycopy().... go figure...
        //

        for (int i = 0; i < len; ++i) {
            char c = argVal.charAt(i);

            switch (c) {
                case 0: /* Must be escaped for 'mysql' */
                    buf.append('\\');
                    buf.append('0');

                    break;

                case '\n': /* Must be escaped for logs */
                    buf.append('\\');
                    buf.append('n');

                    break;

                case '\r':
                    buf.append('\\');
                    buf.append('r');

                    break;

                case '\\':
                    buf.append('\\');
                    buf.append('\\');

                    break;

                case '\'':
                    buf.append('\\');
                    buf.append('\'');

                    break;

                case '"': /* Better safe than sorry */
                    if (escapeDoubleQuotes) {
                        buf.append('\\');
                    }

                    buf.append('"');

                    break;

                case '\032': /* This gives problems on Win32 */
                    buf.append('\\');
                    buf.append('Z');

                    break;

                case '\u00a5':
                case '\u20a9':
                    Charset charset = null;
                    try {
                        if(!Validate.isNullOrEmptyOrAllSpace(charsetName)) {
                            charset = Charset.forName(charsetName);
                        }
                    }catch (Exception e){
                        charset = null;
                    }
                    if(null != charset) {
                        CharsetEncoder charsetEncoder = charset.newEncoder();
                        // escape characters interpreted as backslash by mysql
                        if (charsetEncoder != null) {
                            CharBuffer cbuf = CharBuffer.allocate(1);
                            ByteBuffer bbuf = ByteBuffer.allocate(1);
                            cbuf.put(c);
                            cbuf.position(0);
                            charsetEncoder.encode(cbuf, bbuf, true);
                            if (bbuf.get(0) == '\\') {
                                buf.append('\\');
                            }
                        }
                    }
                    buf.append(c);
                    break;
                default:
                    buf.append(c);
            }
        }

        buf.append('\'');

        String rtn = buf.toString();
        return rtn;
    }

    public String toSqlValue(int sqlType,Object value,String charset) {
        if (null == value)
            return null;
        switch (sqlType) {
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
                return injectionArgumentEscaper(value.toString(), true, charset);
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return "'" + value.toString() + "'";
        }
        return value.toString();
    }
//
//        public final static int BIT             =  -7;
//        public final static int TINYINT         =  -6;
//        public final static int SMALLINT        =   5;
//        public final static int INTEGER         =   4;
//        public final static int BIGINT          =  -5;
//        public final static int FLOAT           =   6;
//        public final static int REAL            =   7;
//        public final static int DOUBLE          =   8;
//        public final static int NUMERIC         =   2;
//        public final static int DECIMAL         =   3;
//
//        public final static int BINARY          =  -2;
//        public final static int VARBINARY       =  -3;
//        public final static int LONGVARBINARY   =  -4;
//        public final static int NULL            =   0;
//        public final static int OTHER           = 1111;
//        public final static int JAVA_OBJECT         = 2000;
//        public final static int DISTINCT            = 2001;
//        public final static int STRUCT              = 2002;
//        public final static int ARRAY               = 2003;
//        public final static int BLOB                = 2004;
//        public final static int CLOB                = 2005;
//        public final static int REF                 = 2006;
//        public final static int DATALINK = 70;
//        public final static int BOOLEAN = 16;
//        public final static int ROWID = -8;
//        public static final int NCLOB = 2011;
//        public static final int SQLXML = 2009;
}
