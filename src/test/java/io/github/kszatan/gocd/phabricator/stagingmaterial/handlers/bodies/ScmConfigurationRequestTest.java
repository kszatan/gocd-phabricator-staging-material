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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class ScmConfigurationRequestTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructorShouldParseCorrectJsonString() throws Exception {
        String json = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"},\"username\":{\"value\":\"kszatan\"},\"password\":{\"value\":\"hunter2\"}}}";
        ScmConfigurationRequest request = new ScmConfigurationRequest(json);
        ScmConfiguration configuration = request.getConfiguration();
        assertThat(configuration.getUrl(), equalTo("https://github.com/kszatan/gocd-phabricator-staging-material.git"));
        assertThat(configuration.getUsername(), equalTo("kszatan"));
        assertThat(configuration.getPassword(), equalTo("hunter2"));
    }

    @Test
    public void constructorShouldAcceptMissingUrlField() throws Exception {
        String json = "{\"scm-configuration\":{\"username\":{\"value\":\"kszatan\"},\"password\":{\"value\":\"hunter2\"}}}";
        ScmConfigurationRequest request = new ScmConfigurationRequest(json);
        ScmConfiguration configuration = request.getConfiguration();
        assertThat(configuration.getUrl(), equalTo(""));
        assertThat(configuration.getUsername(), equalTo("kszatan"));
        assertThat(configuration.getPassword(), equalTo("hunter2"));
    }

    @Test
    public void constructorShouldAcceptMissingUserField() throws Exception {
        String json = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"},\"password\":{\"value\":\"hunter2\"}}}";
        ScmConfigurationRequest request = new ScmConfigurationRequest(json);
        ScmConfiguration configuration = request.getConfiguration();
        assertThat(configuration.getUrl(), equalTo("https://github.com/kszatan/gocd-phabricator-staging-material.git"));
        assertThat(configuration.getUsername(), equalTo(""));
        assertThat(configuration.getPassword(), equalTo("hunter2"));
    }

    @Test
    public void constructorShouldAcceptMissingPasswordField() throws Exception {
        String json = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"},\"username\":{\"value\":\"kszatan\"}}}";
        ScmConfigurationRequest request = new ScmConfigurationRequest(json);
        ScmConfiguration configuration = request.getConfiguration();
        assertThat(configuration.getUrl(), equalTo("https://github.com/kszatan/gocd-phabricator-staging-material.git"));
        assertThat(configuration.getUsername(), equalTo("kszatan"));
        assertThat(configuration.getPassword(), equalTo(""));
    }

    @Test
    public void constructorShouldThrowAnExceptionGivenInvalidJson() throws Exception {
        thrown.expect(InvalidJson.class);
        String json = "{\"scm-view\":2}";
        new ScmConfigurationRequest(json);
    }
}