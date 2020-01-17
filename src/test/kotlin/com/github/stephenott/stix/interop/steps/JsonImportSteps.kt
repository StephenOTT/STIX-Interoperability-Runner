package com.github.stephenott.stix.interop.steps

import com.github.stephenott.stix.interop.glue.Scenario
import com.github.stephenott.stix.interop.glue.World

class JsonImportSteps(val world: World) : Scenario(world) {

    init {
        Given("a STIX JSON object at {string}") { filename: String ->
            world.desiredFilename = filename
        }

        When("I parse it as STIX") {
            val jsonFile = javaClass.getResource(world.desiredFilename)
            world.parsedObject = world.mapper.readTree(jsonFile)

        }

        Then("it must be STIX type {string}") { stixType: String ->
            assert(world.parsedObject!!.has("type"))
            assert(world.parsedObject!!["type"].asText() == stixType,
                    lazyMessage = {"could not find type property with value $stixType"})
        }

        Then("it must have property {string} as type {string}") {propertyName: String, propType: String ->
            val type = JsonTypes.valueOf(propType)
            assert (world.parsedObject!!.has(propertyName), lazyMessage = {"Missing property Name $propertyName"})

            val prop = world.parsedObject!![propertyName]

            val typeErrorMessage = "incorrect property type, was expecting $propType but found ${prop.nodeType.name}"

            when (type){
                JsonTypes.NUMBER -> assert(prop.isNumber, lazyMessage = {typeErrorMessage})
                JsonTypes.STRING -> assert(prop.isTextual, lazyMessage = {typeErrorMessage})
                JsonTypes.ARRAY -> assert(prop.isArray, lazyMessage = {typeErrorMessage})
                JsonTypes.OBJECT -> assert(prop.isObject, lazyMessage = {typeErrorMessage})
                JsonTypes.BOOLEAN -> assert(prop.isBoolean, lazyMessage = {typeErrorMessage})
            }
        }

        Then("print the object") {
            println(world.parsedObject.toString())
        }

        Then("print parsed object {string}") { parsedObjectId: String ->
            println(world.mapper.writeValueAsString(world.parsedStix[parsedObjectId]))
        }
    }

}

enum class JsonTypes{
    NUMBER, STRING, ARRAY, OBJECT, BOOLEAN
}