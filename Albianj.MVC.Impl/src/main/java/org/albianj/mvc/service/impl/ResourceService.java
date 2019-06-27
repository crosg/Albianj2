/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.albianj.mvc.service.impl;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.mvc.HttpContext;
import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.lang.HttpHelper;
import org.albianj.mvc.service.IResourceService;
import org.albianj.service.*;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.service.parser.FreeParserService;
import org.albianj.verify.Validate;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Provides a default Click static resource service class. This class will
 * serve static resources contained in the web applications JARs, under the
 * resource path META-INF/resources and which are contained under the WAR file
 * web root.
 * <p/>
 * This service is useful for application servers which do not allow Click to
 * automatically deploy resources to the web root directory.
 */
@ServiceTag(Id = IResourceService.Name, Interface = IResourceService.class)
public class ResourceService extends FreeParserService implements IResourceService {

    private Properties mtProperties = null;

    /**
     * The click resources cache.
     */
//    protected Map<String, byte[]> resourceCache = new ConcurrentHashMap<String, byte[]>();
    @ServiceFieldTag(Type = ServiceFieldType.Ref, Value = "AlbianMvcConfigurtionService.HttpConfigurtion",SetterLifetime = ServiceFieldSetterLifetime.AfterNew)
    private AlbianHttpConfigurtion c;

    public String getServiceName() {
        return Name;
    }

    public void setHttpConfigurtion(AlbianHttpConfigurtion c) {
        this.c = c;
    }

