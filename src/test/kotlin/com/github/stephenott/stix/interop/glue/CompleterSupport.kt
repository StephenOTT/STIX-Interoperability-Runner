package com.github.stephenott.stix.interop.glue

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.tomakehurst.wiremock.core.Admin
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.PostServeAction
import com.github.tomakehurst.wiremock.stubbing.ServeEvent
import java.util.concurrent.CompletableFuture

class NotifyOfReceipt: PostServeAction() {

    override fun getName(): String {
        return "notifyOfReceipt"
    }

    override fun doAction(serveEvent: ServeEvent, admin: Admin, parameters: Parameters) {
        println("running action...${parameters.`as`(CompleterId::class.java)}")
        val completer = parameters.`as`(CompleterId::class.java)
        println(completer::class)
        Completers.getCompleter(completer).complete(Unit)
        println("all done action")
    }
}

/**
 * Must be a globally unique ID or else risk overwrite from parallel tests...
 */
data class CompleterId(
        @JsonProperty("id") val id: String
){
    //The @JsonProperty annotations are required for the Wiremock ObjectMapper to pick up the fields...
}

/**
 * Holds the list
 */
object Completers{
    private val completers: MutableMap<CompleterId, CompletableFuture<Unit>> = mutableMapOf()

    fun clearCompleters(){
        completers.clear()
    }

    fun addCompleter(id: CompleterId, future: CompletableFuture<Unit>){
        completers[id] = future
    }

    fun getCompleter(completerId: CompleterId): CompletableFuture<Unit> {
        return completers[completerId] ?: throw IllegalArgumentException("Invalid Id!!")
    }
}