/*******************************************************************************
 * Copyright (c) 2008, 2011 Sonatype Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype Inc. - initial API and implementation
 *    SAP AG - inject nested class path elements into maven model (TYCHO-483)
 *******************************************************************************/
package org.eclipse.tycho.core.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.tycho.ArtifactDescriptor;
import org.eclipse.tycho.ArtifactKey;
import org.eclipse.tycho.ReactorProject;
import org.eclipse.tycho.core.TargetPlatform;
import org.eclipse.tycho.core.osgitools.BundleReader;
import org.osgi.framework.Constants;

public final class MavenDependencyInjector {

    /* see RepositoryLayoutHelper#getP2Gav */
    private static final String P2_GROUPID_PREFIX = "p2.";

    /**
     * Injects the dependencies of a project (as determined by the p2 target platform resolver) back
     * into the Maven model.
     * 
     * @param project
     *            A project
     * @param target
     *            The p2-resolved target platform of the project.
     */
    public static void injectMavenDependencies(MavenProject project, TargetPlatform target, BundleReader bundleReader,
            Logger logger) {
        MavenDependencyInjector generator = new MavenDependencyInjector(project, bundleReader, logger);
        for (ArtifactDescriptor artifact : target.getArtifacts()) {
            generator.addDependency(artifact);
        }
    }

    /**
     * Filters out the system scope artifacts added through this class.
     * 
     * @param artifacts
     *            the list of the resolved artifacts of a project which may have dependencies
     *            injected by this class
     * @return the list of artifacts required by non-injected POM dependencies.
     */
    public static List<Artifact> filterInjectedDependencies(Collection<? extends Artifact> artifacts) {
        List<Artifact> filteredArtifacts = new ArrayList<Artifact>();
        for (Artifact artifact : artifacts) {
            if (artifact.getGroupId().startsWith(P2_GROUPID_PREFIX) && artifact.getScope() == Artifact.SCOPE_SYSTEM)
                continue; // came through injected dependency -> remove
            else
                filteredArtifacts.add(artifact);
        }
        return filteredArtifacts;
    }

    private static final List<String> DOT_CLASSPATH = Collections.singletonList(".");
    private static final List<Dependency> NO_DEPENDENCIES = Collections.emptyList();

    private final BundleReader bundleReader;
    private final Logger logger;

    private final MavenProject project;

    MavenDependencyInjector(MavenProject project, BundleReader bundleReader, Logger logger) {
        this.project = project;
        this.bundleReader = bundleReader;
        this.logger = logger;
    }

    void addDependency(ArtifactDescriptor artifact) {
        List<Dependency> dependencyList = new ArrayList<Dependency>();
        if (artifact.getMavenProject() != null) {
            if (!artifact.getMavenProject().sameProject(project)) {
                dependencyList.addAll(newProjectDependencies(artifact));
            }
        } else {
            dependencyList.addAll(newExternalDependencies(artifact));
        }
        Model model = project.getModel();
        for (Dependency dependency : dependencyList) {
            model.addDependency(dependency);
        }
    }

    private List<Dependency> newExternalDependencies(ArtifactDescriptor artifact) {
        File location = artifact.getLocation();
        if (!location.isFile() || !location.canRead()) {
            logger.warn("Dependency at location " + location
                    + " can not be represented in Maven model and will not be visible to non-OSGi aware Maven plugins");
            return NO_DEPENDENCIES;
        }
        List<Dependency> result = new ArrayList<Dependency>();
        if (ArtifactKey.TYPE_ECLIPSE_PLUGIN.equals(artifact.getKey().getType())) {
            for (String classpathElement : getClasspathElements(location)) {
                if (".".equals(classpathElement)) {
                    result.add(createSystemScopeDependency(artifact.getKey(), location));
                } else {
                    File nestedJarOrDir = bundleReader.getEntry(location, classpathElement);
                    if (nestedJarOrDir != null) {
                        if (nestedJarOrDir.isFile()) {
                            Dependency nestedJarDependency = createSystemScopeDependency(artifact.getKey(),
                                    nestedJarOrDir);
                            nestedJarDependency.setClassifier(classpathElement);
                            result.add(nestedJarDependency);
                        } else if (nestedJarOrDir.isDirectory()) {
                            // system-scoped dependencies on directories are not supported
                            logger.warn("Dependency from "
                                    + project.getBasedir()
                                    + " to nested directory classpath entry "
                                    + nestedJarOrDir
                                    + " can not be represented in Maven model and will not be visible to non-OSGi aware Maven plugins");
                        }
                    }
                }
            }
        } else {
            result.add(createSystemScopeDependency(artifact.getKey(), location));
        }
        return result;
    }