    public void loadConf() throws AlbianParserException {
        mtProperties = initMimeTypeDefault();
        String filename = null;
        try {
            filename = Path.getExtendResourcePath(KernelSetting.getAlbianConfigFilePath()
                    + "mimetype.properties");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (Path.isExist(filename)) {
            InputStream inputStream = null;

                try {
                    inputStream = new FileInputStream(filename);
                        mtProperties.load(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException ioe) {
                    }
                }
        }
        super.loadConf();
    }

    private Properties initMimeTypeDefault() {
        Properties mt = new Properties();
        mt.put("abs", "audio/x-mpeg");
        mt.put("ai", "application/postscript");
        mt.put("aif", "audio/x-aiff");
        mt.put("aifc", "audio/x-aiff");
        mt.put("aiff", "audio/x-aiff");
        mt.put("aim", "application/x-aim");
        mt.put("art", "image/x-jg");
        mt.put("asf=", "ideo/x-ms-asf");
        mt.put("asx", "video/x-ms-asf");
        mt.put("au", "audio/basic");
        mt.put("avi", "video/x-msvideo");
        mt.put("avx", "video/x-rad-screenplay");
        mt.put("bcpio", "application/x-bcpio");
        mt.put("bin", "application/octet-stream");
        mt.put("bmp", "image/bmp");
        mt.put("body", "text/html");
        mt.put("cdf", "application/x-cdf");
        mt.put("cer", "application/x-x509-ca-cert");
        mt.put("class", "application/java");
        mt.put("cpio", "application/x-cpio");
        mt.put("csh", "application/x-csh");
        mt.put("css", "text/css");
        mt.put("csv", "text/csv");
        mt.put("dib", "image/bmp");
        mt.put("doc", "application/msword");
        mt.put("dtd", "application/xml-dtd");
        mt.put("dv", "video/x-dv");
        mt.put("dvi", "application/x-dvi");
        mt.put("eps", "application/postscript");
        mt.put("etx", "text/x-setext");
        mt.put("exe", "application/octet-stream");
        mt.put("gif", "image/gif");
        mt.put("gtar", "application/x-gtar");
        mt.put("gz", "application/x-gzip");
        mt.put("hdf", "application/x-hdf");
        mt.put("hqx", "application/mac-binhex40");
        mt.put("htc", "text/x-component");
        mt.put("htm", "text/html");
        mt.put("html", "text/html");
        mt.put("ief", "image/ief");
        mt.put("jad", "text/vnd.sun.j2me.app-descriptor");
        mt.put("jar", "application/java-archive");
        mt.put("code", "text/plain");
        mt.put("jnlp", "application/x-java-jnlp-file");
        mt.put("jpe", "image/jpeg");
        mt.put("jpeg", "image/jpeg");
        mt.put("jpg", "image/jpeg");
        mt.put("js", "text/javascript");
        mt.put("jsf", "text/plain");
        mt.put("json", "application/json");
        mt.put("jspf", "text/plain");
        mt.put("kar", "audio/x-midi");
        mt.put("latex", "application/x-latex");
        mt.put("m3u", "audio/x-mpegurl");
        mt.put("mac", "image/x-macpaint");
        mt.put("man", "application/x-troff-man");
        mt.put("mathml", "application/mathml+xml");
        mt.put("me", "application/x-troff-me");
        mt.put("mid", "audio/x-midi");
        mt.put("midi", "audio/x-midi");
        mt.put("mif", "application/x-mif");
        mt.put("mov", "video/quicktime");
        mt.put("movie", "video/x-sgi-movie");
        mt.put("mp1", "audio/x-mpeg");
        mt.put("mp2", "audio/x-mpeg");
        mt.put("mp3", "audio/x-mpeg");
        mt.put("mpa", "audio/x-mpeg");
        mt.put("mpe", "video/mpeg");
        mt.put("mpeg", "video/mpeg");
        mt.put("mpega", "audio/x-mpeg");
        mt.put("mpg", "video/mpeg");
        mt.put("mpv2", "video/mpeg2");
        mt.put("ms", "application/x-wais-source");
        mt.put("msg", "application/vnd.ms-outlook");
        mt.put("nc", "application/x-netcdf");
        mt.put("oda", "application/oda");
        mt.put("ogg", "application/ogg");
        mt.put("pbm", "image/x-portable-bitmap");
        mt.put("pct", "image/pict");
        mt.put("pdf", "application/pdf");
        mt.put("pgm", "image/x-portable-graymap");
        mt.put("pic", "image/pict");
        mt.put("pict", "image/pict");
        mt.put("pls", "audio/x-scpls");
        mt.put("png", "image/png");
        mt.put("pnm", "image/x-portable-anymap");
        mt.put("pnt", "image/x-macpaint");
        mt.put("ppm", "image/x-portable-pixmap");
        mt.put("ppt", "application/powerpoint");
        mt.put("ps", "application/postscript");
        mt.put("psd", "image/x-photoshop");
        mt.put("qt", "video/quicktime");
        mt.put("qti", "image/x-quicktime");
        mt.put("qtif", "image/x-quicktime");
        mt.put("ras", "image/x-cmu-raster");
        mt.put("rdf", "application/rdf+xml");
        mt.put("rgb", "image/x-rgb");
        mt.put("rm", "application/vnd.rn-realmedia");
        mt.put("roff", "application/x-troff");
        mt.put("rtf", "application/rtf");
        mt.put("rtx", "text/richtext");
        mt.put("sh", "application/x-sh");
        mt.put("shar", "application/x-shar");
        mt.put("shtml", "text/x-server-parsed-html");
        mt.put("smf", "audio/x-midi");
        mt.put("sit", "application/x-stuffit");
        mt.put("snd", "audio/basic");
        mt.put("src", "application/x-wais-source");
        mt.put("sv4cpio", "application/x-sv4cpio");
        mt.put("sv4crc", "application/x-sv4crc");
        mt.put("svgxml", "image/svg+xml");
        mt.put("swf", "application/x-shockwave-flash");
        mt.put("t", "application/x-troff");
        mt.put("tar", "application/x-tar");
        mt.put("tcl", "application/x-tcl");
        mt.put("tex", "application/x-tex");
        mt.put("texi", "application/x-texinfo");
        mt.put("texinfo", "application/x-texinfo");
        mt.put("tif", "image/tiff");
        mt.put("tiff", "image/tiff");
        mt.put("tr", "application/x-troff");
        mt.put("tsv", "text/tab-separated-values");
        mt.put("txt", "text/plain");
        mt.put("ulw", "audio/basic");
        mt.put("ustar", "application/x-ustar");
        mt.put("vxml", "application/voicexml+xml");
        mt.put("xbm", "image/x-xbitmap");
        mt.put("xht", "application/xhtml+xml");
        mt.put("xhtml", "application/xhtml+xml");
        mt.put("xls", "application/vnd.ms-excel");
        mt.put("xml", "application/xml");
        mt.put("xpm", "image/x-xpixmap");
        mt.put("xsl", "application/xml");
        mt.put("xslt", "application/xslt+xml");
        mt.put("xul", "application/vnd.mozilla.xul+xml");
        mt.put("xwd", "image/x-xwindowdump");
        mt.put("wav", "audio/x-wav");
        mt.put("svg", "image/svg");
        mt.put("svgz", "image/svg");
        mt.put("vsd", "application/x-visio");
        mt.put("wbmp", "image/vnd.wap.wbmp");
        mt.put("wml", "text/vnd.wap.wml");
        mt.put("wmlc", "application/vnd.wap.wmlc");
        mt.put("wmls", "text/vnd.wap.wmlscript");
        mt.put("wmlscriptc", "application/vnd.wap.wmlscriptc");
        mt.put("wrl", "x-world/x-vrml");
        mt.put("Z", "application/x-compress");
        mt.put("z", "application/x-compress");
        mt.put("zip", "application/zip");
        return mt;
    }


    public String getMimeType(String key) {
        if (Validate.isNullOrEmptyOrAllSpace(key))
            return "text/html";
        Object o = mtProperties.get(key);
        if (null == o) return "text/html";

        return o.toString();
    }

    /**
     * @return true if the request is for a static click resource
     */
    public boolean isResourceRequest(HttpContext ctx) {
        String resourcePath = HttpHelper.getResourcePath(ctx.getCurrentRequest());

        // If not a click page and not JSP and not a directory
        return !HttpHelper.isTemplate(resourcePath, c.getSuffix())
                && !resourcePath.endsWith("/");
    }

    /**
     */
    public void renderResource(HttpContext ctx)
            throws IOException {

        String resourcePath = HttpHelper.getResourcePath(ctx.getCurrentRequest());

        byte[] resourceData = loadResourceData(ctx, resourcePath);
        // resourceCache.get(resourcePath);

//        if (resourceData == null) {
//            // Lazily load resource
//            resourceData = loadResourceData(ctx, resourcePath);
//
        if (resourceData == null) {
            ctx.getCurrentResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
//        }

        String suffix = FilenameUtils.getExtension(resourcePath);
        String mimeType = getMimeType(suffix);
        if (mimeType != null) {
            ctx.getCurrentResponse().setContentType(mimeType);
        }
        renderResource(ctx.getCurrentResponse(), resourceData);
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the list of directories that contains cacheable resources.
     * <p/>
     * By default only resource packaged under the "<tt>/click</tt>" directory
     * will be processed. To serve resources from other directories you need to
     * override this method and return a list of directories to process.
     * <p/>
     * For example:
     *
     * <pre class="prettyprint">
     * public class MyResourceService extends ClickResourceService {
     *
     *     protected List<String> getCacheableDirs() {
     *         // Get default dirs which includes /click
     *         List list = super.getCacheableDirs();
     *
     *         // Add resources packaged under the folder /clickclick
     *         list.add("/clickclick");
     *         // Add resources packaged under the folder /mycorp
     *         list.add("/mycorp");
     *     }
     * } </pre>
     *
     * You also need to add a mapping in your <tt>web.xml</tt> to forward
     * requests for these resources on to Click:
     *
     * <pre class="prettyprint">
     * &lt;-- The default Click *.htm mapping --&gt;
     * &lt;servlet-mapping&gt;
     *   &lt;servlet-name&gt;ClickServlet&lt;/servlet-name&gt;
     *   &lt;url-pattern&gt;*.htm&lt;/url-pattern&gt;
     * &lt;/servlet-mapping&gt;
     *
     * &lt;-- Add a mapping to serve all resources under /click directly from
     * the JARs. --&gt;
     * &lt;servlet-mapping&gt;
     *   &lt;servlet-name&gt;ClickServlet&lt;/servlet-name&gt;
     *   &lt;url-pattern&gt;/click/*&lt;/url-pattern&gt;
     * &lt;/servlet-mapping&gt;
     *
     * &lt;-- Add another mapping to serve all resources under /clickclick
     * from the JARs. --&gt;
     * &lt;servlet-mapping&gt;
     *   &lt;servlet-name&gt;ClickServlet&lt;/servlet-name&gt;
     *   &lt;url-pattern&gt;/clickclick/*&lt;/url-pattern&gt;
     * &lt;/servlet-mapping&gt;
     *
     * &lt;-- Add a mapping to serve all resources under /mycorp
     * from the JARs. --&gt;
     * &lt;servlet-mapping&gt;
     *   &lt;servlet-name&gt;ClickServlet&lt;/servlet-name&gt;
     *   &lt;url-pattern&gt;/mycorp/*&lt;/url-pattern&gt;
     * &lt;/servlet-mapping&gt;
     * </pre>
     *
     * @return list of directories that should be cached
     */
//    protected List<String> getCacheableDirs() {
//       List<String> list = new ArrayList<String>();
//       list.add("/click");
//       return list;
//    }

    // Private Methods --------------------------------------------------------

    /**
     * Store the resource under the given resource path.
     *
     * @param resourcePath the path to store the resource under
     * @param data         the resource byte array
     */
    private void storeResourceData(String resourcePath, byte[] data) {
    }

    /**
     * Load the resource for the given resourcePath. This method will load the
     * resource from the servlet context, and if not found, load it from the
     * classpath under the folder 'META-INF/resources'.
     *
     * @param resourcePath the path to the resource to load
     * @return the resource as a byte array
     * @throws IOException if the resources cannot be loaded
     */
    private byte[] loadResourceData(HttpContext ctx, String resourcePath) throws IOException {

        byte[] resourceData = null;


        ServletContext servletContext = ctx.getCurrentServlet();

        resourceData = getServletResourceData(servletContext, resourcePath);
        if (resourceData != null) {
            storeResourceData(resourcePath, resourceData);
        } else {
            resourceData = getClasspathResourceData("META-INF/resources"
                    + resourcePath);

            if (resourceData != null) {
                storeResourceData(resourcePath, resourceData);
            }
        }

        return resourceData;
    }

    /**
     * Load the resource for the given resourcePath from the servlet context.
     *
     * @param servletContext the application servlet context
     * @param resourcePath   the path of the resource to load
     * @return the byte array for the given resource path
     * @throws IOException if the resource could not be loaded
     */
    private byte[] getServletResourceData(ServletContext servletContext,
                                          String resourcePath) throws IOException {

        InputStream inputStream = null;
        try {
            inputStream = servletContext.getResourceAsStream(resourcePath);

            if (inputStream != null) {
                return IOUtils.toByteArray(inputStream);
            } else {
                return null;
            }

        } finally {
            if (null != inputStream) {
                inputStream.close();
            }
//            ClickUtils.close(inputStream);
        }
    }

    /**
     * Load the resource for the given resourcePath from the classpath.
     *
     * @param resourcePath the path of the resource to load
     * @return the byte array for the given resource path
     * @throws IOException if the resource could not be loaded
     */
    private byte[] getClasspathResourceData(String resourcePath) throws IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            inputStream = getClass().getResourceAsStream(resourcePath);
        }

        try {

            if (inputStream != null) {
                return IOUtils.toByteArray(inputStream);
            } else {
                return null;
            }

        } finally {
            if (null != inputStream) {
                inputStream.close();
            }
//            ClickUtils.close(inputStream);
        }
    }

    /**
     * Render the given resourceData byte array to the response.
     *
     * @param response     the response object
     * @param resourceData the resource byte array
     * @throws IOException if the resource data could not be rendered
     */
    private void renderResource(HttpServletResponse response,
                                byte[] resourceData) throws IOException {

        OutputStream outputStream = null;
        try {
            response.setContentLength(resourceData.length);

            outputStream = response.getOutputStream();
            outputStream.write(resourceData);
            outputStream.flush();

        } finally {
            if (null != outputStream) {
                outputStream.flush();
                outputStream.close();
            }
//            ClickUtils.close(outputStream);
        }
    }
}
