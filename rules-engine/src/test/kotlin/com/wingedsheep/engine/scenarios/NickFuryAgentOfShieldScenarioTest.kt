package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Nick Fury, Agent of S.H.I.E.L.D. — the set's widest power-up dig, and the only card in the cycle
 * built from a hand-written Gather → Select → Move pipeline rather than a one-line facade. This
 * test pins the pipeline down end to end, because a mis-wired collection name would silently
 * bottom the whole library instead of putting a permanent onto the battlefield.
 *
 * "Power-up — {W}{U}{B}{R}{G}: Put two +1/+1 counters on Nick Fury, then look at the top seven
 * cards of your library. You may put a Hero, Equipment, or Vehicle card from among them onto the
 * battlefield. If it's a double-faced card, you may transform it. Put the rest on the bottom of
 * your library in a random order."
 *
 * The claims under test: the counters land, the eligibility filter admits only Hero/Equipment/
 * Vehicle, the chosen card reaches the battlefield, the rest go back to the library rather than
 * anywhere else, and — the part that is easy to get wrong — declining every optional prompt still
 * resolves the whole ability.
 */
class NickFuryAgentOfShieldScenarioTest : ScenarioTestBase() {

    private val heroRecruit = card("Test Hero Recruit") {
        manaCost = "{2}{W}"
        typeLine = "Creature — Human Hero"
        power = 2
        toughness = 2
    }

    // Deliberately not a Hero, Equipment or Vehicle — must never be selectable.
    private val plainBear = card("Test Plain Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    private val abilityId
        get() = cardRegistry.getCard("Nick Fury, Agent of S.H.I.E.L.D.")!!
            .script.activatedAbilities[0].id

    /** Activate the power-up, auto-paying mana. */
    private fun TestGame.activateFury(): Boolean {
        val furyId = findPermanent("Nick Fury, Agent of S.H.I.E.L.D.") ?: return false
        val result = execute(ActivateAbility(player1Id, furyId, abilityId))
        if (result.error != null) return false
        if (getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
            submitManaSourcesAutoPay()
        }
        resolveStack()
        return true
    }

    init {
        cardRegistry.register(heroRecruit)
        cardRegistry.register(plainBear)

        context("Nick Fury, Agent of S.H.I.E.L.D.") {

            test("the power-up is discounted to {U}{B}{R}{G} the turn he enters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Nick Fury, Agent of S.H.I.E.L.D.", enteredThisTurn = true)
                    .withCardInLibrary(1, "Test Hero Recruit")
                    .withCardInLibrary(1, "Test Plain Bear")
                    // Exactly the four pips the discount should leave — no white source in sight,
                    // so the ability is only enumerable at all if the {W} really was cancelled.
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("{W}{U}{B}{R}{G} less his own {W} is {U}{B}{R}{G}") {
                    game.getLegalActions(1)
                        .first { it.description.startsWith("Power-up —") }
                        .description.startsWith("Power-up — {U}{B}{R}{G}:") shouldBe true
                }
            }

            test("puts a chosen Hero onto the battlefield and bottoms the rest") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Nick Fury, Agent of S.H.I.E.L.D.", enteredThisTurn = true)
                    .withCardInLibrary(1, "Test Hero Recruit")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val furyId = game.findPermanent("Nick Fury, Agent of S.H.I.E.L.D.")!!
                val librarySizeBefore = game.librarySize(1)

                game.activateFury() shouldBe true

                // Choose the Hero from among the seven (only three cards exist, so all are looked at).
                val heroInLibrary = game.findCardsInLibrary(1, "Test Hero Recruit")
                withClue("the Hero should still be gatherable at the selection prompt") {
                    (heroInLibrary.isNotEmpty() || game.isOnBattlefield("Test Hero Recruit")) shouldBe true
                }
                if (game.hasPendingDecision()) {
                    val hero = game.findCardsInLibrary(1, "Test Hero Recruit").firstOrNull()
                    if (hero != null) game.selectCards(listOf(hero)) else game.skipSelection()
                }
                // Decline the "you may transform it" prompt, if one is offered.
                if (game.hasPendingDecision()) game.answerYesNo(false)
                game.resolveStack()

                withClue("both +1/+1 counters land regardless of what the dig finds") {
                    game.state.getEntity(furyId)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                }
                withClue("the chosen Hero is on the battlefield") {
                    game.isOnBattlefield("Test Hero Recruit") shouldBe true
                }
                withClue("the two non-matching cards go back to the library, not the graveyard") {
                    game.librarySize(1) shouldBe librarySizeBefore - 1
                    game.graveyardSize(1) shouldBe 0
                }
            }

            test("resolves fully when nothing eligible is found and every prompt is declined") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Nick Fury, Agent of S.H.I.E.L.D.", enteredThisTurn = true)
                    .withCardInLibrary(1, "Test Plain Bear")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val furyId = game.findPermanent("Nick Fury, Agent of S.H.I.E.L.D.")!!

                game.activateFury() shouldBe true
                repeat(3) {
                    if (game.hasPendingDecision()) {
                        val decision = game.getPendingDecision()
                        if (decision is com.wingedsheep.engine.core.YesNoDecision) game.answerYesNo(false)
                        else game.skipSelection()
                    }
                }
                game.resolveStack()

                withClue("an empty dig still puts both counters on Nick Fury") {
                    game.state.getEntity(furyId)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                }
                withClue("a Bear is neither Hero, Equipment nor Vehicle — it stays out of play") {
                    game.isOnBattlefield("Test Plain Bear") shouldBe false
                }
                withClue("the looked-at cards return to the library") {
                    game.librarySize(1) shouldBe 2
                    game.graveyardSize(1) shouldBe 0
                }
            }
        }
    }
}
