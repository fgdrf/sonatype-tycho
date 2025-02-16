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
package org.eclipse.tycho.maven.test;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.tycho.model.BundleConfiguration;
import org.eclipse.tycho.model.FeatureRef;
import org.eclipse.tycho.model.Launcher;
import org.eclipse.tycho.model.LauncherArguments;
import org.eclipse.tycho.model.PluginRef;
import org.eclipse.tycho.model.ProductConfiguration;
import org.junit.Test;

public class ProductConfigurationParseTest {

    @Test
    public void testLauncherArgsParse() throws Exception {
        ProductConfiguration config = ProductConfiguration.read(getClass().getResourceAsStream(
                "/product/LauncherArgsTest.product"));

        LauncherArguments launcherArgs = config.getLauncherArguments();
        testArgsOneAndTwo(launcherArgs.getProgramArgs(), "-all", "all");
        testArgsOneAndTwo(launcherArgs.getLinuxProgramArgs(), "-linux", "linux");
        testArgsOneAndTwo(launcherArgs.getMacProgramArgs(), "-mac", "mac");
        testArgsOneAndTwo(launcherArgs.getSolarisProgramArgs(), "-solaris", "solaris");
        testArgsOneAndTwo(launcherArgs.getWindowsProgramArgs(), "-win32", "win32");

        testArgsOneAndTwo(launcherArgs.getVMArgs(), "-all", "vmall");
        testArgsOneAndTwo(launcherArgs.getLinuxVMArgs(), "-linux", "vmlinux");
        testArgsOneAndTwo(launcherArgs.getMacVMArgs(), "-mac", "vmmac");
        testArgsOneAndTwo(launcherArgs.getSolarisVMArgs(), "-solaris", "vmsolaris");
        testArgsOneAndTwo(launcherArgs.getWindowsVMArgs(), "-win32", "vmwin32");
    }

    private void testArgsOneAndTwo(List<String> arguments, String expectedOne, String expectedSecond) {

        Assert.assertNotNull(expectedOne);
        Assert.assertNotNull(expectedSecond);

        Assert.assertNotNull(arguments);
        Assert.assertEquals(2, arguments.size());

        Assert.assertEquals(expectedOne, arguments.get(0));
        Assert.assertEquals(expectedSecond, arguments.get(1));
    }

