/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.build.common.model;

import java.util.ArrayList;
import java.util.List;

import org.wildfly.build.ArtifactResolver;
import org.wildfly.build.pack.model.Artifact;

/**
 * Represents an artifact that is copies into a specific location in the final
 * build.
 *
 *
 * @author Stuart Douglas
 */
public class CopyArtifact {

    private final Artifact artifact;
    private final String toLocation;
    private final boolean extract;
    private final String fromLocation;
    private final List<FileFilter> filters = new ArrayList<>();


    public CopyArtifact(String artifact, String toLocation, boolean extract, String fromLocation) {
        this.artifact = Artifact.parse(artifact);
        this.toLocation = toLocation;
        this.extract = extract || fromLocation != null;
        if ( fromLocation != null ) {
            if ( ! fromLocation.endsWith( "/" ) ) {
                fromLocation = fromLocation + "/";
            }
        }
        this.fromLocation = fromLocation;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public Artifact getArtifact(ArtifactResolver resolver) {
        String version = artifact.getVersion();
        if (version != null) {
            return artifact;
        } else {
            Artifact artifact = resolver.getArtifact(this.artifact);
            if(artifact == null) {
                throw new RuntimeException("Could not resolve artifact for copy artifact " + this.artifact);
            }
            return artifact;
        }
    }

    public String getToLocation() {
        return toLocation;
    }

    public boolean isExtract() {
        return extract;
    }

    public List<FileFilter> getFilters() {
        return filters;
    }

    public boolean includeFile(final String path) {
        for(FileFilter filter : filters) {
            if(filter.matches(path)) {
                return filter.isInclude();
            }
        }
        return true; //default include
    }

    public String relocatedPath(final String path) {
        if ( this.fromLocation == null ) {
            return path;
        }

        if ( path.startsWith( this.fromLocation ) ) {
            return path.substring( this.fromLocation.length() );
        }

        return null;
    }
}
