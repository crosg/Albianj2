package org.albianj.aop.impl;

import org.albianj.aop.IAlbianServiceAopAttribute;
import org.albianj.verify.Validate;

import java.util.List;

/**
 * Created by xuhaifeng on 16/5/31.
 */
public class AlbianServiceAopAttribute implements IAlbianServiceAopAttribute {
    String _service = null;
    String beginWith = null;
    String notBeginWith = null;
    String endWith = null;
    String notEndWith = null;
    String contain = null;
    String notContain = null;
    String fullname = null;
    boolean isAll = false;

    @Override
    public String getBeginWith() {
        return this.beginWith;
    }

    @Override
    public void setBeginWith(String beginWith) {
        this.beginWith = beginWith;
    }

    @Override
    public String getNotBeginWith() {
        return this.notBeginWith;
    }

    @Override
    public void setNotBeginWith(String notBeginWith) {
        this.notBeginWith = notBeginWith;
    }

    @Override
    public String getEndWith() {
        return this.endWith;
    }

    @Override
    public void setEndWith(String endWith) {
        this.endWith = endWith;
    }

    @Override
    public String getNotEndWith() {
        return this.notEndWith;
    }

    @Override
    public void setNotEndWith(String notEndWith) {
        this.notEndWith = notEndWith;
    }

    @Override
    public String getContain() {
        return this.contain;
    }

    @Override
    public void setContain(String contain) {
        this.contain = contain;
    }

    @Override
    public String getNotContain() {
        return this.notContain;
    }

    @Override
    public void setNotContain(String notContain) {
        this.notContain = notContain;
    }


    @Override
    public String getServiceName() {
        return this._service;
    }

    @Override
    public void setServiceName(String serviceName) {
        this._service = serviceName;
    }

    public boolean matches(String name) {
        if(isAll) return true;
        return Validate.isNullOrEmptyOrAllSpace(this.fullname)
                ?
                Validate.isNullOrEmptyOrAllSpace(this.beginWith) ? true : name.startsWith(this.beginWith)
                        && Validate.isNullOrEmptyOrAllSpace(this.notBeginWith) ? true : !name.startsWith(this.notBeginWith)
                        && Validate.isNullOrEmptyOrAllSpace(this.endWith) ? true : name.endsWith(this.endWith)
                        && Validate.isNullOrEmptyOrAllSpace(this.notEndWith) ? true : !name.endsWith(this.notEndWith)
                        && Validate.isNullOrEmptyOrAllSpace(this.contain) ? true : name.contains(this.contain)
                        && Validate.isNullOrEmptyOrAllSpace(this.notContain) ? true : !name.contains(this.notContain)
                : name.equals(this.fullname);

    }



    public String getFullName() {
        return fullname;
    }

    public void setFullName(String fullname) {
        this.fullname = fullname;
    }



    public boolean getIsAll(){
        return isAll;
    }
    public void setIsAll(boolean isAll){
        this.isAll = isAll;
    }

}
