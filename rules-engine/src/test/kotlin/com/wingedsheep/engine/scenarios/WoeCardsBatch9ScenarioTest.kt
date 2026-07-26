package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for the ninth batch of Wilds of Eldraine cards. All five cards in the batch are
 * composed from existing primitives, so the snapshot and lint nets already pin their *shape* — what
 * this covers is the handful of places where a plausible-looking model could still resolve wrong:
 *
 *  - **Twisted Sewer-Witch** — the Rat it creates has to be inside the "for each Rat you control"
 *    snapshot. The card hinges on the `ForEach` group being sampled *after* the token step, and
 *    nothing in the definition itself would catch that ordering being inverted.
 *  - **Living Lectern** — both sides of its "up to one other target": with a target chosen, and
 *    declined (which per the WOE ruling must still draw, and must be activatable even when you
 *    control no other creature at all).
 *  - **Rip the Seams** — "tapped" is a *targeting* restriction, so an untapped creature has to be
 *    rejected at cast time rather than quietly destroyed on resolution.
 *  - **Tempest Hart** — mana value 5 is a boundary, so both sides of it are checked.
 */
class WoeCardsBatch9ScenarioTest : ScenarioTestBase() {

    private val lecternAbilityId by lazy {
        cardRegistry.requireCard("Living Lectern").activatedAbilities[0].id
    }

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    private fun rolesAttachedTo(game: TestGame, roleName: String, host: EntityId): Int =
        game.findAllPermanents(roleName).count { role ->
            game.state.getEntity(role)?.get<AttachedToComponent>()?.targetId == host
        }

    init {
        context("Twisted Sewer-Witch — a Wicked Role on every Rat you control") {
            test("the Rat created by the same trigger is inside the 'for each Rat' snapshot") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Twisted Sewer-Witch")
                    // A Rat already on the battlefield, so the count spans pre-existing and new.
                    .withCardOnBattlefield(1, "Voracious Vermin")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val vermin = game.findPermanent("Voracious Vermin")!!

                game.castSpell(1, "Twisted Sewer-Witch").error shouldBe null
                game.resolveStack()

                val witch = game.findPermanent("Twisted Sewer-Witch")!!
                val ratTokens = game.findAllPermanents("Rat Token")

                withClue("the Witch's ETB created one Rat token") {
                    ratTokens.size shouldBe 1
                }

                withClue("every Rat you control got a Wicked Role — the new token included") {
                    rolesAttachedTo(game, "Wicked Role", ratTokens.single()) shouldBe 1
                    rolesAttachedTo(game, "Wicked Role", vermin) shouldBe 1
                    game.findAllPermanents("Wicked Role").size shouldBe 2
                }

                withClue("the Witch is a Human Warlock, not a Rat, so it gets no Role") {
                    rolesAttachedTo(game, "Wicked Role", witch) shouldBe 0
                }

                withClue("Wicked Role grants +1/+1 — the 1/1 Rat token projects as 2/2") {
                    game.state.projectedState.getPower(ratTokens.single()) shouldBe 2
                    game.state.projectedState.getToughness(ratTokens.single()) shouldBe 2
                }
            }
        }

        context("Living Lectern — 'up to one other target creature you control'") {
            test("with a target chosen: draw a card and attach a Sorcerer Role to it") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Living Lectern")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lectern = game.findPermanent("Living Lectern")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val handBefore = game.handSize(1)

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = lectern,
                        abilityId = lecternAbilityId,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                    )
                ).error shouldBe null
                game.resolveStack()

                game.handSize(1) shouldBe handBefore + 1
                withClue("the Lectern sacrificed itself as part of the cost") {
                    game.isOnBattlefield("Living Lectern") shouldBe false
                }
                rolesAttachedTo(game, "Sorcerer Role", bears) shouldBe 1
                withClue("Sorcerer Role grants +1/+1 to the 2/2 Bears") {
                    game.state.projectedState.getPower(bears) shouldBe 3
                }
            }

            test("declining the target still draws — even with no other creature to target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    // Deliberately your only creature, so there is no legal "other" target at all.
                    .withCardOnBattlefield(1, "Living Lectern")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lectern = game.findPermanent("Living Lectern")!!
                val handBefore = game.handSize(1)

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = lectern,
                        abilityId = lecternAbilityId,
                        targets = emptyList(),
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("'up to one' declined — the draw is not gated on the Role") {
                    game.handSize(1) shouldBe handBefore + 1
                }
                withClue("no target chosen means no Sorcerer Role is created") {
                    game.findPermanent("Sorcerer Role") shouldBe null
                }
            }
        }

        context("Rip the Seams — 'target tapped creature' is a targeting restriction") {
            test("a tapped creature is destroyed, and the card exiles itself (CR 715.3d)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Threadbind Clique")
                    .withCardOnBattlefield(2, "Grizzly Bears", tapped = true)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val clique = game.findCardsInHand(1, "Threadbind Clique").single()
                val bears = game.findPermanent("Grizzly Bears")!!

                // faceIndex = 0 is the Adventure face (CR 715).
                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = clique,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                        faceIndex = 0,
                    )
                ).error shouldBe null
                game.resolveStack()

                game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                game.isInExile(1, "Threadbind Clique") shouldBe true
            }

            test("an untapped creature is not a legal target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Threadbind Clique")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val clique = game.findCardsInHand(1, "Threadbind Clique").single()
                val bears = game.findPermanent("Grizzly Bears")!!

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = clique,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                        faceIndex = 0,
                    )
                ).error shouldNotBe null
                game.isOnBattlefield("Grizzly Bears") shouldBe true
            }
        }

        context("Tempest Hart — the mana value 5 threshold") {
            test("a mana value 6 spell adds a counter, a mana value 2 spell does not") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Tempest Hart")
                    .withCardInHand(1, "Craw Wurm")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 8)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hart = game.findPermanent("Tempest Hart")!!
                plusOneCounters(game, hart) shouldBe 0

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()
                withClue("Grizzly Bears is mana value 2 — below the threshold") {
                    plusOneCounters(game, hart) shouldBe 0
                }

                game.castSpell(1, "Craw Wurm").error shouldBe null
                game.resolveStack()
                withClue("Craw Wurm is mana value 6 — at or above 5") {
                    plusOneCounters(game, hart) shouldBe 1
                }
                withClue("3/4 base plus the counter") {
                    game.state.projectedState.getPower(hart) shouldBe 4
                    game.state.projectedState.getToughness(hart) shouldBe 5
                }
            }
        }

        context("Mend the Wilds — returning a permanent card from your graveyard") {
            test("the targeted permanent card goes on top of your library and is drawn next") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Woodland Acolyte")
                    .withCardInGraveyard(1, "Craw Wurm")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val acolyte = game.findCardsInHand(1, "Woodland Acolyte").single()
                val wurm = game.findCardsInGraveyard(1, "Craw Wurm").single()

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = acolyte,
                        targets = listOf(ChosenTarget.Card(wurm, game.player1Id, Zone.GRAVEYARD)),
                        faceIndex = 0,
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("the Wurm left the graveyard for the top of the library") {
                    game.isInGraveyard(1, "Craw Wurm") shouldBe false
                    game.findCardsInLibrary(1, "Craw Wurm").size shouldBe 1
                    game.state.getLibrary(game.player1Id).first() shouldBe wurm
                }
                withClue("the Adventure exiled itself, so the creature is castable later") {
                    game.isInExile(1, "Woodland Acolyte") shouldBe true
                }
            }
        }
    }
}
