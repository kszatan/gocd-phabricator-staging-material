package io.github.kszatan.gocd.phabricator.stagingmaterial.scm.git;

import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConfiguration;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class JGitWrapperIntegrationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void lsRemoteShouldThrowWhenCannotOpenRepository() throws Exception {
        thrown.expect(JGitWrapperException.class);
        thrown.expectMessage("Transport layer exception:");
        
        Path repositoryPath = uninitializedRepository();

        JGitWrapper wrapper = new JGitWrapper();
        wrapper.lsRemote(repositoryPath.toString());
    }

    @Test
    public void lsRemoteShouldThrowGivenInvalidUrl() throws Exception {
        thrown.expect(JGitWrapperException.class);
        thrown.expectMessage("Runtime JGit exception:");

        JGitWrapper wrapper = new JGitWrapper();
        wrapper.lsRemote("protocol://9.asdf");
    }

    @Test
    public void lsRemoteShouldThrowGivenEmptyUrl() throws Exception {
        thrown.expect(JGitWrapperException.class);
        thrown.expectMessage("Invalid remote: ");

        JGitWrapper wrapper = new JGitWrapper();
        wrapper.lsRemote("");
    }

    @Test
    public void lsRemoteShouldThrowGivenUnsupportedUrl() throws Exception {
        thrown.expect(JGitWrapperException.class);
        thrown.expectMessage("Invalid remote: ");

        JGitWrapper wrapper = new JGitWrapper();
        wrapper.lsRemote("/");
    }

    @Test
    public void lsRemoteShouldReturnHeadsForRepository() throws Exception {
        Path repositoryPath = defaultRepository();
        JGitWrapper wrapper = new JGitWrapper();

        Collection<String> heads = wrapper.lsRemote(repositoryPath.toString());

        assertThat(heads, hasItems("refs/heads/master", "refs/heads/branch1", "refs/heads/branch2"));
    }

    @Test
    public void cloneShouldBeAbleToCloneBareRepository() throws Exception {
        Path repositoryPath = defaultRepository();
        JGitWrapper wrapper = new JGitWrapper();
        ScmConfiguration configuration = new ScmConfiguration();
        configuration.setUrl(repositoryPath.toString());

        Path clonedRepositoryPath = uninitializedRepository();
        wrapper.cloneOrUpdateRepository(configuration, clonedRepositoryPath.toString(), true);

        try (org.eclipse.jgit.lib.Repository repository = openRepository(clonedRepositoryPath)) {
            assertThat(repository.isBare(), equalTo(true));
        }
    }

    @Test
    public void cloneShouldBeAbleToCloneNonBareRepository() throws Exception {
        Path repositoryPath = defaultRepository();
        JGitWrapper wrapper = new JGitWrapper();
        ScmConfiguration configuration = new ScmConfiguration();
        configuration.setUrl(repositoryPath.toString());

        Path clonedRepositoryPath = uninitializedRepository();
        wrapper.cloneOrUpdateRepository(configuration, clonedRepositoryPath.toString(), false);

        try (org.eclipse.jgit.lib.Repository repository = openRepository(clonedRepositoryPath.resolve(".git"))) {
            assertThat(repository.isBare(), equalTo(false));
        }
    }

    @Test
    public void cloneShouldFetchTags() throws Exception {
        Path repositoryPath = defaultRepository();
        JGitWrapper wrapper = new JGitWrapper();
        ScmConfiguration configuration = new ScmConfiguration();
        configuration.setUrl(repositoryPath.toString());

        Path clonedRepositoryPath = uninitializedRepository();
        wrapper.cloneOrUpdateRepository(configuration, clonedRepositoryPath.toString(), true);

        try (org.eclipse.jgit.lib.Repository repository = openRepository(clonedRepositoryPath)) {
            org.eclipse.jgit.api.Git git = new org.eclipse.jgit.api.Git(repository);
            List<Ref> tagList = git.tagList().call();
            assertThat(tagList.stream().map(Ref::getName).collect(Collectors.toList()),
                    hasItems("refs/tags/phabricator/base/1", "refs/tags/phabricator/base/2",
                            "refs/tags/phabricator/diff/1", "refs/tags/phabricator/diff/2"));
        }
    }

    @Test
    public void fetchTagsShouldFetchTagsObviously() throws Exception {
        Path repositoryPath = defaultRepository();
        Repository repository = new Repository(org.eclipse.jgit.api.Git.open(repositoryPath.toFile()));
        JGitWrapper wrapper = new JGitWrapper();

        Collection<Tag> tags = wrapper.fetchTags(repository);

        assertThat(tags.stream().map(Tag::getName).collect(Collectors.toList()),
                hasItems("refs/tags/phabricator/base/1", "refs/tags/phabricator/base/2",
                        "refs/tags/phabricator/diff/1", "refs/tags/phabricator/diff/2"));
    }

    @Test
    public void logShouldReturnCommitsForATag() throws Exception {
        Path repositoryPath = defaultRepository();
        Repository repository = new Repository(org.eclipse.jgit.api.Git.open(repositoryPath.toFile()));
        JGitWrapper wrapper = new JGitWrapper();

        Collection<Tag> tags = wrapper.fetchTags(repository);
        Optional<Tag> tag = tags.stream().filter(t -> t.getName().equals("refs/tags/phabricator/base/2")).findFirst();
        Collection<Commit> commits = wrapper.log(repository, tag.get());

        assertThat(commits.size(), equalTo(2));
        assertThat(commits.stream().map(c -> c.comment()).collect(Collectors.toList()),
                hasItems("Create file1", "Create file2"));
    }

    @Test
    public void givenCorrectCommitRangeDiffShouldReturnCommitDifference() throws Exception {
        Path repositoryPath = defaultRepository();
        Repository repository = new Repository(org.eclipse.jgit.api.Git.open(repositoryPath.toFile()));
        JGitWrapper wrapper = new JGitWrapper();

        Collection<Tag> tags = wrapper.fetchTags(repository);
        Optional<Tag> tag1 = tags.stream().filter(t -> t.getName().equals("refs/tags/phabricator/base/2")).findFirst();
        Commit commit1 = wrapper.log(repository, tag1.get()).iterator().next();
        Optional<Tag> tag2 = tags.stream().filter(t -> t.getName().equals("refs/tags/phabricator/base/1")).findFirst();
        Commit commit2 = wrapper.log(repository, tag2.get()).iterator().next();
        Collection<DiffEntry> diffs = wrapper.diff(repository, commit1, commit2);

        assertThat(diffs.size(), equalTo(1));
        DiffEntry diff = diffs.iterator().next();
        assertThat(diff.action(), equalTo("added"));
        assertThat(diff.path(), equalTo("file2"));
    }

    private org.eclipse.jgit.lib.Repository openRepository(Path gitDirPath) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder.setGitDir(gitDirPath.toFile())
                .readEnvironment()
                .setup()
                .build();
    }

    private Path uninitializedRepository() throws java.io.IOException {
        return Files.createTempDirectory("");
    }

    private Path defaultRepository() throws java.io.IOException, GitAPIException {
        Path repositoryPath = uninitializedRepository();
        Files.createFile(repositoryPath.resolve("file1"));
        Files.createFile(repositoryPath.resolve("file2"));
        org.eclipse.jgit.api.Git git = org.eclipse.jgit.api.Git.init().setDirectory(repositoryPath.toFile()).call();
        git.add().addFilepattern("file1").call();
        git.commit().setMessage( "Create file1" ).call();
        git.tag().setName("phabricator/base/1").setAnnotated(false).call();
        git.tag().setName("phabricator/diff/1").setAnnotated(false).call();
        git.add().addFilepattern("file2").call();
        git.commit().setMessage( "Create file2").call();
        git.tag().setName("phabricator/base/2").setAnnotated(false).call();
        git.tag().setName("phabricator/diff/2").setAnnotated(false).call();
        git.branchCreate().setName("branch1").call();
        git.branchCreate().setName("branch2").call();

        git.checkout().setName("branch1").call();
        Files.createFile(repositoryPath.resolve("file3"));
        git.add().addFilepattern("file3").call();
        git.commit().setMessage( "Create file3" ).call();

        git.checkout().setName("branch2").call();
        Files.createFile(repositoryPath.resolve("file4"));
        git.add().addFilepattern("file4").call();
        git.commit().setMessage( "Create file4" ).call();
        return repositoryPath;
    }
}
