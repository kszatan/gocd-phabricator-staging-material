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
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.LatestRevisionResult;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ModifiedFile;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.Revision;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConfiguration;

import java.util.*;

class Git implements Scm {
    private final ScmConfiguration configuration;
    private final JGitWrapper jgitWrapper;
    private String lastErrorMessage;

    private static Logger LOGGER = Logger.getLoggerFor(Scm.class);

    Git(ScmConfiguration configuration, JGitWrapper jgitWrapper) {
        this.configuration = configuration;
        this.jgitWrapper = jgitWrapper;
        this.lastErrorMessage = "";
    }

    @Override
    public Boolean canConnect() {
        Boolean connected = true;
        try {
            if (configuration.hasCredentials()) {
                jgitWrapper.lsRemote(configuration.url, configuration.username, configuration.password);
            } else {
                jgitWrapper.lsRemote(configuration.url);
            }
        } catch (JGitWrapperException e) {
            lastErrorMessage = e.getMessage();
            connected = false;
        }
        return connected;
    }

    @Override
    public Optional<LatestRevisionResult> getLatestRevision(String workDirPath) {
        Revision revision = new Revision();
        try {
            Repository repository = jgitWrapper.cloneRepository(configuration, workDirPath);
            Collection<Tag> tagList = jgitWrapper.fetchTags(repository);
            Optional<Tag> lastRevisionTag = tagList.stream()
                    .filter(t -> t.getName().startsWith("refs/tags/phabricator/diff/"))
                    .sorted(Comparator.comparing(Tag::getName))
                    .reduce((a, b) -> b);
            if (lastRevisionTag.isPresent()) {
                Tag tag = lastRevisionTag.get();
                revision.revision = tag.getName().replace("refs/tags/phabricator/diff/", "");
                Collection<Commit> commits = jgitWrapper.log(repository, tag);
                Commit tip = commits.iterator().next();
                fillRevisionCommitInfo(revision, tip);
                Commit tipParent = tip.parent();
                fillRevisionModifiedFiles(revision, jgitWrapper.diff(repository, tip, tipParent));
            }
        } catch (JGitWrapperException e) {
            lastErrorMessage = e.getMessage();
            return Optional.empty();
        }
        return Optional.of(new LatestRevisionResult(revision));
    }

    @Override
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    private void fillRevisionCommitInfo(Revision revision, Commit commit) {
        revision.revisionComment = commit.comment();
        revision.timestamp = commit.commitTime();
        revision.user = commit.author();
    }

    private void fillRevisionModifiedFiles(Revision revision, Collection<DiffEntry> entries) {
        for (DiffEntry entry : entries) {
            ModifiedFile file = new ModifiedFile(entry.path(), entry.action());
            revision.modifiedFiles.add(file);
        }
    }
}
