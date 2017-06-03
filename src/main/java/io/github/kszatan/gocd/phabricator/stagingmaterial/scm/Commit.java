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

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;

import java.sql.Timestamp;
import java.util.Date;

public class Commit {
    private final RevCommit commit;

    public Commit(RevCommit commit) {
        this.commit = commit;
    }

    public Commit parent() {
        return new Commit(commit.getParent(0));
    }

    public RevTree getTree() {
        return commit.getTree();
    }

    public String comment() {
        return commit.getFullMessage();
    }

    public Date commitTime() {
        return commit.getAuthorIdent().getWhen();
//        Timestamp timestamp = new Timestamp(commit.getCommitTime());
//        return new Date(timestamp.getTime());
    }

    public String author() {
        PersonIdent ident = commit.getAuthorIdent();
        return ident.getName() + " " + ident.getEmailAddress();
    }
}
