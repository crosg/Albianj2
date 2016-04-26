package org.albianj.service.parser;

import org.albianj.service.AlbianServiceException;
import org.albianj.service.FreeAlbianService;

public abstract class FreeAlbianParserService extends FreeAlbianService implements IAlbianParserService {

	@Override
	public void loading() throws AlbianServiceException, AlbianParserException {
		// TODO Auto-generated method stub
		init();
		super.loading();
	}
	

	
	
	
}
