/*
 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jggug.kobo.groovyserv.test

import static org.junit.Assert.*

/**
 * Utilities only for tests.
 */
class TestUtils {

    static Process executeClient(args, closure = null) {
        executeClientWithEnv(args, null, closure)
    }

    static Process executeClientOk(args, closure = null) {
        executeClientOkWithEnv(args, null, closure)
    }

    static Process executeClientWithEnv(args, Map envMap, closure = null) {
        def client = clientExecutablePath.split(" ") as List
        def p = createProcessBuilder([* client, * args], envMap).start()
        if (closure) closure.call(p)
        p.waitFor()
        return p
    }

    static Process executeClientOkWithEnv(args, Map envMap, closure = null) {
        def p = executeClientWithEnv(args, envMap, closure)
        if (p.exitValue() != 0) {
            fail "ERROR: exitValue:${p.exitValue()}, in:[${p.in.text}], err:[${p.err.text}]"
        }
        return p
    }

    static startServer(verbose = false) {
        def p = createProcessBuilder(["sh", serverExecutablePath, "-r", "-v"]).start()
        p.waitFor()
        if (verbose) {
            def inText = p.in.text
            if (inText) System.out.println inText
            def errText = p.err.text
            if (errText) System.err.println errText
        }
    }

    static shutdownServer(verbose = false) {
        def p = createProcessBuilder(["sh", serverExecutablePath, "-k"]).start()
        p.waitFor()
        if (verbose) {
            def inText = p.in.text
            if (inText) System.out.println inText
            def errText = p.err.text
            if (errText) System.err.println errText
        }
    }

    private static createProcessBuilder(List commandLine, Map envMap = [:]) {
        ProcessBuilder processBuilder = new ProcessBuilder()
        def actualCommand = processBuilder.command()

        // This doesn't work on cygwin/windows. command line is somehow split by white space.
//        def env = processBuilder.environment()
//        envMap.each { key, value ->
//            env.put(key.toString(), value.toString()) // without this, ArrayStoreException may occur
//        }
        actualCommand << "env"
        envMap.each { key, value ->
            actualCommand << "${key}=${value}".toString() // without this, ArrayStoreException may occur
        }

        commandLine.each { arg ->
            actualCommand << arg.toString() // without this, ArrayStoreException may occur
        }

        return processBuilder
    }

    private static getClientExecutablePath() {
        System.getProperty("groovyserv.executable.client")
    }

    private static getServerExecutablePath() {
        System.getProperty("groovyserv.executable.server")
    }

    /**
     * Reading and return available bytes.
     * This method is not blocking.
     */
    static getAvailableText(ins) {
        def byteList = new ArrayList<Byte>()
        int length
        while ((length = ins.available()) > 0) {
            byte[] bytes = new byte[length]
            int ret = ins.read(bytes, 0, length)
            if (ret == -1) {
                break
            }
            for (int i = 0; i < ret; i++) {
                byteList.add(bytes[i])
            }
        }
        return new String(byteList as byte[])
    }

}