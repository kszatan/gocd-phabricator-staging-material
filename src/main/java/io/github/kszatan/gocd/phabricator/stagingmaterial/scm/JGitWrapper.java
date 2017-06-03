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

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.util.Collection;
import java.util.stream.Collectors;

public class JGitWrapper {
    public Collection<String> lsRemote(String url) throws JGitWrapperException {
        LsRemoteCommand lsRemote = lsRemoteRepository(url);
        return call(lsRemote);
    }

    public Collection<String> lsRemote(String url, String username, String password) throws JGitWrapperException {
        LsRemoteCommand lsRemote = lsRemoteRepository(url)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
        return call(lsRemote);
    }

    public CloneCommand cloneRepository() {
        return org.eclipse.jgit.api.Git.cloneRepository();
    }

    private LsRemoteCommand lsRemoteRepository(String url) {
        return org.eclipse.jgit.api.Git.lsRemoteRepository()
                .setHeads(true)
                .setRemote(url);
    }

    private Collection<String> call(LsRemoteCommand command) throws JGitWrapperException {
        Collection<Ref> refs;
        try {
            refs = command.call();
        } catch (TransportException e) {
            throw new JGitWrapperException("Transport layer exception: " + e.getMessage());
        } catch (InvalidRemoteException e) {
            throw new JGitWrapperException("Invalid remote: " + e.getMessage());
        } catch (GitAPIException e) {
            throw new JGitWrapperException("General JGit exception: " + e.getMessage());
        }
        return refs.stream().map(Ref::getName).collect(Collectors.toList());
    }
}
