package org.albianj.configurtion;

import java.math.BigInteger;
import java.sql.Timestamp;

import org.albianj.persistence.object.AlbianObjectMemberAttribute;
import org.albianj.persistence.object.FreeAlbianObject;

public class ConfigItem extends FreeAlbianObject implements IConfigItem {
	
	private BigInteger id = null;
	private String name = null;
	private Object value = null;
	private BigInteger pid = null;
	private boolean enable = true;
	private String desc = null;
	private Timestamp createTime = null;
	private Timestamp lastModify = null;
	private String author = null;
	private String mender = null;
	private boolean isDelete = false;
	private String parentNamePath = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public BigInteger getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	@AlbianObjectMemberAttribute(IsPrimaryKey=true,IsSave=true,IsAllowNull=false)
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
	@AlbianObjectMemberAttribute(IsAllowNull=false)
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return this.value;
	}

	@Override
	public void setValue(Object o) {
		// TODO Auto-generated method stub
		this.value = o;
	}

	@Override
	public BigInteger getParentId() {
		// TODO Auto-generated method stub
		return this.pid;
	}

	@Override
	@AlbianObjectMemberAttribute(IsAllowNull=false)
	public void setParentId(BigInteger pid) {
		// TODO Auto-generated method stub
		this.pid = pid;
	}

	@Override
	public boolean getEnable() {
		// TODO Auto-generated method stub
		return this.enable;
	}

	@Override
	@AlbianObjectMemberAttribute(IsAllowNull=false)
	public void setEnable(boolean enable) {
		// TODO Auto-generated method stub
		this.enable = enable;
	}

	@Override
	public String getDescribe() {
		// TODO Auto-generated method stub
		return this.desc;
	}

	@Override
	@AlbianObjectMemberAttribute(IsAllowNull=false)
	public void setDescribe(String describe) {
		// TODO Auto-generated method stub
		this.desc = describe;
	}

	@Override
	public Timestamp getCreateTime() {
		// TODO Auto-generated method stub
		return this.createTime;
	}

	@Override
	@AlbianObjectMemberAttribute(IsAllowNull=false)
	public void setCreateTime(Timestamp ts) {
		// TODO Auto-generated method stub
		this.createTime = ts;
	}

	@Override
	public Timestamp getLastModify() {
		// TODO Auto-generated method stub
		return this.lastModify;
	}

	@Override
	@AlbianObjectMemberAttribute(IsAllowNull=false)
	public void setLastModify(Timestamp ts) {
		// TODO Auto-generated method stub
		this.lastModify = ts;
	}

	@Override
	public String getAuthor() {
		// TODO Auto-generated method stub
		return this.author;
	}

	@Override
	@AlbianObjectMemberAttribute(IsAllowNull=false)
	public void setAuthor(String author) {
		// TODO Auto-generated method stub
		this.author = author;
	}

	@Override
	public String getLastMender() {
		// TODO Auto-generated method stub
		return this.mender;
	}

	@Override
	@AlbianObjectMemberAttribute(IsAllowNull=false)
	public void setLastMender(String mender) {
		// TODO Auto-generated method stub
		this.mender = mender;
	}

	@Override
	public boolean getIsDelete() {
		// TODO Auto-generated method stub
		return this.isDelete;
	}

	@Override
	@AlbianObjectMemberAttribute(IsAllowNull=false)
	public void setIsDelete(boolean isDelete) {
		// TODO Auto-generated method stub
		this.isDelete = isDelete;
	}

	@Override
	public String getParentNamePath() {
		// TODO Auto-generated method stub
		return this.parentNamePath;
	}

	@Override
	@AlbianObjectMemberAttribute(IsAllowNull=false)
	public void setParentNamePath(String pnp) {
		// TODO Auto-generated method stub
		this.parentNamePath = pnp;
	}

}
