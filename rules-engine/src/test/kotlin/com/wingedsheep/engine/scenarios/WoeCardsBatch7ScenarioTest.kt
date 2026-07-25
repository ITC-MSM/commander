package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for a batch of Wilds of Eldraine cards implemented together. All five are pure
 * composition over existing SDK primitives, so these tests pin the *composition* — the parts that
 * could plausibly be wired wrong:
 *
 *  - Obyra's Attendants // Desperate Parry ({4}{U} 3/4 flier) — the Adventure is a `-4/-0` pump,
 *    so it must shrink power without ever killing (toughness untouched).
 *  - Skybeast Tracker ({3}{G} 2/4) — a *cast* trigger gated on mana value 5 or greater; the
 *    threshold is the risky bit (a 4-drop must not make Food).
 *  - Splashy Spellcaster ({3}{U} 2/4) — "up to one **other** target creature you control": the
 *    optional target must be declinable (no token) and must exclude the Spellcaster itself.
 *  - Return from the Wilds ({2}{G} sorcery) — `choose two` of three non-targeting modes.
 *  - Not Dead After All ({B} instant) — a granted dies-trigger that returns the creature tapped
 *    and then crowns *the same* card with a Wicked Role. Like Return Triumphant, this leans on
 *    entity identity surviving the graveyard → battlefield move.
 */
class WoeCardsBatch7ScenarioTest : ScenarioTestBase() {

