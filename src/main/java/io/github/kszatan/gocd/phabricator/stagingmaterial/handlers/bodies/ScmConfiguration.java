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

public class ScmConfiguration {
    public class Entry {
        private String value = "";
    }
    private Entry url;
    private Entry username;
    private Entry password;

    public ScmConfiguration() {
        url = new Entry();
        username = new Entry();
        password = new Entry();
    }

    public String getUrl() {
        return url.value;
    }

    public String getUsername() {
        return username.value;
    }

    public String getPassword() {
        return password.value;
    }

    public void setUrl(String url) {
        this.url.value = url;
    }

    public void setUsername(String username) {
        this.username.value = username;
    }

    public void setPassword(String password) {
        this.password.value = password;
    }

    public boolean hasCredentials() {
        return !username.value.isEmpty() && !password.value.isEmpty();
    }
}
