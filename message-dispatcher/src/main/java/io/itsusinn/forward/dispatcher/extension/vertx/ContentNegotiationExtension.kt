package io.itsusinn.forward.dispatcher.extension.vertx

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.itsusinn.extension.jackson.asPrettyString
import io.vertx.core.http.HttpServerResponse

private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

fun HttpServerResponse.html(): HttpServerResponse {
   return this.putHeader("content-type", "text/html; charset=utf-8")
}

fun HttpServerResponse.endWithHtml(chunk: String) =
   html().end(chunk)

fun HttpServerResponse.json(): HttpServerResponse =
   putHeader("content-type", "application/json; charset=utf-8")

fun HttpServerResponse.endWithJson(chunk: Any) =
   json().end(chunk.asPrettyString)

fun HttpServerResponse.text(): HttpServerResponse =
   putHeader("content-type", "text/plain; charset=utf-8")

fun HttpServerResponse.endWithText(chunk: String) =
   text().end(chunk)
