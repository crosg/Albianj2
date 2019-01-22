package org.albianj.mvc.ctags;

/**
 * Created by xuhaifeng on 16/12/21.
 */
public class PagingInfo {

    private long recordsCount;
    private int pagesize = 10;
    private int currentPageNumber = 0;

    public PagingInfo() {

    }

    public PagingInfo(long recordsCount, int pagesize, int currentPageNumber) {
        this.recordsCount = recordsCount;
        this.pagesize = pagesize;
        this.currentPageNumber = currentPageNumber;
    }

    public long getRecordsCount() {
        return this.recordsCount;
    }

    public void setRecordsCount(long total) {
        this.recordsCount = total;
    }

    public int getPagesize() {
        return this.pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getCurrentPageNumber() {
        return this.currentPageNumber;
    }

    public void setCurrentPageNumber(int cpn) {
        this.currentPageNumber = cpn;
    }
}
