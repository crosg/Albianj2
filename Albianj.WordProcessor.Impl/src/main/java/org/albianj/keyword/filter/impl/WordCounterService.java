package org.albianj.keyword.filter.impl;

import org.albianj.boot.tags.CommentsTag;
import org.albianj.keyword.filter.IWordCounterService;
import org.albianj.service.ServiceTag;
import org.albianj.service.FreeService;

import java.util.regex.Pattern;

/**
 * Created by xuhaifeng on 17/2/16.
 */
@ServiceTag(Id = IWordCounterService.Name, Interface = IWordCounterService.class)
public class WordCounterService extends FreeService implements IWordCounterService {

    public String getServiceName() {
        return Name;
    }

    @CommentsTag("未经测试")
    public int countWithoutBlankOnlyWithEnglish(String value) {
        String[] sections = value.split("\\s");
        int count = 0;
        for (String s : sections) {
            Pattern pat = Pattern.compile("[\u0000-\u00ff]+");
            int ci = pat.matcher(s).groupCount();
            for (char c : s.toCharArray()) {
                if ((int) c > 0x00FF) ci++;
            }
            count += ci;
        }
        return count;


//        var sec = Regex.Split(value, @"\s");
//        int count = 0;
//        foreach (var si in sec)
//        {
//            int ci = Regex.Matches(si, @"[\u0000-\u00ff]+").Count;
//            foreach (var c in si)
//            if ((int)c > 0x00FF) ci++;
//            count += ci;
//        }
//        return count;
    }

    public int countWithoutBlank(String input) {
        WordCounter wc = new WordCounter(input);
        return wc.countWithoutBlank();
    }

    public int countWithEnglishWordWithoutBlank(String input) {
        WordCounter wc = new WordCounter(input);
        return wc.countWithEnglishWordWithoutBlank();
    }

}
