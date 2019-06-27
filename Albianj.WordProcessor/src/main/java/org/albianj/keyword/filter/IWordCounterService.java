package org.albianj.keyword.filter;

import org.albianj.service.IService;

/**
 * Created by xuhaifeng on 17/2/16.
 */
public interface IWordCounterService extends IService {

    String Name = "AlbianWordCounterService";

    int countWithoutBlank(String input);

    int countWithEnglishWordWithoutBlank(String input);

    int countWithoutBlankOnlyWithEnglish(String value);
}
