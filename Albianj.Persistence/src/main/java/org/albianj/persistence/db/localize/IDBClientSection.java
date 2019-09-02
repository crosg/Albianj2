package org.albianj.persistence.db.localize;

public interface IDBClientSection {
    String toSqlValue(int sqlType,Object value,String charset);
}
