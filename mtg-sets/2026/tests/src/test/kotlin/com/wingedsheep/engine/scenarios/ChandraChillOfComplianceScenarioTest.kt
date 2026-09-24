package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Chandra, Chill of Compliance (FRA #212).
 *
 * - +1: Surveil 1. If you put a noncreature, nonland card into your graveyard this way, put that
 *   card into your hand.
 * - +1: Add {U}. Spend this mana only to cast a noncreature spell.
 * - −X: Tap target artifact or creature. Put X stun counters on it.
 * - −6: You get an emblem with "Whenever you cast a spell, draw a card."
 */
class ChandraChillOfComplianceScenarioTest : ScenarioTestBase() {
    init {
        val abilities = cardRegistry.getCard("Chandra, Chill of Compliance")!!.script.activatedAbilities
        val plusOnes = abilities.filter { (it.cost as? AbilityCost.Loyalty)?.change == 1 }
        val surveilId = plusOnes[0].id
        val manaId = plusOnes[1].id
        val minusXId = abilities.single { it.cost is AbilityCost.LoyaltyX }.id
        val minusSixId = abilities.single { (it.cost as? AbilityCost.Loyalty)?.change == -6 }.id

        fun base() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Chandra, Chill of Compliance")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        fun surveilInto(topCard: String, toGraveyard: Boolean): ScenarioTestBase.TestGame {
            val game = base().withCardInLibrary(1, topCard).withCardInLibrary(2, "Island").build()
            val chandra = game.findPermanent("Chandra, Chill of Compliance")!!
            game.execute(ActivateAbility(game.player1Id, chandra, surveilId)).error shouldBe null
            game.resolveStack()
            val top = game.findCardsInLibrary(1, topCard)
            if (toGraveyard) game.selectCards(top).error shouldBe null else game.selectCards(emptyList()).error shouldBe null
            game.resolveStack()
            return game
        }

        test("+1: a noncreature, nonland card surveiled into the graveyard goes to your hand") {
            val game = surveilInto("Lightning Bolt", toGraveyard = true)
            game.isInHand(1, "Lightning Bolt") shouldBe true
            game.isInGraveyard(1, "Lightning Bolt") shouldBe false
        }

        test("+1: a creature card surveiled into the graveyard stays there") {
            val game = surveilInto("Grizzly Bears", toGraveyard = true)
            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            game.isInHand(1, "Grizzly Bears") shouldBe false
        }

        test("+1: a land card surveiled into the graveyard stays there") {
            val game = surveilInto("Island", toGraveyard = true)
            game.isInGraveyard(1, "Island") shouldBe true
            game.handSize(1) shouldBe 0
        }

        test("+1: keeping the card on top puts nothing in your hand") {
            val game = surveilInto("Lightning Bolt", toGraveyard = false)
            game.librarySize(1) shouldBe 1
            game.handSize(1) shouldBe 0
        }

        test("+1: the {U} can cast a noncreature spell but not a creature spell") {
            val game = base()
                .withCardInHand(1, "Opt")
                .withCardInHand(1, "Merfolk of the Pearl Trident")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .build()
            val chandra = game.findPermanent("Chandra, Chill of Compliance")!!
            game.execute(ActivateAbility(game.player1Id, chandra, manaId)).error shouldBe null
            game.resolveStack()

            withClue("the restricted {U} can't pay for a creature spell") {
                game.castSpell(1, "Merfolk of the Pearl Trident").error shouldNotBe null
            }
            game.castSpell(1, "Opt").error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Merfolk of the Pearl Trident") shouldBe true
            withClue("Opt was cast with the restricted {U}") { game.isInHand(1, "Opt") shouldBe false }
        }

        test("−X taps target creature and puts X stun counters on it") {
            val game = base().withCardOnBattlefield(2, "Hill Giant").build()
            val chandra = game.findPermanent("Chandra, Chill of Compliance")!!
            val giant = game.findPermanent("Hill Giant")!!

            game.execute(
                ActivateAbility(
                    game.player1Id, chandra, minusXId,
                    targets = listOf(ChosenTarget.Permanent(giant)), xValue = 2
                )
            ).error shouldBe null
            game.resolveStack()

            game.state.getEntity(giant)!!.has<TappedComponent>() shouldBe true
            game.state.getEntity(giant)!!.get<CountersComponent>()!!.getCount(CounterType.STUN) shouldBe 2
            game.state.getEntity(chandra)!!.get<CountersComponent>()!!.getCount(CounterType.LOYALTY) shouldBe 1
        }

        test("−X with X=0 still taps the target") {
            val game = base().withCardOnBattlefield(2, "Hill Giant").build()
            val chandra = game.findPermanent("Chandra, Chill of Compliance")!!
            val giant = game.findPermanent("Hill Giant")!!

            game.execute(
                ActivateAbility(
                    game.player1Id, chandra, minusXId,
                    targets = listOf(ChosenTarget.Permanent(giant)), xValue = 0
                )
            ).error shouldBe null
            game.resolveStack()

            game.state.getEntity(giant)!!.has<TappedComponent>() shouldBe true
            (game.state.getEntity(giant)!!.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0) shouldBe 0
        }

        test("−6 emblem: whenever you cast a spell, draw a card") {
            val game = base()
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .build()
            val chandra = game.findPermanent("Chandra, Chill of Compliance")!!
            game.state = game.state.updateEntity(chandra) {
                it.with(CountersComponent(mapOf(CounterType.LOYALTY to 6)))
            }

            game.execute(ActivateAbility(game.player1Id, chandra, minusSixId)).error shouldBe null
            game.resolveStack()

            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.resolveStack()

            withClue("the emblem drew a card for the Bolt") {
                game.handSize(1) shouldBe 1
            }
            game.getLifeTotal(2) shouldBe 17
        }
    }
}
