package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.player.LandDropsComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Kellan, Inquisitive Prodigy // Tail the Suspect (MKM #212).
 *
 * Creature face: {2}{G}{U} 3/4 Legendary Human Faerie Detective with flying and vigilance,
 * "Whenever Kellan attacks, destroy up to one target artifact. If you controlled that permanent,
 * draw a card."
 * Adventure face: Tail the Suspect {G}{U}, "Investigate. You may play an additional land this turn."
 *
 * The attack trigger's ordering is the thing worth pinning. The draw is conditioned on *your*
 * control of the artifact, and the artifact is destroyed in the same resolution — so a control test
 * that ran after the destruction would look at a graveyard card and always answer no, silently
 * turning the card into plain artifact removal. These tests separate the two halves: your own
 * artifact draws, an opponent's does not, and "up to one" means the trigger can resolve having
 * chosen nothing at all.
 */
class KellanInquisitiveProdigyScenarioTest : ScenarioTestBase() {

    init {
        context("Kellan, Inquisitive Prodigy") {

            test("attacking and destroying your own artifact draws a card") {
                val game = scenario()
                    .withPlayers("Kellan", "Opponent")
                    .withCardOnBattlefield(1, "Kellan, Inquisitive Prodigy", summoningSickness = false)
                    .withCardOnBattlefield(1, "Magnifying Glass")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                val glass = game.findPermanent("Magnifying Glass")!!
                val handBefore = game.handSize(1)

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Kellan, Inquisitive Prodigy" to 2)).error shouldBe null

                game.selectTargets(listOf(glass)).error shouldBe null
                game.resolveStack()

                withClue("your own artifact is destroyed all the same") {
                    game.isInGraveyard(1, "Magnifying Glass") shouldBe true
                }
                withClue("and because you controlled it, you draw") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("destroying an opponent's artifact draws nothing") {
                val game = scenario()
                    .withPlayers("Kellan", "Opponent")
                    .withCardOnBattlefield(1, "Kellan, Inquisitive Prodigy", summoningSickness = false)
                    .withCardOnBattlefield(2, "Magnifying Glass")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                val glass = game.findPermanent("Magnifying Glass")!!
                val handBefore = game.handSize(1)

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Kellan, Inquisitive Prodigy" to 2)).error shouldBe null

                game.selectTargets(listOf(glass)).error shouldBe null
                game.resolveStack()

                withClue("the opponent's artifact is destroyed") {
                    game.isInGraveyard(2, "Magnifying Glass") shouldBe true
                }
                withClue("but you never controlled it, so no card") {
                    game.handSize(1) shouldBe handBefore
                }
            }

            test("'up to one target' resolves cleanly with nothing chosen") {
                val game = scenario()
                    .withPlayers("Kellan", "Opponent")
                    .withCardOnBattlefield(1, "Kellan, Inquisitive Prodigy", summoningSickness = false)
                    .withCardOnBattlefield(1, "Magnifying Glass")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                val handBefore = game.handSize(1)

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Kellan, Inquisitive Prodigy" to 2)).error shouldBe null

                game.skipTargets().error shouldBe null
                game.resolveStack()

                withClue("nothing was destroyed") {
                    game.isOnBattlefield("Magnifying Glass") shouldBe true
                }
                withClue("and nothing was drawn") { game.handSize(1) shouldBe handBefore }
            }

            test("Tail the Suspect investigates and grants a second land drop") {
                val game = scenario()
                    .withPlayers("Kellan", "Opponent")
                    .withCardInHand(1, "Kellan, Inquisitive Prodigy")
                    .withCardsInHand(1, "Forest", 2)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val kellan = game.findCardsInHand(1, "Kellan, Inquisitive Prodigy").single()

                // faceIndex 0 is the Adventure face (CR 715.3).
                val cast = game.execute(
                    CastSpell(playerId = game.player1Id, cardId = kellan, faceIndex = 0)
                )
                withClue("casting the Adventure face should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("investigate made a Clue") { game.findPermanents("Clue").size shouldBe 1 }
                withClue("the adventurer card went on an adventure rather than to the graveyard") {
                    game.isInExile(1, "Kellan, Inquisitive Prodigy") shouldBe true
                }

                val forests = game.findCardsInHand(1, "Forest")
                game.execute(PlayLand(game.player1Id, forests[0])).error shouldBe null
                withClue("the extra land drop makes a second land legal this turn") {
                    game.execute(PlayLand(game.player1Id, forests[1])).error shouldBe null
                }
                withClue("both land drops were spent") {
                    game.state.getEntity(game.player1Id)?.get<LandDropsComponent>()?.remaining shouldBe 0
                }
            }
        }
    }
}
