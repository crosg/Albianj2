package org.albianj.keyword.filter.impl;


/**
 * 字数统计
 *
 * @author p_blu
 */
public class WordCounter {
    private int p = 0;
    private String input;
    private int len;
    private String empty = "　\n\r\t ";

    //--字数统计所需常量--
    private String emptyWord = "—";
    private String letter = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String num = "0123456789";
    private String specialChar = "…";
    private int cL = 33;
    private int cH = 126;

    public WordCounter(String input) {
        this.input = input;
        p = 0;
        len = input == null ? 0 : input.length();
    }

    /**
     * 返回内容
     *
     * @return 内容
     */
    public String returnInput() {
        if (input == null) {
            return "";
        }
        return input;
    }

    /**
     * 空白符包括     英文空格，中文空格，换行符\r，回车符\n，制表符\t
     *
     * @return 不计空白符的字数统计
     */
    public int countWithoutBlank() {
        if (input == null) {
            return 0;
        }

        int count = 0;
        for (p = 0; p < len; p++) {
            if (isEmpty()) continue;
            count++;
        }
        return count;
    }

    /**
     * 连续的字母，数字，一些特殊符号 记为一个字  一些字符不进行统计
     *
     * @return 字数
     */
    public int countWithEnglishWordWithoutBlank() {
        if (input == null) {
            return 0;
        }

        int count = 0;
        int len = input.length();
        for (p = 0; p < len; ) {
            if (isEmptyWord()) {
                p++;
                continue;
            }

            if (isLetter()) matchLetter();
            else if (isNum()) matchNum();
            else if (isSpecialChar()) matchSpecial();
            else p++;
            count++;
        }
        return count;
    }

    /**
     * 计算章节价格
     *
     * @return 价格
     */
//    public int wordAmount(int unitPrice) {
//        int words = countWithEnglishWordWithoutBlank();
//        if (unitPrice <= 0) {
//            unitPrice = 5;
//        }
//        int price = words * unitPrice / 1000;
//        return price;
//    }

    /**
     * 计算章节价格
     *
     * @return 价格
     */
//    public int wordAmount(int words, int unitPrice) {
//        if (words <= 0) {
//            words = countWithEnglishWordWithoutBlank();
//        }
//        if (unitPrice <= 0) {
//            unitPrice = 5;
//        }
//        int price = words * unitPrice / 1000;
//        return price;
//    }


    //空字符
    private boolean isEmpty() {
        return empty.indexOf(input.charAt(p)) > -1;
    }

    //空字
    private boolean isEmptyWord() {
        return isEmpty() || emptyWord.indexOf(input.charAt(p)) > -1;
    }

    //数字
    private boolean isNum() {
        return num.indexOf(input.charAt(p)) > -1;
    }

    //字母
    private boolean isLetter() {
        return letter.indexOf(input.charAt(p)) > -1;
    }

    //是否连续出现算作一个字符
    private boolean isSpecialChar() {
        int c = input.charAt(p);
        return (c >= cL && c <= cH) || specialChar.indexOf(c) > -1;
    }

    private void matchLetter() {
        do {
            if (++p >= len) return;
        } while (isLetter());
    }

    private void matchNum() {
        do {
            if (++p >= len) return;
        } while (isNum());
    }

    private void matchSpecial() {
        char c = input.charAt(p);
        do {
            if (++p >= len) return;
        } while (c == input.charAt(p));
    }
}
