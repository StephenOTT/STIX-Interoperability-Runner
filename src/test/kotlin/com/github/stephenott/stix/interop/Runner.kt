package com.github.stephenott.stix.interop

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
        plugin = ["pretty"],
        glue = ["com.github.stephenott.stix.interop.glue",
            "com.github.stephenott.stix.interop.steps"],
        features = ["src/test/resources/features"])
class Runner {
}