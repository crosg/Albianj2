package org.albianj.keyword.filter.impl;

import org.albianj.keyword.filter.IAlbianKeywordFilterService;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.service.AlbianServiceException;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.service.parser.FreeAlbianParserService;
import org.albianj.verify.Validate;

import java.io.*;
import java.util.*;

/**
 * Created by xuhaifeng on 17/1/24.
 */
public abstract class FreeAlbianKeywordFilterService extends FreeAlbianParserService implements IAlbianKeywordFilterService {

    @SuppressWarnings("rawtypes")
    protected HashMap sensitiveWordMap;
    private String filename = "keyword.idc";
    private String encoding = "utf8";    //字符编码

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public void loading() throws AlbianServiceException, AlbianParserException {
        Set<String> keyWordSet = new HashSet<String>();
        readSensitiveWordFile(keyWordSet);
        if (Validate.isNullOrEmpty(keyWordSet)) { // init by configurtion
//            IAlbianConfigurtionService acs = AlbianServiceRouter.getSingletonService(IAlbianConfigurtionService.class, IAlbianConfigurtionService.Name, false);
//            if (null != acs) {
//                Object oKeyword = acs.findConfigurtionValue("root", "keyword");
                Object oKeyword = null ;//= acs.findConfigurtionValue("root", "keyword");
                if (null != oKeyword) {
                    String keyword = oKeyword.toString();
                    line2Set(keyword, keyWordSet);
                }
//            }
        }

        if (Validate.isNullOrEmpty(keyWordSet)) {
            AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianRunningLoggerName,
                    "not find the keyword dictionary then not enable the keyword service.");
            super.loading();
            return;
        }

        //将敏感词库加入到HashMap中
        addSensitiveWordToHashMap(keyWordSet);
        super.loading();
    }

    /**
     * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：<br>
     * 中 = {
     * isEnd = 0
     * 国 = {<br>
     * isEnd = 1
     * 人 = {isEnd = 0
     * 民 = {isEnd = 1}
     * }
     * 男  = {
     * isEnd = 0
     * 人 = {
     * isEnd = 1
     * }
     * }
     * }
     * }
     * 五 = {
     * isEnd = 0
     * 星 = {
     * isEnd = 0
     * 红 = {
     * isEnd = 0
     * 旗 = {
     * isEnd = 1
     * }
     * }
     * }
     * }
     *
     * @param keyWordSet 敏感词库
     * @version 1.0
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
        if (null == keyWordSet) return;
        sensitiveWordMap = new HashMap(keyWordSet.size());     //初始化敏感词容器，减少扩容操作
        String key = null;
        Map nowMap = null;
        Map<String, String> newWorMap = null;
        //迭代keyWordSet
        Iterator<String> iterator = keyWordSet.iterator();
        while (iterator.hasNext()) {
            key = iterator.next();    //关键字
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                char keyChar = key.charAt(i);       //转换成char型
                Object wordMap = nowMap.get(keyChar);       //获取

                if (wordMap != null) {        //如果存在该key，直接赋值
                    nowMap = (Map) wordMap;
                } else {     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    newWorMap = new HashMap<String, String>();
                    newWorMap.put("isEnd", "0");     //不是最后一个
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");    //最后一个
                }
            }
        }
    }

    /**
     * 读取敏感词库中的内容，将内容添加到set集合中
     *
     * @return
     * @version 1.0
     */
    @SuppressWarnings("resource")
    private void readSensitiveWordFile(Set<String> set) {
        String fname = confirmConfigFile(filename);
        File file = new File(fname);    //读取文件
        if (!file.isFile() || !file.exists()) {
            AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianRunningLoggerName,
                    "the keyword dictionary -> %s is not exist.then use albian configurtion module find from  with path:root-keyword.",
                    fname);
            return;
        }
        InputStreamReader read = null;
        try {
            read = new InputStreamReader(new FileInputStream(file), encoding);
            BufferedReader bufferedReader = new BufferedReader(read);
            String txt = null;
            while ((txt = bufferedReader.readLine()) != null) {    //读取文件，将文件内容放入到set中
                if (txt.trim().startsWith("#")) continue; //#注释
                if (0 != txt.indexOf('|')) {
                    line2Set(txt, set);
                } else {
                    set.add(txt);
                }
            }
        } catch (Exception e) {
            AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianRunningLoggerName, "read keyword file:%s is fail.",
                    fname);
            return;
        } finally {
            if (null != read) {
                try {
                    read.close();     //关闭文件流
                } catch (IOException e) {
                }
            }
        }
        return;
    }

    private void line2Set(String line, Set<String> set) {
        String[] arr = line.split("|");
        for (String s : arr) {
            set.add(s);
        }
    }
}
