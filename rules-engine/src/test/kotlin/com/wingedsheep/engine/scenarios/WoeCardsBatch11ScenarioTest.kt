package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.woe.cards.OldFlitterfang
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for the eleventh batch of Wilds of Eldraine cards.
 *
 * Every card in this batch is composed from primitives that already existed, so these tests are not
 * about new vocabulary — they pin down the five places where a plausible-looking composition
 * resolves wrong:
 *
 *  - **Charging Hooligan** — the pump has to be counted at *resolution* over **all** attackers
 *    including itself (2023-09-01 ruling), and the trample rider is a separate resolution-time gate
 *    that must stay off when no Rat is attacking.
 *  - **Wildwood Mentor** — "a token you control enters" must fire per token and must not fire for a
 *    nontoken creature; the attack trigger's +X/+X reads the Mentor's power *after* those counters,
 *    and must not be able to choose the Mentor itself.
 *  - **Old Flitterfang** — the intervening 'if' has to suppress the whole ability when nothing died,
 *    and the Food trigger has to fire in the *opponent's* end step too.
 *  - **Specter of Mortality** — X is the size of the exiled set, applied to every creature *except*
 *    the Specter; declining the exile must apply nothing at all.
 *  - **Twisted Fealty** — the two targets are independent: the Role goes on whichever creature was
 *    picked second, and the spell still resolves with no second target chosen.
 */
class WoeCardsBatch11ScenarioTest : ScenarioTestBase() {

    private fun power(game: TestGame, id: EntityId): Int? = game.state.projectedState.getPower(id)
    private fun toughness(game: TestGame, id: EntityId): Int? = game.state.projectedState.getToughness(id)

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    private fun auraOn(game: TestGame, auraName: String, host: EntityId): EntityId? =
        game.findPermanents(auraName).firstOrNull { aura ->
            game.state.getEntity(aura)?.get<AttachedToComponent>()?.targetId == host
        }

    /**
     * Twisted Fealty has two independent targets, which the base fixture's single-target
     * [ScenarioTestBase.TestGame.castSpell] can't express — cast it directly so the "up to one"
     * second target can be present or absent.
     */
    private fun TestGame.castTwistedFealty(targets: List<EntityId>) = execute(
        CastSpell(
            player1Id,
            findCardsInHand(1, "Twisted Fealty").first(),
            targets.map { ChosenTarget.Permanent(it) }
        )
    )