    init {
        context("Obyra's Attendants // Desperate Parry — the Adventure shrinks power only") {
            test("Desperate Parry gives -4/-0 and never kills the creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Obyra's Attendants")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardOnBattlefield(2, "Centaur Courser", summoningSickness = false)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                withClue("Centaur Courser starts as a 3/3") {
                    game.state.projectedState.getPower(courser) shouldBe 3
                    game.state.projectedState.getToughness(courser) shouldBe 3
                }

                // faceIndex = 0 is the Adventure face; the creature face casts with faceIndex = null.
                val cardId = game.findCardsInHand(1, "Obyra's Attendants").first()
                game.execute(
                    CastSpell(game.player1Id, cardId, listOf(ChosenTarget.Permanent(courser)), faceIndex = 0)
                ).isSuccess shouldBe true
                game.resolveStack()

                withClue("-4/-0 — power drops by four, toughness is untouched") {
                    game.state.projectedState.getPower(courser) shouldBe -1
                    game.state.projectedState.getToughness(courser) shouldBe 3
                }
                withClue("a 3/3 hit by -4/-0 still has toughness 3, so it survives") {
                    game.isOnBattlefield("Centaur Courser") shouldBe true
                }
                withClue("resolving the Adventure exiles the card so it can be cast as a creature later") {
                    game.isInExile(1, "Obyra's Attendants") shouldBe true
                }
            }

            test("the creature face is a 3/4 flier") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Obyra's Attendants")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Obyra's Attendants")
                game.resolveStack()

                val attendants = game.findPermanent("Obyra's Attendants")!!
                game.state.projectedState.getPower(attendants) shouldBe 3
                game.state.projectedState.getToughness(attendants) shouldBe 4
                withClue("Flying") {
                    game.state.projectedState.hasKeyword(attendants, com.wingedsheep.sdk.core.Keyword.FLYING) shouldBe true
                }
            }
        }

        context("Skybeast Tracker — Food only on mana value 5 or greater") {
            test("casting a 6-drop creates a Food token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Skybeast Tracker", summoningSickness = false)
                    .withCardInHand(1, "Craw Wurm")
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.findPermanents("Food").size shouldBe 0

                // Craw Wurm is {4}{G}{G} — mana value 6.
                game.castSpell(1, "Craw Wurm")
                game.resolveStack()

                withClue("the cast trigger made exactly one Food") {
                    game.findPermanents("Food").size shouldBe 1
                }
            }

            test("casting a 4-drop makes no Food — the threshold is 5, not 4") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Skybeast Tracker", summoningSickness = false)
                    .withCardInHand(1, "Hill Giant")
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Hill Giant is {3}{R} — mana value 4.
                game.castSpell(1, "Hill Giant")
                game.resolveStack()

                game.findPermanents("Food").size shouldBe 0
            }
        }

        context("Splashy Spellcaster — up to one OTHER target creature you control") {
            test("casting an instant crowns another creature with a Sorcerer Role") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Splashy Spellcaster", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Shock", bears)
                // The cast trigger goes on the stack above Shock and asks for its target first.
                game.selectTargets(listOf(bears))
                game.resolveStack()

                val role = game.findPermanent("Sorcerer Role")
                withClue("the Sorcerer Role token was created") { role shouldNotBe null }
                withClue("attached to the Bears, not the Spellcaster") {
                    game.state.getEntity(role!!)?.get<AttachedToComponent>()?.targetId shouldBe bears
                }
            }

            test("declining the optional target creates no Role token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Splashy Spellcaster", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Shock", bears)
                game.skipTargets()
                game.resolveStack()

                withClue("no target chosen -> no Sorcerer Role (per the card's ruling)") {
                    game.findPermanent("Sorcerer Role") shouldBe null
                }
            }
        }

        context("Return from the Wilds — choose two of three modes") {
            test("Human + Food makes one of each token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Return from the Wilds")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = game.findCardsInHand(1, "Return from the Wilds").first()
                game.execute(
                    CastSpell(game.player1Id, cardId, emptyList(), chosenModes = listOf(1, 2))
                ).isSuccess shouldBe true
                game.resolveStack()

                game.findPermanents("Human Token").size shouldBe 1
                game.findPermanents("Food").size shouldBe 1
                withClue("the land-search mode was not chosen") {
                    game.librarySize(1) shouldBe 1
                }
            }

            test("land search + Human puts a basic land onto the battlefield tapped") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Return from the Wilds")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = game.findCardsInHand(1, "Return from the Wilds").first()
                game.execute(
                    CastSpell(game.player1Id, cardId, emptyList(), chosenModes = listOf(0, 1))
                ).isSuccess shouldBe true
                game.resolveStack()

                // The library holds a single Plains — the search auto-resolves to it.
                if (game.hasPendingDecision()) {
                    game.selectCards(game.findCardsInLibrary(1, "Plains"))
                    game.resolveStack()
                }

                val plains = game.findPermanent("Plains")
                withClue("the searched basic land is on the battlefield") { plains shouldNotBe null }
                withClue("…and it entered tapped") {
                    game.state.getEntity(plains!!)?.has<TappedComponent>() shouldBe true
                }
                game.findPermanents("Human Token").size shouldBe 1
                game.findPermanents("Food").size shouldBe 0
            }

            test("choosing only one mode is illegal — the spell demands two") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Return from the Wilds")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = game.findCardsInHand(1, "Return from the Wilds").first()
                game.execute(
                    CastSpell(game.player1Id, cardId, emptyList(), chosenModes = listOf(2))
                ).isSuccess shouldBe false
            }
        }

        context("Not Dead After All — return tapped, then crown with a Wicked Role") {
            test("the granted creature dies and comes back tapped wearing a Wicked Role") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Not Dead After All")
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Not Dead After All", bears)
                game.resolveStack()

                game.castSpell(1, "Doom Blade", bears)
                game.resolveStack()

                withClue("same entity is back on the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.findPermanent("Grizzly Bears") shouldBe bears
                }
                withClue("…tapped") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
                }
                val role = game.findPermanent("Wicked Role")
                withClue("…wearing a Wicked Role") {
                    role shouldNotBe null
                    game.state.getEntity(role!!)?.get<AttachedToComponent>()?.targetId shouldBe bears
                }
                withClue("2/2 Bears + the Wicked Role's +1/+1 = 3/3") {
                    game.state.projectedState.getPower(bears) shouldBe 3
                    game.state.projectedState.getToughness(bears) shouldBe 3
                }
            }

            test("the grant is until end of turn only — a later death is permanent (CR 400.7)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Not Dead After All")
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Not Dead After All", bears)
                game.resolveStack()

                withClue("while it is live the grant rides the Bears") {
                    game.state.grantedTriggeredAbilities.any { it.entityId == bears } shouldBe true
                }

                game.castSpell(1, "Doom Blade", bears)
                game.resolveStack()

                withClue("the returned Bears is a new object — the grant did not follow it") {
                    game.state.grantedTriggeredAbilities.any { it.entityId == bears } shouldBe false
                }
            }
        }
    }
}
