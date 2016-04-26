package org.albianj.service;

import org.albianj.kernel.AlbianKernel;
import org.albianj.service.parser.AlbianParserException;

/**
 * ??????albianj???service??????????????????????????????FreeAlbianService???????
 * ????????????????????
 * 
 * @author Seapeak
 *
 */
@AlbianKernel
public interface IAlbianService {

	public AlbianServiceLifetime getAlbianServiceState();

	public void beforeLoad() throws RuntimeException;

	public void loading() throws RuntimeException,AlbianParserException;

	public void afterLoading() throws RuntimeException;

	public void beforeUnload() throws RuntimeException;

	public void unload() throws RuntimeException;

	public void afterUnload() throws RuntimeException;
}
