package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.JoinTheDead
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Join the Dead (LCI #110): {1}{B}{B} Instant
 *
 * "Target creature gets -5/-5 until end of turn.
 *  Descend 4 — That creature gets -10/-10 until end of turn instead if there are four or more
 *  permanent cards in your graveyard."
 *
 * Covered:
 *  - With fewer than four permanent cards in the graveyard: applies -5/-5 (base). A 7/7
 *    creature survives at 2/2.
 *  - With four or more permanent cards in the graveyard: applies -10/-10 instead (Descend 4).
 *    The same 7/7 creature dies (projected -3/-3 triggers lethal SBA).
 *
 * The distinction proves the ConditionalEffect selects the correct branch: the two outcomes
 * are mutually exclusive and produce clearly different projected stats.
 */
class JoinTheDeadScenarioTest : FunSpec({

    // A large vanilla 7/7 creature: survives -5/-5 (→ 2/2) but dies to -10/-10 (→ -3/-3).
    val BigVanillaCreature = CardDefinition.creature(
        name = "Big Vanilla Creature",
        manaCost = ManaCost.parse("{6}"),
        subtypes = setOf(Subtype("Beast")),
        power = 7,
        toughness = 7
    )

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(JoinTheDead, BigVanillaCreature))
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
            startingPlayer = 0
        )
        return driver
    }

    test("applies -5/-5 when fewer than four permanent cards are in the graveyard") {
        val driver = createDriver()
        val caster = driver.player1
        val opponent = driver.getOpponent(caster)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe caster

        // Place the target 7/7 creature on the opponent's battlefield.
        val target = driver.putCreatureOnBattlefield(opponent, "Big Vanilla Creature")

        // Seed caster's graveyard with only three permanent cards — one short of Descend 4.
        repeat(3) { driver.putCardInGraveyard(caster, "Grizzly Bears") }

        // Cast Join the Dead ({1}{B}{B}).
        val spell = driver.putCardInHand(caster, "Join the Dead")
        driver.giveColorlessMana(caster, 1)
        driver.giveMana(caster, Color.BLACK, 2)
        driver.castSpell(caster, spell, targets = listOf(target))
        driver.bothPass() // spell resolves

        // Condition (4+ permanents in GY) is false → base -5/-5 branch fires.
        // 7/7 − 5/5 = 2/2 projected; creature survives.
        projector.getProjectedPower(driver.state, target) shouldBe 2
        projector.getProjectedToughness(driver.state, target) shouldBe 2
        driver.findPermanent(opponent, "Big Vanilla Creature") shouldBe target
    }

    test("applies -10/-10 instead when four or more permanent cards are in the graveyard") {
        val driver = createDriver()
        val caster = driver.player1
        val opponent = driver.getOpponent(caster)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe caster

        // Place the target 7/7 creature on the opponent's battlefield.
        val target = driver.putCreatureOnBattlefield(opponent, "Big Vanilla Creature")

        // Seed caster's graveyard with exactly four permanent cards — meets Descend 4 threshold.
        repeat(4) { driver.putCardInGraveyard(caster, "Grizzly Bears") }

        // Cast Join the Dead ({1}{B}{B}).
        val spell = driver.putCardInHand(caster, "Join the Dead")
        driver.giveColorlessMana(caster, 1)
        driver.giveMana(caster, Color.BLACK, 2)
        driver.castSpell(caster, spell, targets = listOf(target))
        driver.bothPass() // spell resolves → -10/-10 applied → 7/7 becomes -3/-3 → SBA destroys it

        // Descend 4 condition is true → -10/-10 branch fires.
        // 7/7 − 10/10 = -3/-3: state-based actions move it to the graveyard during resolution.
        driver.findPermanent(opponent, "Big Vanilla Creature") shouldBe null
        driver.getGraveyard(opponent) shouldContain target
    }
})
