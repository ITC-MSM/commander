package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario test for Mm'menon, the Right Hand ({3}{U}{U}, 3/4 Legendary Creature — Jellyfish Advisor):
 *   Flying
 *   You may look at the top card of your library any time.
 *   You may cast artifact spells from the top of your library.
 *   Artifacts you control have "{T}: Add {U}. Spend this mana only to cast a spell from anywhere
 *     other than your hand."
 *
 * Anchors the integration claim that the unit
 * [com.wingedsheep.engine.handlers.mana.ManaSpendRestrictionOnlyToCastSpellsFromNonHandTest] makes
 * at the [com.wingedsheep.engine.mechanics.mana.ManaPool] level: that
 * [com.wingedsheep.sdk.scripting.effects.ManaRestriction.CastFromNonHandOnly] mana, sourced through
 * Mm'menon's granted artifact ability, funds a top-of-library cast but does NOT fund a hand cast of
 * the same spell. The symmetry depends on `SpellPaymentContext.isFromHand` being populated by
 * [com.wingedsheep.engine.handlers.actions.spell.CastSpellHandler] (true for hand casts, false for
 * the non-hand paths) and the source's restriction being honoured during enumeration.
 */
class MmmenonTheRightHandTest : ScenarioTestBase() {

    init {
        context("Mm'menon, the Right Hand — restricted artifact mana funds non-hand casts only") {

            test("top-of-library artifact cast is legal when only restricted-mana sources are available") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Mm'menon, the Right Hand")
                    .withCardOnBattlefield(1, "Cryogen Relic")
                    .withCardOnBattlefield(1, "Cryogen Relic")
                    .withCardInLibrary(1, "Cryogen Relic")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val topOfLibrary = game.state.getLibrary(game.player1Id).first()
                withClue("Top of library must be the Cryogen Relic we seeded") {
                    game.state.getEntity(topOfLibrary)?.get<CardComponent>()?.name shouldBe "Cryogen Relic"
                }

                val legalActions = game.getLegalActions(1)

                val topOfLibraryCast = legalActions.find { info ->
                    info.actionType == "CastSpell" &&
                        info.sourceZone == "LIBRARY" &&
                        (info.action as? CastSpell)?.cardId == topOfLibrary
                }
                withClue(
                    "Top-of-library Cryogen Relic should be castable using Mm'menon's granted {U} " +
                        "mana from the two on-battlefield artifacts (no other mana exists). " +
                        "If this fails, the granted ability isn't reaching artifacts you control " +
                        "or CastFromNonHandOnly mana is being filtered out of non-hand cast " +
                        "enumeration."
                ) {
                    topOfLibraryCast shouldNotBe null
                }
            }

            test("hand-cast of the same spell is NOT legal — restricted mana stays out of hand-cast enumeration") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Mm'menon, the Right Hand")
                    .withCardOnBattlefield(1, "Cryogen Relic")
                    .withCardOnBattlefield(1, "Cryogen Relic")
                    .withCardInHand(1, "Cryogen Relic")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handCryogenRelic = game.state.getHand(game.player1Id).single { entityId ->
                    game.state.getEntity(entityId)?.get<CardComponent>()?.name == "Cryogen Relic"
                }

                val legalActions = game.getLegalActions(1)

                val handCast = legalActions.find { info ->
                    info.actionType == "CastSpell" &&
                        (info.action as? CastSpell)?.cardId == handCryogenRelic
                }
                withClue(
                    "Hand Cryogen Relic must NOT be castable from only CastFromNonHandOnly mana — " +
                        "if it is, the restriction is leaking past hand-cast enumeration."
                ) {
                    handCast shouldBe null
                }
            }

            test("executing the top-of-library cast taps the artifacts and puts the spell on the stack") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Mm'menon, the Right Hand")
                    .withCardOnBattlefield(1, "Cryogen Relic")
                    .withCardOnBattlefield(1, "Cryogen Relic")
                    .withCardInLibrary(1, "Cryogen Relic")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val topOfLibrary = game.state.getLibrary(game.player1Id).first()

                val result = game.execute(CastSpell(game.player1Id, topOfLibrary))
                withClue("CastSpell from top of library should succeed: ${result.error}") {
                    result.error shouldBe null
                }

                withClue("Cryogen Relic should be on the stack after the cast") {
                    game.state.stack.contains(topOfLibrary) shouldBe true
                }
                withClue("Cryogen Relic should no longer be in the library after going to the stack") {
                    game.state.getLibrary(game.player1Id).contains(topOfLibrary) shouldBe false
                }

                // Both artifacts must have been tapped to produce the {1}{U} payment — the only
                // mana sources available were the two CastFromNonHandOnly producers.
                val onBattlefieldRelics = game.findPermanents("Cryogen Relic")
                val tappedRelics = onBattlefieldRelics.count { entityId ->
                    game.state.getEntity(entityId)
                        ?.has<com.wingedsheep.engine.state.components.battlefield.TappedComponent>() == true
                }
                withClue("Both on-battlefield Cryogen Relics should be tapped to fund the cast") {
                    tappedRelics shouldBe 2
                }
            }
        }
    }
}
