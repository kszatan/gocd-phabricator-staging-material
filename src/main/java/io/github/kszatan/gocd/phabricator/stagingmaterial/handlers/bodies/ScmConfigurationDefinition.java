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

public class ScmConfigurationDefinition {
    public class Field {
        @SerializedName("display-name")
        public String displayName;

        @SerializedName("display-order")
        public String displayOrder;

        @SerializedName("default-value")
        public String defaultValue;

        public Boolean secure;

        @SerializedName("part-of-identity")
        public Boolean partOfIdentity;
        public Boolean required;
    }

    public Field url;
    public Field username;
    public Field password;

    public ScmConfigurationDefinition() {
        url = new Field();
        url.displayName = "Repository Url";
        url.displayOrder = "0";

        username = new Field();
        username.displayName = "Username";
        username.displayOrder = "1";
        username.partOfIdentity = false;
        username.required = false;

        password = new Field();
        password.displayName = "Password";
        password.displayOrder = "2";
        password.partOfIdentity = false;
        password.required = false;
        password.secure = true;
    }
}
