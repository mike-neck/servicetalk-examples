/*
 * Copyright 2019 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NativeImageTask extends DefaultTask {

    private <T> Property<T> property(Class<T> klass) {
        return getProject().getObjects().property(klass);
    }

    private GraalExtension extension;

    public NativeImageTask() {
        this.extension = new GraalExtension(getProject());
    }

    public void setExtension(GraalExtension extension) {
        this.extension = extension;
    }

    @TaskAction
    public void createNativeImage() {
        createOutputDirectoryIfNotExisting();
        getProject().exec(execSpec -> {
            execSpec.setExecutable(graalVmHome().resolve("bin/native-image"));
            execSpec.args(arguments());
        });
    }

    private Path graalVmHome() {
        return Paths.get(extension.graalVmHome.get());
    }

    private File outputDirectory() {
        Project project = getProject();
        return project.getBuildDir().toPath().resolve("native-image").toFile();
    }

    private void createOutputDirectoryIfNotExisting() {
        File outputDir = outputDirectory();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    private Collection<File> runtimeClasspath() {
        return extension.runtimeClasspath.get().getFiles();
    }

    private File jarFile() {
        return extension.jarTask.get().getOutputs().getFiles().getSingleFile();
    }

    private List<String> arguments() {
        List<String> args = new ArrayList<>();
        args.add("-cp");
        args.add(classpath());
        args.add("-H:Path=" + outputDirectory().getAbsolutePath());
        if (extension.executableName.isPresent()) {
            args.add("-H:Name=" + extension.executableName.get());
        }
        if (extension.additionalArguments.isPresent()) {
            args.addAll(extension.additionalArguments.get());
        }
        args.add(extension.mainClass.get());
        return Collections.unmodifiableList(args);
    }

    private String classpath() {
        List<File> paths = new ArrayList<>(runtimeClasspath());
        paths.add(jarFile());
        return paths.stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(":"));
    }

    @OutputFile
    public File getOutputExecutable() {
        getLogger().info("status of executableName: {}, extension: {}", extension.executableName.isPresent(), extension.toString());
        return outputDirectory().toPath().resolve(extension.executableName.get()).toFile();
    }
}
