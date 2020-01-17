package com.github.stephenott.stix.interop.steps

import com.github.stephenott.stix.interop.glue.*
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.Admin
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.PostServeAction
import com.github.tomakehurst.wiremock.stubbing.ServeEvent
import org.apache.http.HttpStatus
import java.util.*
import java.util.concurrent.CompletableFuture

class EndpointCallbackSteps(val world: World) : Scenario(world) {

    init {

        When("wait for submission of object at {string} {string} as {string}") { method: String, path: String, submissionId: String ->
            world.wireMockServer.start() // Refactor this into hooks
            configureFor("localhost", world.wireMockServer.port());
            println("Wiremock on localhost:${world.wireMockServer.port()}")

            val myCompleter = CompletableFuture<Unit>()
            val myCompleterId = CompleterId(UUID.randomUUID().toString())

            Completers.addCompleter(myCompleterId, myCompleter)

            val mapping = stubFor(post(urlEqualTo(path))
                    .willReturn(aResponse().withStatus(202))
                    .withPostServeAction("notifyOfReceipt", myCompleterId)
            )

            println("waiting for submission at $method $path")

            myCompleter.join()
            verify(postRequestedFor(urlEqualTo(path)))

            findAll(postRequestedFor(urlEqualTo(path))).single().let {
                println("submission with id: $submissionId has body of: ${it.bodyAsString}")
                world.submissions[submissionId] = it.bodyAsString
            }

            println("ALL DONE")
            Completers.clearCompleters() //Refactor into hooks
            world.wireMockServer.stop() // Refactor this into hooks
        }

        When("wait for take of object at {string} {string}") { method: String, path: String ->
            //@TODO create method and url param types for expression

            world.wireMockServer.start()
            configureFor("localhost", world.wireMockServer.port());
            println("Wiremock on localhost:${world.wireMockServer.port()}")

            val myCompleter = CompletableFuture<Unit>()
            val myCompleterId = CompleterId(UUID.randomUUID().toString())

            Completers.addCompleter(myCompleterId, myCompleter)

            val mapping = stubFor(get(urlEqualTo(path))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withJsonBody(world.parsedObject)
                            .withHeader("Content-Type", "application/json"))
                    .withPostServeAction("notifyOfReceipt", myCompleterId)
            )

            println("waiting for take at $method $path")

            myCompleter.join()

            verify(getRequestedFor(urlEqualTo(path)))

            println("ALL DONE")
            Completers.clearCompleters() //Refactor into hooks
            world.wireMockServer.stop() // Refactor this into hooks

        }

        Then("parse {string} as an {string}"){ submissionId: String, stixType: String ->
            val parsedStix = world.mapper.readTree(world.submissions[submissionId])
            world.parsedStix.put(submissionId, parsedStix)
        }

        And("{string} must have a different {string} property") { submissionId: String, propertyName: String ->
            val original = world.parsedObject!!
            val updated = world.parsedStix[submissionId]!!

            assert(updated[propertyName] != original[propertyName], lazyMessage = {"Submitted object must have a updated $propertyName property value"})
        }
    }
}