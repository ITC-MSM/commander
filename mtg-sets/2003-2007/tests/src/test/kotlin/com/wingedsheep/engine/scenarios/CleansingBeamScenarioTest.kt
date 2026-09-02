package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.CleansingBeam
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Cleansing Beam (RAV #118) — "Radiance — Cleansing Beam deals 2 damage to target creature and
 * each other creature that shares a color with it."
 *
 * The radiance group is a resolution-time battlefield filter relative to the spell's own target
 * (`sharingColorWith(EntityReference.Target(0))`), which is what these tests pin down: a
 * multicolor target radiates to every creature sharing *any* of its colors, on both sides of
 * the table; a colorless target radiates to nothing (ruling 2005-10-01); and the target itself
 * is damaged exactly once.
 */
class CleansingBeamScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + CleansingBeam)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.damageOn(id: EntityId): Int = state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    fun GameTestDriver.beam(caster: EntityId, target: EntityId) {
        giveMana(caster, Color.RED, 5)
        val beam = putCardInHand(caster, "Cleansing Beam")
        castSpellWithTargets(caster, beam, listOf(ChosenTarget.Permanent(target))).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    test("a black-green target radiates 2 damage to every other black or green creature, on both sides") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val trampler = d.putCreatureOnBattlefield(opp, "Deathtouch Trampler")  // {1}{B}{G} 3/3
        d.putCreatureOnBattlefield(opp, "Black Creature")                        // {1}{B} 2/2 — dies
        val courser = d.putCreatureOnBattlefield(me, "Centaur Courser")          // {2}{G} 3/3 — mine, still hit
        val lions = d.putCreatureOnBattlefield(opp, "Savannah Lions")            // {W} 2/1 — spared
        val guide = d.putCreatureOnBattlefield(me, "Goblin Guide")               // {R} — spared
        val golem = d.putCreatureOnBattlefield(opp, "Artifact Creature")         // colorless — spared

        d.beam(me, trampler)

        withClue("the target took exactly 2 — not 2 from the target clause plus 2 from the group") {
            d.damageOn(trampler) shouldBe 2
        }
        withClue("Black Creature shares black and died to the 2 damage") {
            d.findPermanent(opp, "Black Creature").shouldBeNull()
            d.getGraveyardCardNames(opp) shouldBe listOf("Black Creature")
        }
        withClue("my own Centaur Courser shares green and was hit too") {
            d.damageOn(courser) shouldBe 2
        }
        withClue("white, red and colorless creatures share no color with the target and were spared") {
            d.damageOn(lions) shouldBe 0
            d.damageOn(guide) shouldBe 0
            d.damageOn(golem) shouldBe 0
            d.findPermanent(opp, "Savannah Lions").shouldNotBeNull()
        }
    }

    test("a colorless target shares a color with nothing, so only it is damaged") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val golem = d.putCreatureOnBattlefield(opp, "Artifact Creature")         // 2/2 — dies
        val myr = d.putCreatureOnBattlefield(opp, "Palladium Myr")               // another colorless 2/2
        val courser = d.putCreatureOnBattlefield(opp, "Centaur Courser")

        d.beam(me, golem)

        withClue("the colorless target itself still took the 2 damage") {
            d.findPermanent(opp, "Artifact Creature").shouldBeNull()
            d.getGraveyardCardNames(opp) shouldBe listOf("Artifact Creature")
        }
        withClue("colorless creatures don't share a color even with each other") {
            d.findPermanent(opp, "Palladium Myr").shouldNotBeNull()
            d.damageOn(myr) shouldBe 0
        }
        d.damageOn(courser) shouldBe 0
    }
})
