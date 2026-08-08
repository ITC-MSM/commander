package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Modal double-faced cards whose **back face is a permanent** (CR 712.3) — the Marvel Super Heroes
 * hero cycle. Engine-level, so it covers the mechanic across several cards rather than one card.
 *
 * These are modal *and* transforming, which CR 712.3 allows in as many words: *"Modal double-faced
 * cards have a Magic card face on each side. These faces are usually independent from one another,
 * but they may have an ability that allows them to 'transform' or 'convert' on either face."* Three
 * things follow, and each is pinned below:
 *
 *  1. **Either face is castable from hand.** CR 712.11b — the caster chooses a face before the card
 *     goes on the stack; CR 712.11c — only that face is evaluated for legality. The back is cast for
 *     its own printed mana cost and resolves onto the battlefield back face up (CR 712.13).
 *  2. **The front can still transform into the back**, through its printed activated ability.
 *  3. **A modal back face keeps its own mana value.** CR 712.8f says a modal DFC on the battlefield
 *     "has only the characteristics of the face that's up", with *no* mana-value exception — unlike
 *     CR 712.8e, which keeps the front's for a nonmodal DFC. So The Sensational She-Hulk is mana
 *     value 6, not Jennifer Walters' 2, however she got there.
 */
class ModalDfcPermanentBackTest : ScenarioTestBase() {

    init {
        /** Six lands covering `{3}{G}{W}{W}` (and, with three Forests, `{1}{W}` too). */
        fun sixLands() = scenario()
            .withPlayers("Player1", "Player2")
            .withCardInHand(1, "Jennifer Walters")
            .withLandsOnBattlefield(1, "Forest", 3)
            .withLandsOnBattlefield(1, "Plains", 3)
            .withCardInLibrary(1, "Forest")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        fun TestGame.manaValueOf(cardName: String): Int =
            state.getEntity(findPermanent(cardName)!!)!!.get<CardComponent>()!!.manaCost.cmc

        test("the front face is castable from hand for its own cost") {
            val game = sixLands()

            game.castSpell(1, "Jennifer Walters").error shouldBe null
            game.resolveStack()

            withClue("cast normally, the card enters as its front face (CR 712.13)") {
                game.isOnBattlefield("Jennifer Walters") shouldBe true
                game.isOnBattlefield("The Sensational She-Hulk") shouldBe false
            }
            withClue("front face up → the front's mana value") {
                game.manaValueOf("Jennifer Walters") shouldBe 2
            }
        }

        test("the back face is castable from hand for its own mana cost (CR 712.11b)") {
            val game = sixLands()
            val cardId = game.findCardsInHand(1, "Jennifer Walters").first()

            val result = game.execute(
                CastSpell(
                    game.player1Id,
                    cardId,
                    useAlternativeCost = true,
                    alternativeCostType = AlternativeCostType.MODAL_BACK_FACE
                )
            )
            withClue("casting the back face is legal with {3}{G}{W}{W} available") {
                result.error shouldBe null
            }
            game.resolveStack()

            withClue("it resolved onto the battlefield back face up (CR 712.13)") {
                game.isOnBattlefield("The Sensational She-Hulk") shouldBe true
                game.isOnBattlefield("Jennifer Walters") shouldBe false
            }
            withClue("with the back face's P/T, not the front's 2/3") {
                val hulk = game.findPermanent("The Sensational She-Hulk")!!
                val stats = game.state.getEntity(hulk)!!.get<CardComponent>()!!.baseStats
                stats?.basePower shouldBe 6
                stats?.baseToughness shouldBe 6
            }
            withClue("CR 712.8f: a modal back face keeps its *own* mana value, not the front's 2") {
                game.manaValueOf("The Sensational She-Hulk") shouldBe 6
            }
            withClue("the engine still knows which face is up, so it can transform back") {
                game.state.getEntity(game.findPermanent("The Sensational She-Hulk")!!)!!
                    .get<DoubleFacedComponent>()?.currentFace shouldBe DoubleFacedComponent.Face.BACK
            }
        }

        test("the back-face cast is offered as a legal action alongside the front") {
            val game = sixLands()

            val descriptions = game.getLegalActions(1).map { it.description }
            withClue("both faces belong in the action menu (CR 712.11b): $descriptions") {
                descriptions.any { "Jennifer Walters" in it && "Cast" in it } shouldBe true
                descriptions.any { "The Sensational She-Hulk" in it && "Cast" in it } shouldBe true
            }
        }

        test("the back-face cast is not offered without enough mana for the back's cost") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Jennifer Walters")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withCardInLibrary(1, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val affordable = game.getLegalActions(1)
                .filter { "The Sensational She-Hulk" in it.description }
            withClue("two Plains cannot pay {3}{G}{W}{W}, so the offer is not affordable") {
                affordable.none { it.isAffordable } shouldBe true
            }
            withClue("but the front face is castable for {1}{W}") {
                game.castSpell(1, "Jennifer Walters").error shouldBe null
            }
        }

        test("transforming still reaches the same back face, with the back's mana value") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Jennifer Walters", summoningSickness = false)
                .withLandsOnBattlefield(1, "Forest", 3)
                .withLandsOnBattlefield(1, "Plains", 3)
                .withCardInLibrary(1, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val jennifer = game.findPermanent("Jennifer Walters")!!
            val transformAbility = cardRegistry.getCard("Jennifer Walters")!!
                .script.activatedAbilities.first().id
            val result = game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = jennifer, abilityId = transformAbility)
            )
            withClue("activating the transform ability should succeed: ${result.error}") {
                result.error shouldBe null
            }
            game.resolveStack()

            withClue("CR 712.3 — a modal DFC may still carry a transform ability") {
                game.isOnBattlefield("The Sensational She-Hulk") shouldBe true
            }
            withClue("however she got there, the face that's up sets the mana value (CR 712.8f)") {
                game.manaValueOf("The Sensational She-Hulk") shouldBe 6
            }
        }

        test("every MSH hero back face carries a real cost and no color indicator") {
            // The colors of these backs come from their own mana cost, which is why the printed
            // cards show no CR 204 color indicator. `modalDoubleFacedPermanent` enforces both, so
            // this is really a guard that the whole cycle goes through that factory.
            val heroes = listOf(
                "Jennifer Walters" to "The Sensational She-Hulk",
                "Bruce Banner" to "The Incredible Hulk",
                "King T'Challa" to "Black Panther, Hope Enduring",
                "Tony Stark" to "The Invincible Iron Man",
                "Monica Rambeau" to "Photon, Living Light",
            )
            heroes.forEach { (frontName, backName) ->
                val front = cardRegistry.getCard(frontName)!!
                withClue("$frontName is a modal DFC") {
                    front.layout shouldBe com.wingedsheep.sdk.model.CardLayout.MODAL_DFC
                }
                val back = front.backFace
                withClue("$frontName has $backName as a permanent back face") {
                    back?.name shouldBe backName
                    back?.isPermanent shouldBe true
                }
                withClue("$backName is castable, so it carries its own mana cost") {
                    back!!.manaCost.isEmpty() shouldBe false
                }
                withClue("$backName takes no color indicator — its colors come from that cost") {
                    back!!.colorIndicator shouldBe null
                }
                withClue("$backName's colors match its mana cost") {
                    back!!.colors shouldBe back.manaCost.colors
                }
            }
        }
    }
}
