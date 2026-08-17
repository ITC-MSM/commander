package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.mechanics.mana.ManaSolver
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tla.cards.BadgermoleCub
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.basicLand
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Badgermole Cub ({1}{G}, 2/2 Badger Mole).
 *
 *   When this creature enters, earthbend 1.
 *   Whenever you tap a creature for mana, add an additional {G}.
 *
 * Exercises [com.wingedsheep.sdk.scripting.AdditionalManaOnSourceTap] in its
 * `youControl()` form (the source-filter-based "you tap" gating), including the
 * ManaSolver-side awareness of the bonus and the projected-controller path that
 * makes the "you" perspective track control-changing effects.
 */
class BadgermoleCubTest : FunSpec({

    val TestForest = basicLand("Forest") {}

    val tapForGreenCreature = card("Tap-for-Green Elf") {
        manaCost = "{G}"
        colorIdentity = "G"
        typeLine = "Creature — Elf"
        power = 1
        toughness = 1
        oracleText = "{T}: Add {G}."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.AddMana(Color.GREEN, 1)
            manaAbility = true
        }
    }

    // "{T}: Add one mana of any color" — a mana ability that pauses for a color decision when the
    // activation carries no `manaColorChoice`, which is the path the AI and the gym always take.
    val tapForAnyColorCreature = card("Any-Color Elf") {
        manaCost = "{G}"
        colorIdentity = "G"
        typeLine = "Creature — Elf"
        power = 1
        toughness = 1
        oracleText = "{T}: Add one mana of any color."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.AddAnyColorMana(1)
            manaAbility = true
        }
    }

    val testCards = listOf(TestForest, BadgermoleCub, tapForGreenCreature, tapForAnyColorCreature)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + testCards)
        return driver
    }

    fun createRegistry(): CardRegistry {
        val registry = CardRegistry()
        registry.register(TestCards.all + testCards)
        return registry
    }

    test("ManaSolver counts the Badgermole Cub bonus when costing creature mana") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(activePlayer, "Badgermole Cub")
        val elf = driver.putCreatureOnBattlefield(activePlayer, "Tap-for-Green Elf")
        // Mana sources must be activatable — summoning-sick creatures without haste are skipped.
        driver.removeSummoningSickness(elf)

        val solver = ManaSolver(createRegistry())
        // 1 from Elf + 1 bonus from Cub = 2 green available
        solver.canPay(driver.state, activePlayer, ManaCost.parse("{G}{G}")) shouldBe true
        solver.canPay(driver.state, activePlayer, ManaCost.parse("{G}{G}{G}")) shouldBe false
    }

    test("Bonus fires for an any-color mana ability that pauses for the color choice") {
        // Regression: "{T}: Add one mana of any color" pauses for a color decision when the
        // activation doesn't carry one (the AI never supplies `manaColorChoice`). The handler
        // returns before its inline `AdditionalManaOnSourceTap` pass, so the bonus has to be
        // applied by the color-choice continuation instead — and that path used to handle only
        // the mirror form (color = null), silently dropping the Cub's fixed {G}.
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(activePlayer, "Badgermole Cub")
        val prism = driver.putCreatureOnBattlefield(activePlayer, "Any-Color Elf")
        driver.removeSummoningSickness(prism)

        val manaAbilityId = tapForAnyColorCreature.activatedAbilities[0].id
        // No manaColorChoice — the effect pauses and asks for the color.
        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = prism, abilityId = manaAbilityId)
        )
        withClue("activation error: ${result.error}") { result.error shouldBe null }

        val decision = driver.pendingDecision
        withClue("the any-color ability should have paused for a color choice") {
            decision shouldNotBe null
        }
        driver.submitDecision(activePlayer, ColorChosenResponse(decision!!.id, Color.BLUE))

        val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()!!
        withClue("one chosen blue + one bonus green from the Cub, pool was $pool") {
            pool.blue shouldBe 1
            pool.green shouldBe 1
        }
    }

    test("Bonus does not fire when an opponent controls the Badgermole Cub") {
        // "Whenever you tap a creature for mana, add an additional {G}" — the bonus
        // belongs to whoever controls the Cub, gated by projected controller. If P2
        // controls the Cub but P1 taps their own creature, neither player gets a bonus.
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(opponent, "Badgermole Cub")
        val elf = driver.putCreatureOnBattlefield(activePlayer, "Tap-for-Green Elf")
        // Clear summoning sickness so the Elf can tap the same turn.
        driver.removeSummoningSickness(elf)

        val manaAbilityId = tapForGreenCreature.activatedAbilities[0].id
        val result = driver.submit(
            ActivateAbility(playerId = activePlayer, sourceId = elf, abilityId = manaAbilityId)
        )
        result.isSuccess shouldBe true

        val activePool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()!!
        val opponentPool = driver.state.getEntity(opponent)?.get<ManaPoolComponent>()!!
        activePool.green shouldBe 1   // just the Elf — no Cub bonus
        opponentPool.green shouldBe 0 // opponent controls the Cub but didn't tap anything
    }
})
