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

package com.sun.xml.rpc.wsdl.document.soap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.EntityAction;
import com.sun.xml.rpc.wsdl.framework.Extension;
import com.sun.xml.rpc.wsdl.framework.ExtensionVisitor;
import com.sun.xml.rpc.wsdl.framework.QNameAction;

/**
 * A SOAP header extension.
 *
 * @author JAX-RPC Development Team
 */
public class SOAPHeader extends Extension {

    public SOAPHeader() {
        _faults = new ArrayList();
    }

    public void add(SOAPHeaderFault fault) {
        _faults.add(fault);
    }

    public Iterator faults() {
        return _faults.iterator();
    }

    public QName getElementName() {
        return SOAPConstants.QNAME_HEADER;
    }

    public String getNamespace() {
        return _namespace;
    }

    public void setNamespace(String s) {
        _namespace = s;
    }

    public SOAPUse getUse() {
        return _use;
    }

    public void setUse(SOAPUse u) {
        _use = u;
    }

    public boolean isEncoded() {
        return _use == SOAPUse.ENCODED;
    }

    public boolean isLiteral() {
        return _use == SOAPUse.LITERAL;
    }

    public String getEncodingStyle() {
        return _encodingStyle;
    }

    public void setEncodingStyle(String s) {
        _encodingStyle = s;
    }

    public String getPart() {
        return _part;
    }

    public void setMessage(QName message) {
        _message = message;
    }

    public QName getMessage() {
        return _message;
    }

    public void setPart(String s) {
        _part = s;
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        super.withAllSubEntitiesDo(action);

        for (Iterator iter = _faults.iterator(); iter.hasNext();) {
            action.perform((Entity) iter.next());
        }
    }

    public void withAllQNamesDo(QNameAction action) {
        super.withAllQNamesDo(action);

        if (_message != null) {
            action.perform(_message);
        }
    }

    public void accept(ExtensionVisitor visitor) throws Exception {
        visitor.preVisit(this);
        for (Iterator iter = _faults.iterator(); iter.hasNext();) {
            ((SOAPHeaderFault) iter.next()).accept(visitor);
        }
        visitor.postVisit(this);
    }

    public void validateThis() {
        if (_message == null) {
            failValidation("validation.missingRequiredAttribute", "message");
        }
        if (_part == null) {
            failValidation("validation.missingRequiredAttribute", "part");
        }
        // Fix for bug 4851427
        //        if (_use == null) {
        //            failValidation("validation.missingRequiredAttribute", "use");
        //        }
    }

    private String _encodingStyle;
    private String _namespace;
    private String _part;
    private QName _message;
    private SOAPUse _use;
    private List _faults;
}
