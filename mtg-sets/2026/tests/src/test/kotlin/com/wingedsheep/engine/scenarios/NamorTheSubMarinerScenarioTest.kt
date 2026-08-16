package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.msh.cards.NamorTheSubMariner
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Namor the Sub-Mariner (MSH #69) — {1}{U}{U} Legendary Creature — Mutant Merfolk Villain, `*`/4.
 *
 *  - Flying
 *  - Namor's power is equal to the number of Merfolk you control.
 *  - Whenever you cast a noncreature spell with one or more blue mana symbols in its mana cost,
 *    create that many 1/1 blue Merfolk creature tokens.
 *
 * The trigger is the behavioural gate on the two new primitives:
 * `CardPredicate.ColoredManaSymbolsAtLeast` (the filter) and
 * `EntityNumericProperty.ColoredManaSymbolCount` (the count). Both count hybrid and Phyrexian pips
 * as their colour(s) per CR 107.4e/f; the symbol-by-symbol rule is pinned in
 * `ManaCostColoredSymbolCountTest`, the engine wiring in `ColoredManaSymbolCountTest`, and this
 * file proves the card actually mints the right number of Merfolk.
 *
 * The two feed each other: every Merfolk token minted also raises Namor's power, so the power
 * assertions double as evidence the tokens really are Merfolk.
 */
class NamorTheSubMarinerScenarioTest : FunSpec({

    val projector = StateProjector()

    /** Noncreature spells that differ only in their printed mana cost. */
    fun instant(name: String, cost: String) = CardDefinition(
        name = name,
        manaCost = ManaCost.parse(cost),
        typeLine = TypeLine.instant()
    )

    val OneBluePip = instant("Namor Test One Blue", "{U}")
    val TwoBluePips = instant("Namor Test Two Blue", "{1}{U}{U}")
    val ThreeBluePips = instant("Namor Test Three Blue", "{U}{U}{U}")
    val NoBluePip = instant("Namor Test No Blue", "{2}")
    val RedOnly = instant("Namor Test Red", "{1}{R}")
    val HybridBlueRed = instant("Namor Test Hybrid", "{U/R}")
    val TwobridBlue = instant("Namor Test Twobrid", "{2/U}")
    val PhyrexianBlue = instant("Namor Test Phyrexian", "{U/P}")
    val XAndBlue = instant("Namor Test X Blue", "{X}{U}")

    // A *creature* spell with two blue pips — the "noncreature" half of the filter.
    val BlueCreatureSpell = CardDefinition.creature(
        name = "Namor Test Blue Creature",
        manaCost = ManaCost.parse("{U}{U}"),
        subtypes = emptySet(),
        power = 1, toughness = 1
    )

    // A plain non-Merfolk creature, so the power count can be shown to exclude it.
    val PlainBeast = CardDefinition.creature(
        name = "Namor Test Beast",
        manaCost = ManaCost.parse("{2}{G}"),
        subtypes = setOf(Subtype("Beast")),
        power = 2, toughness = 2
    )

    // A non-token Merfolk, so the power count can be shown to include cards as well as tokens.
    val PlainMerfolk = CardDefinition.creature(
        name = "Namor Test Merfolk",
        manaCost = ManaCost.parse("{1}{U}"),
        subtypes = setOf(Subtype.MERFOLK),
        power = 1, toughness = 1
    )

    // A Kindred *noncreature* permanent carrying the Merfolk subtype — the reason Namor's power
    // filter is `Any.withSubtype(MERFOLK)` and not `Creature.withSubtype(...)`: the card says
    // "the number of Merfolk you control", not "Merfolk creatures", and a Kindred enchantment's
    // subtypes *are* creature types (CR 308.2, whose own example is "Kindred Enchantment —
    // Merfolk").
    val MerfolkBanner = CardDefinition(
        name = "Namor Test Merfolk Banner",
        manaCost = ManaCost.parse("{2}"),
        typeLine = TypeLine(
            cardTypes = setOf(CardType.KINDRED, CardType.ENCHANTMENT),
            subtypes = setOf(Subtype.MERFOLK)
        )
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(
                NamorTheSubMariner,
                OneBluePip, TwoBluePips, ThreeBluePips, NoBluePip, RedOnly,
                HybridBlueRed, TwobridBlue, PhyrexianBlue, XAndBlue,
                BlueCreatureSpell, PlainBeast, PlainMerfolk, MerfolkBanner
            )
        )
        return driver
    }

    /** Resolve the stack to completion (no player decisions are expected). */
    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (!isPaused && stackSize > 0 && guard++ < 50) bothPass()
    }

    fun GameTestDriver.merfolkTokens(player: EntityId): Int =
        getPermanents(player).count { getCardName(it) == "Merfolk Token" }

    fun GameTestDriver.namorPower(player: EntityId): Int? {
        val namor = getPermanents(player).first { getCardName(it) == "Namor the Sub-Mariner" }
        return projector.project(state).getPower(namor)
    }

    /**
     * Put Namor out, cast [spellName], and report how many Merfolk tokens exist afterwards.
     * Mana is handed over generously so payment never becomes the variable under test.
     */
    fun castWithNamor(spellName: String): Pair<Int, Int?> {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(player, "Namor the Sub-Mariner")
        driver.merfolkTokens(player) shouldBe 0

        val spell = driver.putCardInHand(player, spellName)
        driver.giveMana(player, Color.BLUE, 6)
        driver.giveMana(player, Color.RED, 3)
        driver.giveMana(player, Color.GREEN, 3)
        driver.castSpell(player, spell)
        driver.resolveStack()
        return driver.merfolkTokens(player) to driver.namorPower(player)
    }

    test("one blue pip mints one Merfolk token") {
        val (tokens, power) = castWithNamor("Namor Test One Blue")
        tokens shouldBe 1
        withClue("Namor counts himself plus the new token") { power shouldBe 2 }
    }

    test("two blue pips mint two tokens, three mint three") {
        castWithNamor("Namor Test Two Blue").first shouldBe 2
        castWithNamor("Namor Test Three Blue").first shouldBe 3
    }

    test("a noncreature spell with no blue mana symbol mints nothing") {
        val (generic, genericPower) = castWithNamor("Namor Test No Blue")
        generic shouldBe 0
        withClue("Namor alone is a 1/4") { genericPower shouldBe 1 }
        castWithNamor("Namor Test Red").first shouldBe 0
    }

    test("the trigger itself does not go on the stack for a spell with no blue pip") {
        // Distinct from "mints nothing": a fired trigger with a count of 0 and a trigger that
        // never fired both leave zero tokens behind, so the token count alone cannot tell the
        // filter apart from the amount. Observe the stack directly, before resolution.
        fun stackAfterCasting(spellName: String): Int {
            val driver = createDriver()
            driver.initMirrorMatch(deck = Deck.of("Island" to 40))
            val player = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
            driver.putPermanentOnBattlefield(player, "Namor the Sub-Mariner")
            val spell = driver.putCardInHand(player, spellName)
            driver.giveMana(player, Color.BLUE, 6)
            driver.giveMana(player, Color.RED, 3)
            driver.castSpell(player, spell)
            return driver.stackSize
        }

        withClue("{U} — the spell plus Namor's trigger") {
            stackAfterCasting("Namor Test One Blue") shouldBe 2
        }
        withClue("{2} — no blue mana symbol, so only the spell is on the stack") {
            stackAfterCasting("Namor Test No Blue") shouldBe 1
        }
        withClue("{1}{R} — no blue mana symbol, so only the spell is on the stack") {
            stackAfterCasting("Namor Test Red") shouldBe 1
        }
    }

    test("a hybrid {U/R} is one blue mana symbol (CR 107.4e)") {
        castWithNamor("Namor Test Hybrid").first shouldBe 1
    }

    test("a monocolored hybrid {2/U} is one blue mana symbol (CR 107.4e)") {
        castWithNamor("Namor Test Twobrid").first shouldBe 1
    }

    test("a Phyrexian {U/P} is one blue mana symbol (CR 107.4f)") {
        castWithNamor("Namor Test Phyrexian").first shouldBe 1
    }

    test("{X} contributes nothing — {X}{U} mints exactly one token") {
        // "In its mana cost" reads the symbols printed on the card (CR 202.1/202.1a), and {X} is
        // a generic symbol (CR 107.4b), so the announced value of X never changes the pip count.
        castWithNamor("Namor Test X Blue").first shouldBe 1
    }

    test("a counterspell in response does not undo the tokens") {
        // The trigger fires on *cast* (CR 601.2i) and is independent of the spell it triggered
        // off: countering that spell does nothing to the trigger already on the stack, and the
        // trigger still reads the pip count off the (now graveyard-bound) spell object. Without
        // that, the token count would silently collapse to 0 — which is exactly the shape a
        // last-known-information refactor could introduce.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(player, "Namor the Sub-Mariner")

        val spell = driver.putCardInHand(player, "Namor Test Two Blue")
        driver.giveMana(player, Color.BLUE, 6)
        driver.castSpell(player, spell)
        withClue("the {1}{U}{U} instant plus Namor's trigger") { driver.stackSize shouldBe 2 }
        val spellOnStack = driver.state.stack.first { driver.getCardName(it) == "Namor Test Two Blue" }

        driver.passPriority(player)
        driver.giveMana(opponent, Color.BLUE, 2)
        val counter = driver.putCardInHand(opponent, "Counterspell")
        driver.submit(
            CastSpell(
                playerId = opponent,
                cardId = counter,
                targets = listOf(ChosenTarget.Spell(spellOnStack)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        driver.resolveStack()

        withClue("the instant really was countered") {
            driver.getGraveyardCardNames(player) shouldContain "Namor Test Two Blue"
        }
        withClue("the trigger resolved anyway and still saw two blue pips") {
            driver.merfolkTokens(player) shouldBe 2
        }
    }

    test("a creature spell with blue pips does not trigger the noncreature filter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(player, "Namor the Sub-Mariner")

        val creature = driver.putCardInHand(player, "Namor Test Blue Creature")
        driver.giveMana(player, Color.BLUE, 6)
        driver.castSpell(player, creature)
        driver.resolveStack()

        withClue("{U}{U} on a creature spell is filtered out by 'noncreature'") {
            driver.merfolkTokens(player) shouldBe 0
        }
    }

    test("Namor's power is the number of Merfolk he controls, himself included") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Namor the Sub-Mariner")
        withClue("alone, Namor is a 1/4 — he is himself a Merfolk") {
            driver.namorPower(player) shouldBe 1
        }

        driver.putCreatureOnBattlefield(player, "Namor Test Beast")
        withClue("a Beast is not a Merfolk") { driver.namorPower(player) shouldBe 1 }

        driver.putCreatureOnBattlefield(player, "Namor Test Merfolk")
        driver.putCreatureOnBattlefield(player, "Namor Test Merfolk")
        withClue("two more Merfolk cards → 3") { driver.namorPower(player) shouldBe 3 }
    }

    test("a noncreature Merfolk permanent counts toward Namor's power") {
        // Pins the deliberate `Any.withSubtype(MERFOLK)` filter: "Merfolk you control" is not
        // "Merfolk creatures you control", so a Kindred Enchantment — Merfolk counts (CR 308.2).
        // With `Creature.withSubtype(...)` instead, Namor would stay a 1/4 here.
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Namor the Sub-Mariner")
        driver.namorPower(player) shouldBe 1

        driver.putPermanentOnBattlefield(player, "Namor Test Merfolk Banner")
        withClue("a Kindred Enchantment — Merfolk is a Merfolk you control") {
            driver.namorPower(player) shouldBe 2
        }
    }

    test("Namor has flying") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val namor = driver.putPermanentOnBattlefield(player, "Namor the Sub-Mariner")

        projector.project(driver.state).hasKeyword(namor, Keyword.FLYING) shouldBe true
    }

    test("an opponent's Merfolk does not raise Namor's power") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Namor the Sub-Mariner")
        driver.putCreatureOnBattlefield(opponent, "Namor Test Merfolk")
        driver.putCreatureOnBattlefield(opponent, "Namor Test Merfolk")

        withClue("'Merfolk you control' excludes the opponent's") {
            driver.namorPower(player) shouldBe 1
        }
    }

    test("an opponent casting a blue noncreature spell does not trigger Namor") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(player, "Namor the Sub-Mariner")
        val spell = driver.putCardInHand(opponent, "Namor Test Two Blue")
        driver.giveMana(opponent, Color.BLUE, 6)
        driver.castSpell(opponent, spell)
        driver.resolveStack()

        withClue("the trigger is 'whenever YOU cast'") {
            driver.merfolkTokens(player) shouldBe 0
            driver.merfolkTokens(opponent) shouldBe 0
        }
    }

    test("the minted tokens are 1/1 blue Merfolk") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(player, "Namor the Sub-Mariner")

        val spell = driver.putCardInHand(player, "Namor Test One Blue")
        driver.giveMana(player, Color.BLUE, 6)
        driver.castSpell(player, spell)
        driver.resolveStack()

        val token = driver.getPermanents(player).first { driver.getCardName(it) == "Merfolk Token" }
        val projected = projector.project(driver.state)
        projected.getPower(token) shouldBe 1
        projected.getToughness(token) shouldBe 1
        projected.getColors(token) shouldBe setOf(Color.BLUE.name)
        withClue("the token has the Merfolk subtype") {
            projected.getSubtypes(token).any { it.equals("Merfolk", ignoreCase = true) } shouldBe true
        }
    }
})
