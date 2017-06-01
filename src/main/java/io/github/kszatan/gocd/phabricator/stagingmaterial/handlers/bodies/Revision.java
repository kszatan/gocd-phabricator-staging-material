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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Revision {
    public RevisionData data;
    public String revisionComment;
    public String revision;
    public String timestamp;
    public String user;
    public List<ModifiedFile> modifiedFiles;

    public Revision() {
        data = new RevisionData();
        revisionComment = "";
        revision = "";
        timestamp = "";
        user = "";
        modifiedFiles = new ArrayList<>();
    }

    public Revision(String json) {
        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(json).getAsJsonObject();
        Gson gson = new Gson();
        if (root.has("data")) {
            data = gson.fromJson(root.getAsJsonObject("data").toString(), RevisionData.class);
        }
        if (root.has("modifiedFiles")) {
            Type type = new TypeToken<List<ModifiedFile>>() {}.getType();
            modifiedFiles = gson.fromJson(root.getAsJsonArray("modifiedFiles").toString(), type);
        }
        revisionComment = getField(root.getAsJsonObject(), "revisionComment");
        timestamp = getField(root.getAsJsonObject(), "timestamp");
        revision = getField(root.getAsJsonObject(), "revision");
        user = getField(root.getAsJsonObject(), "user");
    }

    private String getField(JsonObject jsonObject, String field) {
        if (!jsonObject.has(field)) {
            return "";
        }
        return jsonObject.get(field).getAsString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null) { return false; }
        if (getClass() != o.getClass()) { return false; }
        Revision result = (Revision) o;
        return Objects.equals(data, result.data)
                && Objects.equals(revisionComment, result.revisionComment)
                && Objects.equals(revision, result.revision)
                && Objects.equals(timestamp, result.timestamp)
                && Objects.equals(user, result.user)
                && Objects.equals(modifiedFiles, result.modifiedFiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, revisionComment, revision, timestamp, user, modifiedFiles);
    }
}
