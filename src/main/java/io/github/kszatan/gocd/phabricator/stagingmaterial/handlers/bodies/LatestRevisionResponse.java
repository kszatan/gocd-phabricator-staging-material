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

package io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class LatestRevisionResponse {
    public Revision revision;
    @SerializedName("scm-data")
    public ScmData scmData;

    public LatestRevisionResponse() {
    }

    public LatestRevisionResponse(Revision revision) {
        this.revision = revision;
    }

    public String toJson() {
        return GsonService.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null) { return false; }
        if (getClass() != o.getClass()) { return false; }
        LatestRevisionResponse result = (LatestRevisionResponse) o;
        return Objects.equals(revision, result.revision)
                && Objects.equals(scmData, result.scmData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revision, scmData);
    }
}