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
package org.eclipse.tycho.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.pdark.decentxml.Element;

/*
 <launcherArgs>
 <programArgs>-nl en_US -console</programArgs>
 <programArgsLin>-mLinux</programArgsLin>
 <programArgsMac>-mMac</programArgsMac>
 <programArgsSol>-mSolaris</programArgsSol>
 <programArgsWin>-mWin</programArgsWin>
 <vmArgs>-Xms256M -Xmx512M -XX:MaxPermSize=512M</vmArgs>
 <vmArgsLin>-vmLinux</vmArgsLin>
 <vmArgsMac>-XstartOnFirstThread -Dorg.eclipse.swt.internal.carbon.smallFonts</vmArgsMac>
 <vmArgsSol>-vmSolaris</vmArgsSol>
 <vmArgsWin>-vmWin</vmArgsWin>
 </launcherArgs>

 -nl
 en_US
 -console

 -vmargs
 -Xms256M
 -Xmx512M
 -XX:MaxPermSize=512M

 */
public class LauncherArguments {

    public static final String PROG_ARGS = "programArgs";
    public static final String PROG_ARGS_WIN = "programArgsWin";
    public static final String PROG_ARGS_MAC = "programArgsMac";
    public static final String PROG_ARGS_SOLARIS = "programArgsSol";
    public static final String PROG_ARGS_LINUX = "programArgsLin";

    public static final String VM_ARGS = "vmArgs";
    public static final String VM_ARGS_WIN = "vmArgsWin";
    public static final String VM_ARGS_MAC = "vmArgsMac";
    public static final String VM_ARGS_SOLARIS = "vmArgsSol";
    public static final String VM_ARGS_LINUX = "vmArgsLin";

    private Element dom;

    public LauncherArguments(Element domLauncher) {
        this.dom = domLauncher;
    }

    /**
     * @return A List of common arguments defined in <b>programArgs</b> section or an empty List - never <b>null</b> 
     */
    public List<String> getProgramArgs() {
        return getArguments(PROG_ARGS);
    }

    /**
     * @return A List of Windows specific arguments defined in <b>programArgsWin</b> section or an empty List - never <b>null</b> 
     */
    public List<String> getWindowsProgramArgs() {
        return getArguments(PROG_ARGS_WIN);
    }

    /**
     * @return A List of Linux specific arguments defined in <b>programArgsLin</b> section or an empty List - never <b>null</b> 
     */
    public List<String> getLinuxProgramArgs() {
        return getArguments(PROG_ARGS_LINUX);
    }

    /**
     * @return A List of Solaris specific arguments defined in <b>programArgsSol</b> section or an empty List - never <b>null</b> 
     */
    public List<String> getSolarisProgramArgs() {
        return getArguments(PROG_ARGS_SOLARIS);
    }

    /**
     * @return A List of Mac specific arguments defined in <b>programArgsMac</b> section or an empty List - never <b>null</b> 
     */
    public List<String> getMacProgramArgs() {
        return getArguments(PROG_ARGS_MAC);
    }

    /**
     * @return A List of common specific arguments defined in <b>vmArgs</b> section or an empty List - never <b>null</b> 
     */
    public List<String> getVMArgs() {
        return getArguments(VM_ARGS);
    }

    /**
     * @return A List of Windows specific arguments defined in <b>vmArgsWin</b> section or an empty List - never <b>null</b> 
     */
    public List<String> getWindowsVMArgs() {
        return getArguments(VM_ARGS_WIN);
    }

    /**
     * @return A List of Linux specific arguments defined in <b>vmArgsWin</b> section or an empty List - never <b>null</b> 
     */
    public List<String> getLinuxVMArgs() {
        return getArguments(VM_ARGS_LINUX);
    }

    /**
     * @return A List of Solaris specific arguments defined in <b>vmArgsWin</b> section or an empty List - never <b>null</b> 
     */
    public List<String> getSolarisVMArgs() {
        return getArguments(VM_ARGS_SOLARIS);
    }

    /**
     * @return A List of Mac specific arguments defined in <b>vmArgsWin</b> section or an empty List - never <b>null</b> 
     */
    public List<String> getMacVMArgs() {
        return getArguments(VM_ARGS_MAC);
    }

    private List<String> getArguments(String environment) {
        Element linuxDom = dom.getChild(environment);
        if (linuxDom != null) {
            List<String> arguments = new ArrayList<String>();
            putIfNotNull(arguments, linuxDom.getNormalizedText());
            return arguments;
        }
        return Collections.emptyList();
    }

    private void putIfNotNull(List<String> set, String values) {
        if (values != null) {
            String[] arguments = values.split(" ");
            if (arguments != null) {
                for (String argument : arguments) {
                    set.add(argument);
                }
            }
        }
    }
}