    private List<String> getClasspathElements(File bundleLocation) {
        ManifestElement[] classpathHeader = bundleReader.parseHeader(Constants.BUNDLE_CLASSPATH,
                bundleReader.loadManifest(bundleLocation));
        if (classpathHeader == null || classpathHeader.length == 0) {
            return DOT_CLASSPATH;
        }
        List<String> result = new ArrayList<String>(classpathHeader.length);
        for (ManifestElement classPathElement : classpathHeader) {
            result.add(classPathElement.getValue());
        }
        return result;
    }

    private Dependency createSystemScopeDependency(ArtifactKey artifactKey, File location) {
        return createSystemScopeDependency(artifactKey, P2_GROUPID_PREFIX + artifactKey.getType(), location);
    }

    private Dependency createSystemScopeDependency(ArtifactKey artifactKey, String groupId, File location) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactKey.getId());
        dependency.setVersion(artifactKey.getVersion());
        dependency.setScope(Artifact.SCOPE_SYSTEM);
        dependency.setSystemPath(location.getAbsolutePath());
        return dependency;
    }

    private List<Dependency> newProjectDependencies(ArtifactDescriptor artifact) {
        ReactorProject dependentMavenProjectProxy = artifact.getMavenProject();
        List<Dependency> result = new ArrayList<Dependency>();
        result.add(createProvidedScopeDependency(dependentMavenProjectProxy));
        if (ArtifactKey.TYPE_ECLIPSE_PLUGIN.equals(dependentMavenProjectProxy.getPackaging())) {
            for (String classpathElement : getClasspathElements(dependentMavenProjectProxy.getBasedir())) {
                if (".".equals(classpathElement)) {
                    // covered by provided-scope dependency above
                    continue;
                } else /* nested classpath entry */
                {
                    File jar = new File(dependentMavenProjectProxy.getBasedir(), classpathElement);
                    // we can only add a system scope dependency for an existing (checked-in) jar file
                    // otherwise maven will throw a DependencyResolutionException
                    if (jar.isFile()) {
                        Dependency systemScopeDependency = createSystemScopeDependency(artifact.getKey(), artifact
                                .getMavenProject().getGroupId(), jar);
                        systemScopeDependency.setClassifier(classpathElement);
                        result.add(systemScopeDependency);
                    } else {
                        logger.warn("Dependency from "
                                + project.getBasedir()
                                + " to nested classpath entry "
                                + jar.getAbsolutePath()
                                + " can not be represented in Maven model and will not be visible to non-OSGi aware Maven plugins");
                    }
                }
            }
        }
        return result;
    }

    private Dependency createProvidedScopeDependency(ReactorProject dependentReactorProject) {
        Dependency dependency = new Dependency();
        dependency.setArtifactId(dependentReactorProject.getArtifactId());
        dependency.setGroupId(dependentReactorProject.getGroupId());
        dependency.setVersion(dependentReactorProject.getVersion());
        dependency.setType(dependentReactorProject.getPackaging());
        dependency.setScope(Artifact.SCOPE_PROVIDED);
        return dependency;
    }
}
