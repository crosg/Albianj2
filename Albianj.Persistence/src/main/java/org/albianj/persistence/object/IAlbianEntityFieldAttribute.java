package org.albianj.persistence.object;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IAlbianEntityFieldAttribute  extends  IMemberAttribute{

    Field getEntityField();

    void setEntityField(Field f);

    String getPropertyName();

    void setPropertyName(String propertyName);

    Method getPropertySetter();

    void setPropertySetter(Method propertySetter);

    Method getPropertyGetter();

    void setPropertyGetter(Method propertyGetter);

}
