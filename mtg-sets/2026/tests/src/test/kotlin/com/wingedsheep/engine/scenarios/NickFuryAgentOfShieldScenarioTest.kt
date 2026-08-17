package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
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
 *
 * The transform clause gets its own four cases, because this card was once withdrawn over it. It
 * must be **asked only of a double-faced card** — neither of a single-faced permanent nor when
 * nothing entered at all, since the printed text offers no choice in either case — it must actually
 * flip when accepted, and it must be **post-entry**: the permanent enters on its front face, its
 * enters-the-battlefield trigger fires on *that* face, and only then is it turned over. That is not
 * the same as entering transformed.
 */
class NickFuryAgentOfShieldScenarioTest : ScenarioTestBase() {

    private val heroRecruit = CardDefinition.creature(
        name = "Test Hero Recruit",
        manaCost = ManaCost.parse("{2}{W}"),
        subtypes = setOf(Subtype("Human"), Subtype("Hero")),
        power = 2,
        toughness = 2
    )

    // Deliberately not a Hero, Equipment or Vehicle — must never be selectable.
    private val plainBear = CardDefinition.creature(
        name = "Test Plain Bear",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2
    )

    private val flipHeroBack = CardDefinition.creature(
        name = "Test Flip Hero Back",
        manaCost = ManaCost.ZERO,
        subtypes = setOf(Subtype("Human"), Subtype("Hero")),
        power = 5,
        toughness = 5
    )

    /**
     * A second double-faced Hero, whose **front** face has an enters-the-battlefield trigger its
     * back face doesn't. Pins the printed order: the card is put onto the battlefield front face up
     * and its ETB trigger fires on that face, and only then is the transform offered — which is why
     * this clause is not the same thing as entering transformed.
     */
    private val etbFlipHeroBack = CardDefinition.creature(
        name = "Test ETB Flip Hero Back",
        manaCost = ManaCost.ZERO,
        subtypes = setOf(Subtype("Human"), Subtype("Hero")),
        power = 4,
        toughness = 4
    )

    private val etbFlipHeroFront: CardDefinition = CardDefinition.doubleFacedCreature(
        frontFace = CardDefinition.creature(
            name = "Test ETB Flip Hero Front",
            manaCost = ManaCost.parse("{2}{W}"),
            subtypes = setOf(Subtype("Human"), Subtype("Hero")),
            power = 1,
            toughness = 1,
            oracleText = "When this creature enters, draw a card.",
            script = CardScript.creature(
                TriggeredAbility.create(
                    trigger = EventPattern.ZoneChangeEvent(to = Zone.BATTLEFIELD),
                    binding = TriggerBinding.SELF,
                    effect = Effects.DrawCards(1)
                )
            )
        ),
        backFace = etbFlipHeroBack
    )

    /** A double-faced Hero — the only kind of card Nick Fury's transform clause may ask about. */
    private val flipHeroFront: CardDefinition = CardDefinition.doubleFacedCreature(
        frontFace = CardDefinition.creature(
            name = "Test Flip Hero Front",
            manaCost = ManaCost.parse("{2}{W}"),
            subtypes = setOf(Subtype("Human"), Subtype("Hero")),
            power = 1,
            toughness = 1
        ),
        backFace = flipHeroBack
    )

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

    /** The board every test here uses: Fury plus exactly the four pips his discounted cost needs. */
    private fun furyBoard() = scenario()
        .withPlayers("Player", "Opponent")
        .withCardOnBattlefield(1, "Nick Fury, Agent of S.H.I.E.L.D.", enteredThisTurn = true)
        .withLandsOnBattlefield(1, "Island", 1)
        .withLandsOnBattlefield(1, "Swamp", 1)
        .withLandsOnBattlefield(1, "Mountain", 1)
        .withLandsOnBattlefield(1, "Forest", 1)
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

