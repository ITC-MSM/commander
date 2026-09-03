package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.WojekEmbermage
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Wojek Embermage (RAV #152) — "Radiance — {T}: This creature deals 1 damage to target creature
 * and each other creature that shares a color with it."
 *
 * The first *activated* radiance ability in the corpus: the previous five were all spells, so
 * these tests pin the group down where the target list belongs to an ability rather than to a
 * spell on the stack. The Embermage is itself red, and "each other creature" is relative to the
 * **target**, not to the source — so a red target radiates back onto the Embermage that fired the
 * ability. That self-hit is the assertion most likely to be lost to a stray `excludeSelf`.
 */
class WojekEmbermageScenarioTest : FunSpec({

    val embermageAbility = WojekEmbermage.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + WojekEmbermage)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.damageOn(id: EntityId): Int = state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    /** An Embermage of player 1's, already past its summoning sickness. */
    fun GameTestDriver.embermage(): EntityId =
        putCreatureOnBattlefield(player1, "Wojek Embermage").also { removeSummoningSickness(it) }

    fun GameTestDriver.fire(source: EntityId, target: EntityId) {
        submit(
            ActivateAbility(player1, source, embermageAbility, targets = listOf(ChosenTarget.Permanent(target)))
        ).isSuccess shouldBe true
        // The activator keeps priority after putting the ability on the stack.
        passPriority(player2)
        bothPass()
    }

    test("a red target radiates 1 damage onto every other red creature, the Embermage included") {
        val d = driver()
        val opp = d.player2
        val firing = d.embermage()
        val bystander = d.embermage()
        val guide = d.putCreatureOnBattlefield(opp, "Goblin Guide")        // {R} 2/1 — the target, dies
        val lions = d.putCreatureOnBattlefield(opp, "Savannah Lions")      // {W} 1/1 — spared
        val golem = d.putCreatureOnBattlefield(opp, "Artifact Creature")   // colorless 2/2 — spared

        d.fire(firing, guide)

        withClue("the target took its 1 damage and a 2/1 died to it") {
            d.findPermanent(opp, "Goblin Guide").shouldBeNull()
            d.getGraveyardCardNames(opp) shouldBe listOf("Goblin Guide")
        }
        withClue("'each other creature' is relative to the target, so the firing Embermage hits itself") {
            d.damageOn(firing) shouldBe 1
            d.damageOn(bystander) shouldBe 1
        }
        withClue("white and colorless creatures share no color with the target") {
            d.damageOn(lions) shouldBe 0
            d.damageOn(golem) shouldBe 0
            d.findPermanent(opp, "Savannah Lions").shouldNotBeNull()
        }
        withClue("the ability's tap cost was paid") {
            d.isTapped(firing) shouldBe true
            d.isTapped(bystander) shouldBe false
        }
    }

    test("a colorless target shares a color with nothing, so only it is damaged") {
        val d = driver()
        val opp = d.player2
        val firing = d.embermage()
        val golem = d.putCreatureOnBattlefield(opp, "Artifact Creature")   // colorless 2/2 — the target
        val myr = d.putCreatureOnBattlefield(opp, "Palladium Myr")         // another colorless 2/2

        d.fire(firing, golem)

        d.damageOn(golem) shouldBe 1
        withClue("colorless creatures don't share a color even with each other") {
            d.damageOn(myr) shouldBe 0
        }
        withClue("the red Embermage shares no color with a colorless target either") {
            d.damageOn(firing) shouldBe 0
        }
    }
})
