package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.Outcome
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.leg.cards.AlabarasCarpet
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Al-abara's Carpet (LEG) — "{5}, {T}: Prevent all damage that would be dealt to you this turn by
 * attacking creatures without flying."
 *
 * Only the controller is protected: an attacker without flying that is blocked still deals its
 * damage to the blocker, and a flying attacker still hits the controller.
 */
class AlabarasCarpetScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingLife = 20)
        return d
    }

    test("prevents damage to its controller from attacking creatures without flying, and nothing else") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val giant = d.putCreatureOnBattlefield(p1, "Hill Giant")
        val angel = d.putCreatureOnBattlefield(p1, "Serra Angel")
        listOf(bears, giant, angel).forEach { d.removeSummoningSickness(it) }
        val carpet = d.putPermanentOnBattlefield(p2, "Al-abara's Carpet")
        val wall = d.putCreatureOnBattlefield(p2, "Wall of Wood")

        // p2 activates the Carpet during p1's main phase.
        d.passPriority(p1)
        d.giveColorlessMana(p2, 5)
        d.submit(
            ActivateAbility(
                playerId = p2,
                sourceId = carpet,
                abilityId = AlabarasCarpet.script.activatedAbilities[0].id
            )
        ).outcome shouldBe Outcome.Done
        d.bothPass()

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(bears, giant, angel), p2)
        d.bothPass()
        d.declareBlockers(p2, mapOf(wall to listOf(giant)))
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // Bears' 2 to p2 is prevented; Serra Angel flies, so its 4 is dealt.
        d.assertLifeTotal(p2, 16)
        // The Hill Giant's 3 to the blocking Wall of Wood (0/3) is not prevented — only "you" is.
        d.getGraveyardCardNames(p2) shouldContain "Wall of Wood"
    }
})
