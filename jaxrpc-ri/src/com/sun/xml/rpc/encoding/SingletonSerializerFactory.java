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

package com.sun.xml.rpc.encoding;

import java.util.Iterator;

import javax.xml.rpc.encoding.Serializer;
import javax.xml.rpc.encoding.SerializerFactory;

import com.sun.xml.rpc.util.SingleElementIterator;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SingletonSerializerFactory implements SerializerFactory {
    protected Serializer serializer;

    public SingletonSerializerFactory(Serializer serializer) {
        this.serializer = serializer;
    }

    public Serializer getSerializerAs(String mechanismType) {
        if (!EncodingConstants.JAX_RPC_RI_MECHANISM.equals(mechanismType)) {
            throw new TypeMappingException(
                "typemapping.mechanism.unsupported",
                mechanismType);
        }
        return serializer;
    }

    public Iterator getSupportedMechanismTypes() {
        return new SingleElementIterator(
            EncodingConstants.JAX_RPC_RI_MECHANISM);
    }

    public Iterator getSerializers() {
        return new SingleElementIterator(serializer);
    }
}
