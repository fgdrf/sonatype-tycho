package org.codehaus.tycho.osgitools;

import java.io.File;
import java.util.Set;

import org.sonatype.tycho.ArtifactDescriptor;
import org.sonatype.tycho.ArtifactKey;
import org.sonatype.tycho.resolver.DependentMavenProjectProxy;

public class DefaultArtifactDescriptor
    implements ArtifactDescriptor
{

    private final ArtifactKey key;

    private final File location;

    private final DependentMavenProjectProxy project;

    private final Set<Object> installableUnits;

    public DefaultArtifactDescriptor( ArtifactKey key, File location, DependentMavenProjectProxy project, Set<Object> installableUnits )
    {
        this.key = key;
        this.location = location;
        this.project = project;
        this.installableUnits = installableUnits;
    }

    public ArtifactKey getKey()
    {
        return key;
    }

    public File getLocation()
    {
        return location;
    }

    public DependentMavenProjectProxy getMavenProject()
    {
        return project;
    }

    public Set<Object> getInstallableUnits()
    {
        return installableUnits;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( key.toString() ).append( ": " );
        if ( project != null )
        {
            sb.append( project.toString() );
        }
        else
        {
            sb.append( location );
        }
        return sb.toString();
    }
}