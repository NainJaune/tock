/*
 * Copyright (C) 2017 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.tock.shared.security.auth

import ai.tock.shared.jackson.mapper
import ai.tock.shared.vertx.WebVerticle
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.Cookie
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.SessionHandler

/**
 *
 */
internal abstract class SSOTockAuthProvider(val vertx: Vertx) : TockAuthProvider {

    private object AddSSOCookieHandler : Handler<RoutingContext> {

        override fun handle(c: RoutingContext) {
            val cookie = Cookie.cookie("tock-sso", "1")
            cookie.path = "/"
            // Don't set max age - it's a session cookie
            c.addCookie(cookie)
            c.next()
        }
    }

    override val sessionCookieName: String get() = "tock-sso-session"

    abstract fun createAuthHandler(verticle: WebVerticle): AuthHandler

    override fun protectPaths(
        verticle: WebVerticle,
        pathsToProtect: Set<String>,
        sessionHandler: SessionHandler
    ): AuthHandler {
        val authHandler = createAuthHandler(verticle)
        with(verticle) {
            val excluded = excludedPaths(verticle)
            router.route("/*").handler(WithExcludedPathHandler(excluded, sessionHandler))
            router.route("/*").handler(WithExcludedPathHandler(excluded, authHandler))
            router.route("/*").handler(AddSSOCookieHandler)

            router.get("$basePath/user").handler { it.response().end(mapper.writeValueAsString(toTockUser(it))) }
        }
        return authHandler
    }
}