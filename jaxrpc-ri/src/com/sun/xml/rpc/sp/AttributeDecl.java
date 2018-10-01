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

package com.sun.xml.rpc.sp;

/**
 * Encapsulate an attribute declaration.
 *
 * @author David Brownell
 * @author JAX-RPC RI Development Team
 */
class AttributeDecl {
    String name;

    String type;
    String values[]; // for notation, enumeration only

    String defaultValue;
    boolean isRequired;
    boolean isFixed;
    boolean isFromInternalSubset;

    final static String CDATA = "CDATA";

    final static String ID = "ID";
    final static String IDREF = "IDREF";
    final static String IDREFS = "IDREFS";
    final static String ENTITY = "ENTITY";
    final static String ENTITIES = "ENTITIES";
    final static String NMTOKEN = "NMTOKEN";
    final static String NMTOKENS = "NMTOKENS";

    final static String NOTATION = "NOTATION";

    final static String ENUMERATION = "ENUMERATION";

    AttributeDecl(String s) {
        name = s;
    }
}
