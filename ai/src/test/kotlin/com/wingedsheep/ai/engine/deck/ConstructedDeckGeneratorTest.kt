package com.wingedsheep.ai.engine.deck

import com.wingedsheep.engine.limited.BoosterGenerator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.sdk.core.DeckFormat
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.model.ScryfallMetadata
import io.kotest.assertions.withClue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.FunSpec

/**
 * The AI seat's constructed build.
 *
 * The bug this closes: a quick lobby's deck-format restriction only ever applied to the *human's*
 * deck. A Pauper lobby validated your list down to commons and then sat you across from a sealed
 * pool full of rares. These tests pin the two properties that fixes it — the deck is built to the
 * constructed shape, and every card in it is legal in the requested format — plus the explicit
 * refusal to fake a Commander deck.
 */
class ConstructedDeckGeneratorTest : FunSpec({

    fun card(
        name: String,
        cost: String,
        rarity: Rarity,
        formats: Set<DeckFormat>,
    ): CardDefinition = CardDefinition.creature(
        name = name,
        manaCost = ManaCost.parse(cost),
        subtypes = emptySet(),
        power = 2,
        toughness = 2,
        metadata = ScryfallMetadata(collectorNumber = name.hashCode().toString(), rarity = rarity),
    ).copy(legalFormats = formats)

    /**
     * A mono-green pool spread across the curve, with a rarity split that matters: commons are
     * Pauper-legal, rares are not. Everything is Modern-legal, so "Modern" means "the whole pool".
     */
    fun pool(prefix: String): List<CardDefinition> =
        (1..12).flatMap { i ->
            val cmc = (i % 5) + 1
            listOf(
                card("$prefix Common $i", "{${cmc - 1}}{G}", Rarity.COMMON, setOf(DeckFormat.PAUPER, DeckFormat.MODERN)),
                card("$prefix Rare $i", "{${cmc - 1}}{G}", Rarity.RARE, setOf(DeckFormat.MODERN)),
            )
        }

    fun basics(): List<CardDefinition> = listOf(
        CardDefinition.basicLand("Forest", Subtype.FOREST, ScryfallMetadata(collectorNumber = "300")),
    )

    fun generatorOver(vararg sets: Pair<String, List<CardDefinition>>): ConstructedDeckGenerator {
        val configs = sets.associate { (code, cards) ->
            code to BoosterGenerator.SetConfig(
                setCode = code,
                setName = "Set $code",
                cards = cards,
                basicLands = basics(),
            )
        }
        val registry = CardRegistry()
        sets.forEach { (_, cards) -> registry.register(cards) }
        registry.register(basics())
        return ConstructedDeckGenerator(BoosterGenerator(configs), registry)
    }

    test("builds a 60-card deck from the chosen sets") {
        val gen = generatorOver("AAA" to pool("AAA"))

        val deck = gen.generate(listOf("AAA"), DeckFormat.MODERN)

        deck.values.sum() shouldBe RandomDeckGenerator.DECK_SIZE
    }

    test("only uses cards legal in the requested format") {
        // The whole point: a Pauper build must not reach for the rares sharing the set.
        val gen = generatorOver("AAA" to pool("AAA"))

        val deck = gen.generate(listOf("AAA"), DeckFormat.PAUPER)

        val nonBasics = deck.keys.filterNot { it.startsWith("Forest") }
        nonBasics.shouldNotBeEmpty()
        nonBasics.forEach { name ->
            withClue("$name is not Pauper-legal but was put in a Pauper deck") {
                name.contains("Common") shouldBe true
            }
        }
    }

    test("scopes the pool to the chosen sets") {
        val gen = generatorOver("AAA" to pool("AAA"), "BBB" to pool("BBB"))

        val deck = gen.generate(listOf("BBB"), DeckFormat.MODERN)

        deck.keys.filterNot { it.startsWith("Forest") }.forEach { it.startsWith("BBB") shouldBe true }
    }

    test("an empty set list draws on the whole registered card base") {
        val gen = generatorOver("AAA" to pool("AAA"), "BBB" to pool("BBB"))

        val deck = gen.generate(emptyList(), DeckFormat.MODERN)

        deck.values.sum() shouldBe RandomDeckGenerator.DECK_SIZE
    }

    test("resolves a set's reprint rows through the registry") {
        // A set whose own `cards` are empty but which reprints AAA's pool: without the printings
        // lookup its legal pool would look empty and the build would fail.
        val aaa = pool("AAA")
        val registry = CardRegistry()
        registry.register(aaa)
        registry.register(basics())
        val gen = ConstructedDeckGenerator(
            BoosterGenerator(
                mapOf(
                    "AAA" to BoosterGenerator.SetConfig("AAA", "Set AAA", aaa, basics()),
                    "RPT" to BoosterGenerator.SetConfig(
                        setCode = "RPT",
                        setName = "Reprint Set",
                        cards = emptyList(),
                        basicLands = basics(),
                        printings = aaa.mapIndexed { i, c ->
                            Printing(
                                oracleId = "oracle-$i",
                                name = c.name,
                                setCode = "RPT",
                                collectorNumber = i.toString(),
                            )
                        },
                    ),
                )
            ),
            registry,
        )

        gen.generate(listOf("RPT"), DeckFormat.MODERN).values.sum() shouldBe RandomDeckGenerator.DECK_SIZE
    }

    test("refuses commander-shape formats rather than faking one") {
        // Commander needs a designated commander, singleton construction and a colour-identity
        // constraint — none of which this builder models. AiDeckResolver relies on the throw to
        // fall back to a limited deck instead of seating an illegal 60-card pile.
        val gen = generatorOver("AAA" to pool("AAA"))

        listOf(DeckFormat.COMMANDER, DeckFormat.BRAWL, DeckFormat.STANDARD_BRAWL).forEach { format ->
            shouldThrow<IllegalArgumentException> { gen.generate(listOf("AAA"), format) }
        }
    }

    test("refuses a format with no legal cards rather than building an illegal deck") {
        // Nothing in the pool is Standard-legal. Failing here is what lets the resolver fall back.
        val gen = generatorOver("AAA" to pool("AAA"))

        shouldThrow<IllegalArgumentException> { gen.generate(listOf("AAA"), DeckFormat.STANDARD) }
    }

    test("rejects an unknown set code") {
        val gen = generatorOver("AAA" to pool("AAA"))

        shouldThrow<IllegalArgumentException> { gen.generate(listOf("NOPE"), DeckFormat.MODERN) }
    }

    test("keeps a playable land count") {
        val gen = generatorOver("AAA" to pool("AAA"))

        val deck = gen.generate(listOf("AAA"), DeckFormat.MODERN)

        val lands = deck.entries.filter { it.key.startsWith("Forest") }.sumOf { it.value }
        lands shouldBe RandomDeckGenerator.LAND_COUNT
    }

    test("never exceeds the four-copy limit") {
        val gen = generatorOver("AAA" to pool("AAA"))

        val deck = gen.generate(listOf("AAA"), DeckFormat.MODERN)

        deck.filterKeys { !it.startsWith("Forest") }.values.forEach {
            (it <= RandomDeckGenerator.MAX_COPIES) shouldBe true
        }
    }
})
