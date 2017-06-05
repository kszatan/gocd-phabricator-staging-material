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

import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.LatestRevisionResponse;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.LatestRevisionsSinceResponse;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.Revision;

import java.util.Optional;

/**
 * Common interface for classes implementing logic for particular SCMs. So far
 * the only supported SCM is git.
 */
public interface Scm {
    /**
     * Try to connect to a repository.
     * @return {@code true} if successfully connected, {@code false} otherwise.
     */
    Boolean canConnect();

    /**
     * Enquire repository of latest revision info.
     * @param workDirPath Path to a directory SCM can use for this operation.
     * @return latest revision info.
     */
    Optional<LatestRevisionResponse> getLatestRevision(String workDirPath);

    /**
     * Enquire repository of latest revisions info.
     * @param workDirPath Path to a directory SCM can use for this operation.
     * @param latestRevision Latest known revision.
     * @return latest revision info.
     */
    Optional<LatestRevisionsSinceResponse> getLatestRevisionsSince(String workDirPath, Revision latestRevision);

    /**
     * Checkout revision to checkoutDirPath directory.
     * @param revision Revision to checkout.
     * @param checkoutDirPath Path to checkout directory.
     * @return {@code true} if successfully checked out, {@code false} otherwise.
     */
    Boolean checkout(Revision revision, String checkoutDirPath);

    /**
     * @return error returned from the last invoked operation, if any.
     */
    String getLastErrorMessage();
}
