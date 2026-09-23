package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Gallia, Tragic Host (FRA #228) — "{4}{B}, Exile another creature card from your graveyard:
 * Return this card from your graveyard to the battlefield tapped with a +1/+1 counter on her."
 *
 * Pins `CostAtom.ExileFrom.excludeSelf`: Gallia is herself a creature card in that graveyard, so
 * without the exclusion she could pay her own cost.
 */
class GalliaTragicHostScenarioTest : ScenarioTestBase() {
    init {
        fun game(withOtherCreatureCard: Boolean) = scenario().withPlayers()
            .withCardInGraveyard(1, "Gallia, Tragic Host")
            .apply { if (withOtherCreatureCard) withCardInGraveyard(1, "Grizzly Bears") }
            .withLandsOnBattlefield(1, "Swamp", 5)
            .withCardInLibrary(1, "Swamp")
            .withCardInLibrary(2, "Swamp")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        val abilityId = cardRegistry.getCard("Gallia, Tragic Host")!!.script.activatedAbilities.single().id

        test("Gallia can't exile herself to pay her own cost") {
            val game = game(withOtherCreatureCard = false)
            val gallia = game.findCardsInGraveyard(1, "Gallia, Tragic Host").single()

            withClue("the ability isn't offered with no other creature card in the graveyard") {
                game.getLegalActions(1).none { (it.action as? ActivateAbility)?.sourceId == gallia } shouldBe true
            }
            game.execute(ActivateAbility(game.player1Id, gallia, abilityId)).error shouldNotBe null
            game.isInGraveyard(1, "Gallia, Tragic Host") shouldBe true
        }

        test("exiling another creature card returns her tapped with a +1/+1 counter") {
            val game = game(withOtherCreatureCard = true)
            val gallia = game.findCardsInGraveyard(1, "Gallia, Tragic Host").single()

            game.getLegalActions(1).any { (it.action as? ActivateAbility)?.sourceId == gallia } shouldBe true
            game.execute(ActivateAbility(game.player1Id, gallia, abilityId)).error shouldBe null
            game.isInExile(1, "Grizzly Bears") shouldBe true
            game.resolveStack()

            val onField = game.findPermanent("Gallia, Tragic Host")
            onField shouldNotBe null
            game.state.getEntity(onField!!)!!.has<TappedComponent>() shouldBe true
            game.state.getEntity(onField)!!.get<CountersComponent>()!!
                .getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
        }
    }
}
