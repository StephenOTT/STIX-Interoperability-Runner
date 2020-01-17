package com.github.stephenott.stix.interop.glue

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options


class World() {

    val mapper: ObjectMapper = jacksonObjectMapper() //@TODO Redo

    var wireMockServer: WireMockServer = WireMockServer(options()
            .port(8081) // Make configurable
            .extensions(NotifyOfReceipt())
    )

    var desiredFilename: String? = null
    var parsedObject: JsonNode? = null

    val submissions: MutableMap<String, String> = mutableMapOf()
    val parsedStix: MutableMap<String, JsonNode> = mutableMapOf()
}