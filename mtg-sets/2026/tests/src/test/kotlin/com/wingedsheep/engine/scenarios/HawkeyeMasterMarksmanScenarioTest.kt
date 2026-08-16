package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Hawkeye, Master Marksman (Marvel Super Heroes #130).
 *
 * {1}{R} · Legendary Creature — Human Archer Hero · 2/2
 *   Reach, first strike
 *   Trick Arrows — Whenever Hawkeye becomes tapped, you may pay {1} up to three times. When you do,
 *   choose up to that many —
 *   • Net — Target creature can't block this turn.
 *   • Explosive — Hawkeye deals 2 damage to target player.
 *   • Boomerang — Discard a card, then draw a card.
 *
 * The repeated-payment primitive itself (its affordability cap, colour-awareness, decline paths and
 * the count surviving the CR 603.12 stack round-trip) is covered at engine level in
 * `PayManaCostRepeatedlyTest`. These cover the *card*: that the payment count is what caps the
 * modes, that each arrow does what it prints, and that attacking is a way of becoming tapped.
 */
class HawkeyeMasterMarksmanScenarioTest : ScenarioTestBase() {

    /** A free Twiddle, so a test can tap Hawkeye without dragging combat in. */
    private val tapper = card("Test Tapper") {
        manaCost = "{0}"
        typeLine = "Instant"
        spell {
            target = TargetCreature()
            effect = Effects.Tap(EffectTarget.ContextTarget(0))
        }
    }

    private val bear = card("Test Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    /** A 2/2 flier, for the reach + first-strike block. */
    private val flier = card("Test Flier") {
        manaCost = "{1}{U}"
        typeLine = "Creature — Bird"
        power = 2
        toughness = 2
        keywords(Keyword.FLYING)
    }

    private fun modeQuestion(game: TestGame): ChooseOptionDecision =
        game.getPendingDecision() as? ChooseOptionDecision
            ?: error("expected a mode question; got ${game.getPendingDecision()}")

    /** Pick the offered mode whose label contains [label]. */
    private fun chooseMode(game: TestGame, label: String) {
        val decision = modeQuestion(game)
        val index = decision.options.indexOfFirst { it.contains(label, ignoreCase = true) }
        check(index >= 0) { "$label not offered; options=${decision.options}" }
        game.submitDecision(OptionChosenResponse(decision.id, optionIndex = index))
    }

    /**
     * Answer whatever target question is pending, one legal target per requirement — the opponent
     * for Explosive's player requirement, the Bear for Net's creature requirement. Handles both a
     * single multi-requirement decision and a sequence of one-requirement ones.
     */
    private fun answerTargetsIfAsked(game: TestGame) {
        while (true) {
            val decision = game.getPendingDecision() as? ChooseTargetsDecision ?: return
            val bear = game.findPermanent("Test Bear")
            val picks = decision.targetRequirements.associate { requirement ->
                val legal = decision.legalTargets[requirement.index].orEmpty()
                val pick = legal.firstOrNull { it == game.player2Id }
                    ?: legal.firstOrNull { it == bear }
                    ?: legal.first()
                requirement.index to listOf(pick)
            }
            game.submitDecision(TargetsResponse(decision.id, picks))
        }
    }

    /**
     * Hawkeye and [mountains] untapped Mountains for Player 1, an opposing Bear, and the free
     * tapper in hand. Stops on the "you may pay {1} up to three times" question.
     */
    private fun hawkeyeTappedByEffect(mountains: Int = 5, extraHandCard: String? = null): TestGame {
        val builder = scenario()
            .withPlayers("Player1", "Player2")
            .withCardOnBattlefield(1, "Hawkeye, Master Marksman")
            .withLandsOnBattlefield(1, "Mountain", mountains)
            .withCardInHand(1, "Test Tapper")
            // Exactly one card in the library, so Boomerang's draw is observable.
            .withCardInLibrary(1, "Mountain")
            .withCardOnBattlefield(2, "Test Bear")
            .withActivePlayer(1)
            .withPriorityPlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        if (extraHandCard != null) builder.withCardInHand(1, extraHandCard)
        val game = builder.build()

        val hawkeye = game.findPermanent("Hawkeye, Master Marksman")!!
        game.castSpell(1, "Test Tapper", targetId = hawkeye).error shouldBe null
        game.resolveStack()
        return game
    }

    init {
        cardRegistry.register(tapper)
        cardRegistry.register(bear)
        cardRegistry.register(flier)

        context("Trick Arrows — the payment gates the arrows") {

            test("becoming tapped offers the payment, capped at three") {
                val game = hawkeyeTappedByEffect()

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true)

                val count = game.getPendingDecision() as? ChooseNumberDecision
                    ?: error("expected the repeat-count question; got ${game.getPendingDecision()}")
                count.minValue shouldBe 1
                withClue("'up to three times', even with five Mountains untapped") {
                    count.maxValue shouldBe 3
                }
            }

            test("declining fires no arrows at all") {
                val game = hawkeyeTappedByEffect()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("no mode question, no damage — the reflexive half never triggered") {
                    (game.getPendingDecision() is ChooseOptionDecision) shouldBe false
                }
                game.getLifeTotal(2) shouldBe 20
            }

            test("paying once allows exactly one arrow") {
                val game = hawkeyeTappedByEffect()
                game.answerYesNo(true)
                game.chooseNumber(1)

                chooseMode(game, "Explosive")
                if (game.getPendingDecision() is ChooseTargetsDecision) {
                    game.selectTargets(listOf(game.player2Id))
                }
                withClue("one payment, one mode — no second mode question") {
                    (game.getPendingDecision() is ChooseOptionDecision) shouldBe false
                }

                game.resolveStack()
                game.getLifeTotal(2) shouldBe 18
            }

            test("paying twice allows two different arrows") {
                val game = hawkeyeTappedByEffect(extraHandCard = "Test Bear")
                // Player 1's graveyard already holds the resolved Test Tapper, and the only card
                // left in hand is the Bear — so "the Bear reached the graveyard" is the discard,
                // and nothing else in this test can put it there.
                game.graveyardSize(1) shouldBe 1
                game.librarySize(1) shouldBe 1

                game.answerYesNo(true)
                game.chooseNumber(2)

                chooseMode(game, "Explosive")
                chooseMode(game, "Boomerang")

                answerTargetsIfAsked(game)
                game.resolveStack()

                // The Boomerang discard may ask which card to pitch.
                (game.getPendingDecision() as? SelectCardsDecision)?.let { pick ->
                    game.selectCards(pick.options.take(1))
                    game.resolveStack()
                }

                withClue("Explosive resolved") { game.getLifeTotal(2) shouldBe 18 }
                withClue("Boomerang discarded the one card in hand") {
                    game.findCardsInGraveyard(1, "Test Bear").size shouldBe 1
                    game.graveyardSize(1) shouldBe 2
                }
                withClue("…and then drew one: the library's only card is now the only card in hand") {
                    game.librarySize(1) shouldBe 0
                    game.handSize(1) shouldBe 1
                }
            }

            test("'up to that many' is a ceiling, not a quota — pay twice, fire one arrow") {
                // The payment count caps the modes; it does not oblige them. This is why the modal
                // carries only `dynamicChooseCount` and no `dynamicMinChooseCount`: the minimum
                // stays 0, so the executor offers a decline option once the first mode is picked.
                val game = hawkeyeTappedByEffect()
                game.answerYesNo(true)
                game.chooseNumber(2)

                chooseMode(game, "Explosive")

                withClue("a second mode is offered, and declining it is one of the options") {
                    modeQuestion(game).options.any {
                        it.contains("Don't choose a mode", ignoreCase = true)
                    } shouldBe true
                }
                chooseMode(game, "Don't choose a mode")

                answerTargetsIfAsked(game)
                game.resolveStack()

                withClue("the one arrow chosen resolved") { game.getLifeTotal(2) shouldBe 18 }
                withClue("the declined arrows did not — no discard, nothing drawn") {
                    game.graveyardSize(1) shouldBe 1
                    game.librarySize(1) shouldBe 1
                }
                withClue("both repetitions were still paid — the mana is spent either way") {
                    game.findPermanents("Mountain").count {
                        game.state.getEntity(it)?.get<TappedComponent>() != null
                    } shouldBe 2
                }
            }

            test("paying the printed maximum asks three times and no fourth") {
                val game = hawkeyeTappedByEffect(extraHandCard = "Test Bear")
                game.answerYesNo(true)
                game.chooseNumber(3)

                chooseMode(game, "Net")
                chooseMode(game, "Explosive")
                chooseMode(game, "Boomerang")

                withClue("three payments, three modes — 'up to that many' is not off by one") {
                    (game.getPendingDecision() is ChooseOptionDecision) shouldBe false
                }

                answerTargetsIfAsked(game)
                game.resolveStack()
                (game.getPendingDecision() as? SelectCardsDecision)?.let { pick ->
                    game.selectCards(pick.options.take(1))
                    game.resolveStack()
                }

                withClue("all three arrows resolved") {
                    game.getLifeTotal(2) shouldBe 18
                    game.findCardsInGraveyard(1, "Test Bear").size shouldBe 1
                }
                withClue("three repetitions of {1} tapped three of the five Mountains") {
                    game.findPermanents("Mountain").count {
                        game.state.getEntity(it)?.get<TappedComponent>() != null
                    } shouldBe 3
                }
            }
        }

        context("Trick Arrows — attacking is a way of becoming tapped") {

            test("attacking turns the ability on, and Net keeps a blocker out of combat") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hawkeye, Master Marksman")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardOnBattlefield(2, "Test Bear")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Hawkeye, Master Marksman" to 2)).error shouldBe null
                game.resolveStack()

                withClue("attacking taps him, which is 'becomes tapped'") {
                    game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                }
                game.answerYesNo(true)
                game.chooseNumber(1)

                chooseMode(game, "Net")
                val bearId = game.findPermanent("Test Bear")!!
                if (game.getPendingDecision() is ChooseTargetsDecision) {
                    game.selectTargets(listOf(bearId))
                }
                game.resolveStack()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                val block = game.declareBlockers(
                    mapOf("Test Bear" to listOf("Hawkeye, Master Marksman"))
                )
                withClue("the netted creature can't block this turn") {
                    block.error shouldNotBe null
                }
            }
        }

        context("the printed evasion") {

            test("reach blocks the flier, first strike kills it before it hits back") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hawkeye, Master Marksman", summoningSickness = false)
                    .withCardOnBattlefield(2, "Test Flier", summoningSickness = false)
                    .withActivePlayer(2)
                    .withPriorityPlayer(2)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Test Flier" to 1)).error shouldBe null
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                withClue("reach — a grounded archer with no reach could not block a flier") {
                    game.declareBlockers(
                        mapOf("Hawkeye, Master Marksman" to listOf("Test Flier"))
                    ).error shouldBe null
                }

                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() != null) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("first strike — the 2/2 flier dies before dealing its damage") {
                    game.findPermanent("Test Flier") shouldBe null
                }
                withClue("…so Hawkeye takes none of it and survives") {
                    game.findPermanent("Hawkeye, Master Marksman") shouldNotBe null
                    game.getLifeTotal(1) shouldBe 20
                }
            }
        }
    }
}
