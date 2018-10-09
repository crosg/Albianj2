package org.albianj.persistence.impl.object;

import org.albianj.persistence.object.IAlbianEntityFieldAttribute;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AlbianEntityFieldAttribute extends MemberAttribute implements IAlbianEntityFieldAttribute {

    /*
        Name is the field name,maybe begin with '_' or word
        PropertyName is the property name,allways the setter and getter name,
                    but it must begin with lower letter,and  begin without '_'
        FieldName is the sql field name,the same as sql,default is PropertyName with begin with upper letter


        in the map,key is PropertyName with all lower letter and value is IAlbianEntityFieldAttribute
        so if you make where must use PropertyName
     */
    private Field f = null;
    private String propertyName = null;
    private Method propertySetter = null;
    private Method propertyGetter = null;

    @Override
    public Field getEntityField() {
        return f;
    }

    @Override
    public void setEntityField(Field f) {
        this.f = f;
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public Method getPropertySetter() {
        return this.propertySetter;
    }

    @Override
    public void setPropertySetter(Method propertySetter) {
        this.propertySetter = propertySetter;
    }

    @Override
    public Method getPropertyGetter() {
        return this.propertyGetter;
    }

    @Override
    public void setPropertyGetter(Method propertyGetter) {
        this.propertyGetter = propertyGetter;
    }
}
