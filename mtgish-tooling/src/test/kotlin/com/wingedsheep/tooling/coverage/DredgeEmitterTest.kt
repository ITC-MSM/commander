package com.wingedsheep.tooling.coverage

import com.wingedsheep.tooling.coverage.emitter.Emitter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class DredgeEmitterTest : StringSpec({
    val effects = Registry.loadEffectSerialNames()
    val keywords = Registry.loadKeywords()

    fun render(number: String) = Emitter.renderCard(Json.parseToJsonElement("""
        {"Name":"Test Dredger","Typeline":{"Supertypes":[],"Cardtypes":["Creature"],"Subtypes":["Plant"]},
         "ManaCost":[{"_ManaSymbol":"ManaCostG"}],"CardPT":{"Power":1,"Toughness":1},
         "Rules":[{"_Rule":"Dredge","args":$number}]}
    """).jsonObject, null, effects, keywords)

    "dredge renders its numeric ability rather than a bare keyword" {
        for (amount in listOf(0, 1, 3, 6)) {
            val result = render("""{"_GameNumber":"Integer","args":$amount}""")
            result.complete shouldBe true
            result.text shouldContain "keywordAbility(KeywordAbility.dredge($amount))"
            result.text shouldNotContain "keywords(Keyword.DREDGE)"
        }
    }
    "missing or negative dredge amounts decline to scaffolds" {
        for (number in listOf("null", """{"_GameNumber":"Integer","args":-1}""")) {
            val result = render(number)
            result.complete shouldBe false
            result.text shouldNotContain "KeywordAbility.dredge("
        }
    }
})
