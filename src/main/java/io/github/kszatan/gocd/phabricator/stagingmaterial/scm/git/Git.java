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

package io.github.kszatan.gocd.phabricator.stagingmaterial.scm.git;

import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.*;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.Scm;

import java.util.*;
import java.util.stream.Collectors;

public class Git implements Scm {
    private final ScmConfiguration configuration;
    private final JGitWrapper jgitWrapper;
    private String lastErrorMessage;

    private static final String TAG_PREFIX = "refs/tags/phabricator/diff/";

    public Git(ScmConfiguration configuration, JGitWrapper jgitWrapper) {
        this.configuration = configuration;
        this.jgitWrapper = jgitWrapper;
        this.lastErrorMessage = "";
    }

    @Override
    public Boolean canConnect() {
        Boolean connected = true;
        try {
            if (configuration.hasCredentials()) {
                jgitWrapper.lsRemote(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
            } else {
                jgitWrapper.lsRemote(configuration.getUrl());
            }
        } catch (JGitWrapperException e) {
            lastErrorMessage = e.getMessage();
            connected = false;
        }
        return connected;
    }

    @Override
    public Optional<LatestRevisionResponse> getLatestRevision(String workDirPath) {
        Revision revision = new Revision();
        try {
            Repository repository = jgitWrapper.cloneOrUpdateRepository(configuration, workDirPath, true);
            Collection<Tag> tagList = jgitWrapper.fetchTags(repository);
            Optional<Tag> lastRevisionTag = getLastRevisionTag(tagList);
            if (lastRevisionTag.isPresent()) {
                revision = getLastRevisionInfoForTag(repository, lastRevisionTag.get());
            }
        } catch (JGitWrapperException e) {
            lastErrorMessage = e.getMessage();
            return Optional.empty();
        }
        return Optional.of(new LatestRevisionResponse(revision));
    }

    private Revision getLastRevisionInfoForTag(Repository repository, Tag tag) throws JGitWrapperException {
        Revision revision = new Revision();
        revision.revision = getLastRevisionNumber(tag);
        Commit tip = getLastCommitForTag(repository, tag);
        fillRevisionCommitInfo(revision, tip);
        Collection<DiffEntry> diff = jgitWrapper.diff(repository, tip, tip.parent());
        fillRevisionModifiedFiles(revision, diff);
        return revision;
    }

    private Commit getLastCommitForTag(Repository repository, Tag tag) throws JGitWrapperException {
        Collection<Commit> commits = jgitWrapper.log(repository, tag);
        return commits.iterator().next();
    }

    private String getLastRevisionNumber(Tag tag) {
        return tag.getName().replace(TAG_PREFIX, "");
    }

    private Optional<Tag> getLastRevisionTag(Collection<Tag> tagList) {
        return tagList.stream()
                .filter(t -> t.getName().startsWith(TAG_PREFIX))
                .sorted(Comparator.comparing(Tag::getName))
                .reduce((a, b) -> b);
    }

    @Override
    public Optional<LatestRevisionsSinceResponse> getLatestRevisionsSince(String workDirPath, Revision latestRevision) {
        ArrayList<Revision> revisions;
        try {
            Repository repository = jgitWrapper.cloneOrUpdateRepository(configuration, workDirPath, true);
            Collection<Tag> tagList = jgitWrapper.fetchTags(repository);
            Integer latestRevisionNum = Integer.valueOf(latestRevision.revision);
            List<Tag> latestTags = getTagsAddedSinceLatestRevision(tagList, latestRevisionNum);
            revisions = getLastRevisionsInfoForTags(repository, latestTags);
        } catch (JGitWrapperException e) {
            lastErrorMessage = e.getMessage();
            return Optional.empty();
        }
        return Optional.of(new LatestRevisionsSinceResponse(revisions));
    }

    private ArrayList<Revision> getLastRevisionsInfoForTags(Repository repository, List<Tag> latestTags) throws JGitWrapperException {
        ArrayList<Revision> revisions = new ArrayList<>();
        for (Tag tag : latestTags) {
            revisions.add(getLastRevisionInfoForTag(repository, tag));
        }
        return revisions;
    }

    private List<Tag> getTagsAddedSinceLatestRevision(Collection<Tag> tagList, Integer latestRevisionNum) {
        return tagList.stream()
                .filter(t -> t.getName().startsWith(TAG_PREFIX))
                .filter(t -> {
                    String rev = getLastRevisionNumber(t);
                    return Integer.valueOf(rev) > latestRevisionNum;
                })
                .sorted(Comparator.comparing(Tag::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean checkout(Revision revision, String checkoutDirPath) {
        try {
            Repository repository = jgitWrapper.cloneOrUpdateRepository(configuration, checkoutDirPath, false);
            jgitWrapper.checkout(repository, TAG_PREFIX + revision.revision);
            return true;
        } catch (JGitWrapperException e) {
            lastErrorMessage = e.getMessage();
            return false;
        }
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
