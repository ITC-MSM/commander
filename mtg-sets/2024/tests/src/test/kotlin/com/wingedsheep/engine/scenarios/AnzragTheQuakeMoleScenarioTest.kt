package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.AdditionalPhasesComponent
import com.wingedsheep.engine.state.components.player.ExtraPhaseKind
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.AnzragTheQuakeMole
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Anzrag, the Quake-Mole (MKM #186) — {2}{R}{G} 8/4 Legendary Creature — Mole God.
 *
 * "Whenever Anzrag becomes blocked, untap each creature you control. After this phase, there is an
 *  additional combat phase.
 *  {3}{R}{R}{G}{G}: Anzrag must be blocked each combat this turn if able."
 *
 * The card is a loop: the activated ability forces a block, the block fires the trigger, the
 * trigger untaps the team and queues another combat — and the block requirement is still standing
 * for that combat. The load-bearing detail is the *duration*: `MustBeBlockedExecutor` installs a
 * [Duration.EndOfTurn] floating effect, not a this-combat one, which is the only reason "each
 * combat this turn" is faithful. A this-combat duration would look identical in a single-combat
 * test and silently break the card's whole engine, so the duration is asserted directly.
 *
 * The extra combat phase is asserted on the active player's [AdditionalPhasesComponent] queue
 * (CR 500.8) rather than by driving a second combat, so the test stays inside one turn.
 */
class AnzragTheQuakeMoleScenarioTest : FunSpec({

    val blocker = card("Anzrag Test Wall") {
        manaCost = "{2}"
        typeLine = "Creature — Wall"
        power = 0
        toughness = 6
    }

    val bystander = card("Anzrag Test Ox") {
        manaCost = "{2}{G}"
        typeLine = "Creature — Ox"
        power = 3
        toughness = 3
    }

    val mustBeBlockedAbilityId = AnzragTheQuakeMole.activatedAbilities.single().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AnzragTheQuakeMole, blocker, bystander))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        return driver
    }

    // Player 1 may not be active at game start (random turn order) — advance until it is.
    fun GameTestDriver.advanceToPlayer1DeclareAttackers() {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
    }

    fun GameTestDriver.isTapped(id: EntityId) =
        state.getEntity(id)?.has<TappedComponent>() == true

    fun GameTestDriver.queuedExtraPhases(playerId: EntityId) =
        state.getEntity(playerId)?.get<AdditionalPhasesComponent>()?.phases.orEmpty()

    test("becoming blocked untaps the team and queues an additional combat phase") {
        val driver = createDriver()
        val anzrag = driver.putCreatureOnBattlefield(driver.player1, "Anzrag, the Quake-Mole")
        val ox = driver.putCreatureOnBattlefield(driver.player1, "Anzrag Test Ox")
        val wall = driver.putCreatureOnBattlefield(driver.player2, "Anzrag Test Wall")
        driver.removeSummoningSickness(anzrag)
        driver.removeSummoningSickness(ox)

        driver.advanceToPlayer1DeclareAttackers()
        driver.tapPermanent(ox) // a creature that stayed home, tapped for mana
        driver.declareAttackers(driver.player1, listOf(anzrag), driver.player2)
        driver.isTapped(anzrag) shouldBe true

        driver.bothPass() // end declare attackers → declare blockers
        driver.declareBlockers(driver.player2, mapOf(wall to listOf(anzrag)))
        driver.bothPass() // the becomes-blocked trigger resolves

        // "untap each creature you control" — including the attacker itself, which stays in combat.
        driver.isTapped(anzrag) shouldBe false
        driver.isTapped(ox) shouldBe false
        // …and the defending player's creature is untouched by "you control".
        driver.state.getEntity(wall)?.has<TappedComponent>() shouldBe false

        val queued = driver.queuedExtraPhases(driver.player1)
        queued.size shouldBe 1
        queued.single().kind shouldBe ExtraPhaseKind.COMBAT
    }

    test("attacking unblocked does not fire the trigger") {
        val driver = createDriver()
        val anzrag = driver.putCreatureOnBattlefield(driver.player1, "Anzrag, the Quake-Mole")
        driver.removeSummoningSickness(anzrag)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(anzrag), driver.player2)
        driver.bothPass()
        driver.declareNoBlockers(driver.player2)
        driver.bothPass()

        driver.isTapped(anzrag) shouldBe true
        driver.queuedExtraPhases(driver.player1) shouldBe emptyList()
    }

    test("the activated ability installs a must-be-blocked requirement that lasts the whole turn") {
        val driver = createDriver()
        val anzrag = driver.putCreatureOnBattlefield(driver.player1, "Anzrag, the Quake-Mole")
        driver.removeSummoningSickness(anzrag)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(driver.player1, Color.RED, 4)
        driver.giveMana(driver.player1, Color.GREEN, 3)

        driver.submit(
            ActivateAbility(playerId = driver.player1, sourceId = anzrag, abilityId = mustBeBlockedAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        val requirement = driver.state.floatingEffects.single {
            it.effect.modification is SerializableModification.MustBeBlockedIfAble
        }
        requirement.effect.affectedEntities shouldBe setOf(anzrag)
        // "each combat this turn" — not "this combat". An end-of-turn duration is what makes the
        // requirement survive into the additional combat phase the trigger queues.
        requirement.duration shouldBe Duration.EndOfTurn
    }
})
