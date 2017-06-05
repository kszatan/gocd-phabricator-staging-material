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

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class CheckoutRequestTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructorShouldParseCorrectJsonString() throws Exception {
        String json = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"}},\"destination-folder\":\"/var/lib/go-agent/pipelines/pipeline-name/destination\",\"revision\": {\"revision\": \"revision-1\",\"timestamp\": \"2011-07-14T19:43:37.100Z\",\"data\":{}}}";
        CheckoutRequest request = new CheckoutRequest(json);
        Checkout Checkout = request.getCheckout();
        assertThat(Checkout.configuration.getUrl(),
                equalTo("https://github.com/kszatan/gocd-phabricator-staging-material.git"));
        assertThat(Checkout.destinationFolder,
                equalTo("/var/lib/go-agent/pipelines/pipeline-name/destination"));
        Revision revision = Checkout.revision;
        assertThat(revision.revisionComment, equalTo(""));
        assertThat(revision.revision, equalTo("revision-1"));
        assertThat(revision.timestamp, equalTo(GsonService.fromJson("\"2011-07-14T19:43:37.100Z\"", Date.class)));
        assertThat(revision.user, equalTo(""));
        assertThat(revision.modifiedFiles.size(), equalTo(0));
    }

    @Test
    public void constructorShouldThrowWhenDestinationFolderFieldIsMissing() throws Exception {
        thrown.expect(IncompleteJson.class);
        thrown.expectMessage("Missing fields: ");
        String json = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"}},\"revision\": {\"revision\": \"revision-1\",\"timestamp\": \"2011-07-14T19:43:37.100Z\",\"data\":{}}}";
        new CheckoutRequest(json);
    }

    @Test
    public void constructorShouldThrowWhenRevisionFieldIsMissing() throws Exception {
        thrown.expect(IncompleteJson.class);
        thrown.expectMessage("Missing fields: ");
        String json = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"}},\"destination-folder\":\"/var/lib/go-agent/pipelines/pipeline-name/destination\"}";
        new CheckoutRequest(json);
    }

    @Test
    public void constructorShouldThrowWhenScmConfigurationFieldIsMissing() throws Exception {
        thrown.expect(IncompleteJson.class);
        thrown.expectMessage("Missing fields: ");
        String json = "{\"destination-folder\":\"/var/lib/go-agent/pipelines/pipeline-name/destination\",\"revision\": {\"revision\": \"revision-1\",\"timestamp\": \"2011-07-14T19:43:37.100Z\",\"data\":{}}}";
        new CheckoutRequest(json);
    }

    @Test
    public void constructorShouldThrowGivenInvalidJson() throws Exception {
        thrown.expect(InvalidJson.class);
        thrown.expectMessage("Malformed JSON: ");
        String json = "Invalid JSON";
        new CheckoutRequest(json);
    }
}