package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.TrappedInTheScreen
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Trapped in the Screen (DSK #36) — {2}{W} Enchantment.
 *
 * "Ward {2}
 *  When this enchantment enters, exile target artifact, creature, or enchantment an opponent
 *  controls until this enchantment leaves the battlefield."
 *
 * An Oblivion Ring variant restricted to "artifact, creature, or enchantment an opponent controls".
 * Exercises the linked-exile pattern (exile on ETB, return on leaves) and the new three-type
 * opponent-controlled target filter.
 */
class TrappedInTheScreenScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + TrappedInTheScreen)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isInExile(playerId: EntityId, cardName: String): Boolean =
        state.getExile(playerId).any { state.getEntity(it)?.get<CardComponent>()?.name == cardName }

    fun GameTestDriver.castTrappedTargeting(caster: EntityId, target: EntityId) {
        giveMana(caster, Color.WHITE, 3)
        val card = putCardInHand(caster, "Trapped in the Screen")
        castSpell(caster, card)
        bothPass() // resolve spell → enters → ETB trigger goes on stack
        pendingDecision shouldNotBe null
        submitTargetSelection(caster, listOf(target))
        bothPass() // resolve ETB exile
    }

    test("exiles an opponent's creature on ETB and returns it when it leaves the battlefield") {
        val d = newDriver()
        val p1 = d.player1
        val p2 = d.player2

        val creature = d.putCreatureOnBattlefield(p2, "Glory Seeker")
        d.castTrappedTargeting(caster = p1, target = creature)

        // The creature is exiled and linked to Trapped in the Screen.
        d.findPermanent(p2, "Glory Seeker") shouldBe null
        d.isInExile(p2, "Glory Seeker") shouldBe true
        val trapped = d.findPermanent(p1, "Trapped in the Screen")
        trapped.shouldNotBeNull()
        d.state.getEntity(trapped)?.get<LinkedExileComponent>()?.exiledIds?.shouldContain(creature)

        // Destroy Trapped in the Screen — the exiled creature returns.
        val wipe = d.putCardInHand(p1, "Wipe Clean")
        d.giveMana(p1, Color.WHITE, 2)
        d.castSpellWithTargets(p1, wipe, listOf(ChosenTarget.Permanent(trapped)))
        d.bothPass() // resolve Wipe Clean
        d.bothPass() // resolve LTB return trigger

        d.findPermanent(p2, "Glory Seeker").shouldNotBeNull()
        d.findPermanent(p1, "Trapped in the Screen") shouldBe null
    }

    test("can exile an opponent's noncreature enchantment") {
        val d = newDriver()
        val p1 = d.player1
        val p2 = d.player2

        // A plain enchantment (not a creature) is still a valid target — "artifact, creature, or
        // enchantment", so the enchantment branch of the new filter resolves.
        val enchantment = d.putPermanentOnBattlefield(p2, "Test Enchantment")
        d.castTrappedTargeting(caster = p1, target = enchantment)

        d.isInExile(p2, "Test Enchantment") shouldBe true
        d.findPermanent(p2, "Test Enchantment") shouldBe null
    }
})
