package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome
import io.kotest.matchers.shouldNotBe

/**
 * Covers the `CardPredicate.IsNonartifact` predicate end-to-end on the battlefield, via a
 * Terror-style spell: "Destroy target nonartifact, nonblack creature. It can't be regenerated."
 * (`TargetFilter.Creature.nonartifact().notColor(Color.BLACK)` + `Effects.Destroy(noRegenerate = true)`).
 *
 * The predicate is the new vocabulary; the test pins that target enumeration / validation treats an
 * artifact creature (and a black creature) as illegal targets while a plain creature is legal — i.e.
 * `IsNonartifact` is evaluated against projected battlefield state, not silently passed through.
 */
class NonartifactTargetFilterTest : FunSpec({

    // Inline "Terror"-shaped instant exercising the nonartifact filter (mirrors the mtgish AUTOGEN draft).
    val terrorLike = card("Nonartifact Slayer") {
        manaCost = "{1}{B}"
        typeLine = "Instant"
        spell {
            val t = target(
                "target",
                TargetCreature(filter = TargetFilter.Creature.nonartifact().notColor(Color.BLACK))
            )
            effect = Effects.Destroy(t, noRegenerate = true)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(terrorLike)
        return driver
    }

    fun setup(): GameTestDriver {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("can destroy a nonartifact, nonblack creature — and it can't be regenerated") {
        val driver = setup()
        val player = driver.activePlayer!!

        val centaur = driver.putCreatureOnBattlefield(player, "Centaur Courser") // green vanilla creature
        val spell = driver.putCardInHand(player, "Nonartifact Slayer")
        driver.giveMana(player, Color.BLACK, 2) // pays {1}{B}

        val result = driver.castSpellWithTargets(player, spell, listOf(ChosenTarget.Permanent(centaur)))
        result.outcome shouldBe Outcome.Done
        driver.bothPass()

        driver.findPermanent(player, "Centaur Courser") shouldBe null
        driver.getGraveyardCardNames(player).contains("Centaur Courser") shouldBe true
    }

    test("cannot target an artifact creature — IsNonartifact excludes it") {
        val driver = setup()
        val player = driver.activePlayer!!

        val golem = driver.putCreatureOnBattlefield(player, "Artifact Creature") // artifact creature
        val spell = driver.putCardInHand(player, "Nonartifact Slayer")
        driver.giveMana(player, Color.BLACK, 2) // pays {1}{B}

        val result = driver.castSpellWithTargets(player, spell, listOf(ChosenTarget.Permanent(golem)))
        result.outcome shouldNotBe Outcome.Done
        // The artifact creature survives the illegal cast attempt.
        driver.findPermanent(player, "Artifact Creature") shouldBe golem
    }

    test("cannot target a black creature — the notColor leg still holds alongside nonartifact") {
        val driver = setup()
        val player = driver.activePlayer!!

        val zombie = driver.putCreatureOnBattlefield(player, "Black Creature")
        val spell = driver.putCardInHand(player, "Nonartifact Slayer")
        driver.giveMana(player, Color.BLACK, 2) // pays {1}{B}

        val result = driver.castSpellWithTargets(player, spell, listOf(ChosenTarget.Permanent(zombie)))
        result.outcome shouldNotBe Outcome.Done
        driver.findPermanent(player, "Black Creature") shouldBe zombie
    }
})
