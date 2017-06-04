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
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.git.Commit;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.git.DiffEntry;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.git.Repository;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.git.Tag;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public Repository cloneRepository(ScmConfiguration configuration, String workDirPath) throws JGitWrapperException {
        CloneCommand command = org.eclipse.jgit.api.Git.cloneRepository()
                .setBare(true)
                .setGitDir(new File(workDirPath))
                .setNoCheckout(true)
                .setURI(configuration.getUrl());
        if (configuration.hasCredentials()) {
            command.setCredentialsProvider(new UsernamePasswordCredentialsProvider(configuration.getUsername(), configuration.getPassword()));
        }
        return call(command);
    }

    public Repository cloneOrUpdateRepository(ScmConfiguration configuration, String workDirPath) throws JGitWrapperException {
        org.eclipse.jgit.api.Git git;
        try {
            git = org.eclipse.jgit.api.Git.open(new File(workDirPath));
            FetchCommand command = git.fetch().setTagOpt(TagOpt.FETCH_TAGS);
            call(command);
        } catch (IOException e) {
            return cloneRepository(configuration, workDirPath);
        }
        return new Repository(git);
    }

    public Collection<Tag> fetchTags(Repository repository) throws JGitWrapperException {
        ListTagCommand command = repository.getGit().tagList();
        Collection<Ref> refs = call(command);
        return refs.stream().map(r -> new Tag(r)).collect(Collectors.toList());
    }

    public Collection<Commit> log(Repository repository, Tag tag) throws JGitWrapperException {
        LogCommand command = repository.getGit().log();
        try {
            command.add(tag.getObjectId());
        } catch (MissingObjectException | IncorrectObjectTypeException e) {
            throw new JGitWrapperException("JGit was unable to get log: " + e.getMessage());
        }
        Iterable<RevCommit> commits = call(command);
        return StreamSupport.stream(commits.spliterator(), false)
                .map(c -> new Commit(c))
                .collect(Collectors.toList());
    }

    public Collection<DiffEntry> diff(Repository repository, Commit begin, Commit end) throws JGitWrapperException {
        org.eclipse.jgit.api.Git git = repository.getGit();
        ObjectReader reader = git.getRepository().newObjectReader();
        CanonicalTreeParser newTreeIterator;
        CanonicalTreeParser oldTreeIterator;
        try {
            newTreeIterator = new CanonicalTreeParser(null, reader, begin.getTree().getId());
            oldTreeIterator = new CanonicalTreeParser(null, reader, end.getTree().getId());
        } catch (Exception e) {
            throw new JGitWrapperException("Unable to parse commit tree: " + e.getMessage());
        }
        DiffCommand command = git.diff()
                .setNewTree(newTreeIterator)
                .setOldTree(oldTreeIterator)
                .setShowNameAndStatusOnly(true);
        return call(command).stream().map(d -> new DiffEntry(d)).collect(Collectors.toList());
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

    private Repository call(CloneCommand command) throws JGitWrapperException {
        try {
            return new Repository(command.call());
        } catch (TransportException e) {
            throw new JGitWrapperException("Transport layer exception: " + e.getMessage());
        } catch (InvalidRemoteException e) {
            throw new JGitWrapperException("Invalid remote: " + e.getMessage());
        } catch (GitAPIException e) {
            throw new JGitWrapperException("General JGit exception: " + e.getMessage());
        }
    }

    private Collection<Ref> call(ListTagCommand command) throws JGitWrapperException {
        try {
            return command.call();
        } catch (GitAPIException e) {
            throw new JGitWrapperException("General JGit exception: " + e.getMessage());
        }
    }

    private Iterable<RevCommit> call(LogCommand command) throws JGitWrapperException {
        try {
            return command.call();
        } catch (NoHeadException e) {
            throw new JGitWrapperException("No HEAD found: " + e.getMessage());
        } catch (GitAPIException e) {
            throw new JGitWrapperException("General JGit exception: " + e.getMessage());
        }
    }

    private FetchResult call(FetchCommand command) throws JGitWrapperException {
        try {
            return command.call();
        } catch (TransportException e) {
            throw new JGitWrapperException("Transport layer exception: " + e.getMessage());
        } catch (InvalidRemoteException e) {
            throw new JGitWrapperException("Invalid remote: " + e.getMessage());
        } catch (GitAPIException e) {
            throw new JGitWrapperException("General JGit exception: " + e.getMessage());
        }
    }

    private Collection<org.eclipse.jgit.diff.DiffEntry> call(DiffCommand command) throws JGitWrapperException {
        try {
            return command.call();
        } catch (GitAPIException e) {
            throw new JGitWrapperException("General JGit exception: " + e.getMessage());
        }
    }
}
