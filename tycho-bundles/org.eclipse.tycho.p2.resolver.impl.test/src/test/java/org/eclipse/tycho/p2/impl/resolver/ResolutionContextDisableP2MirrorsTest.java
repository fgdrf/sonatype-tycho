package org.eclipse.tycho.p2.impl.resolver;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.net.URI;

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.repository.artifact.spi.AbstractArtifactRepository;
import org.eclipse.tycho.p2.impl.test.MavenLoggerStub;
import org.eclipse.tycho.test.util.ResourceUtil;
import org.junit.Before;
import org.junit.Test;

public class ResolutionContextDisableP2MirrorsTest {

    private P2RepositoryCache repositoryCache;
    private File localRepo;

    @Before
    public void setUp() {
        localRepo = new File("target/localrepo");
        IProvisioningAgent agent = P2ResolverFactoryImpl.getProvisioningAgent(localRepo, false);
        repositoryCache = (P2RepositoryCache) agent.getService(P2RepositoryCache.SERVICE_NAME);
        assertNotNull(repositoryCache);
    }

    @Test
    public void testDisableP2Mirrors() throws Exception {
        ResolutionContextImpl context = createResolutionContext(true);

        URI location = ResourceUtil.resourceFile("p2-mirrors-disable/disablemirrors").toURI();
        context.addP2Repository(location);
        assertNull(getP2MirrorsUrlFromCachedRepository(location));
    }

    @Test
    public void testWithoutDisableP2Mirrors() throws Exception {
        ResolutionContextImpl context = createResolutionContext(false);

        // need a different URI here, to force reloading
        // TODO why is the cache active in this test -> test through the right interface?
        URI location = ResourceUtil.resourceFile("p2-mirrors-disable/usemirrors").toURI();
        context.addP2Repository(location);
        assertNotNull(getP2MirrorsUrlFromCachedRepository(location));
    }

    private ResolutionContextImpl createResolutionContext(boolean disableP2Mirrors) {
        P2ResolverFactoryImpl p2ResolverFactoryImpl = new P2ResolverFactoryImpl();
        ResolutionContextImpl context = p2ResolverFactoryImpl.createResolutionContext(localRepo, false,
                disableP2Mirrors, new MavenLoggerStub());
        return context;
    }

    private String getP2MirrorsUrlFromCachedRepository(URI location) {
        String p2MirrorsUrl = repositoryCache.getArtifactRepository(location).getProperty(
                AbstractArtifactRepository.PROP_MIRRORS_URL);
        return p2MirrorsUrl;
    }
}
