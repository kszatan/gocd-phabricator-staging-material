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

import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConfiguration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for executing operations on a repository indicated by {@code configuration} parameter. So far the
 * only supported SCM is git. If other SCMs are to be supported this should be made an interface.
 */
public class Scm {
    private ScmConfiguration configuration;
    private List<String> errors;

    public Scm(ScmConfiguration configuration) {
        this.configuration = configuration;
        this.errors = new ArrayList<>();
    }

    public Boolean canConnect() {
        errors.clear();
        TransportCommand lsRemote = Git.lsRemoteRepository().setHeads(true).setRemote(configuration.url);
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

    /**
     * @return errors returned from the last invoked operation, if any.
     */
    public List<String> getErrors() {
        return errors;
    }

}
