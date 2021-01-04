package io.github.itsusinn.extension

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.http.HttpServerResponse

private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

fun HttpServerResponse.html() : HttpServerResponse {
   return this.putHeader("content-type","text/html; charset=utf-8")
}

fun HttpServerResponse.endWithHtml(chunk:String)
        = this.html().end(chunk)

fun HttpServerResponse.json() : HttpServerResponse
        = this.putHeader("content-type","application/json; charset=utf-8")

fun HttpServerResponse.endWithJson(chunk:Any)
        = this.json().end(mapper.writeValueAsString(chunk))

fun HttpServerResponse.text() : HttpServerResponse
        = this.putHeader("content-type","text/plain; charset=utf-8")

fun HttpServerResponse.endWithText(chunk: String)
        = this.text().end(chunk)
