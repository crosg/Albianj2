package Albian.Test.Model.Impl;

import Albian.Test.Model.IUTF8M64;
import org.albianj.persistence.object.FreeAlbianObject;
import org.albianj.persistence.object.rants.AlbianObjectDataFieldRant;
import org.albianj.persistence.object.rants.AlbianObjectRant;

@AlbianObjectRant(Interface = IUTF8M64.class)
public class UTF8M64 extends FreeAlbianObject implements IUTF8M64 {

    @AlbianObjectDataFieldRant(IsPrimaryKey = true,FieldName = "id")
    private  int id = 0;

    @AlbianObjectDataFieldRant(FieldName = "v")
    private String v;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getV() {
        return this.v;
    }

    @Override
    public void setV(String v) {
        this.v = v;
    }
}
