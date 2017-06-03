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

import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GitTest {
    private ScmConfiguration configuration;
    private Scm scm;
    private JGitWrapper wrapper;

    @Before
    public void setUp() throws Exception {
        configuration = new ScmConfiguration();
        configuration.url = "https://github.com/kszatan/gocd-phabricator-staging-material.git";
        wrapper = mock(JGitWrapper.class);
        scm = new Git(configuration, wrapper);
    }

    @Test
    public void checkConnectionShouldReturnTrueInAbsenceOfExceptions() throws Exception {
        when(wrapper.lsRemote(anyString())).thenReturn(new ArrayList<String>());
        assertTrue(scm.canConnect());
        verify(wrapper).lsRemote(configuration.url);
    }

    @Test
    public void checkConnectionWithCredentialShouldReturnTrueInAbsenceOfExceptions() throws Exception {
        when(wrapper.lsRemote(anyString(), anyString(), anyString())).thenReturn(new ArrayList<String>());
        configuration.username = "kszatan";
        configuration.password = "hunter2";
        assertTrue(scm.canConnect());
        verify(wrapper).lsRemote(configuration.url, configuration.username, configuration.password);
    }

    @Test
    public void checkConnectionShouldReturnFalseWhenWrapperThrowsException() throws Exception {
        String message = "What we've got here is failure to communicate";
        when(wrapper.lsRemote(anyString())).thenThrow(new JGitWrapperException(message));
        assertFalse(scm.canConnect());
        verify(wrapper).lsRemote(configuration.url);
        assertThat(scm.getLastErrorMessage(), equalTo(message));
    }

}