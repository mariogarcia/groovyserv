/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jggug.kobo.groovyserv

/**
 * Tests for the {@link org.jggug.kobo.groovyserv.CurrentDirHolder} class.
 */
class CurrentDirHolderTest extends GroovyTestCase {

    CurrentDirHolder holder = CurrentDirHolder.instance
    String originalClasspath
    String workDir

    void setUp() {
        holder.reset()

        originalClasspath = System.properties["groovy.classpath"]
        System.properties.remove("groovy.classpath")

        workDir = File.createTempFile("groovyserv-", "-dummy").parent
    }

    void tearDown() {
        holder.reset()
        if (originalClasspath) {
            System.properties["groovy.classpath"] = originalClasspath
        } else {
            System.properties.remove("groovy.classpath")
        }
    }

    void testSet_notSetYet() {
        holder.setDir(workDir)
        assertCurrentDir(workDir)
    }

    void testSet_alreadySet_sameDir() {
        holder.setDir(workDir)
        assertCurrentDir(workDir)

        holder.setDir(workDir)
        assertCurrentDir(workDir)
    }

    void testSet_alreadySet_differentDir() {
        holder.setDir(workDir)
        shouldFail(GroovyServerException) {
            holder.setDir(workDir + "DIFFERENT")
        }
        assertCurrentDir(workDir)
    }

    void testSet_reset() {
        holder.setDir(workDir)
        assertCurrentDir(workDir)
        holder.reset()
        assertReset(workDir)
    }

    private void assertCurrentDir(dir) {
        assert holder.currentDir == dir
        assert System.properties['user.dir'] == dir
        assert System.properties["groovy.classpath"] == dir
    }

    private void assertReset(dir) {
        assert holder.currentDir == null
        assert System.properties['user.dir'] == CurrentDirHolder.ORIGINAL_USER_DIR
        assert System.properties["groovy.classpath"] == dir // this is not reset in current implementation
    }

}

