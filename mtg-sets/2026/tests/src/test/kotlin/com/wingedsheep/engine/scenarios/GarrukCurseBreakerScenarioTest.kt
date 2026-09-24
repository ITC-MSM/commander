package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Garruk, Curse Breaker (FRA #259).
 *
 * - Whenever a creature you control with power 4 or greater enters, draw a card.
 * - +2: Untap up to two target lands.
 * - −3: Create a 4/4 green Beast creature token with trample.
 * - −4: Until your next turn, whenever one or more creatures attack one of your opponents, those
 *   creatures get +2/+2 and gain trample until end of turn.
 */
class GarrukCurseBreakerScenarioTest : ScenarioTestBase() {
    init {
        val abilities = cardRegistry.getCard("Garruk, Curse Breaker")!!.script.activatedAbilities
        fun loyalty(change: Int) = abilities.single { (it.cost as? AbilityCost.Loyalty)?.change == change }.id

        fun base() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Garruk, Curse Breaker")
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(2, "Swamp")
            .withCardInLibrary(2, "Swamp")
            .withCardInLibrary(2, "Swamp")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("−3 makes a 4/4 trampling Beast, whose entry draws a card") {
            val game = base().build()
            val garruk = game.findPermanent("Garruk, Curse Breaker")!!
            val handBefore = game.handSize(1)

            game.execute(ActivateAbility(game.player1Id, garruk, loyalty(-3))).error shouldBe null
            game.resolveStack()

            val beast = game.findPermanent("Beast Token")!!
            game.state.projectedState.getPower(beast) shouldBe 4
            game.state.projectedState.getToughness(beast) shouldBe 4
            game.state.projectedState.hasKeyword(beast, Keyword.TRAMPLE) shouldBe true
            withClue("the power-4 Beast entering draws a card") {
                game.handSize(1) shouldBe handBefore + 1
            }
        }

        test("a creature with power less than 4 entering doesn't draw") {
            val game = base()
                .withCardInHand(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Forest", 2)
                .build()

            game.castSpell(1, "Grizzly Bears").error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Grizzly Bears") shouldBe true
            game.handSize(1) shouldBe 0
        }

        test("an opponent's power-4 creature entering doesn't draw") {
            val game = base()
                .withCardInHand(2, "Hill Giant")
                .withLandsOnBattlefield(2, "Mountain", 4)
                .withActivePlayer(2)
                .build()

            game.castSpell(2, "Hill Giant").error shouldBe null
            game.resolveStack()

            game.handSize(1) shouldBe 0
        }

        test("+2 untaps up to two target lands") {
            val game = base()
                .withCardOnBattlefield(1, "Forest", tapped = true)
                .withCardOnBattlefield(1, "Mountain", tapped = true)
                .build()
            val garruk = game.findPermanent("Garruk, Curse Breaker")!!
            val forest = game.findPermanent("Forest")!!
            val mountain = game.findPermanent("Mountain")!!

            game.execute(
                ActivateAbility(
                    game.player1Id, garruk, loyalty(2),
                    targets = listOf(ChosenTarget.Permanent(forest), ChosenTarget.Permanent(mountain))
                )
            ).error shouldBe null
            game.resolveStack()

            game.state.getEntity(forest)!!.has<TappedComponent>() shouldBe false
            game.state.getEntity(mountain)!!.has<TappedComponent>() shouldBe false
        }

        test("+2 can be activated with no targets") {
            val game = base().build()
            val garruk = game.findPermanent("Garruk, Curse Breaker")!!
            game.execute(ActivateAbility(game.player1Id, garruk, loyalty(2))).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Garruk, Curse Breaker") shouldBe true
        }

        test("−4: your attackers get +2/+2 and trample; it lasts through the opponent's turn only") {
            val game = base()
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .build()
            val garruk = game.findPermanent("Garruk, Curse Breaker")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val giant = game.findPermanent("Hill Giant")!!

            game.execute(ActivateAbility(game.player1Id, garruk, loyalty(-4))).error shouldBe null
            game.resolveStack()
            withClue("the −4 took Garruk to 1 loyalty; he stays") {
                game.isOnBattlefield("Garruk, Curse Breaker") shouldBe true
            }

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
            game.resolveStack()

            game.state.projectedState.getPower(bears) shouldBe 4
            game.state.projectedState.getToughness(bears) shouldBe 4
            game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe true

            // Opponent's turn: their Hill Giant attacks *you*, not one of your opponents.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.state.activePlayerId shouldBe game.player2Id
            withClue("the pump wore off at end of turn") {
                game.state.projectedState.getPower(bears) shouldBe 2
            }
            game.declareAttackers(mapOf("Hill Giant" to 1)).error shouldBe null
            game.resolveStack()
            withClue("creatures attacking you don't get the bonus") {
                game.state.projectedState.getPower(giant) shouldBe 3
                game.state.projectedState.hasKeyword(giant, Keyword.TRAMPLE) shouldBe false
            }

            // Your next turn: the delayed trigger has expired.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.state.activePlayerId shouldBe game.player1Id
            game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
            game.resolveStack()
            withClue("until your next turn — no bonus on your next turn") {
                game.state.projectedState.getPower(bears) shouldBe 2
                game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe false
            }
        }

        test("−4 keeps working after Garruk leaves the battlefield") {
            val game = base()
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .build()
            val garruk = game.findPermanent("Garruk, Curse Breaker")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.execute(ActivateAbility(game.player1Id, garruk, loyalty(-4))).error shouldBe null
            game.resolveStack()
            game.castSpell(1, "Lightning Bolt", garruk).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Garruk, Curse Breaker") shouldBe false

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
            game.resolveStack()

            game.state.projectedState.getPower(bears) shouldBe 4
            game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe true
        }
    }
}
