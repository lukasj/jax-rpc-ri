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

package com.sun.xml.rpc.processor.generator;

import java.io.IOException;
import java.util.Set;

import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.IndentingWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPEncoding implements GeneratorConstants {

    public static void writeStaticSerializer(
        IndentingWriter p,
        String portPackage,
        SOAPType type,
        Set processedTypes,
        SerializerWriterFactory writerFactory,
        Names names)
        throws IOException {

        String qnameMember;
        if (processedTypes
            .contains(
                type.getName() + ";" + type.getJavaType().getRealName())) {
            return;
        }
        processedTypes.add(
            type.getName() + ";" + type.getJavaType().getRealName());
        qnameMember = names.getTypeQName(type.getName());
        if (!processedTypes.contains(type.getName() + "TYPE_QNAME")) {
            GeneratorUtil.writeQNameTypeDeclaration(p, type.getName(), names);
            processedTypes.add(type.getName() + "TYPE_QNAME");
        }
        SerializerWriter writer = writerFactory.createWriter(portPackage, type);
        writer.declareSerializer(p, false, false);
    }
}
