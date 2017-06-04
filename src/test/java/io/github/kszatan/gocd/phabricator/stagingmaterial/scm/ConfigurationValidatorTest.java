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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ConfigurationValidatorTest {
    private String url;
    
    @Parameterized.Parameters
    public static Iterable<? extends Object> data() {
        return Arrays.asList(
                "user@host:repo",
                "git@github.com:user/project.git",
                "https://github.com/user/project.git",
                "http://github.com/user/project.git",
                "git@192.168.101.127:user/project.git",
                "https://192.168.101.127/user/project.git",
                "http://192.168.101.127/user/project.git",
                "ssh://user@host.xz:port/path/to/repo.git/",
                "ssh://user@host.xz/path/to/repo.git/",
                "ssh://host.xz:port/path/to/repo.git/",
                "ssh://host.xz/path/to/repo.git/",
                "ssh://user@host.xz/path/to/repo.git/",
                "ssh://host.xz/path/to/repo.git/",
                "ssh://user@host.xz/~user/path/to/repo.git/",
                "ssh://host.xz/~user/path/to/repo.git/",
                "ssh://user@host.xz/~/path/to/repo.git",
                "ssh://host.xz/~/path/to/repo.git",
                "git://host.xz/path/to/repo.git/",
                "git://host.xz/~user/path/to/repo.git/",
                "http://host.xz/path/to/repo.git/",
                "https://host.xz/path/to/repo.git/",
                "user@host.xz:/path/to/repo.git/",
                "user@host.xz:~user/path/to/repo.git/",
                "user@host.xz:path/to/repo.git");
    }

    public ConfigurationValidatorTest(String url) {
        this.url = url;
    }
    
    @Test
    public void validateShouldWorkForRemoteUrls() throws Exception {
        ConfigurationValidator validator = new ConfigurationValidator();
        ScmConfiguration configuration = new ScmConfiguration();
        configuration.setUrl(url);
        assertThat(validator.validate(configuration).errors.isEmpty(), is(true));
    }

}