package org.albianj.persistence.object;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * Created by xuhaifeng on 16/9/25.
 */
public interface IExAlbianObject extends IAlbianObject {
    public BigInteger getId();

    public void setId(BigInteger id);

    public boolean getIsDelete();

    public void setIsDelete(boolean delete);

    public String getAuthor();

    public void setAuthor(String author);

    public Timestamp getCreateTime();

    public void setCreateTime(Timestamp createTime);

    public Timestamp getLastUpdate();

    public void setLastUpdate(Timestamp lastUpdate);

    public String getLastModifier();

    public void setLastModifier(String lastModifier);

}
