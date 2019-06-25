package org.albianj.keyword.filter;


import org.albianj.boot.tags.Comments;
import org.albianj.service.parser.IAlbianParserService;

import java.util.Set;


/**
 * Created by xuhaifeng on 17/1/24.
 */
@Comments("关键字过滤service接口")
public interface IAlbianKeywordFilterService extends IAlbianParserService {

    @Comments("AlbianKeywordFilterService在service.xml中的id")
    String Name = "AlbianKeywordFilterService";


    @Comments("最小匹配规则")
    int MinMatchType = 1;      //最小匹配规则

    @Comments("最大匹配规则")
    int MaxMatchType = 2;      //最大匹配规则

    /**
     * 判断文字是否包含敏感字符
     *
     * @param txt       文字
     * @param matchType 匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
     * @return 若包含返回true，否则返回false
     * @version 1.0
     */
    @Comments("判断是否存在敏感字符")
    public boolean isContaintKeyWord(@Comments("需要判断的字符串") String txt, @Comments("匹配规则") int matchType);


    /**
     * 获取文字中的敏感词
     *
     * @param txt       文字
     * @param matchType 匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
     * @return
     * @version 1.0
     */
    @Comments("获取存在的敏感字符")
    public Set<String> getKeyWord(@Comments("需要判断的字符串") String txt, @Comments("匹配规则") int matchType);

    /**
     * 替换敏感字字符
     *
     * @param txt
     * @param matchType
     * @param replaceChar 替换字符，默认*
     * @version 1.0
     */
    public String replaceKeyWord(String txt, int matchType, String replaceChar);

    /**
     * 替换敏感字字符,替换字符默认*
     *
     * @param txt
     * @param matchType
     * @version 1.0
     */
    public String replaceKeyWord(String txt, int matchType);


    /**
     * 检查文字中是否包含敏感字符，检查规则如下：<br>
     *
     * @param txt
     * @param beginIndex
     * @param matchType
     * @return，如果存在，则返回敏感词字符的长度，不存在返回0
     * @version 1.0
     */
    public int affirmKeyWord(String txt, int beginIndex, int matchType);

}
