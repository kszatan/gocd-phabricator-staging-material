/*
 * Copyright (c) 2017 kszatan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.kszatan.gocd.phabricator.stagingmaterial.scm;

import com.thoughtworks.go.plugin.api.logging.Logger;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.LatestRevision;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.LatestRevisionResult;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.Revision;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConfiguration;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.util.*;

class Git implements Scm {
    private final ScmConfiguration configuration;
    private List<String> errors;

    Git(ScmConfiguration configuration) {
        this.configuration = configuration;
        errors = new ArrayList<>();
    }

    @Override
    public Boolean canConnect() {
        errors.clear();
        TransportCommand lsRemote = org.eclipse.jgit.api.Git.lsRemoteRepository()
                .setHeads(true)
                .setRemote(configuration.url);
        if (configuration.hasCredentials()) {
            lsRemote.setCredentialsProvider(new UsernamePasswordCredentialsProvider(configuration.username, configuration.password));
        }
        try {
            lsRemote.call();
        } catch (Exception e) {
            errors.add(e.getMessage());
        }
        return errors.isEmpty();
    }

    @Override
    public LatestRevisionResult getLatestRevision(String gitDir) {
        return new LatestRevisionResult();
    }
    
    @Override
    public Collection<String> getErrors() {
        return errors;
    }
}