    @Test
    public void testProductConfigurationParse() throws Exception {
        ProductConfiguration config = ProductConfiguration.read(getClass().getResourceAsStream(
                "/product/MyFirstRCP.product"));

        Assert.assertEquals("My First RCP", config.getName());
        Assert.assertEquals("MyFirstRCP.product1", config.getProduct());
        Assert.assertEquals("MyFirstRCP.application", config.getApplication());
        Assert.assertEquals(false, config.useFeatures());

        /*
         * ConfigIni configIni = config.getConfigIni(); Assert.assertNotNull(configIni);
         * Assert.assertEquals("linux.ini", configIni.getLinuxIcon());
         * Assert.assertEquals("macosx.ini", configIni.getMacosxIcon());
         * Assert.assertEquals("solaris.ini", configIni.getSolarisIcon());
         * Assert.assertEquals("win32.ini", configIni.getWin32());
         * 
         * LauncherArguments launcherArgs = config.getLauncherArgs();
         * Assert.assertNotNull(launcherArgs); Assert.assertEquals("-all args",
         * launcherArgs.getProgramArgs()); Assert.assertEquals("-linux args",
         * launcherArgs.getProgramArgsLin()); Assert.assertEquals("-mac args",
         * launcherArgs.getProgramArgsMac()); Assert.assertEquals("-solaris args",
         * launcherArgs.getProgramArgsSol()); Assert.assertEquals("-win32 args",
         * launcherArgs.getProgramArgsWin()); Assert.assertEquals("-all vm",
         * launcherArgs.getVmArgs()); Assert.assertEquals("-linux vm", launcherArgs.getVmArgsLin());
         * Assert.assertEquals("-mac vm", launcherArgs.getVmArgsMac());
         * Assert.assertEquals("-solaris vm", launcherArgs.getVmArgsSol());
         * Assert.assertEquals("-win32 vm", launcherArgs.getVmArgsWin());
         */

        Launcher launcher = config.getLauncher();
        Assert.assertNotNull(launcher);
        Assert.assertEquals("launchername", launcher.getName());
        Assert.assertEquals("XPM", launcher.getLinuxIcon().get(Launcher.ICON_LINUX));
        Assert.assertEquals("icns", launcher.getMacosxIcon().get(Launcher.ICON_MAC));
        Assert.assertEquals("large", launcher.getSolarisIcon().get(Launcher.ICON_SOLARIS_LARGE));
        Assert.assertEquals("medium", launcher.getSolarisIcon().get(Launcher.ICON_SOLARIS_MEDIUM));
        Assert.assertEquals("small", launcher.getSolarisIcon().get(Launcher.ICON_SOLARIS_SMALL));
        Assert.assertEquals("tiny", launcher.getSolarisIcon().get(Launcher.ICON_SOLARIS_TINY));
        Assert.assertEquals(false, launcher.getWindowsUseIco());
//		Assert.assertEquals("iconon", launcher.getWindowsIcon().getIco().getPath());
        Assert.assertEquals("16-32", launcher.getWindowsIcon().get(Launcher.ICON_WINDOWS_SMALL_HIGH));
        Assert.assertEquals("16-8", launcher.getWindowsIcon().get(Launcher.ICON_WINDOWS_SMALL_LOW));
        Assert.assertEquals("32-32", launcher.getWindowsIcon().get(Launcher.ICON_WINDOWS_MEDIUM_HIGH));
        Assert.assertEquals("32-8", launcher.getWindowsIcon().get(Launcher.ICON_WINDOWS_MEDIUM_LOW));
        Assert.assertEquals("48-32", launcher.getWindowsIcon().get(Launcher.ICON_WINDOWS_LARGE_HIGH));
        Assert.assertEquals("48-8", launcher.getWindowsIcon().get(Launcher.ICON_WINDOWS_LARGE_LOW));

        List<PluginRef> plugins = config.getPlugins();
        Assert.assertNotNull(plugins);
        Assert.assertEquals(2, plugins.size());

        PluginRef plugin = plugins.get(0);
        Assert.assertNotNull(plugin);
        Assert.assertEquals("HeadlessProduct", plugin.getId());
        Assert.assertNull(plugin.getVersion());

        List<FeatureRef> features = config.getFeatures();
        Assert.assertNotNull(features);
        Assert.assertEquals(2, features.size());

        FeatureRef feature = features.get(0);
        Assert.assertNotNull(feature);
        Assert.assertEquals("HeadlessFeature", feature.getId());
        Assert.assertEquals("1.0.0", feature.getVersion());
    }

    @Test
    public void testProductConfigurationParseWithStartLevel() throws Exception {
        ProductConfiguration config = ProductConfiguration.read(getClass().getResourceAsStream(
                "/product/MyProduct.product"));
        Map<String, BundleConfiguration> bundles = config.getPluginConfiguration();
//		<plugin id="org.eclipse.core.contenttype" autoStart="true" startLevel="1" />
        BundleConfiguration contentType = bundles.get("org.eclipse.core.contenttype");
        Assert.assertNotNull(contentType);
        Assert.assertTrue(contentType.isAutoStart());
        Assert.assertEquals(1, contentType.getStartLevel());

//	      <plugin id="HeadlessProduct" autoStart="false" startLevel="2" />
        BundleConfiguration headlessProduct = bundles.get("HeadlessProduct");
        Assert.assertNotNull(headlessProduct);
        Assert.assertFalse(headlessProduct.isAutoStart());
        Assert.assertEquals(2, headlessProduct.getStartLevel());

    }

    @Test
    public void testSplashLocation() throws Exception {
        ProductConfiguration config = ProductConfiguration.read(getClass().getResourceAsStream(
                "/product/MyProduct.product"));

        String splashLocation = config.getSplashLocation();

        Assert.assertNotNull(splashLocation);
        Assert.assertEquals("org.sonatype.tycho.splashLocation", splashLocation);
    }
}
