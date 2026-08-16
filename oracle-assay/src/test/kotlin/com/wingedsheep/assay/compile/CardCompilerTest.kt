package com.wingedsheep.assay.compile

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CharacteristicValue
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * The compiler's contract, stated as the two things a sandbox card has to be: **whole** — never a
 * card missing an ability it printed — and **playable** — a `CardDefinition` the engine can index,
 * dispatch and validate like any other.
 *
 * Written against pasted JSON rather than corpus cards on purpose: that is the input the Scenario
 * Builder sends, it is the only input a custom card can have, and it needs no network.
 */
class CardCompilerTest : StringSpec({

    fun json(
        name: String,
        text: String,
        typeLine: String = "Creature — Bird Soldier",
        manaCost: String = "{2}{W}",
        power: String? = "2",
        toughness: String? = "3",
        extra: String = "",
    ) = """
        {
          "name": "$name",
          "mana_cost": "$manaCost",
          "type_line": "$typeLine",
          "oracle_text": "${text.replace("\n", "\\n")}",
          ${power?.let { "\"power\": \"$it\"," } ?: ""}
          ${toughness?.let { "\"toughness\": \"$it\"," } ?: ""}
          $extra
          "layout": "normal"
        }
    """.trimIndent()

    fun compiled(source: String): CompileResult.Compiled {
        val result = CardCompiler.compile(source)
        if (result is CompileResult.Declined) {
            error("expected a compile, got declines: ${result.declines}")
        }
        return result as CompileResult.Compiled
    }

    fun declined(source: String): CompileResult.Declined =
        CardCompiler.compile(source) as? CompileResult.Declined
            ?: error("expected a decline, got a card")

    "a custom card with no Scryfall entry compiles from its pasted header and text" {
        val card = compiled(json("Argentum Sentinel", "Flying, vigilance")).definition

        card.name shouldBe "Argentum Sentinel"
        card.manaCost.toString() shouldBe "{2}{W}"
        card.typeLine.toString() shouldBe "Creature — Bird Soldier"
        card.creatureStats.shouldNotBeNull().let {
            it.power.toString() shouldContain "2"
            it.toughness.toString() shouldContain "3"
        }
    }

    "a parameterless keyword lands in both places the SDK spells it" {
        val card = compiled(json("Argentum Sentinel", "Flying, vigilance")).definition

        // `keywords` is the derivation `CardBuilder.build` does and half the engine reads.
        card.keywords shouldBe setOf(Keyword.FLYING, Keyword.VIGILANCE)
        card.keywordAbilities.map { it.keyword } shouldContainExactly listOf(Keyword.FLYING, Keyword.VIGILANCE)
    }

    "every ability gets its own id, so activation cannot dispatch to the wrong one" {
        val card = compiled(
            json(
                "Twin Trigger",
                "When this creature enters, draw a card.\n" +
                    "When this creature dies, you gain 2 life.\n" +
                    "{T}: Add {G}.",
                typeLine = "Creature — Elf Druid",
            )
        ).definition

        val ids = card.script.triggeredAbilities.map { it.id } + card.script.activatedAbilities.map { it.id }
        ids.size shouldBe 3
        ids.distinct().size shouldBe 3
    }

    "a land with no mana cost is not marked uncastable" {
        val card = compiled(
            json("Custom Wastes", "{T}: Add {C}.", typeLine = "Land", manaCost = "", power = null, toughness = null)
        ).definition

        card.hasNoManaCost shouldBe false
        card.script.activatedAbilities.single().isManaAbility shouldBe true
    }

    "one unreadable line declines the whole card rather than dropping the ability" {
        val result = declined(
            json("Half Read", "Flying\nWhenever this creature becomes the target of a spell, flip a coin.")
        )

        result.declines.map { it.kind } shouldContainExactly listOf(DeclineKind.LINE_DECLINED)
        result.declines.single().line shouldContain "flip a coin"
        // The reading is still returned — the point of a decline is to say what was missing.
        result.assay.shouldNotBeNull().lines.size shouldBe 2
    }

    /**
     * The `*` in a stat box and the line that defines it are two halves of one value (CR 208.2), and
     * the compiler is the only place they meet — the grammar's unit is a line and the star is in the
     * header. So the pairing is fail-closed in both directions: a star with nothing to define it is
     * still a decline, and a defining line over a printed number is one too.
     */
    "a characteristic-defining power declines when no line defines it" {
        val result = declined(json("Star Bear", "Flying", power = "*", toughness = "3"))

        result.declines.map { it.kind } shouldContainExactly listOf(DeclineKind.HEADER)
        result.declines.single().detail shouldContain "*"
    }

    "a characteristic-defining line fills the star it was printed for" {
        val card = compiled(
            json(
                "Nightmare Horse",
                "Flying\nNightmare Horse's power and toughness are each equal to the number of Swamps you control.",
                power = "*",
                toughness = "*",
            )
        ).definition

        val swamps = DynamicAmount.AggregateBattlefield(
            Player.You,
            GameObjectFilter.Land.withSubtype("Swamp"),
        )
        card.creatureStats.shouldNotBeNull().power shouldBe CharacteristicValue.Dynamic(swamps)
        card.creatureStats.shouldNotBeNull().toughness shouldBe CharacteristicValue.Dynamic(swamps)
        // The line contributes the stat box and nothing else — it is not an ability.
        card.script.staticAbilities.shouldBeEmpty()
    }

    "a defined half pairs with a printed half, and the arithmetic of the star is checked" {
        val text = "Goyf's power is equal to the number of creature cards in all graveyards " +
            "and its toughness is equal to that number plus 1."
        val card = compiled(json("Goyf", text, power = "*", toughness = "1+*")).definition
        val types = DynamicAmount.Count(Player.Each, Zone.GRAVEYARD, GameObjectFilter.Creature)

        card.creatureStats.shouldNotBeNull().toughness shouldBe
            CharacteristicValue.DynamicWithOffset(types, 1)

        // The same text over a plain `*` toughness is a card whose box and text disagree.
        declined(json("Goyf", text, power = "*", toughness = "*"))
            .declines.map { it.kind } shouldContainExactly listOf(DeclineKind.HEADER)
    }

    "a defining line over a printed number declines rather than overriding the box" {
        val result = declined(
            json(
                "Confused Bear",
                "Confused Bear's power and toughness are each equal to the number of Swamps you control.",
                power = "2",
                toughness = "2",
            )
        )

        result.declines.map { it.kind } shouldContainExactly listOf(DeclineKind.HEADER)
    }

    "a multi-faced card declines rather than guessing which slot the back face fills" {
        val result = CardCompiler.compile(
            """
            {
              "name": "Front // Back",
              "layout": "transform",
              "type_line": "Creature — Human // Creature — Werewolf",
              "card_faces": [
                {"name": "Front", "mana_cost": "{1}{R}", "type_line": "Creature — Human",
                 "oracle_text": "Flying", "power": "2", "toughness": "2"},
                {"name": "Back", "mana_cost": "", "type_line": "Creature — Werewolf",
                 "oracle_text": "Trample", "power": "3", "toughness": "3"}
              ]
            }
            """.trimIndent()
        ) as CompileResult.Declined

        result.declines.map { it.kind } shouldContainExactly listOf(DeclineKind.MULTI_FACE)
    }

    /**
     * Spinal Parasite and the Un-sets print negative power. `CreatureStats` refuses a negative base
     * and refuses it by *throwing*, so before this was caught the compiler crashed on the card —
     * which the corpus bake found the first time anything handed it all 35,000 cards, and which the
     * Scenario Builder would have turned into a 500 on a paste. A card the SDK cannot represent is
     * the same product as a line the grammar cannot read: a named decline.
     */
    "a negative printed power declines instead of throwing out of the compiler" {
        val result = declined(json("Sub Zero", "Flying", power = "-1", toughness = "3"))

        result.declines.map { it.kind } shouldContainExactly listOf(DeclineKind.HEADER)
        result.declines.single().detail shouldContain "-1"
    }

    "text that is not a card object is a named decline, not an exception" {
        val result = declined("""{"not": "a card"}""")

        result.declines.map { it.kind } shouldContainExactly listOf(DeclineKind.UNREADABLE_JSON)
        result.assay shouldBe null
    }
})