    init {
        cardRegistry.register(heroRecruit)
        cardRegistry.register(plainBear)
        cardRegistry.register(flipHeroFront)
        cardRegistry.register(etbFlipHeroFront)

        context("Nick Fury, Agent of S.H.I.E.L.D.") {

            test("the power-up is discounted to {U}{B}{R}{G} the turn he enters") {
                val game = furyBoard()
                    .withCardInLibrary(1, "Test Hero Recruit")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .build()

                withClue("{W}{U}{B}{R}{G} less his own {W} is {U}{B}{R}{G}") {
                    game.getLegalActions(1)
                        .first { it.description.startsWith("Power-up —") }
                        .description.startsWith("Power-up — {U}{B}{R}{G}:") shouldBe true
                }
            }

            test("puts a chosen Hero onto the battlefield and bottoms the rest") {
                val game = furyBoard()
                    .withCardInLibrary(1, "Test Hero Recruit")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .build()

                val furyId = game.findPermanent("Nick Fury, Agent of S.H.I.E.L.D.")!!
                val librarySizeBefore = game.librarySize(1)

                game.activateFury() shouldBe true

                val hero = game.findCardsInLibrary(1, "Test Hero Recruit").single()
                game.selectCards(listOf(hero))
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

            test("never asks about transforming when the card it put onto the battlefield is single-faced") {
                val game = furyBoard()
                    .withCardInLibrary(1, "Test Hero Recruit")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .build()

                game.activateFury() shouldBe true

                val hero = game.findCardsInLibrary(1, "Test Hero Recruit").single()
                game.selectCards(listOf(hero))
                game.resolveStack()

                withClue("the printed card offers no choice here, so the engine must not raise one") {
                    game.getPendingDecision().shouldBeNull()
                }
                withClue("and the ability finished — nothing is left waiting on the stack") {
                    game.state.stack.isEmpty() shouldBe true
                    game.isOnBattlefield("Test Hero Recruit") shouldBe true
                }
            }

            test("offers the optional transform for a double-faced card, after it has entered") {
                val game = furyBoard()
                    .withCardInLibrary(1, "Test Flip Hero Front")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .build()

                game.activateFury() shouldBe true

                val flipHero = game.findCardsInLibrary(1, "Test Flip Hero Front").single()
                game.selectCards(listOf(flipHero))

                withClue("a double-faced card does get the prompt") {
                    game.hasPendingDecision() shouldBe true
                    (game.getPendingDecision() is com.wingedsheep.engine.core.YesNoDecision) shouldBe true
                }
                withClue("the prompt comes *after* the permanent entered on its front face") {
                    game.state.getEntity(flipHero)?.get<CardComponent>()?.name shouldBe "Test Flip Hero Front"
                    game.isOnBattlefield("Test Flip Hero Front") shouldBe true
                }

                game.answerYesNo(true)
                game.resolveStack()

                withClue("accepting turns it over in place — same entity, back face up") {
                    game.state.getEntity(flipHero)?.get<CardComponent>()?.name shouldBe "Test Flip Hero Back"
                    game.state.getEntity(flipHero)?.get<DoubleFacedComponent>()
                        ?.currentFace shouldBe DoubleFacedComponent.Face.BACK
                }
            }

            test("the front face's enters-the-battlefield trigger fires before the flip") {
                val game = furyBoard()
                    .withCardInLibrary(1, "Test ETB Flip Hero Front")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .build()

                game.activateFury() shouldBe true
                val handBefore = game.handSize(1)

                val flipHero = game.findCardsInLibrary(1, "Test ETB Flip Hero Front").single()
                game.selectCards(listOf(flipHero))

                withClue("the transform is offered only after the card has entered") {
                    (game.getPendingDecision() is com.wingedsheep.engine.core.YesNoDecision) shouldBe true
                }

                game.answerYesNo(true)
                game.resolveStack()

                withClue("it is on its back face now") {
                    game.state.getEntity(flipHero)?.get<CardComponent>()?.name shouldBe
                        "Test ETB Flip Hero Back"
                }
                withClue("but it entered front face up, so the front face's ETB trigger drew a card") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("declining the transform leaves the double-faced permanent on its front face") {
                val game = furyBoard()
                    .withCardInLibrary(1, "Test Flip Hero Front")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .build()

                game.activateFury() shouldBe true

                val flipHero = game.findCardsInLibrary(1, "Test Flip Hero Front").single()
                game.selectCards(listOf(flipHero))
                game.answerYesNo(false)
                game.resolveStack()

                withClue("declining is a legal answer and changes nothing") {
                    game.state.getEntity(flipHero)?.get<CardComponent>()?.name shouldBe "Test Flip Hero Front"
                    game.state.getEntity(flipHero)?.get<DoubleFacedComponent>()
                        ?.currentFace shouldBe DoubleFacedComponent.Face.FRONT
                }
                withClue("the rest of the ability still finished") {
                    game.graveyardSize(1) shouldBe 0
                    game.state.stack.isEmpty() shouldBe true
                }
            }

            test("resolves fully when nothing eligible is found and every prompt is declined") {
                val game = furyBoard()
                    .withCardInLibrary(1, "Test Plain Bear")
                    .withCardInLibrary(1, "Test Plain Bear")
                    .build()

                val furyId = game.findPermanent("Nick Fury, Agent of S.H.I.E.L.D.")!!

                game.activateFury() shouldBe true

                // Spelled out rather than looped over: nothing entered the battlefield, so the
                // printed card offers no transform choice here either, and a loop that *answered* a
                // stray prompt would hide exactly the regression this card was withdrawn for.
                withClue("the only question is which card to put onto the battlefield") {
                    (game.getPendingDecision() is com.wingedsheep.engine.core.SelectCardsDecision) shouldBe true
                }
                game.skipSelection()
                withClue("declining it must not raise a transform prompt — the collection is empty") {
                    game.getPendingDecision().shouldBeNull()
                }
                game.resolveStack()
                withClue("and the ability finished on its own") {
                    game.getPendingDecision().shouldBeNull()
                    game.state.stack.isEmpty() shouldBe true
                }

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
