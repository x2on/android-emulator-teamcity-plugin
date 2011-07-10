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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface AndroidEmulatorConstants {

    @NonNls
    @NotNull
    String ENV_ANDROID_HOME = "ANDROID_HOME";

    @NonNls
    @NotNull
    String RUNNER_TYPE = "Android-Emulator";

    @NonNls
    @NotNull
    String PARAM_AVDNAME = "avdName";

    @NonNls
    @NotNull
    String PARAM_WIPE = "wipe";

    @NonNls
    @NotNull
    String PARAM_XWINDOW = "xwindow";

}
