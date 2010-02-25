package org.codehaus.tycho;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.tycho.model.Target;

public class TargetPlatformConfiguration
{
    private String resolver;

    private List<TargetEnvironment> environments = new ArrayList<TargetEnvironment>();

    private boolean implicitTargetEnvironment = true;

    private Target target;

    private String pomDependencies;

    private boolean ignoreTychoRepositories;

    public List<TargetEnvironment> getEnvironments()
    {
        return environments;
    }

    public String getTargetPlatformResolver()
    {
        return resolver;
    }

    public Target getTarget()
    {
        return target;
    }

    public void addEnvironment( TargetEnvironment environment )
    {
        this.environments.add( environment );
    }

    public void setResolver( String resolver )
    {
        this.resolver = resolver;
    }

    public void setTarget( Target target )
    {
        this.target = target;
    }

    public void setPomDependencies( String pomDependencies )
    {
        this.pomDependencies = pomDependencies;
    }

    public String getPomDependencies()
    {
        return pomDependencies;
    }

    public void setIgnoreTychoRepositories( boolean ignoreTychoRepositories )
    {
        this.ignoreTychoRepositories = ignoreTychoRepositories;
    }

    public boolean isIgnoreTychoRepositories()
    {
        return ignoreTychoRepositories;
    }

    public boolean isImplicitTargetEnvironment()
    {
        return implicitTargetEnvironment;
    }

    public void setImplicitTargetEnvironment( boolean implicitTargetEnvironment )
    {
        this.implicitTargetEnvironment = implicitTargetEnvironment;
    }
}
