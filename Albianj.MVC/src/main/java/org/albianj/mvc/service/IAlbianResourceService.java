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
package org.albianj.mvc.service;

import org.albianj.mvc.HttpContext;
import org.albianj.service.AlbianBuiltinServiceNamePair;
import org.albianj.service.IAlbianService;

import java.io.IOException;


/**
 * Provides a static resource service interface.
 *
 * <h3>Configuration</h3>
 * The default ResourceService is {@link ClickResourceService}.
 * <p/>
 * However you can instruct Click to use a different implementation by adding
 * the following element to your <tt>click.xml</tt> configuration file.
 *
 * <pre class="codeConfig">
 * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
 * &lt;click-app charset="UTF-8"&gt;
 *
 *     &lt;pages package="com.mycorp.page"/&gt;
 *
 *     &lt;<span class="red">resource-service</span> classname="<span class="blue">com.mycorp.service.DynamicResourceService</span>"&gt;
 *
 * &lt;/click-app&gt; </pre>
 */
public interface IAlbianResourceService extends IAlbianService {

    String Name = AlbianBuiltinServiceNamePair.AlbianResourceServiceName;


    /**
     * Return true if the request is for a static resource.
     *
     * @return true if the request is for a static resource
     */
    public boolean isResourceRequest(HttpContext ctx);

    /**
     * Render the resource request to the given servlet resource response.
     *
     * @throws IOException if an IO error occurs rendering the resource
     */
    public void renderResource(HttpContext ctx)
            throws IOException;

}
