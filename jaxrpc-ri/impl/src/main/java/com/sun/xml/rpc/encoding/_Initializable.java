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

/**
 * Stubs/Serializers implement this interface so that they can initialize
 * the serializers they will need
 * 
 * @author JAX-RPC Development Team
 */
public interface _Initializable {
    /** Allows the implementors to retrieve and cache serializers during
     * system intialization
     *
     *  @throws java.lang.Exception This exception may be
     *          thrown if there is a problem initializing
    **/
    public void _initialize(InternalTypeMappingRegistry registry)
        throws Exception;
}
