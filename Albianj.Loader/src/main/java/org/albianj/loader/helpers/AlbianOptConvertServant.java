package org.albianj.loader.helpers;

public class AlbianOptConvertServant {
    public static AlbianOptConvertServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new AlbianOptConvertServant();
        }
    }

    protected AlbianOptConvertServant() {

    }

    public boolean toBoolean(String value, boolean def) {
        if (value == null)
            return def;
        String trimmedVal = value.trim();
        if ("true".equalsIgnoreCase(trimmedVal))
            return true;
        if ("false".equalsIgnoreCase(trimmedVal))
            return false;
        return def;
    }

    public int toInt(String value, int dEfault) {
        if (value != null) {
            String s = value.trim();
            try {
                return Integer.valueOf(s).intValue();
            } catch (NumberFormatException e) {
//                LogLog.error("[" + s + "] is not in proper int form.");
                e.printStackTrace();
            }
        }
        return dEfault;
    }

    public long toFileSize(String value, long dEfault) {
        if (value == null)
            return dEfault;

        String s = value.trim().toUpperCase();
        long multiplier = 1;
        int index;

        if ((index = s.indexOf("KB")) != -1) {
            multiplier = 1024;
            s = s.substring(0, index);
        } else if ((index = s.indexOf("MB")) != -1) {
            multiplier = 1024 * 1024;
            s = s.substring(0, index);
        } else if ((index = s.indexOf("GB")) != -1) {
            multiplier = 1024 * 1024 * 1024;
            s = s.substring(0, index);
        }
        if (s != null) {
            try {
                return Long.valueOf(s).longValue() * multiplier;
            } catch (NumberFormatException e) {
//                LogLog.error("[" + s + "] is not in proper int form.");
//                LogLog.error("[" + value + "] not in expected format.", e);
            }
        }
        return dEfault;
    }
}
