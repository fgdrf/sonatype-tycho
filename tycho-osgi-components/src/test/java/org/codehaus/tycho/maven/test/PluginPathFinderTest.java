package org.codehaus.tycho.maven.test;

import java.io.File;
import java.util.List;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.tycho.osgitools.PluginPathFinder;

public class PluginPathFinderTest extends PlexusTestCase {

	public void testTargetPlatform() throws Exception {
		PluginPathFinder finder = new PluginPathFinder();

		File targetPlatform = new File("src/test/resources/targetplatforms/wtp-2.0").getCanonicalFile();
		List<File> sites = finder.getSites(targetPlatform);

		assertEquals(3, sites.size());
		assertEquals(targetPlatform, sites.get(0).getCanonicalFile());
		assertEquals(new File(targetPlatform, "dropins/zest-3.4"), sites.get(1).getCanonicalFile());
		assertEquals(new File(".").getCanonicalFile(), sites.get(2).getCanonicalFile());
	}

	public void testPlugins33() throws Exception {
		PluginPathFinder finder = new PluginPathFinder();

		File targetPlatform = new File("src/test/resources/targetplatforms/wtp-2.0").getCanonicalFile();
		List<File> plugins = finder.getPlugins(targetPlatform);

		assertEquals(2, plugins.size());
		assertTrue(plugins.contains(new File(targetPlatform, "plugins/com.ibm.icu.source_3.6.1.v20070906").getCanonicalFile()));
		assertTrue(plugins.contains(new File(targetPlatform, "plugins/org.eclipse.datatools.enablement.sybase.asa.models_1.0.0.200706071.jar").getCanonicalFile()));
	}

	public void testPlugins34() throws Exception {
		PluginPathFinder finder = new PluginPathFinder();

		File targetPlatform = new File("src/test/resources/targetplatforms/wtp-3.0").getCanonicalFile();
		List<File> plugins = finder.getPlugins(targetPlatform);

		assertEquals(2, plugins.size());
		assertTrue(plugins.contains(new File(targetPlatform, "plugins/com.ibm.icu_3.8.1.v20080402.jar").getCanonicalFile()));
		assertTrue(plugins.contains(new File(targetPlatform, "plugins/org.junit4_4.3.1").getCanonicalFile()));
	}

}