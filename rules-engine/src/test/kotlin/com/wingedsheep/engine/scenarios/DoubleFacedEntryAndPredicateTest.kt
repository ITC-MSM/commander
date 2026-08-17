package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Double-faced cards (CR 712) as seen from *outside* the cast pipeline — the two engine facts a
 * card like Nick Fury, Agent of S.H.I.E.L.D. needs before "If it's a double-faced card, you may
 * transform it" can mean anything:
 *
 *  1. **[com.wingedsheep.sdk.scripting.predicates.CardPredicate.IsDoubleFaced]** — a whole-card
 *     layout characteristic, so it answers the same in every zone and keeps answering `true` once
 *     the permanent is sitting on its back face (its back face's own definition has no back face,
 *     so a naive re-derivation would flip to `false` exactly when it matters).
 *  2. **Face tracking on a non-cast battlefield entry** — the cast pipeline stamps a
 *     [DoubleFacedComponent] as a permanent spell resolves, but reanimation, library fetches and
 *     returns from exile all bypass that. Without one, `TransformEffect` finds nothing to turn over
 *     and is silently a no-op. `ZoneTransitionService.applyBattlefieldEntry` now stamps the front
 *     face for every such entry.
 *
 * Uses the shared "Test DFC Front" // "Test DFC Back" pair from `TestCards`, real reanimation
 * (Zombify) for the non-cast entry, and the "Transform Target Creature" test sorcery for the flip.
 */
class DoubleFacedEntryAndPredicateTest : ScenarioTestBase() {

    /**
     * A double-faced *land* with a printed "{T}: Transform this land", the shape of Balamb Garden,
     * SeeD Academy. Defined locally rather than reusing the real card because Balamb enters tapped
     * and its transform costs {5}{G}{U} plus its own {T}, which can't be paid the turn it's played —
     * this one exists purely to exercise the flip on the turn the land arrives.
     */
    private val testFlipLandBack = CardDefinition.artifact(
        name = "Test Flip Land Back",
        manaCost = ManaCost.ZERO,
        oracleText = ""
    )

    private val testFlipLandFront: CardDefinition = CardDefinition.doubleFacedPermanent(
        frontFace = CardDefinition(
            name = "Test Flip Land Front",
            manaCost = ManaCost.ZERO,
            typeLine = TypeLine(cardTypes = setOf(CardType.LAND)),
            oracleText = "{T}: Transform this land.",
            script = CardScript.permanent(
                ActivatedAbility(
                    cost = AbilityCost.Tap,
                    effect = TransformEffect(EffectTarget.Self)
                )
            )
        ),
        backFace = testFlipLandBack
    )

    private val flipLandAbilityId
        get() = cardRegistry.getCard("Test Flip Land Front")!!.script.activatedAbilities[0].id

    private fun TestGame.isDoubleFaced(entityId: EntityId, controllerId: EntityId): Boolean =
        PredicateEvaluator().matches(
            state,
            state.projectedState,
            entityId,
            Filters.DoubleFaced,
            PredicateContext(controllerId = controllerId)
        )

