package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.StolenUniform
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
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Stolen Uniform.
 *
 * Stolen Uniform ({U}, Instant):
 *   "Choose target creature you control and target Equipment. Gain control of that Equipment until
 *    end of turn. Attach it to the chosen creature. When you lose control of that Equipment this
 *    turn, if it's attached to a creature you control, unattach it."
 *
 * Exercises the new SDK plumbing:
 *  - [com.wingedsheep.sdk.dsl.Effects.UnattachEquipment] / `UnattachEquipmentEffect` + its executor.
 *  - The directional [com.wingedsheep.sdk.scripting.EventPattern.ControlChangeEvent] with
 *    [com.wingedsheep.sdk.scripting.ControlChangeDirection.LOST] used as an entity-scoped delayed
 *    trigger ([com.wingedsheep.sdk.dsl.Triggers.LoseControlOfWatched]) — "when you lose control of
 *    that Equipment this turn".
 *  - The general [com.wingedsheep.sdk.scripting.predicates.StatePredicate.AttachedTo] host filter
 *    ("attached to a creature you control") via `GameObjectFilter.attachedTo(...)`.
 */
class StolenUniformScenarioTest : FunSpec({

    val MyBear = CardDefinition.creature(
        name = "Uniform Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2,
        oracleText = ""
    )

    // A vanilla Equipment the opponent controls; only the subtype matters for targeting.
    val TestEquipment = CardDefinition.equipment(
        name = "Borrowed Greatsword",
        manaCost = ManaCost.parse("{2}"),
        equipCost = ManaCost.parse("{2}")
    )

    // Lets the opponent take control of the Equipment back mid-turn, emitting the control-change
    // event my "when you lose control" delayed trigger watches for.
    val Reclaim = card("Reclaim") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "Gain control of target permanent."
        spell {
            val perm = target("target permanent", Targets.Permanent)
            effect = Effects.GainControl(perm)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(MyBear, TestEquipment, StolenUniform, Reclaim))
        return driver
    }

    val projector = StateProjector()

    fun GameTestDriver.attachmentHost(equipmentId: EntityId): EntityId? =
        state.getEntity(equipmentId)?.get<AttachedToComponent>()?.targetId

    fun GameTestDriver.hostAttachments(hostId: EntityId): List<EntityId> =
        state.getEntity(hostId)?.get<AttachmentsComponent>()?.attachedIds ?: emptyList()

    // Control changes are floating effects; the base ControllerComponent is unchanged, so read the
    // effective controller from projected state.
    fun GameTestDriver.controllerOf(entityId: EntityId): EntityId? =
        projector.project(state).getController(entityId)

    test("gain control of the Equipment until end of turn and force-attach it to your creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val myCreature = driver.putCreatureOnBattlefield(me, "Uniform Bear")
        val equipment = driver.putPermanentOnBattlefield(opponent, "Borrowed Greatsword")

        // Sanity: opponent controls the Equipment, it isn't attached to anything yet.
        driver.controllerOf(equipment) shouldBe opponent
        driver.attachmentHost(equipment) shouldBe null

        val spell = driver.putCardInHand(me, "Stolen Uniform")
        driver.giveMana(me, Color.BLUE, 1)
        val result = driver.castSpell(me, spell, targets = listOf(myCreature, equipment))
        result.isSuccess shouldBe true
        driver.bothPass() // resolve the spell

        // Gained control of the Equipment, and it's force-attached to my creature.
        driver.controllerOf(equipment) shouldBe me
        driver.attachmentHost(equipment) shouldBe myCreature
        driver.hostAttachments(myCreature).contains(equipment) shouldBe true
    }

    test("losing control of the Equipment fires the delayed trigger, which unattaches it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val myCreature = driver.putCreatureOnBattlefield(me, "Uniform Bear")
        val equipment = driver.putPermanentOnBattlefield(opponent, "Borrowed Greatsword")

        val spell = driver.putCardInHand(me, "Stolen Uniform")
        driver.giveMana(me, Color.BLUE, 1)
        driver.castSpell(me, spell, targets = listOf(myCreature, equipment)).isSuccess shouldBe true
        driver.bothPass()

        driver.controllerOf(equipment) shouldBe me
        driver.attachmentHost(equipment) shouldBe myCreature

        // The opponent takes control of the Equipment back this turn — "you lose control of that
        // Equipment" — which fires the delayed trigger. The Equipment is still attached to a creature
        // I control, so the trigger's conditional unattaches it.
        val reclaim = driver.putCardInHand(opponent, "Reclaim")
        driver.giveMana(opponent, Color.BLUE, 1)
        // Hand priority to the opponent so they can cast their instant.
        driver.passPriority(me)
        driver.castSpell(opponent, reclaim, targets = listOf(equipment)).isSuccess shouldBe true
        driver.bothPass() // resolve Reclaim → control moves to opponent, delayed trigger fires
        driver.bothPass() // resolve the delayed "unattach" trigger

        // Opponent now controls the Equipment, and it has been unattached from my creature.
        driver.controllerOf(equipment) shouldBe opponent
        driver.attachmentHost(equipment) shouldBe null
        driver.hostAttachments(myCreature).contains(equipment) shouldBe false
    }
})
