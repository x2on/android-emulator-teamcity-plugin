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
import de.felixschulze.teamcity.androidEmulator.tools.AdbTool;
import de.felixschulze.teamcity.androidEmulator.tools.EmulatorTool;
import jetbrains.buildServer.agent.BuildRunnerContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

class AndroidEnvironment {
    @NotNull
    private final BuildRunnerContext context;
    @NotNull
    private final String androidHome;

    public AndroidEnvironment(@NotNull final BuildRunnerContext context,
                              final boolean needTarget,
                              final boolean needJava) {
        String androidHome;
        this.context = context;

        final Map<String, String> environmentVariables = context.getBuildParameters().getEnvironmentVariables();

        //ONLY FOR DEBUG
        for (String str : environmentVariables.keySet()) {
            System.out.println(str + ": " + environmentVariables.get(str));
        }

        androidHome = environmentVariables.get(de.felixschulze.teamcity.androidEmulator.AndroidEmulatorConstants.ENV_ANDROID_HOME);
        System.out.println("ANDROID_HOME: " + androidHome);

        if (androidHome == null) {
            //DEBUG
            androidHome = "/usr/local/Cellar/android-sdk/r11/";
        }

        this.androidHome = androidHome;
    }

    @NotNull
    public AdbTool getAdbTool() {
        return new AdbTool(getSdkToolPath("adb"), context);
    }

    @NotNull
    public EmulatorTool getEmulatorTool() {
        return new EmulatorTool(getSdkToolPath("emulator"), context);
    }

    @NotNull
    private String getSdkToolPath(@NotNull final String toolName) {
        return getAndroidToolPath(androidHome, toolName);
    }

    @NotNull
    private static String getAndroidToolPath(@NotNull final String home, @NotNull final String toolName) {
        return getToolPath(home, "bin", toolName);
    }

    @NotNull
    private static String getToolPath(@NotNull final String home, @NotNull final String dir, @NotNull final String toolName) {
        return new File(new File(home, dir), toolName).getAbsolutePath();
    }
}
