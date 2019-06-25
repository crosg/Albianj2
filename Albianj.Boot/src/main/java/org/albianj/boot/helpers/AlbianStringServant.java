package org.albianj.boot.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlbianStringServant {

    public static AlbianStringServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new AlbianStringServant();
        }
    }

    protected AlbianStringServant() {

    }

    public String join(Object... args) {
        if (null == args || 0 == args.length) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            sb.append(arg);
        }
        return sb.toString();
    }

    /**
     * 通过具名参数的方式来格式化
     * @param template
     * @param values
     * @code
     * String template = "Welcome to {theWorld}. My name is {myName}.";
     * Map<String, String> values = new HashMap<>();
     * values.put("theWorld", "Stackoverflow");
     * values.put("myName", "Thanos");
     * @return
     */
    public String format(String template, Map<String, Object> values) {
        StringBuilder formatter = new StringBuilder(template);
        List<Object> valueList = new ArrayList<Object>();

        Matcher matcher = Pattern.compile("\\$\\{(\\w+)}").matcher(template);

        while (matcher.find()) {
            String key = matcher.group(1);
            String formatKey = String.format("${%s}", key);
            int index = formatter.indexOf(formatKey);

            if (index != -1) {
                formatter.replace(index, index + formatKey.length(), "%s");
                valueList.add(values.get(key));
            }
        }

        return String.format(formatter.toString(), valueList.toArray());
    }

    public boolean isNullOrEmpty(String value) {
        return null == value || value.isEmpty();
    }
    public boolean isNotNullOrEmpty(String value) {
        return !(null == value || value.isEmpty());
    }

    public boolean isNullOrEmptyOrAllSpace(String value) {
        return null == value || value.trim().isEmpty();
    }

    public boolean isNotNullOrEmptyOrAllSpace(String value) {
        return !(null == value || value.trim().isEmpty());
    }

    /**
     * 首字母小写
     * @param txt
     * @return
     */
    public String initialUpper(String txt) {
        char[] cs = txt.toCharArray();
        if ('a' <= cs[0] && 'z' >= cs[0])
            cs[0] -= 32;
        return String.valueOf(cs);

    }

    /**
     * 首字母大写
     * @param txt
     * @return
     */
    public String initialLower(String txt) {
        char[] cs = txt.toCharArray();
        if ('A' <= cs[0] && 'Z' >= cs[0])
            cs[0] += 32;
        return String.valueOf(cs);
    }

    /**
     * 按照index来格式化字符串
     * AlbianStringServant.Instance.format("hello {0}，{1}！","world","baby");
     * @param fmt
     * @param values
     * @return
     */
    public String format(String fmt, Object...values) {
        StringBuilder formatter = new StringBuilder(fmt);
        List<Object> valueList = new ArrayList<Object>();
        Matcher matcher = Pattern.compile("\\{\\d+\\}").matcher(fmt);
        Pattern pat = Pattern.compile("\\d+");
        while (matcher.find()) {
            String key = matcher.group();
            Matcher match = pat.matcher(key);
            if(match.find()){
                key = match.group();
            }
            int idx = Integer.parseInt(key);
            String formatKey = String.format("{%d}", idx);
            int index = formatter.indexOf(formatKey);
            if (index != -1) {
                formatter.replace(index, index + formatKey.length(), "%s");
                valueList.add(values[idx]);
            }
        }

        return String.format(formatter.toString(), valueList.toArray());
    }
}
