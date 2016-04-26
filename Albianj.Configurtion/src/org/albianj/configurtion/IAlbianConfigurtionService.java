package org.albianj.configurtion;

import java.math.BigInteger;
import java.util.List;

import org.albianj.service.IAlbianService;

public interface IAlbianConfigurtionService extends IAlbianService {
	
	public static  String Name = "AlbianConfigurtionService";
	public static  String AlbianConfigurtionCachedNameDefault = "AlbianConfigurtionCached";
	public static final String AlbianCachedKeySepDefault = "~";
	
	public IConfigItem findConfigurtion( BigInteger id) ;
	public IConfigItem loadConfigurtion(BigInteger id) ;
	public List<IConfigItem> findChildConfigurtions(BigInteger pid) ;
	public List<IConfigItem> loadChildConfigurtions(BigInteger pid) ;
	public IConfigItem findAllConfigurtion(BigInteger id) ;
	public IConfigItem loadAllConfigurtion(BigInteger id) ;
	public List<IConfigItem> findAllChildConfigurtions(BigInteger pid) ;
	public List<IConfigItem> loadAllChildConfigurtions(BigInteger pid);

	
	public Object findConfigurtionValue( String... names) ;
		public Object loadConfigurtionValue(String... names) ;	
	boolean create(IConfigItem cfi,String mender);
	boolean modify(BigInteger id,Object value,String mender);
	
	boolean delete(BigInteger id,String mender);
	boolean disable(BigInteger id,String mender);
	boolean enable(BigInteger id,String mender);
	
	BigInteger getConfigItemId(int level);
	
	void expireConfigItemCacheForce(BigInteger id,String mender);
	void resetConfigItemCacheForce(BigInteger id,String mender);
	void fuelConfigurtionCache();
	
}