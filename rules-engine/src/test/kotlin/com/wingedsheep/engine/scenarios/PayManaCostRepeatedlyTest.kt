package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostRepeatedlyEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Engine-level tests for [PayManaCostRepeatedlyEffect] — "you may pay {1} up to three times"
 * (Hawkeye, Master Marksman) and its uncapped "any number of times" sibling.
 *
 * These cover the *primitive*, not the card: the affordability-aware cap, the color-awareness of
 * that cap, the decline paths (the wrapper's yes/no and an unaffordable cost), the count surviving
 * the CR 603.12 reflexive stack round-trip, and the no-question shortcut when only one repetition
 * is possible. Hawkeye's own modal payoff is covered in `HawkeyeMasterMarksmanScenarioTest`.
 */
class PayManaCostRepeatedlyTest : ScenarioTestBase() {

    /**
     * "When this enters, you may pay {1} up to three times. When you do, you gain that much life."
     * Life is the readout: it equals the number of repetitions, and it is produced by the
     * *reflexive* half, so a nonzero gain proves both the payment and the pipeline hand-off.
     */
    private val repeater = card("Repeating Ritualist") {
        manaCost = "{1}"
        typeLine = "Creature — Human Wizard"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = ReflexiveTriggerEffect(
                action = Effects.PayRepeatedly("{1}", upTo = 3),
                optional = true,
                reflexiveEffect = Effects.GainLife(DynamicAmounts.timesPaid())
            )
            description = "When this enters, you may pay {1} up to three times. " +
                "When you do, you gain that much life."
        }
    }

    /**
     * The same shape with a *colored* repetition unit — `{G}` three times is `{G}{G}{G}`. Its own
     * cost is `{R}{R}` so casting it can only consume Mountains, leaving the Forest count on the
     * battlefield the sole thing that can bound the repetitions.
     */
    private val greenRepeater = card("Verdant Ritualist") {
        manaCost = "{R}{R}"
        typeLine = "Creature — Elf Wizard"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = ReflexiveTriggerEffect(
                action = Effects.PayRepeatedly("{G}", upTo = 3),
                optional = true,
                reflexiveEffect = Effects.GainLife(DynamicAmounts.timesPaid())
            )
            description = "When this enters, you may pay {G} up to three times. " +
                "When you do, you gain that much life."
        }
    }

    /**
     * The `Gate.MayPay` wrapper instead of the reflexive one — "you may pay {1} up to three times.
     * If you do, you gain that much life." The gate asks the yes/no, the effect asks the count, and
     * `then` reads the count out of the pipeline the payment published to.
     */
    private val gatedRepeater = card("Bartering Ritualist") {
        manaCost = "{1}"
        typeLine = "Creature — Human Wizard"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = GatedEffect(
                gate = Gate.MayPay(Effects.PayRepeatedly("{1}", upTo = 3)),
                then = Effects.GainLife(DynamicAmounts.timesPaid())
            )
            description = "When this enters, you may pay {1} up to three times. " +
                "If you do, you gain that much life."
        }
    }

    /** The uncapped wording: "any number of times". */
    private val uncappedRepeater = card("Unbounded Ritualist") {
        manaCost = "{1}"
        typeLine = "Creature — Human Wizard"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = ReflexiveTriggerEffect(
                action = Effects.PayRepeatedly("{1}"),
                optional = true,
                reflexiveEffect = Effects.GainLife(DynamicAmounts.timesPaid())
            )
            description = "When this enters, you may pay {1} any number of times. " +
                "When you do, you gain that much life."
        }
    }

    /**
     * Cast [name] with [lands] untapped lands of [landName] on the battlefield and stop on
     * whatever the enters-trigger asks first. One land pays for the creature itself, so the
     * repetitions have `lands - 1` mana behind them.
     */
    private fun castAndStopOnTrigger(name: String, landName: String, lands: Int): TestGame {
        val game = scenario()
            .withPlayers("Player1", "Player2")
            .withCardInHand(1, name)
            .withLandsOnBattlefield(1, landName, lands)
            .withActivePlayer(1)
            .withPriorityPlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        game.castSpell(1, name).error shouldBe null
        if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
        game.resolveStack()
        return game
    }

    /** How many permanents named [name] are tapped right now. */
    private fun tappedCount(game: TestGame, name: String): Int =
        game.findPermanents(name).count {
            game.state.getEntity(it)
                ?.get<com.wingedsheep.engine.state.components.battlefield.TappedComponent>() != null
        }

    /** Say yes to the wrapper's "you may …" and return the repeat-count question. */
    private fun acceptAndReadCountQuestion(game: TestGame): ChooseNumberDecision {
        game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
        game.answerYesNo(true)
        return game.getPendingDecision() as? ChooseNumberDecision
            ?: error("expected the repeat-count question; got ${game.getPendingDecision()}")
    }

    init {
        cardRegistry.register(repeater)
        cardRegistry.register(greenRepeater)
        cardRegistry.register(gatedRepeater)
        cardRegistry.register(uncappedRepeater)

        context("how many repetitions are offered") {

            test("the cap is the printed maximum when mana is plentiful") {
                val game = castAndStopOnTrigger("Repeating Ritualist", "Mountain", 6)
                val question = acceptAndReadCountQuestion(game)

                withClue("declining is the yes/no's job, so the count starts at one") {
                    question.minValue shouldBe 1
                }
                question.maxValue shouldBe 3
            }

            test("the cap drops to what the payer can actually afford") {
                // 3 lands: one pays for the creature, so only two repetitions are reachable.
                val game = castAndStopOnTrigger("Repeating Ritualist", "Mountain", 3)
                acceptAndReadCountQuestion(game).maxValue shouldBe 2
            }

            test("the cap is color-aware — {G} three times needs three green, and is paid in green") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Verdant Ritualist")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Verdant Ritualist").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("the three Mountains left over can't pay a single {G} — only the Forests count") {
                    acceptAndReadCountQuestion(game).maxValue shouldBe 2
                }

                game.chooseNumber(2)
                game.resolveStack()

                withClue("both repetitions were paid, so the reflexive half saw two") {
                    game.getLifeTotal(1) shouldBe 22
                }
                withClue("{G}{G} came out of the Forests") {
                    tappedCount(game, "Forest") shouldBe 2
                }
                withClue("only the two Mountains the creature's own {R}{R} used are tapped") {
                    tappedCount(game, "Mountain") shouldBe 2
                }
            }

            test("the cap counts only mana the auto-tapper will actually spend, not Treasures") {
                // canPay() counts a Treasure's sacrifice-self ability toward affordability, but the
                // auto-tap solver refuses to sacrifice permanents on the player's behalf — and the
                // auto-tapper is what pays here. Capping with canPay would offer three repetitions
                // and then error on the third. One Mountain goes to the creature itself.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Repeating Ritualist")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardOnBattlefield(1, "Treasure")
                    .withCardOnBattlefield(1, "Treasure")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Repeating Ritualist").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("two untapped Mountains behind the payment; the two Treasures don't count") {
                    acceptAndReadCountQuestion(game).maxValue shouldBe 2
                }

                game.chooseNumber(2)
                game.resolveStack()

                withClue("the offered count was payable — no mid-resolution failure") {
                    game.getLifeTotal(1) shouldBe 22
                }
                withClue("and nothing was silently sacrificed to get there") {
                    game.findPermanents("Treasure").size shouldBe 2
                }
            }

            test("'any number of times' is bounded by the mana on hand, not by a printed cap") {
                val game = castAndStopOnTrigger("Unbounded Ritualist", "Mountain", 5)
                acceptAndReadCountQuestion(game).maxValue shouldBe 4
            }

            test("a single reachable repetition is paid with no question at all") {
                // 2 lands: one for the creature, exactly one left — nothing to decide.
                val game = castAndStopOnTrigger("Repeating Ritualist", "Mountain", 2)
                game.answerYesNo(true)

                withClue("no repeat-count prompt is raised") {
                    (game.getPendingDecision() is ChooseNumberDecision) shouldBe false
                }
                game.resolveStack()
                withClue("the one repetition was paid and the reflexive half saw it") {
                    game.getLifeTotal(1) shouldBe 21
                }
            }
        }

        context("paying, declining, and what the reflexive half reads") {

            test("the repetition count survives the reflexive stack round-trip") {
                val game = castAndStopOnTrigger("Repeating Ritualist", "Mountain", 6)
                acceptAndReadCountQuestion(game)
                game.chooseNumber(2)
                game.resolveStack()

                withClue("'you gain that much life' reads the count the action stored") {
                    game.getLifeTotal(1) shouldBe 22
                }
                withClue("one land paid for the creature, two paid for the repetitions") {
                    game.state.getBattlefield()
                        .mapNotNull { id -> game.state.getEntity(id) }
                        .count { it.get<com.wingedsheep.engine.state.components.battlefield.TappedComponent>() != null }
                        .shouldBe(3)
                }
            }

            test("declining the wrapper's may-question pays nothing and fires no reflexive half") {
                val game = castAndStopOnTrigger("Repeating Ritualist", "Mountain", 6)
                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                game.getLifeTotal(1) shouldBe 20
                withClue("nothing beyond the creature's own cost was tapped") {
                    game.state.getBattlefield()
                        .mapNotNull { id -> game.state.getEntity(id) }
                        .count { it.get<com.wingedsheep.engine.state.components.battlefield.TappedComponent>() != null }
                        .shouldBe(1)
                }
            }

            test("Gate.MayPay wraps it too — 'if you do' reads the same count") {
                val game = castAndStopOnTrigger("Bartering Ritualist", "Mountain", 6)
                acceptAndReadCountQuestion(game)
                game.chooseNumber(3)
                game.resolveStack()

                withClue("the gate's `then` reads the count the cost published") {
                    game.getLifeTotal(1) shouldBe 23
                }
                withClue("one land for the creature, three for the repetitions") {
                    tappedCount(game, "Mountain") shouldBe 4
                }
            }

            test("Gate.MayPay offers no 'yes' when not even one repetition is affordable") {
                // Exactly enough for the creature: the gate must fall through to `otherwise`
                // rather than offering a payment whose cost then errors out.
                val game = castAndStopOnTrigger("Bartering Ritualist", "Mountain", 1)

                withClue("no impossible yes/no is raised") {
                    (game.getPendingDecision() is YesNoDecision) shouldBe false
                }
                game.getLifeTotal(1) shouldBe 20
            }

            test("a payer who can't afford one repetition is never asked") {
                // Exactly enough for the creature and nothing left over.
                val game = castAndStopOnTrigger("Repeating Ritualist", "Mountain", 1)

                withClue("the may-question is suppressed, not raised and refused") {
                    (game.getPendingDecision() is YesNoDecision) shouldBe false
                }
                game.getLifeTotal(1) shouldBe 20
            }
        }

        context("the SDK shape") {

            test("the description reads as the printed wording") {
                PayManaCostRepeatedlyEffect(
                    com.wingedsheep.sdk.core.ManaCost.parse("{1}"), maxTimes = 3
                ).description shouldBe "Pay {1} up to three times"

                PayManaCostRepeatedlyEffect(
                    com.wingedsheep.sdk.core.ManaCost.parse("{2}{R}")
                ).description shouldBe "Pay {2}{R} any number of times"
            }
        }
    }
}
