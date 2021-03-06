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

package com.sun.xml.rpc.processor.modeler.rmi;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class ExceptionModelerBase implements RmiConstants {

    protected RmiModeler modeler;
    protected Class defRuntimeException;
    protected ProcessorEnvironment env;
    protected static final String THROWABLE_CLASSNAME =
        java.lang.Throwable.class.getName();
    protected static final String OBJECT_CLASSNAME =
        java.lang.Object.class.getName();
    protected static final int MESSAGE_FLAG = 2;
    protected static final int LOCALIZED_MESSAGE_FLAG = 4;
    protected static Method GET_MESSAGE_METHOD;
    // Java to WSDL type map
    protected Map faultMap;

    static {
        try {
            GET_MESSAGE_METHOD =
                java.lang.Throwable.class.getDeclaredMethod(
                    GET_MESSAGE,
                    new Class[0]);
        } catch (Exception e) {
        }
    }

    public ExceptionModelerBase(RmiModeler modeler) {
        this.modeler = modeler;
        env = modeler.getProcessorEnvironment();
        faultMap = new HashMap();
        /*
         * Initialize cached definitions for the RuntimeException class.
         */
        try {
            defRuntimeException =
                RmiUtils.getClassForName(
                    RuntimeException.class.getName(),
                    env.getClassLoader());
            GET_MESSAGE_METHOD =
                RmiUtils
                    .getClassForName(
                        java.lang.Throwable.class.getName(),
                        env.getClassLoader())
                    .getDeclaredMethod(GET_MESSAGE, new Class[0]);
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                "rmimodeler.class.not.found",
                RuntimeException.class.getName());
        } catch (NoSuchMethodException e) {
            throw new ModelerException(
                "rmimodeler.no.such.method",
                new Object[] { GET_MESSAGE, Throwable.class.getName()});
        }
    }

    public Fault modelException(
        String typeUri,
        String wsdlUri,
        Class exceptionClass) {
            
        String className = exceptionClass.getName();
        checkForJavaExceptions(className);
        return createFault(typeUri, wsdlUri, exceptionClass);
    }

    protected void checkForJavaExceptions(String className) {
        if (Names.isInJavaOrJavaxPackage(className)) {
            throw new ModelerException(
                "rmimodeler.java.exceptions.not.allowed",
                className);
        }
    }

    public abstract Fault createFault(
        String typeUri,
        String wsdlUri,
        Class classDef);

    protected static Set getDuplicateMembers(Map members) {
        Set types = new HashSet();
        Set duplicateMembers = new HashSet();
        Iterator iter = members.entrySet().iterator();
        Method member;
        RmiType type;
        String memberName;
        while (iter.hasNext()) {
            member = (Method) ((Map.Entry) iter.next()).getValue();
            type = RmiType.getRmiType(member.getReturnType());
            memberName = member.getName();
            if (types.contains(type)) {
                duplicateMembers.add(member);
            } else {
                types.add(type);
            }
        }
        return duplicateMembers;
    }

    // Modified this method to call the static method below for bug fix 4923650
    public void collectMembers(Class classDef, Map members) {
        try {
            if (defRuntimeException.isAssignableFrom(classDef)) {
                throw new ModelerException(
                    "rmimodeler.must.not.extend.runtimeexception",
                    classDef.getName());
            }
            collectExceptionMembers(classDef, members);
        } catch (Exception e) {
            throw new ModelerException(new LocalizableExceptionAdapter(e));
        }
    }

    // This static method was added to support bug fix: 4923650   
    public static void collectExceptionMembers(Class classDef, Map members) {
        try {
            if (classDef.equals(Throwable.class)) {
                members.put(GET_MESSAGE, GET_MESSAGE_METHOD);
                return;
            }
            if (classDef.getSuperclass() != null)
                collectExceptionMembers(classDef.getSuperclass(), members);
            Method[] methods = classDef.getMethods();
            Class decClass;
            for (int i = 0; i < methods.length; i++) {
                decClass = methods[i].getDeclaringClass();
                if (Modifier.isStatic(methods[i].getModifiers())
                    || (decClass.equals(Throwable.class)
                        || decClass.equals(Object.class))) {
                    continue;
                }
                String memberName = methods[i].getName();
                if ((memberName.startsWith("get")
                    || memberName.startsWith("is"))
                    && methods[i].getParameterTypes().length == 0) {
                    //                    if (!members.containsKey(memberName) &&
                    //                        !memberName.equals(GET_LOCALIZED_MESSAGE)) { 
                    if (!members.containsKey(memberName)) {
                        members.put(memberName, methods[i]);
                    }
                }
            }
        } catch (Exception e) {
            throw new ModelerException(new LocalizableExceptionAdapter(e));
        }
    }

    /**
     * returns the Fault for a mapped exception, null if the
     * type is not in the map
     */
    private Fault getMappedFault(String className) {
        return (Fault) faultMap.get(className);
    }

    private void log(ProcessorEnvironment env, String msg) {
        if (env.verbose()) {
            System.out.println(
                "["
                    + Names.stripQualifier(this.getClass().getName())
                    + ": "
                    + msg
                    + "]");
        }
    }
}
