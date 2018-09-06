package org.albianj.persistence.object;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * 这是albianj object的数据库的扩展类
 * 它定义了一些基本的,有用的数据库字段.
 * 实现它的数据子类主要注意:数据库表中必须要自行建立此类所带的属性,以确保此类的属性值可以更新到数据库.
 * Created by xuhaifeng on 16/5/23.
 */
public abstract class FreeExAlbianObject extends FreeAlbianObject implements IExAlbianObject {

    BigInteger id;
    boolean isDelete = false;
    String author;
    Timestamp createTime;
    Timestamp lastUpdate;
    String lastModifier;


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean delete) {
        isDelete = delete;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

}
