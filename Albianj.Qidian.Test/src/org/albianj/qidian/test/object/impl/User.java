package org.albianj.qidian.test.object.impl;

import java.math.BigInteger;

import org.albianj.persistence.object.FreeAlbianObject;
import org.albianj.qidian.test.object.IUser;

public class User extends FreeAlbianObject implements IUser {

	BigInteger id = null;
	String name = null;
	boolean isDelete = false;
	@Override
	public BigInteger getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setId(BigInteger id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	@Override
	public boolean getIsDelete() {
		// TODO Auto-generated method stub
		return isDelete;
	}

	@Override
	public void setIsDelete(boolean isDalete) {
		// TODO Auto-generated method stub
		this.isDelete = isDelete;
	}
	
	int age = 0;
	public int getAge(){
		return age;
	}
	public void setAge(int age){
		this.age = age;
	}
	
	boolean sex = false;
	public void setSex(boolean sex){
		this.sex = sex;
	}
	public boolean getSex(){
		return this.sex;
	}
	
	String unit = null;
	public String getUnit(){
		return this.unit;
	}
	public void setUnit(String unit){
		this.unit = unit;
	}
	
	String tel = null;
	public String getTelNo(){
		return this.tel;
	}
	public void setTelNo(String tel){
		this.tel = tel;
	}

}
