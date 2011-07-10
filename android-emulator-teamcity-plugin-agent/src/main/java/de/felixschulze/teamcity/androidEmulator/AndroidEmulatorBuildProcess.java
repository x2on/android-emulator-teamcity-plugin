/*
 * Copyright 2011 Felix Schulze
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

package de.felixschulze.teamcity.androidEmulator;

import jetbrains.buildServer.agent.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ExecutionException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AndroidEmulatorBuildProcess extends CallableBuildProcess {

    private static final int BOOT_COMPLETE_TIMEOUT = 120 * 1000;

    @NotNull
    private final AgentRunningBuild build;
    @NotNull
    private final BuildRunnerContext context;
    @NotNull
    private final AndroidEnvironment environment;

    public AndroidEmulatorBuildProcess(@NotNull final AgentRunningBuild build, @NotNull final BuildRunnerContext context) {
        this.build = build;
        this.context = context;
        environment = new AndroidEnvironment(context, true, true);
    }

    @NotNull
    public BuildFinishedStatus call() throws Exception {
        final BuildProgressLogger logger = build.getBuildLogger();

        logger.targetStarted("Android-Emulator");

        //Shutdown running emulator
        shutdownEmulator();

        final String avdName = getParameter(AndroidEmulatorConstants.PARAM_AVDNAME, false);
        final Boolean wipe = Boolean.parseBoolean(getParameter(AndroidEmulatorConstants.PARAM_WIPE, false));
        final Boolean xwindow = Boolean.parseBoolean(getParameter(AndroidEmulatorConstants.PARAM_XWINDOW, false));

        //For wipe more time is needed
        int bootTimeout = BOOT_COMPLETE_TIMEOUT;
        if (wipe) {
            bootTimeout *= 4;
        }

        final long bootTime = System.currentTimeMillis();

        startEmulator(avdName, wipe, xwindow);

        if (waitForBootComplete(bootTimeout)) {
            logger.message("Emulator started.");

            //Unlock only needed if not wiped
            if (!wipe) {
                final long bootDuration = System.currentTimeMillis() - bootTime;
                unlockEmulator(bootDuration);
            }

            final long bootCompleteTime = System.currentTimeMillis();
            logger.message("Emulator is ready for use (took " + ((bootCompleteTime - bootTime) / 1000) + " seconds)");

            logger.targetFinished("Android-Emulator");
            return BuildFinishedStatus.FINISHED_SUCCESS;
        } else {
            logger.error("Emulator could not started.");
            return BuildFinishedStatus.FINISHED_FAILED;
        }
    }

    private String getParameter(@NotNull final String parameterName, final boolean isPath) {
        final String value = context.getRunnerParameters().get(parameterName);
        if (value == null || value.trim().length() == 0) return null;
        String result = value.trim();
        if (isPath) {
            result = preparePath(result);
        }
        return result;
    }


    @NotNull
    private String preparePath(@NotNull final String value) {
        return new File(value).isAbsolute() ? value : new File(context.getBuild().getCheckoutDirectory(), value).getAbsolutePath();
    }


    private void startEmulator(String avdName, Boolean wipe, Boolean xwindow) {
        final BuildProgressLogger logger = build.getBuildLogger();

        logger.targetStarted("startEmulator");
        final List<String> argsAfter = new ArrayList<String>();
        argsAfter.add("-avd");
        argsAfter.add(avdName);

        if (wipe) {
            argsAfter.add("-wipe-data");

        }
        if (!xwindow) {
            argsAfter.add("-no-window");
        }

        //Can speed up bootTime
        argsAfter.add("-no-boot-anim");

        try {
            environment.getEmulatorTool().emulatorExecute(argsAfter);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
        }
        logger.targetFinished("startEmulator");

    }


    private boolean waitForBootComplete(int bootTimeout) {
        final BuildProgressLogger logger = build.getBuildLogger();

        logger.targetStarted("waitForBootComplete");
        final List<String> argsAfter = new ArrayList<String>();
        argsAfter.add("shell");
        argsAfter.add("getprop");
        argsAfter.add("dev.bootcomplete");


        long start = System.currentTimeMillis();
        int sleepTime = bootTimeout / (int) Math.sqrt(bootTimeout / 1000);

        while (System.currentTimeMillis() < start + bootTimeout) {


            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream(4);

                environment.getAdbTool().adbExecute(argsAfter, stream);

                String result = stream.toString().trim();
                if (result.equals("1")) {
                    logger.targetFinished("waitForBootComplete");
                    return true;
                }

                Thread.sleep(sleepTime);

            } catch (IOException e) {
                logger.error(e.getMessage());
            } catch (ExecutionException e) {
                logger.error(e.getMessage());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
        logger.targetFinished("waitForBootComplete");

        return false;
    }


    private void unlockEmulator(long bootDuration) {
        final BuildProgressLogger logger = build.getBuildLogger();
        logger.targetStarted("unlockEmulator");

        logger.message("unlock emulator screen");

        try {
            Thread.sleep(bootDuration / 4);

            final List<String> argsAfter = new ArrayList<String>();
            argsAfter.add("shell");
            argsAfter.add("input");
            argsAfter.add("keyevent");
            argsAfter.add("82");

            environment.getAdbTool().adbExecuteWithoutWaiting(argsAfter);

        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
        }
        logger.targetFinished("unlockEmulator");

    }


    private void shutdownEmulator() {
        final BuildProgressLogger logger = build.getBuildLogger();
        logger.targetStarted("shutdownEmulator");

        try {

            final List<String> argsAfter = new ArrayList<String>();
            argsAfter.add("emu");
            argsAfter.add("kill");

            environment.getAdbTool().adbExecuteWithoutWaiting(argsAfter);

        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
        }

        logger.targetFinished("shutdownEmulator");

    }


}
