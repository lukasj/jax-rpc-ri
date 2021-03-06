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

package com.sun.xml.rpc.encoding.simpletype;

import javax.activation.DataHandler;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class XSDAnyURIEncoder
    extends SimpleTypeEncoderBase
    implements AttachmentEncoder {
        
    private static final SimpleTypeEncoder encoder = new XSDAnyURIEncoder();

    private XSDAnyURIEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        return ((java.net.URI) obj).toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        return new java.net.URI(str);
    }

    public DataHandler objectToDataHandler(Object obj) throws Exception {
        DataHandler dataHandler = new DataHandler(obj, "text/plain");

        return dataHandler;
    }

    public Object dataHandlerToObject(DataHandler dataHandler)
        throws Exception {
            
        return dataHandler.getContent();
    }

}
