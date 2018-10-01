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

package com.sun.xml.rpc.processor.model.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaStructureMember;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPStructureMember {
    
    public SOAPStructureMember() {}
    
    public SOAPStructureMember(QName name, SOAPType type) {
        this(name, type, null);
    }
    
    public SOAPStructureMember(QName name, SOAPType type,
        JavaStructureMember javaStructureMember) {
            
        _name = name;
        _type = type;
        _javaStructureMember = javaStructureMember;
    }
    
    public QName getName() {
        return _name;
    }
    
    public void setName(QName n) {
        _name = n;
    }
    
    public SOAPType getType() {
        return _type;
    }
    
    public void setType(SOAPType t) {
        _type = t;
    }
    
    public boolean isInherited() {
        return isInherited;
    }
    
    public void setInherited(boolean b) {
        isInherited = b;
    }
    
    public JavaStructureMember getJavaStructureMember() {
        return _javaStructureMember;
    }
    
    public void setJavaStructureMember(JavaStructureMember javaStructureMember) {
        _javaStructureMember = javaStructureMember;
    }
    
    private QName _name;
    private SOAPType _type;
    private JavaStructureMember _javaStructureMember;
    private boolean isInherited = false;
}
