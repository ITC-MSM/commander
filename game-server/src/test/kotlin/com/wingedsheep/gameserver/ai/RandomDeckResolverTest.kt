package com.wingedsheep.gameserver.ai

import com.wingedsheep.ai.engine.SealedDeckGenerator
import com.wingedsheep.ai.engine.deck.ConstructedDeckGenerator
import com.wingedsheep.ai.engine.deck.RandomDeckGenerator
import com.wingedsheep.engine.limited.BoosterGenerator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gameserver.lobby.AiDeckSpec
import com.wingedsheep.sdk.core.DeckFormat
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.model.ScryfallMetadata
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * The policy matrix behind "what does a seat with no submitted deck play" — what the seat asked for
 * crossed with what the lobby's format allows.
 *
 * The behaviour under test is the one the feature exists for: a lobby's deck-format restriction used
 * to apply only to a *submitted* deck, so a Pauper or Standard lobby seated a 40-card sealed pool
 * opposite a validated constructed deck. The AI seat was fixed first; a human on "Random" kept the
 * old behaviour until it was routed through this same resolver, which is what the `randomDeck` cases
 * at the bottom pin. Each case covers one cell of the matrix, including the two deliberate fallbacks
 * (commander-shape formats, and a legal pool too thin to build from).
 */
class RandomDeckResolverTest : FunSpec({

    fun card(name: String, cost: String, rarity: Rarity, formats: Set<DeckFormat>) =
        CardDefinition.creature(
            name = name,
            manaCost = ManaCost.parse(cost),
            subtypes = emptySet(),
            power = 2,
            toughness = 2,
            metadata = ScryfallMetadata(collectorNumber = name.hashCode().toString(), rarity = rarity),
        ).copy(legalFormats = formats)

    /**
     * Deep enough to open 8 boosters without exhausting a rarity, and past the 200-card standalone
     * threshold `randomSetCode()` enforces. Commons are Pauper-legal; rares are Modern-only, so a
     * Pauper build has to leave them behind.
     */
    fun pool(prefix: String): List<CardDefinition> =
        (1..200).map {
            card("$prefix Common $it", "{${it % 5}}{G}", Rarity.COMMON, setOf(DeckFormat.PAUPER, DeckFormat.MODERN))
        } + (1..40).map {
            card("$prefix Uncommon $it", "{${it % 5}}{G}", Rarity.UNCOMMON, setOf(DeckFormat.PAUPER, DeckFormat.MODERN))
        } + (1..20).map {
            card("$prefix Rare $it", "{${it % 5}}{G}", Rarity.RARE, setOf(DeckFormat.MODERN))
        }

    val basics = listOf(
        CardDefinition.basicLand("Forest", Subtype.FOREST, ScryfallMetadata(collectorNumber = "300")),
    )

    fun resolver(): RandomDeckResolver {
        val configs = listOf("AAA", "BBB").associateWith { code ->
            BoosterGenerator.SetConfig(
                setCode = code,
                setName = "Set $code",
                cards = pool(code),
                basicLands = basics,
            )
        }
        val booster = BoosterGenerator(configs)
        val registry = CardRegistry()
        configs.values.forEach { registry.register(it.cards) }
        registry.register(basics)
        return RandomDeckResolver(SealedDeckGenerator(booster), ConstructedDeckGenerator(booster, registry))
    }

    val sealedSize = 40
    val constructedSize = RandomDeckGenerator.DECK_SIZE

    test("Auto with no format opens a sealed pool from the lobby's set") {
        val deck = resolver().resolve(AiDeckSpec.Auto, format = null, fallbackSetCode = "AAA")

        deck.values.sum() shouldBe sealedSize
    }

    test("Auto under a constructed format builds to that format instead") {
        val deck = resolver().resolve(AiDeckSpec.Auto, format = DeckFormat.MODERN, fallbackSetCode = "AAA")

        deck.values.sum() shouldBe constructedSize
    }

    test("Auto under Pauper uses only Pauper-legal cards") {
        // The regression this feature closes: the AI's deck must respect the same restriction the
        // human's deck was validated against.
        val deck = resolver().resolve(AiDeckSpec.Auto, format = DeckFormat.PAUPER, fallbackSetCode = "AAA")

        deck.keys.filterNot { it.startsWith("Forest") }.forEach { it.contains("Rare") shouldBe false }
    }

    test("Sets pins the sealed pool to the chosen sets") {
        val deck = resolver().resolve(AiDeckSpec.Sets(listOf("BBB")), format = null, fallbackSetCode = "AAA")

        deck.values.sum() shouldBe sealedSize
        deck.keys.filterNot { it.startsWith("Forest") }.forEach { it.startsWith("BBB") shouldBe true }
    }

    test("Sets under a constructed format builds to the format from those sets") {
        val deck = resolver()
            .resolve(AiDeckSpec.Sets(listOf("BBB")), format = DeckFormat.MODERN, fallbackSetCode = "AAA")

        deck.values.sum() shouldBe constructedSize
        deck.keys.filterNot { it.startsWith("Forest") }.forEach { it.startsWith("BBB") shouldBe true }
    }

    test("an empty set selection falls back to Auto rather than failing the game start") {
        val deck = resolver().resolve(AiDeckSpec.Sets(emptyList()), format = null, fallbackSetCode = "AAA")

        deck.values.sum() shouldBe sealedSize
    }

    test("Fixed plays the submitted list verbatim, whatever the format") {
        val list = mapOf("Anything" to 4, "Forest" to 56)

        resolver().resolve(AiDeckSpec.Fixed(list), format = null, fallbackSetCode = "AAA") shouldBe list
        resolver().resolve(AiDeckSpec.Fixed(list), DeckFormat.PAUPER, "AAA") shouldBe list
        resolver().resolve(AiDeckSpec.Fixed(list), DeckFormat.COMMANDER, "AAA") shouldBe list
    }

    test("a commander-shape format falls back to a limited deck instead of an illegal 60") {
        // Known gap: the builders can't construct a commander deck (singleton, 100 cards, a
        // commander in the command zone). Falling back beats seating something illegal.
        val deck = resolver().resolve(AiDeckSpec.Auto, format = DeckFormat.COMMANDER, fallbackSetCode = "AAA")

        deck.values.sum() shouldBe sealedSize
    }

    test("a format with no legal cards falls back to a limited deck") {
        // Nothing in the synthetic pool is Standard-legal, so the constructed build throws and the
        // resolver has to recover rather than propagate.
        val deck = resolver().resolve(AiDeckSpec.Auto, format = DeckFormat.STANDARD, fallbackSetCode = "AAA")

        deck.values.sum() shouldBe sealedSize
    }

    test("successive Auto resolutions are not identical decks") {
        // Each game start re-rolls; a cached deck would make every AI game the same match.
        val resolver = resolver()
        val decks = (1..8).map { resolver.resolve(AiDeckSpec.Auto, null, "AAA") }

        decks.distinct().size shouldNotBe 1
    }

    test("a human Random seat with no format opens a sealed pool from their set") {
        val deck = resolver().randomDeck(format = null, setCodes = emptyList(), fallbackSetCode = "BBB")

        deck.values.sum() shouldBe sealedSize
        deck.keys.filterNot { it.startsWith("Forest") }.forEach { it.startsWith("BBB") shouldBe true }
    }

    test("a human Random seat under a constructed format builds to that format") {
        // The asymmetry this closes: the AI seat honoured the lobby format while a human on Random
        // always got a 40-card sealed pool, so a Pauper lobby could seat a rare-filled sealed deck
        // opposite a validated 60-card Pauper deck.
        val deck = resolver().randomDeck(DeckFormat.MODERN, setCodes = emptyList(), fallbackSetCode = "AAA")

        deck.values.sum() shouldBe constructedSize
    }

    test("a human Random seat under Pauper uses only Pauper-legal cards") {
        val deck = resolver().randomDeck(DeckFormat.PAUPER, setCodes = emptyList(), fallbackSetCode = "AAA")

        deck.values.sum() shouldBe constructedSize
        deck.keys.filterNot { it.startsWith("Forest") }.forEach { it.contains("Rare") shouldBe false }
    }

    test("a human Random seat in a commander lobby still falls back to a limited deck") {
        val deck = resolver().randomDeck(DeckFormat.COMMANDER, setCodes = emptyList(), fallbackSetCode = "AAA")

        deck.values.sum() shouldBe sealedSize
    }
})
