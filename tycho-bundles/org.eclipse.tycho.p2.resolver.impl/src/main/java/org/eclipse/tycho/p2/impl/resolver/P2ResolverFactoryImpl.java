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
package org.eclipse.tycho.p2.impl.resolver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.equinox.internal.p2.repository.CacheManager;
import org.eclipse.equinox.internal.p2.repository.Transport;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.tycho.core.facade.MavenLogger;
import org.eclipse.tycho.p2.impl.Activator;
import org.eclipse.tycho.p2.resolver.facade.P2ResolverFactory;

@SuppressWarnings("restriction")
public class P2ResolverFactoryImpl implements P2ResolverFactory {

    public ResolutionContextImpl createResolutionContext(File localMavenRepositoryRoot, boolean offline,
            boolean disableP2Mirrors, MavenLogger logger) {
        IProvisioningAgent agent = getProvisioningAgent(localMavenRepositoryRoot, offline);

        return new ResolutionContextImpl(agent, localMavenRepositoryRoot, offline, disableP2Mirrors, logger);
    }

    public P2ResolverImpl createResolver(MavenLogger logger) {
        return new P2ResolverImpl(logger);
    }

    // --------------

    /**
     * IProvisioningAgent's must be long-lived because they keep instances of tycho repository
     * cache. Cache instances are parametrized by local repository path and offline mode and thus
     * different agent must be used for each (localrepo,offline) combination.
     * 
     * @TODO move to activator and stop all agents during Bundle#stop
     */
    private static final Map<AgentKey, IProvisioningAgent> agents = new HashMap<P2ResolverFactoryImpl.AgentKey, IProvisioningAgent>();

    private static class AgentKey {
        public final File localMavenRepositoryRoot;
        public final boolean offline;

        public AgentKey(File localMavenRepositoryRoot, boolean offline) {
            this.localMavenRepositoryRoot = localMavenRepositoryRoot;
            this.offline = offline;
        }

        @Override
        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + localMavenRepositoryRoot.hashCode();
            hash = hash * 31 + (offline ? 1 : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AgentKey)) {
                return false;
            }
            AgentKey other = (AgentKey) obj;
            return localMavenRepositoryRoot.equals(other.localMavenRepositoryRoot) && offline == other.offline;
        }
    }

    public static synchronized IProvisioningAgent getProvisioningAgent(File localMavenRepositoryRoot, boolean offline) {
        AgentKey agentKey = new AgentKey(localMavenRepositoryRoot, offline);
        IProvisioningAgent agent = agents.get(agentKey);
        if (agent == null) {
            try {
                agent = Activator.newProvisioningAgent();

                Transport transport = (Transport) agent.getService(Transport.SERVICE_NAME);

                // setup p2 cache manager
                TychoP2RepositoryCacheManager cacheMgr = new TychoP2RepositoryCacheManager(transport);
                cacheMgr.setOffline(offline);
                cacheMgr.setLocalRepositoryLocation(localMavenRepositoryRoot);
                agent.registerService(CacheManager.SERVICE_NAME, cacheMgr);

                // setup tycho repo cache
                P2RepositoryCache tychoCache = new P2RepositoryCacheImpl();
                agent.registerService(P2RepositoryCache.SERVICE_NAME, tychoCache);

                agents.put(agentKey, agent);
            } catch (ProvisionException e) {
                throw new RuntimeException(e);
            }
        }
        return agent;
    }

}
