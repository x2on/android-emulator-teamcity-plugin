/*
 * Copyright 2000-2010 JetBrains s.r.o.
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
 */

package de.felixschulze.teamcity.androidEmulator.tools;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.Key;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Collections;
import java.util.List;

abstract class AbstractTool {
    @NotNull
    private final String toolPath;
    @NotNull
    private final BuildRunnerContext context;

    public AbstractTool(@NotNull final String toolPath, @NotNull final BuildRunnerContext context) {
        this.toolPath = toolPath;
        this.context = context;
    }

    @NotNull
    protected File getWorkingDirectory() {
        return context.getWorkingDirectory();
    }

    protected void execute(@NotNull final List<String> argumentsBefore,
                           @NotNull final List<String> argumentsAfter,
                           @NotNull final String additionalArguments,
                           @NotNull final ByteArrayOutputStream stream) throws IOException, ExecutionException {

        final GeneralCommandLine commandLine = createCommandLine(argumentsBefore, argumentsAfter, additionalArguments);

        final BuildProgressLogger logger = context.getBuild().getBuildLogger();

        logger.message("Executing command: " + commandLine.getCommandLineString());

        final OSProcessHandler processHandler = createProcessHandler(commandLine);

        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void onTextAvailable(final ProcessEvent event, final Key outputType) {

                if (outputType == ProcessOutputTypes.STDERR || outputType == ProcessOutputTypes.STDOUT) {
                    try {
                        stream.write(event.getText().getBytes());
                    } catch (IOException e) {
                        logger.error("Error while getting process output: "+e.getStackTrace());
                    }
                }
            }
        });

        processHandler.startNotify();
        processHandler.waitFor();

    }

        protected void executeWithoutWaiting(@NotNull final List<String> argumentsAfter) throws IOException, ExecutionException {

        final GeneralCommandLine commandLine = createCommandLine(Collections.<String>emptyList(), argumentsAfter, "");

        final BuildProgressLogger logger = context.getBuild().getBuildLogger();

        logger.message("Executing command: " + commandLine.getCommandLineString());

        final OSProcessHandler processHandler = createProcessHandler(commandLine);

        processHandler.startNotify();

    }

    @NotNull
    private static OSProcessHandler createProcessHandler(@NotNull final GeneralCommandLine commandLine) throws ExecutionException {
        return new OSProcessHandler(commandLine.createProcess(), "") {
            @Override
            protected Reader createProcessOutReader() {
                return new InputStreamReader(getProcess().getInputStream());
            }

            @Override
            protected Reader createProcessErrReader() {
                return new InputStreamReader(getProcess().getErrorStream());
            }
        };
    }

    @NotNull
    private GeneralCommandLine createCommandLine(@NotNull final List<String> argumentsBefore,
                                                 @NotNull final List<String> argumentsAfter,
                                                 @NotNull final String additionalArguments) {
        final GeneralCommandLine cmd = new GeneralCommandLine();

        cmd.setExePath(toolPath);
        cmd.setWorkDirectory(getWorkingDirectory().getPath());
        cmd.setPassParentEnvs(false);
        cmd.setEnvParams(context.getBuildParameters().getEnvironmentVariables());
        cmd.addParameters(argumentsBefore);
        cmd.getParametersList().addParametersString(additionalArguments);
        cmd.addParameters(argumentsAfter);

        return cmd;
    }
}
