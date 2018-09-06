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

import org.albianj.mvc.HttpContext;
import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.lang.HttpHelper;
import org.albianj.mvc.service.IAlbianResourceService;
import org.albianj.service.FreeAlbianService;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides a default Click static resource service class. This class will
 * serve static resources contained in the web applications JARs, under the
 * resource path META-INF/resources and which are contained under the WAR file
 * web root.
 * <p/>
 * This service is useful for application servers which do not allow Click to
 * automatically deploy resources to the web root directory.
 */
public class AlbianResourceService extends FreeAlbianService implements IAlbianResourceService {

    public String getServiceName(){
        return Name;
    }


    /** The click resources cache. */
//    protected Map<String, byte[]> resourceCache = new ConcurrentHashMap<String, byte[]>();

    private  AlbianHttpConfigurtion c;

    public void setHttpConfigurtion(AlbianHttpConfigurtion c){
        this.c = c;
    }

    /**
     *
     * @return true if the request is for a static click resource
     */
    public boolean isResourceRequest(HttpContext ctx) {
        String resourcePath = HttpHelper.getResourcePath(ctx.getCurrentRequest());

        // If not a click page and not JSP and not a directory
        return !HttpHelper.isTemplate(resourcePath,c.getSuffix())
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

        String mimeType = HttpHelper.getMimeType(resourcePath);
        if (mimeType != null) {
            ctx.getCurrentResponse().setContentType(mimeType);
        }

//        if (logService.isDebugEnabled()) {
//            HtmlStringBuffer buffer = new HtmlStringBuffer(200);
//            buffer.append("handleRequest: ");
//            buffer.append(request.getMethod());
//            buffer.append(" ");
//            buffer.append(request.getRequestURL());
//            logService.debug(buffer);
//        }
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
     * @param data the resource byte array
     */
    private void storeResourceData(String resourcePath, byte[] data) {
        // Only cache in production modes
//        if (configService.isProductionMode() || configService.isProfileMode()) {
//            resourceCache.put(resourcePath, data);
//        }
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
     * @param resourcePath the path of the resource to load
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
            if(null != inputStream) {
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
            if(null != inputStream) {
                inputStream.close();
            }
//            ClickUtils.close(inputStream);
        }
    }

    /**
     * Render the given resourceData byte array to the response.
     *
     * @param response the response object
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
            if(null != outputStream){
                outputStream.flush();
                outputStream.close();
            }
//            ClickUtils.close(outputStream);
        }
    }
}
