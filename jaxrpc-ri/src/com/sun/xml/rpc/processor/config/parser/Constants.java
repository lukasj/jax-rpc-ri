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

package com.sun.xml.rpc.processor.config.parser;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface Constants {
    public static final String NS_NAME =
        "http://java.sun.com/xml/ns/jax-rpc/ri/config";

    public static final QName QNAME_CONFIGURATION =
        new QName(NS_NAME, "configuration");
    public static final QName QNAME_INTERFACE = new QName(NS_NAME, "interface");
    public static final QName QNAME_MODELFILE = new QName(NS_NAME, "modelfile");
    public static final QName QNAME_SERVICE = new QName(NS_NAME, "service");
    public static final QName QNAME_WSDL = new QName(NS_NAME, "wsdl");
    public static final QName QNAME_J2EE_MAPPING_FILE =
        new QName(NS_NAME, "j2eeMappingFile");
    public static final QName QNAME_NO_METADATA =
        new QName(NS_NAME, "noMetadata");
    public static final QName QNAME_TYPE_MAPPING_REGISTRY =
        new QName(NS_NAME, "typeMappingRegistry");
    public static final QName QNAME_TYPE_MAPPING =
        new QName(NS_NAME, "typeMapping");
    public static final QName QNAME_ENTRY = new QName(NS_NAME, "entry");
    public static final QName QNAME_HANDLER_CHAINS =
        new QName(NS_NAME, "handlerChains");
    public static final QName QNAME_CHAIN = new QName(NS_NAME, "chain");
    public static final QName QNAME_HANDLER = new QName(NS_NAME, "handler");
    public static final QName QNAME_PROPERTY = new QName(NS_NAME, "property");
    public static final QName QNAME_NAMESPACE_MAPPING_REGISTRY =
        new QName(NS_NAME, "namespaceMappingRegistry");
    public static final QName QNAME_NAMESPACE_MAPPING =
        new QName(NS_NAME, "namespaceMapping");
    public static final QName QNAME_IMPORT = new QName(NS_NAME, "import");
    public static final QName QNAME_SCHEMA = new QName(NS_NAME, "schema");
    public static final QName QNAME_ADDITIONAL_TYPES =
        new QName(NS_NAME, "additionalTypes");
    public static final QName QNAME_CLASS = new QName(NS_NAME, "class");

    public static final String ATTR_NAME = "name";
    public static final String ATTR_VALUE = "value";
    public static final String ATTR_CLASS_NAME = "className";
    public static final String ATTR_DESERIALIZER_FACTORY =
        "deserializerFactory";
    public static final String ATTR_ENCODING = "encoding";
    public static final String ATTR_ENCODING_STYLE = "encodingStyle";
    public static final String ATTR_JAVA_TYPE = "javaType";
    public static final String ATTR_LOCATION = "location";
    public static final String ATTR_PACKAGE_NAME = "packageName";
    public static final String ATTR_SCHEMA_TYPE = "schemaType";
    public static final String ATTR_SERIALIZER_FACTORY = "serializerFactory";
    public static final String ATTR_SERVANT_NAME = "servantName";
    public static final String ATTR_SOAP_ACTION = "soapAction";
    public static final String ATTR_SOAP_ACTION_BASE = "soapActionBase";
    public static final String ATTR_TARGET_NAMESPACE = "targetNamespace";
    public static final String ATTR_TYPE_NAMESPACE = "typeNamespace";
    public static final String ATTR_ROLES = "roles";
    public static final String ATTR_HEADERS = "headers";
    public static final String ATTR_RUN_AT = "runAt";
    public static final String ATTR_NAMESPACE = "namespace";
    public static final String ATTR_VERSION = "version";
    public static final String ATTR_INTERFACE_NAME = "interfaceName";
    public static final String ATTR_SERVICE_INTERFACE_NAME =
        "serviceInterfaceName";
    public static final String ATTR_SERVICE_NAME = "serviceName";
    public static final String ATTR_PORT_NAME = "portName";
    public static final String ATTR_SOAP_VERSION ="soapVersion";
    public static final String ATTR_WSDL_LOCATION ="wsdlLocation";

    public static final String ATTRVALUE_VERSION_1_0 = "1.0";
    public static final String ATTRVALUE_CLIENT = "client";
    public static final String ATTRVALUE_SERVER = "server";
    public static final String ATTRVALUE_SOAP_1_1 = "1.1";
    public static final String ATTRVALUE_SOAP_1_2 = "1.2";
}
