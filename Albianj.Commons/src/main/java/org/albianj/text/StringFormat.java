package org.albianj.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormat {
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
    public static String byName(String template, Map<String, Object> values) {
        StringBuilder formatter = new StringBuilder(template);
        List<Object> valueList = new ArrayList<Object>();

        Matcher matcher = Pattern.compile("\\$\\{(\\w+)}").matcher(template);

        while (matcher.find()) {
            String key = matcher.group();
            String formatKey = String.format("${%s}", key);
            int index = formatter.indexOf(formatKey);

            if (index != -1) {
                formatter.replace(index, index + formatKey.length(), "%s");
                valueList.add(values.get(key));
            }
        }

        return String.format(formatter.toString(), valueList.toArray());
    }

    /**
     * 按照index来格式化字符串
     * StringServant.Instance.format("hello {0}，{1}！","world","baby");
     * @param fmt
     * @param values
     * @return
     */
    public static String byIndex(String fmt, Object...values) {
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

    /**
     * 按照index来格式化字符串
     * StringServant.Instance.format("hello {}，{ }！","world","baby");
     * @param fmt
     * @param values
     * @return
     */
    public static String byBlock(String fmt, Object...values) {
        StringBuilder formatter = new StringBuilder(fmt);
        Matcher matcher = Pattern.compile("\\{\\s*\\}").matcher(fmt);
        while (matcher.find()) {
            String key = matcher.group();
            int index = formatter.indexOf(key);
            if (index != -1) {
                formatter.replace(index, index + key.length(), "%s");
            }
        }
        return String.format(formatter.toString(), values);
    }
}
