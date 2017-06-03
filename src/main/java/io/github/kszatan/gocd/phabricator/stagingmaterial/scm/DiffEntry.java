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

public class DiffEntry {
    private final org.eclipse.jgit.diff.DiffEntry entry;

    public DiffEntry(org.eclipse.jgit.diff.DiffEntry entry) {
        this.entry = entry;
    }

    public String path() {
        String path;
        switch (entry.getChangeType()) {
            case ADD:
            case COPY:
            case MODIFY:
                path = entry.getNewPath();
                break;
            case DELETE:
                path = entry.getOldPath();
                break;
            default:
                path = entry.getNewPath();
        }
        return path;
    }

    public String action() {
        String action;
        switch (entry.getChangeType()) {
            case ADD:
                action = "Added";
                break;
            case COPY:
                action = "Copied";
                break;
            case DELETE:
                action = "Deleted";
                break;
            case MODIFY:
                action = "Modified";
                break;
            case RENAME:
                action = "Renamed";
                break;
            default:
                action = "Changed";
        }
        return action;
    }
}
