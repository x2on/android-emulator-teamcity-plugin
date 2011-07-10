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

package de.felixschulze.teamcity.androidEmulator;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

abstract class CallableBuildProcess implements BuildProcess, Callable<BuildFinishedStatus> {

    @NotNull
    private static final Logger LOG = Logger.getLogger(CallableBuildProcess.class);
    private Future<BuildFinishedStatus> future;

    public void start() throws RunBuildException {
        try {
            future = Executors.newSingleThreadExecutor().submit(this);
            LOG.info("BuildProcess started");
        } catch (final RejectedExecutionException e) {
            LOG.error("BuildProcess couldn't start: ", e);
            throw new RunBuildException(e);
        }
    }

    public void interrupt() {
        LOG.info("BuildProcess interrupt");
        future.cancel(true);
    }

    public boolean isInterrupted() {
        return future.isCancelled() && isFinished();
    }

    public boolean isFinished() {
        return future.isDone();
    }

    @NotNull
    public BuildFinishedStatus waitFor() throws RunBuildException {
        try {
            final BuildFinishedStatus status = future.get();
            LOG.info("BuildProcess finished");
            return status;
        } catch (final ExecutionException e) {
            throw new RunBuildException(e);
        } catch (final InterruptedException e) {
            throw new RunBuildException(e);
        } catch (final CancellationException e) {
            LOG.info("BuildProcess interrupted: ", e);
            return BuildFinishedStatus.INTERRUPTED;
        }
    }
}
