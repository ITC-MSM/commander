package com.wingedsheep.gameserver.scenario

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.sdk.core.Keyword
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

/**
 * The three properties that keep the custom-card sandbox inside Assay's "never a card loader" rule:
 * it is off unless dev endpoints are on, a compiled card lives in a session-scoped overlay and never
 * in the live registry, and a card Assay cannot read whole is refused with the line that stopped it.
 */
class AssayCardServiceTest : StringSpec({

    val sentinel = """
        {
          "name": "Argentum Sentinel",
          "mana_cost": "{2}{W}",
          "type_line": "Creature — Bird Soldier",
          "oracle_text": "Flying, vigilance",
          "power": "2",
          "toughness": "3"
        }
    """.trimIndent()

    fun request(vararg cards: String) = ScenarioRequest(customCards = cards.toList())

    "a compiled card lands in an overlay, leaving the live registry untouched" {
        val live = CardRegistry()
        val resolution = AssayCardService(live, enabled = true).resolve(request(sentinel))

        resolution.errors.shouldBeEmpty()
        resolution.registry shouldNotBe live
        resolution.registry.getCard("Argentum Sentinel").shouldNotBeNull()
            .keywords shouldBe setOf(Keyword.FLYING, Keyword.VIGILANCE)
        // The sandbox card is invisible to every other game on this server.
        live.getCard("Argentum Sentinel") shouldBe null
    }

    "a scenario with no custom cards is handed the live registry unchanged" {
        val live = CardRegistry()
        val resolution = AssayCardService(live, enabled = true).resolve(ScenarioRequest())

        resolution.registry shouldBe live
        resolution.cards.shouldBeEmpty()
    }

    "custom cards are refused when dev endpoints are off" {
        val resolution = AssayCardService(CardRegistry(), enabled = false).resolve(request(sentinel))

        resolution.errors.single() shouldContain "dev endpoints"
        resolution.cards.shouldBeEmpty()
    }

    "a card Assay cannot read whole is refused, naming the line that stopped it" {
        val unreadable = sentinel.replace(
            "\"oracle_text\": \"Flying, vigilance\"",
            "\"oracle_text\": \"Flying\\nWhenever this creature attacks, flip a coin.\"",
        )
        val resolution = AssayCardService(CardRegistry(), enabled = true).resolve(request(unreadable))

        resolution.cards.shouldBeEmpty()
        resolution.errors.single() shouldContain "flip a coin"
    }

    "inspect reports a verdict per printed line, and the compiled card when it compiles" {
        val response = AssayCardService(CardRegistry(), enabled = true).inspect(sentinel)

        response.compiled shouldBe true
        response.cardName shouldBe "Argentum Sentinel"
        response.lines.single().verdict shouldBe "ROUND_TRIP"
        response.definition.shouldNotBeNull()
    }

    "inspect points at the token a decline died on rather than failing the request" {
        val response = AssayCardService(CardRegistry(), enabled = true).inspect(
            sentinel.replace("Flying, vigilance", "Whenever this creature attacks, flip a coin.")
        )

        response.compiled shouldBe false
        response.definition shouldBe null
        response.lines.single().explanation.shouldNotBeNull() shouldContain "^"
        response.declines.single().kind shouldBe "LINE_DECLINED"
    }
})
