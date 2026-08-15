package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.SummoningSicknessComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ons.cards.BlatantThievery
import com.wingedsheep.mtg.sets.definitions.ons.cards.CustodyBattle
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Verifies CR 302.6: a creature can't attack (or use tap/untap activated abilities)
 * unless it has been under its controller's control continuously since their most
 * recent turn began. Any change of control breaks that chain, so the engine must
 * stamp a new SummoningSicknessComponent onto the permanent on every transfer.
 *
 * This covers all five control-change executors:
 *   - GainControlExecutor (Blatant Thievery)
 *   - GainControlByActivePlayerExecutor       (covered indirectly — same fix shape)
 *   - GainControlByMostExecutor               (covered indirectly — same fix shape)
 *   - GiveControlToTargetPlayerExecutor (Custody Battle suffer branch)
 *   - ExchangeControlExecutor (Exchange Ritual — minimal sorcery wrapper)
 */
class ControlChangeSummoningSicknessTest : FunSpec({

    val projector = StateProjector()

    val TargetCreature = CardDefinition.creature(
        name = "Target Creature",
        manaCost = ManaCost.parse("{1}{R}"),
        subtypes = setOf(Subtype("Warrior")),
        power = 2,
        toughness = 2
    )

    // Minimal {2}{U} sorcery wrapping ExchangeControlEffect so the executor is
    // exercised through the normal cast pipeline without needing a face-down /
    // morph fixture for Chromeshell Crab.
    val ExchangeRitual = card("Exchange Ritual") {
        manaCost = "{2}{U}"
        typeLine = "Sorcery"
        oracleText = "Exchange control of target creature you control and target creature an opponent controls."
        spell {
            val mine = target("yours", Targets.CreatureYouControl)
            val theirs = target("theirs", Targets.CreatureOpponentControls)
            effect = Effects.ExchangeControl(mine, theirs)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(BlatantThievery, CustodyBattle, TargetCreature, ExchangeRitual)
        )
        return driver
    }

    fun advanceToPlayerUpkeep(driver: GameTestDriver, targetPlayer: EntityId) {
        driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        if (driver.activePlayer == targetPlayer) {
            driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        }
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.currentStep shouldBe Step.UPKEEP
        driver.activePlayer shouldBe targetPlayer
    }

    context("GainControlExecutor (Blatant Thievery)") {

        test("stolen creature has summoning sickness on the steal turn") {
            val driver = createDriver()
            driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
            val activePlayer = driver.activePlayer!!
            val opponent = driver.getOpponent(activePlayer)
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val theirCreature = driver.putCreatureOnBattlefield(opponent, "Target Creature")
            driver.removeSummoningSickness(theirCreature)

            val thievery = driver.putCardInHand(activePlayer, "Blatant Thievery")
            driver.giveMana(activePlayer, Color.BLUE, 7)
            driver.castSpell(activePlayer, thievery, listOf(theirCreature))
            driver.bothPass()

            withClue("Active player should now control the stolen creature") {
                projector.project(driver.state).getController(theirCreature) shouldBe activePlayer
            }
            withClue("Stolen creature must have summoning sickness (CR 302.6)") {
                driver.state.getEntity(theirCreature)?.has<SummoningSicknessComponent>() shouldBe true
            }
        }

        test("stolen creature cannot attack the same turn it was stolen") {
            val driver = createDriver()
            driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
            val activePlayer = driver.activePlayer!!
            val opponent = driver.getOpponent(activePlayer)
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            // Give the active player a non-sick creature so DECLARE_ATTACKERS isn't
            // auto-skipped on this turn (the engine skips it when there are no valid
            // attackers, which would otherwise land us on the *next* untap step where
            // sickness has legitimately worn off).
            val ownAttacker = driver.putCreatureOnBattlefield(activePlayer, "Target Creature")
            driver.removeSummoningSickness(ownAttacker)

            val theirCreature = driver.putCreatureOnBattlefield(opponent, "Target Creature")
            driver.removeSummoningSickness(theirCreature)

            val thievery = driver.putCardInHand(activePlayer, "Blatant Thievery")
            driver.giveMana(activePlayer, Color.BLUE, 7)
            driver.castSpell(activePlayer, thievery, listOf(theirCreature))
            driver.bothPass()

            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
            withClue("Should still be on the active player's first turn at declare attackers") {
                driver.currentStep shouldBe Step.DECLARE_ATTACKERS
                driver.state.priorityPlayerId shouldBe activePlayer
                driver.state.getEntity(theirCreature)?.has<SummoningSicknessComponent>() shouldBe true
            }

            val result = driver.declareAttackers(activePlayer, listOf(theirCreature), opponent)
            withClue("Same-turn attack with stolen creature must fail (CR 508.1a / 302.6)") {
                result.error shouldNotBe null
            }
        }

        test("stolen creature can attack on the new controller's next turn") {
            val driver = createDriver()
            driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
            val activePlayer = driver.activePlayer!!
            val opponent = driver.getOpponent(activePlayer)
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val theirCreature = driver.putCreatureOnBattlefield(opponent, "Target Creature")
            driver.removeSummoningSickness(theirCreature)

            val thievery = driver.putCardInHand(activePlayer, "Blatant Thievery")
            driver.giveMana(activePlayer, Color.BLUE, 7)
            driver.castSpell(activePlayer, thievery, listOf(theirCreature))
            driver.bothPass()

            // Active player's next untap step should clear summoning sickness.
            advanceToPlayerUpkeep(driver, activePlayer)

            withClue("Untap step should clear summoning sickness for the new controller") {
                driver.state.getEntity(theirCreature)?.has<SummoningSicknessComponent>() shouldBe false
            }

            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
            val result = driver.declareAttackers(activePlayer, listOf(theirCreature), opponent)
            withClue("After untap step the stolen creature can attack: ${result.error}") {
                result.error shouldBe null
            }
        }
    }

    context("GiveControlToTargetPlayerExecutor (Custody Battle)") {

        test("creature transferred via Custody Battle's suffer branch has summoning sickness") {
            val driver = createDriver()
            driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
            val activePlayer = driver.activePlayer!!
            val opponent = driver.getOpponent(activePlayer)
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val creature = driver.putCreatureOnBattlefield(activePlayer, "Target Creature")
            driver.removeSummoningSickness(creature)

            val custodyBattle = driver.putCardInHand(activePlayer, "Custody Battle")
            driver.giveMana(activePlayer, Color.RED, 2)
            driver.castSpell(activePlayer, custodyBattle, listOf(creature))
            driver.bothPass()

            // Active player has no lands → suffer branch resolves automatically (CustodyBattleTest pattern)
            advanceToPlayerUpkeep(driver, activePlayer)
            driver.stackSize shouldBe 1
            driver.bothPass()

            withClue("Opponent should now control the creature") {
                projector.project(driver.state).getController(creature) shouldBe opponent
            }
            withClue("Transferred creature must have summoning sickness (CR 302.6)") {
                driver.state.getEntity(creature)?.has<SummoningSicknessComponent>() shouldBe true
            }
        }
    }

    context("ExchangeControlExecutor (sorcery wrapper)") {

        test("both creatures swapped by Exchange Control have summoning sickness") {
            val driver = createDriver()
            driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
            val activePlayer = driver.activePlayer!!
            val opponent = driver.getOpponent(activePlayer)
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val mine = driver.putCreatureOnBattlefield(activePlayer, "Target Creature")
            val theirs = driver.putCreatureOnBattlefield(opponent, "Target Creature")
            driver.removeSummoningSickness(mine)
            driver.removeSummoningSickness(theirs)

            val ritual = driver.putCardInHand(activePlayer, "Exchange Ritual")
            driver.giveMana(activePlayer, Color.BLUE, 3)
            driver.castSpell(activePlayer, ritual, listOf(mine, theirs))
            driver.bothPass()

            val projected = projector.project(driver.state)
            withClue("Active player gains control of opponent's creature") {
                projected.getController(theirs) shouldBe activePlayer
            }
            withClue("Opponent gains control of active player's creature") {
                projected.getController(mine) shouldBe opponent
            }
            withClue("Creature now controlled by active player must have summoning sickness (CR 302.6)") {
                driver.state.getEntity(theirs)?.has<SummoningSicknessComponent>() shouldBe true
            }
            withClue("Creature now controlled by opponent must have summoning sickness (CR 302.6)") {
                driver.state.getEntity(mine)?.has<SummoningSicknessComponent>() shouldBe true
            }
        }
    }
})
