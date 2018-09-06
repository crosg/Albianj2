package org.albianj.keyword.filter;

import org.albianj.service.IAlbianService;

/**
 * Created by xuhaifeng on 17/2/16.
 */
public interface IAlbianWordCounterService  extends IAlbianService{

    String Name="AlbianWordCounterService";

    int countWithoutBlank(String input) ;

    int countWithEnglishWordWithoutBlank(String input) ;

    int countWithoutBlankOnlyWithEnglish(String value) ;
}
