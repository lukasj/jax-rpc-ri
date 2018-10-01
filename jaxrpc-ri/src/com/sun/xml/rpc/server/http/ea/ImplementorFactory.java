/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.xml.rpc.server.http.ea;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.ServiceException;

import com.sun.xml.rpc.server.http.Implementor;
import com.sun.xml.rpc.server.http.JAXRPCServletException;

/**
 * A factory for port implementation objects.
 *
 * @author JAX-RPC Development Team
 */
public class ImplementorFactory {

    public ImplementorFactory(ServletConfig servletConfig) {
        _servletConfig = servletConfig;
    }

    public ImplementorFactory(
        ServletConfig servletConfig,
        String configFilePath) {
        if (configFilePath == null) {
            throw new JAXRPCServletException("error.implementorFactory.noConfiguration");
        }
        _registry.readFrom(configFilePath);
        _servletConfig = servletConfig;
    }

    public ImplementorFactory(
        ServletConfig servletConfig,
        InputStream configInputStream) {
        if (configInputStream == null) {
            throw new IllegalArgumentException("error.implementorFactory.noInputStream");
        }
        _registry.readFrom(configInputStream);
        _servletConfig = servletConfig;
    }

    public Implementor getImplementorFor(String name) {

        synchronized (this) {
            Implementor implementor =
                (Implementor) _cachedImplementors.get(name);
            if (implementor != null) {
                return implementor;
            }
        }

        // NOTE - here we avoid synchronizing so that if an init() method never
        // terminates, we don't block the whole JAX-RPC dispatching engine
        // the drawback is that sometimes we create multiple implementors for
        // the same endpoint, and all but one will be destroyed immediately
        // thereafter

        try {
            ImplementorInfo info = _registry.getImplementorInfo(name);
            if (_servletConfig != null) {
                Implementor implementor =
                    info.createImplementor(_servletConfig.getServletContext());
                implementor.init();

                Implementor existingImplementor = null;

                synchronized (this) {
                    existingImplementor =
                        (Implementor) _cachedImplementors.get(name);
                    if (existingImplementor == null) {
                        _cachedImplementors.put(name, implementor);
                    }
                }

                if (existingImplementor == null) {
                    return implementor;
                } else {
                    // it turns out we don't need the new implementor we just
                    // constructed!
                    implementor.destroy();
                    return existingImplementor;
                }
            } else {
                // NOTE - this branch is only used by some unit tests
                // as it does NOT include the necessary synchronization code

                Implementor implementor = info.createImplementor(null);
                _cachedImplementors.put(name, implementor);
                return implementor;
            }
        } catch (IllegalAccessException e) {
            throw new JAXRPCServletException(
                "error.implementorFactory.newInstanceFailed",
                name);
        } catch (InstantiationException e) {
            throw new JAXRPCServletException(
                "error.implementorFactory.newInstanceFailed",
                name);
        } catch (ServiceException e) {
            throw new JAXRPCServletException(
                "error.implementorFactory.newInstanceFailed",
                name);
        } catch (JAXRPCServletException e) {
            throw e;
        } catch (JAXRPCException e) {
            throw new JAXRPCServletException(
                "error.implementorFactory.servantInitFailed",
                name);
        }
    }

    public void releaseImplementor(String name, Implementor implementor) {
        // this seems overly defensive now; given the current implementation
        // of getImplementorFor(), the implementor cache will be monotonic

        boolean mustDestroy = false;
        synchronized (this) {
            Implementor cachedImplementor =
                (Implementor) _cachedImplementors.get(name);
            if (cachedImplementor != implementor) {
                mustDestroy = true;
            }
        }

        if (mustDestroy) {
            implementor.destroy();
        }
    }

    public Iterator names() {
        return _registry.names();
    }

    public void destroy() {
        // this "if" is there because if the server configuration was null,
        // init() was not called, so we shouldn't call destroy either
        if (_servletConfig != null) {

            for (Iterator iter = _cachedImplementors.values().iterator();
                iter.hasNext();
                ) {
                Implementor implementor = (Implementor) iter.next();
                implementor.destroy();
            }
        }

        try {
            _cachedImplementors.clear();
        } catch (UnsupportedOperationException e) {
        }
    }

    protected ServletConfig _servletConfig;
    protected ImplementorRegistry _registry = new ImplementorRegistry();
    protected Map _cachedImplementors = new HashMap();
}
