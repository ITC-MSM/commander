package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.SporogenicInfection
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Sporogenic Infection (DSK #117) — {1}{B} Enchantment — Aura.
 *
 * "Enchant creature
 *  When this Aura enters, target player sacrifices a creature of their choice other than enchanted
 *  creature.
 *  When enchanted creature is dealt damage, destroy it."
 *
 * Exercises: an Aura attaching to a creature; an ETB edict that names its own target player and
 * forces a sacrifice restricted to a creature *other than* the enchanted one (the new
 * `notAttachedToBySource` source-relative filter); and a `takesDamage(binding = ATTACHED)` trigger
 * that destroys the enchanted creature on any damage.
 */
class SporogenicInfectionScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + SporogenicInfection)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castSporogenicOn(caster: EntityId, victim: EntityId) {
        giveMana(caster, Color.BLACK, 2)
        val aura = putCardInHand(caster, "Sporogenic Infection")
        castSpellWithTargets(caster, aura, listOf(ChosenTarget.Permanent(victim))).error shouldBe null
        bothPass() // resolve the Aura spell → it enters attached → ETB edict trigger goes on stack
    }

    test("the Aura attaches to the enchanted creature and the ETB edict makes the target player sacrifice") {
        val d = newDriver()
        val p1 = d.player1
        val p2 = d.player2

        // p2 has the to-be-enchanted creature plus a second creature it can sacrifice.
        val enchanted = d.putCreatureOnBattlefield(p2, "Glory Seeker")
        val other = d.putCreatureOnBattlefield(p2, "Centaur Courser")

        d.castSporogenicOn(caster = p1, victim = enchanted)

        // The Aura is attached to the enchanted creature.
        val aura = d.findPermanent(p1, "Sporogenic Infection")
        aura.shouldNotBeNull()
        d.state.getEntity(aura)?.get<AttachedToComponent>()?.targetId shouldBe enchanted

        // ETB edict: choose p2 as the target player; p2 must sacrifice a creature OTHER than the
        // enchanted one — so the only valid choice is the other creature (auto-sacrificed).
        d.submitTargetSelection(p1, listOf(p2))
        var guard = 0
        while (guard++ < 10 && !(d.state.stack.isEmpty() && d.state.pendingDecision == null)) {
            d.bothPass()
        }

        // The enchanted creature survives the edict; the other creature is sacrificed.
        d.findPermanent(p2, "Glory Seeker").shouldNotBeNull()
        d.findPermanent(p2, "Centaur Courser") shouldBe null
    }

    test("enchanted creature is destroyed when dealt any damage") {
        val d = newDriver()
        val p1 = d.player1
        val p2 = d.player2

        val enchanted = d.putCreatureOnBattlefield(p2, "Glory Seeker")

        d.castSporogenicOn(caster = p1, victim = enchanted)
        // Resolve the ETB edict targeting p1 (who has no other creature → no sacrifice).
        d.submitTargetSelection(p1, listOf(p1))
        var guard = 0
        while (guard++ < 10 && !(d.state.stack.isEmpty() && d.state.pendingDecision == null)) {
            d.bothPass()
        }
        d.findPermanent(p2, "Glory Seeker").shouldNotBeNull()

        // Lightning Bolt deals 3 damage to the enchanted 2/2 — the "dealt damage, destroy it"
        // trigger fires and destroys it (independently of lethal damage).
        val bolt = d.putCardInHand(p1, "Lightning Bolt")
        d.giveMana(p1, Color.RED, 1)
        d.castSpellWithTargets(p1, bolt, listOf(ChosenTarget.Permanent(enchanted))).error shouldBe null
        guard = 0
        while (guard++ < 10 && !(d.state.stack.isEmpty() && d.state.pendingDecision == null)) {
            d.bothPass()
        }

        d.findPermanent(p2, "Glory Seeker") shouldBe null
    }

    test("edict cannot force the targeted player to sacrifice the enchanted creature when it is their only creature") {
        val d = newDriver()
        val p1 = d.player1
        val p2 = d.player2

        // p2's ONLY creature is the one being enchanted — "other than enchanted creature" leaves
        // p2 with no valid creature to sacrifice, so nothing is sacrificed.
        val enchanted = d.putCreatureOnBattlefield(p2, "Glory Seeker")

        d.castSporogenicOn(caster = p1, victim = enchanted)
        d.submitTargetSelection(p1, listOf(p2))
        var guard = 0
        while (guard++ < 10 && !(d.state.stack.isEmpty() && d.state.pendingDecision == null)) {
            d.bothPass()
        }

        // The enchanted creature is untouched — the edict found no other creature to take.
        d.findPermanent(p2, "Glory Seeker").shouldNotBeNull()
    }
})
