package org.albianj.mvc.impl.ctags;

/**
 * Created by xuhaifeng on 16/12/21.
 */
public class PagingInfo {

    private long recordsCount;
    private int pagesize = 10;
    private int currentPageNumber = 0;

    public PagingInfo(){

    }

    public PagingInfo(long recordsCount,int pagesize,int currentPageNumber){
        this.recordsCount = recordsCount;
        this.pagesize = pagesize;
        this.currentPageNumber = currentPageNumber;
    }


    public void setRecordsCount(long total){
        this.recordsCount = total;
    }
    public long getRecordsCount(){
        return this.recordsCount;
    }

    public void setPagesize(int pagesize){
        this.pagesize = pagesize;
    }
    public int getPagesize(){
        return this.pagesize;
    }
    public void setCurrentPageNumber(int cpn){
        this.currentPageNumber = cpn;
    }

    public int getCurrentPageNumber(){
        return this.currentPageNumber;
    }
}