    init {
        cardRegistry.register(testFlipLandFront)

        context("CardPredicate.IsDoubleFaced") {

            test("matches a double-faced card in every zone and never a single-faced one") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Test DFC Front")
                    .withCardInLibrary(1, "Test DFC Front")
                    .withCardInGraveyard(1, "Test DFC Front")
                    .withCardOnBattlefield(1, "Test DFC Front")
                    .withCardInHand(1, "Centaur Courser")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val you = game.player1Id

                withClue("in hand") {
                    game.isDoubleFaced(game.findCardsInHand(1, "Test DFC Front").single(), you) shouldBe true
                }
                withClue("in the library") {
                    game.isDoubleFaced(game.findCardsInLibrary(1, "Test DFC Front").single(), you) shouldBe true
                }
                withClue("in the graveyard") {
                    game.isDoubleFaced(game.findCardsInGraveyard(1, "Test DFC Front").single(), you) shouldBe true
                }
                withClue("on the battlefield") {
                    game.isDoubleFaced(game.findPermanent("Test DFC Front")!!, you) shouldBe true
                }
                withClue("a single-faced card never matches, in hand") {
                    game.isDoubleFaced(game.findCardsInHand(1, "Centaur Courser").single(), you) shouldBe false
                }
                withClue("a single-faced card never matches, on the battlefield") {
                    game.isDoubleFaced(game.findPermanent("Centaur Courser")!!, you) shouldBe false
                }
            }

            test("still matches after the permanent has transformed to its back face") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test DFC Front")
                    .withCardInHand(1, "Transform Target Creature")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dfcId = game.findPermanent("Test DFC Front")!!
                game.castSpell(1, "Transform Target Creature", targetId = dfcId)
                game.resolveStack()

                withClue("the flip happened") {
                    game.state.getEntity(dfcId)?.get<CardComponent>()?.name shouldBe "Test DFC Back"
                }
                withClue("the back face's own definition has no back face — the flag must be carried, not re-derived") {
                    game.isDoubleFaced(dfcId, game.player1Id) shouldBe true
                }
            }
        }

        context("non-cast battlefield entry") {

            test("a double-faced card reanimated onto the battlefield is face-tracked and can transform") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Test DFC Front")
                    .withCardInHand(1, "Zombify")
                    .withCardInHand(1, "Transform Target Creature")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardInGraveyard = game.findCardsInGraveyard(1, "Test DFC Front").single()

                withClue("a card sitting in the graveyard is not face-tracked yet") {
                    game.state.getEntity(cardInGraveyard)?.get<DoubleFacedComponent>().shouldBeNull()
                }

                game.castSpellTargetingGraveyardCard(1, "Zombify", listOf(cardInGraveyard))
                game.resolveStack()

                val permanentId = game.findPermanent("Test DFC Front")
                permanentId.shouldNotBeNull()

                val dfc = game.state.getEntity(permanentId)?.get<DoubleFacedComponent>()
                withClue("the reanimated permanent carries front-face tracking — without it, transforming is a no-op") {
                    dfc.shouldNotBeNull()
                    dfc.frontCardDefinitionId shouldBe "Test DFC Front"
                    dfc.backCardDefinitionId shouldBe "Test DFC Back"
                    dfc.currentFace shouldBe DoubleFacedComponent.Face.FRONT
                }

                game.castSpell(1, "Transform Target Creature", targetId = permanentId)
                game.resolveStack()

                withClue("and it really turns over") {
                    game.state.getEntity(permanentId)?.get<CardComponent>()?.name shouldBe "Test DFC Back"
                    game.state.getEntity(permanentId)?.get<DoubleFacedComponent>()
                        ?.currentFace shouldBe DoubleFacedComponent.Face.BACK
                }
            }

            test("a single-faced card entering the same way is left untracked") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Centaur Courser")
                    .withCardInHand(1, "Zombify")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardInGraveyard = game.findCardsInGraveyard(1, "Centaur Courser").single()
                game.castSpellTargetingGraveyardCard(1, "Zombify", listOf(cardInGraveyard))
                game.resolveStack()

                val permanentId = game.findPermanent("Centaur Courser")
                permanentId.shouldNotBeNull()
                withClue("nothing to turn over — no face tracking is invented") {
                    game.state.getEntity(permanentId)?.get<DoubleFacedComponent>().shouldBeNull()
                }
            }
        }

        context("a land played from hand") {

            // Playing a land is a special action that bypasses ZoneTransitionService entirely, so
            // stamping the front face there wasn't enough — PlayLandHandler needs its own call.
            // These tests deliberately *play* the land rather than using withCardOnBattlefield,
            // which would prove nothing: the scenario fixture stamps DoubleFacedComponent itself
            // for anything it places on the battlefield.

            test("a double-faced land played from hand is face-tracked and really transforms") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Test Flip Land Front")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val landId = game.findCardsInHand(1, "Test Flip Land Front").single()
                game.execute(PlayLand(game.player1Id, landId)).error shouldBe null

                val dfc = game.state.getEntity(landId)?.get<DoubleFacedComponent>()
                withClue("the played land carries front-face tracking (CR 712.14)") {
                    dfc.shouldNotBeNull()
                    dfc.frontCardDefinitionId shouldBe "Test Flip Land Front"
                    dfc.backCardDefinitionId shouldBe "Test Flip Land Back"
                    dfc.currentFace shouldBe DoubleFacedComponent.Face.FRONT
                }

                game.execute(ActivateAbility(game.player1Id, landId, flipLandAbilityId))
                game.resolveStack()

                withClue("'Transform this land' is no longer a silent no-op") {
                    game.state.getEntity(landId)?.get<CardComponent>()?.name shouldBe
                        "Test Flip Land Back"
                    game.state.getEntity(landId)?.get<DoubleFacedComponent>()
                        ?.currentFace shouldBe DoubleFacedComponent.Face.BACK
                }
                withClue("and it is still a double-faced card on its back face") {
                    game.isDoubleFaced(landId, game.player1Id) shouldBe true
                }
            }

            test("the shipped case — Balamb Garden, SeeD Academy — is face-tracked when played") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Balamb Garden, SeeD Academy")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val landId = game.findCardsInHand(1, "Balamb Garden, SeeD Academy").single()
                game.execute(PlayLand(game.player1Id, landId)).error shouldBe null

                val dfc = game.state.getEntity(landId)?.get<DoubleFacedComponent>()
                withClue("without this its printed '{5}{G}{U}, {T}: Transform this land' does nothing") {
                    dfc.shouldNotBeNull()
                    dfc.frontCardDefinitionId shouldBe "Balamb Garden, SeeD Academy"
                    dfc.backCardDefinitionId shouldBe "Balamb Garden, Airborne"
                    dfc.currentFace shouldBe DoubleFacedComponent.Face.FRONT
                }
            }

            test("a single-faced land played from hand is left untracked") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val landId = game.findCardsInHand(1, "Forest").single()
                game.execute(PlayLand(game.player1Id, landId)).error shouldBe null

                game.state.getEntity(landId)?.get<DoubleFacedComponent>().shouldBeNull()
            }
        }

        context("a token copy of a double-faced permanent") {

            // CR 111.1: a token is not a card, and layout is not a copiable value (CR 707.2). So a
            // token copy of a double-faced permanent is a double-faced *token* (CR 707.8a) — it can
            // still be turned over, but it must never answer a "double-faced card" question.
            test("is a double-faced token that transforms, but is not a double-faced card") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test DFC Front")
                    .withCardInHand(1, "Test Token Copy")
                    .withCardInHand(1, "Transform Target Creature")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val original = game.findPermanent("Test DFC Front")!!
                game.castSpell(1, "Test Token Copy", targetId = original)
                game.resolveStack()

                val token = game.findPermanents("Test DFC Front")
                    .single { game.state.getEntity(it)?.get<TokenComponent>() != null }

                withClue("the printed card is double-faced; its token copy is not a card at all") {
                    game.isDoubleFaced(original, game.player1Id) shouldBe true
                    game.isDoubleFaced(token, game.player1Id) shouldBe false
                }

                game.castSpell(1, "Transform Target Creature", targetId = token)
                game.resolveStack()

                withClue("it is still a double-faced token and turns over (CR 707.8a / 712.9)") {
                    game.state.getEntity(token)?.get<CardComponent>()?.name shouldBe "Test DFC Back"
                    game.state.getEntity(token)?.get<DoubleFacedComponent>()
                        ?.currentFace shouldBe DoubleFacedComponent.Face.BACK
                }
                withClue("and turning it over doesn't make it a card either") {
                    game.isDoubleFaced(token, game.player1Id) shouldBe false
                }
            }
        }
    }
}
