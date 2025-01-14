/*******************************************************************************
 * Copyright (c) 2008, 2011 Sonatype Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.tycho.p2.resolver.facade;

import java.io.File;

import org.eclipse.tycho.core.facade.MavenLogger;

public interface P2ResolverFactory {

    public ResolutionContext createResolutionContext(File localMavenRepositoryRoot, boolean offline,
            boolean disableP2Mirrors, MavenLogger logger);

    public P2Resolver createResolver(MavenLogger logger);
}
