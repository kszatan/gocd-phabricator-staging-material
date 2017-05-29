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

import java.util.List;
import java.util.Objects;

public class ScmConnectionResult {
    public String status;
    public List<String> messages;

    public static ScmConnectionResult success(List<String> messages) {
        return create("success", messages);
    }

    public static ScmConnectionResult failure(List<String> messages) {
        return create("failure", messages);
    }

    private static ScmConnectionResult create(String status, List<String> messages) {
        ScmConnectionResult result = new ScmConnectionResult();
        result.status = status;
        result.messages = messages;
        return result;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null) { return false; }
        if (getClass() != o.getClass()) { return false; }
        ScmConnectionResult result = (ScmConnectionResult) o;
        return Objects.equals(status, result.status)
                && Objects.equals(messages, result.messages);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(status, messages);
    }
}