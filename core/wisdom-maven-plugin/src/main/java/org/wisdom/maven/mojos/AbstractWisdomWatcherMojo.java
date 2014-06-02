/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2014 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.wisdom.maven.mojos;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.wisdom.maven.MavenWatcher;
import org.wisdom.maven.Watcher;
import org.wisdom.maven.pipeline.Watchers;

/**
 * Common part.
 */
public abstract class AbstractWisdomWatcherMojo extends AbstractWisdomMojo implements MavenWatcher {

    /**
     * Sets the Maven Session and registers the current mojo to the watcher list (stored in the session).
     * @param session the maven session
     */
    public void setSession(MavenSession session) {
        this.session = session;
        Watchers.add(session, this);
    }

    /**
     * Removes the current mojo from the watcher list.
     */
    public void removeFromWatching() {
        Watchers.remove(session, this);
    }

    @Override
    public MavenSession session() {
        return session;
    }

    @Override
    public MavenProject project() {
        return project;
    }
}
