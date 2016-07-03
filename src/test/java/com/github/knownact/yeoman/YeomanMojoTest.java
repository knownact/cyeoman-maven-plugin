package com.github.knownact.yeoman;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.util.List;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.mockito.ArgumentCaptor;

/*
 * Copyright 2013 Thomas Recloux
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
public class YeomanMojoTest extends AbstractMojoTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test_should_run_all_commands_with_default_configuration() throws Exception {
        MavenProject project = getMavenProject("src/test/resources/test-mojo-default-pom.xml");
        YeomanMojo yeomanMojo = (YeomanMojo) lookupConfiguredMojo(project, "build");

        List<String> commands = executeMojoAndCaptureCommands(yeomanMojo);

        assertThat(commands).containsExactly(
                "node --version",
                "cnpm --version",
                "cnpm install",
                "bower --version",
                "bower install --no-color",
                "grunt --version",
                "grunt test --no-color",
                "grunt build --no-color"
        );
    }

    public void test_should_skip_build_when_flag_set() throws Exception {
        MavenProject project = getMavenProject("src/test/resources/test-mojo-default-pom.xml");
        YeomanMojo yeomanMojo = (YeomanMojo) lookupConfiguredMojo(project, "build");
        yeomanMojo.skipBuild = true;

        List<String> commands = executeMojoAndCaptureCommands(yeomanMojo);

        assertThat(commands).containsExactly(
                "node --version",
                "cnpm --version",
                "cnpm install",
                "bower --version",
                "bower install --no-color",
                "grunt --version",
                "grunt test --no-color"
        );
    }

    public void test_should_skip_npm_install_when_flag_set() throws Exception {
        MavenProject project = getMavenProject("src/test/resources/test-mojo-default-pom.xml");
        YeomanMojo yeomanMojo = (YeomanMojo) lookupConfiguredMojo(project, "build");
        yeomanMojo.skipNpmInstall = true;

        List<String> commands = executeMojoAndCaptureCommands(yeomanMojo);

        assertThat(commands).containsExactly(
                "bower --version",
                "bower install --no-color",
                "grunt --version",
                "grunt test --no-color",
                "grunt build --no-color"
        );
    }

    public void test_should_skip_bower_install_when_flag_set() throws Exception {
        MavenProject project = getMavenProject("src/test/resources/test-mojo-default-pom.xml");
        YeomanMojo yeomanMojo = (YeomanMojo) lookupConfiguredMojo(project, "build");
        yeomanMojo.skipBowerInstall = true;

        List<String> commands = executeMojoAndCaptureCommands(yeomanMojo);

        assertThat(commands).containsExactly(
                "node --version",
                "cnpm --version",
                "cnpm install",
                "grunt --version",
                "grunt test --no-color",
                "grunt build --no-color"
        );
    }

    public void test_should_skip_tests_when_flag_set() throws Exception {
        MavenProject project = getMavenProject("src/test/resources/test-mojo-default-pom.xml");
        YeomanMojo yeomanMojo = (YeomanMojo) lookupConfiguredMojo(project, "build");
        yeomanMojo.skipTests = true;

        List<String> commands = executeMojoAndCaptureCommands(yeomanMojo);

        assertThat(commands).containsExactly(
                "node --version",
                "cnpm --version",
                "cnpm install",
                "bower --version",
                "bower install --no-color",
                "grunt --version",
                "grunt build --no-color"
        );
    }

    public void test_should_run_all_commands_with_custom_args() throws Exception {
        YeomanMojo yeomanMojo = (YeomanMojo) lookupMojo("build", "src/test/resources/test-mojo-configuration-pom.xml");

        List<String> commands = executeMojoAndCaptureCommands(yeomanMojo);

        assertThat(commands).containsExactly(
                "node --version",
                "cnpm --version",
                "cnpm arg1",
                "bower-art --version",
                "bower-art arg2",
                "grunt --version",
                "grunt arg3",
                "grunt arg4"
        );
    }

    public void test_should_configure_using_deprecated_parameters() throws Exception {
        MavenProject project = getMavenProject("src/test/resources/test-mojo-default-pom.xml");
        YeomanMojo yeomanMojo = (YeomanMojo) lookupConfiguredMojo(project, "build");
        yeomanMojo.gruntTestArgs = "arg3";
        yeomanMojo.gruntBuildArgs = "arg4";

        List<String> commands = executeMojoAndCaptureCommands(yeomanMojo);

        assertThat(commands).containsExactly(
                "node --version",
                "cnpm --version",
                "cnpm install",
                "bower --version",
                "bower install --no-color",
                "grunt --version",
                "grunt arg3",
                "grunt arg4"
        );
    }
    
    public void test_should_use_npm_cache() throws Exception {
    	YeomanMojo yeomanMojo = (YeomanMojo) lookupMojo("build", "src/test/resources/test-mojo-with-npm-cache-pom.xml");

        List<String> commands = executeMojoAndCaptureCommands(yeomanMojo);

        assertThat(commands).containsExactly(
                "node --version",
                "cnpm --version",
                "npm-cache arg1",
                "bower-art --version",
                "bower-art arg2",
                "grunt --version",
                "grunt arg3",
                "grunt arg4"
        );
    }

    private List<String> executeMojoAndCaptureCommands(YeomanMojo yeomanMojo) throws MojoExecutionException {
        YeomanMojo spy = spy(yeomanMojo);
        ArgumentCaptor<String> commandsCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(spy).executeCommand(commandsCaptor.capture());

        spy.execute();
        return commandsCaptor.getAllValues();
    }

    private MavenProject getMavenProject(String pomPath) throws Exception {
        File pom = new File(pomPath);
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request.setPom(pom);
        ProjectBuildingRequest configuration = request.getProjectBuildingRequest();
        return lookup( ProjectBuilder.class ).build( pom, configuration ).getProject();
    }
}