    init {
        context("Charging Hooligan — +1/+0 per attacking creature, trample if a Rat attacks") {
            test("attacking alongside two others is +3/+0 — the count includes the Hooligan itself") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Charging Hooligan", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Savannah Lions", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hooligan = game.findPermanent("Charging Hooligan").shouldNotBeNull()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(
                    mapOf("Charging Hooligan" to 2, "Grizzly Bears" to 2, "Savannah Lions" to 2)
                ).error shouldBe null
                game.resolveStack()

                withClue("three attackers → +3/+0 on a 3/3") {
                    power(game, hooligan) shouldBe 6
                    toughness(game, hooligan) shouldBe 3
                }
                withClue("no Rat is attacking, so no trample") {
                    game.state.projectedState.hasKeyword(hooligan, Keyword.TRAMPLE) shouldBe false
                }
            }

            test("attacking alone is only +1/+0 — the creatures left home don't count") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Charging Hooligan", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hooligan = game.findPermanent("Charging Hooligan").shouldNotBeNull()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Charging Hooligan" to 2)).error shouldBe null
                game.resolveStack()

                withClue("only the Hooligan attacks — it counts once, for +1/+0") {
                    power(game, hooligan) shouldBe 4
                }
            }

            test("an attacking Rat turns on trample") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    // Voracious Vermin is a Rat, so it satisfies "if a Rat is attacking".
                    .withCardOnBattlefield(1, "Charging Hooligan", summoningSickness = false)
                    .withCardOnBattlefield(1, "Voracious Vermin", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hooligan = game.findPermanent("Charging Hooligan").shouldNotBeNull()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(
                    mapOf("Charging Hooligan" to 2, "Voracious Vermin" to 2)
                ).error shouldBe null
                game.resolveStack()

                withClue("two attackers → +2/+0") { power(game, hooligan) shouldBe 5 }
                withClue("the Rat is attacking, so trample is granted") {
                    game.state.projectedState.hasKeyword(hooligan, Keyword.TRAMPLE) shouldBe true
                }
            }

            test("a Rat you control that stayed home does not grant trample — it must be attacking") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Charging Hooligan", summoningSickness = false)
                    .withCardOnBattlefield(1, "Voracious Vermin", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hooligan = game.findPermanent("Charging Hooligan").shouldNotBeNull()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Charging Hooligan" to 2)).error shouldBe null
                game.resolveStack()

                withClue("the Rat is on the battlefield but not attacking") {
                    game.state.projectedState.hasKeyword(hooligan, Keyword.TRAMPLE) shouldBe false
                }
            }
        }

        context("Wildwood Mentor — counters from tokens, +X/+X on attack") {
            test("a token entering puts a +1/+1 counter on the Mentor") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Wildwood Mentor", summoningSickness = false)
                    // Voracious Vermin's enters trigger creates one Rat *token*.
                    .withCardInHand(1, "Voracious Vermin")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val mentor = game.findPermanent("Wildwood Mentor").shouldNotBeNull()
                plusOneCounters(game, mentor) shouldBe 0

                game.castSpell(1, "Voracious Vermin").error shouldBe null
                game.resolveStack()

                withClue("the Rat token entering is one trigger; the nontoken Vermin itself is not") {
                    plusOneCounters(game, mentor) shouldBe 1
                }
                withClue("the 1/1 Mentor is now 2/2") {
                    power(game, mentor) shouldBe 2
                    toughness(game, mentor) shouldBe 2
                }
            }

            test("a nontoken creature entering does nothing") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Wildwood Mentor", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val mentor = game.findPermanent("Wildwood Mentor").shouldNotBeNull()

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears is a real card, not a token") {
                    plusOneCounters(game, mentor) shouldBe 0
                }
            }

            test("the attack trigger pumps another attacker by the Mentor's power") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Wildwood Mentor", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(
                    mapOf("Wildwood Mentor" to 2, "Grizzly Bears" to 2)
                ).error shouldBe null

                // Only the Bears is a legal target — "another" excludes the Mentor.
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("X = the Mentor's power (1), so the 2/2 Bears becomes 3/3") {
                    power(game, bears) shouldBe 3
                    toughness(game, bears) shouldBe 3
                }
            }
        }

        context("Old Flitterfang") {
            test("with a creature dead this turn, the end step makes a Food token") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Old Flitterfang", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                game.castSpell(1, "Doom Blade", targetId = bears).error shouldBe null
                game.resolveStack()
                game.isOnBattlefield("Grizzly Bears") shouldBe false

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("a creature died this turn, so the intervening 'if' is satisfied") {
                    game.findPermanent("Food").shouldNotBeNull()
                }
            }

            test("with nothing dead, the ability never triggers — no Food") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Old Flitterfang", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("nothing died, so no Food is created") {
                    game.findPermanent("Food") shouldBe null
                }
            }

            test("sacrificing another artifact for +2/+2 leaves Old Flitterfang itself alone") {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Old Flitterfang", summoningSickness = false)
                    .withCardOnBattlefield(1, "Ornithopter", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val flitterfang = game.findPermanent("Old Flitterfang").shouldNotBeNull()
                val thopter = game.findPermanent("Ornithopter").shouldNotBeNull()

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = flitterfang,
                        abilityId = OldFlitterfang.activatedAbilities[0].id
                    )
                ).error shouldBe null
                // The sacrifice is a cost, so it is paid on activation: pick the Ornithopter.
                if (game.hasPendingDecision()) {
                    game.selectCards(listOf(thopter)).error shouldBe null
                }
                game.resolveStack()

                withClue("the Ornithopter was eaten, not the Flitterfang") {
                    game.isOnBattlefield("Ornithopter") shouldBe false
                    game.isOnBattlefield("Old Flitterfang") shouldBe true
                }
                withClue("the 3/4 becomes 5/6 until end of turn") {
                    power(game, flitterfang) shouldBe 5
                    toughness(game, flitterfang) shouldBe 6
                }
            }
        }

        context("Specter of Mortality — -X/-X for each creature card exiled from your graveyard") {
            test("exiling two creature cards shrinks every other creature by 2") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Specter of Mortality")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Savannah Lions")
                    // A noncreature card in the graveyard must not be selectable.
                    .withCardInGraveyard(1, "Pacifism")
                    .withCardOnBattlefield(1, "Ornithopter", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant").shouldNotBeNull()
                val thopter = game.findPermanent("Ornithopter").shouldNotBeNull()

                game.castSpell(1, "Specter of Mortality").error shouldBe null
                game.resolveStack()

                val specter = game.findPermanent("Specter of Mortality").shouldNotBeNull()
                val graveBears = game.findCardsInGraveyard(1, "Grizzly Bears").first()
                val graveLions = game.findCardsInGraveyard(1, "Savannah Lions").first()

                game.selectCards(listOf(graveBears, graveLions)).error shouldBe null
                game.resolveStack()

                withClue("both creature cards left the graveyard for exile") {
                    game.isInExile(1, "Grizzly Bears") shouldBe true
                    game.isInExile(1, "Savannah Lions") shouldBe true
                }
                withClue("X = 2, so the 3/3 Hill Giant becomes 1/1") {
                    power(game, giant) shouldBe 1
                    toughness(game, giant) shouldBe 1
                }
                withClue("'each other creature' includes your own — the 0/2 Ornithopter dies") {
                    game.isOnBattlefield("Ornithopter") shouldBe false
                }
                withClue("the Specter spares itself and stays 3/3") {
                    power(game, specter) shouldBe 3
                    toughness(game, specter) shouldBe 3
                }
            }

            test("declining the exile applies no modification at all") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Specter of Mortality")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant").shouldNotBeNull()

                game.castSpell(1, "Specter of Mortality").error shouldBe null
                game.resolveStack()

                game.skipSelection().error shouldBe null
                game.resolveStack()

                withClue("nothing was exiled, so nothing shrinks") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                    power(game, giant) shouldBe 3
                    toughness(game, giant) shouldBe 3
                }
            }
        }

        context("Twisted Fealty — borrow a creature, and crown a second one") {
            test("the stolen creature is untapped, hasty and yours; the Role lands on the other target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Twisted Fealty")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", tapped = true)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant").shouldNotBeNull()
                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                game.castTwistedFealty(listOf(giant, bears)).error shouldBe null
                game.resolveStack()

                withClue("the Giant changed controller for the turn and untapped with haste") {
                    game.state.projectedState.getController(giant) shouldBe game.player1Id
                    game.state.getEntity(giant)?.get<TappedComponent>() shouldBe null
                    game.state.projectedState.hasKeyword(giant, Keyword.HASTE) shouldBe true
                }
                withClue("the Wicked Role went on the second target, not the stolen creature") {
                    auraOn(game, "Wicked Role", bears).shouldNotBeNull()
                    auraOn(game, "Wicked Role", giant) shouldBe null
                }
                withClue("Wicked Role grants +1/+1 — the 2/2 Bears projects as 3/3") {
                    power(game, bears) shouldBe 3
                    toughness(game, bears) shouldBe 3
                }
            }

            test("with only the first target chosen the spell still steals, and makes no Role") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Twisted Fealty")
                    .withCardOnBattlefield(2, "Hill Giant", tapped = true)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant").shouldNotBeNull()

                game.castTwistedFealty(listOf(giant)).error shouldBe null
                game.resolveStack()

                withClue("the steal half resolved on its own") {
                    game.state.projectedState.getController(giant) shouldBe game.player1Id
                    game.state.getEntity(giant)?.get<TappedComponent>() shouldBe null
                }
                withClue("no second target was chosen, so no Role token exists") {
                    game.findPermanent("Wicked Role") shouldBe null
                }
            }
        }
    }
}
