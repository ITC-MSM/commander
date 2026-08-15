package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Page, Loose Leaf (Secrets of Strixhaven).
 *
 * {T}: Add {C}.
 * Grandeur — Discard another card named Page, Loose Leaf: Reveal cards from the top of your library
 * until you reveal an instant or sorcery card. Put that card into your hand and the rest on the bottom
 * of your library in a random order.
 *
 * Grandeur is an ability word (no rules meaning); the ability is a normal activated ability with a
 * "discard a card named Page, Loose Leaf" cost. Modeled with GatherUntilMatch (until an instant or
 * sorcery) + two filtered MoveCollections (match → hand; the rest → bottom of library, random order).
 */
class PageLooseLeafScenarioTest : ScenarioTestBase() {

    private val manaAbilityId =
        cardRegistry.getCard("Page, Loose Leaf")!!.activatedAbilities[0].id
    private val grandeurAbilityId =
        cardRegistry.getCard("Page, Loose Leaf")!!.activatedAbilities[1].id

    init {
        context("Page, Loose Leaf") {

            test("{T}: Add {C} taps Page for one colorless mana") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Page, Loose Leaf", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val page = game.findPermanent("Page, Loose Leaf")!!
                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = page, abilityId = manaAbilityId)
                )
                withClue("Tapping Page for mana should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                withClue("Page produced one colorless mana") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.colorless shouldBe 1
                }
            }

            test("Grandeur: discard another Page, reveal until an instant/sorcery, take it, rest to bottom") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Page, Loose Leaf", summoningSickness = false)
                    .withCardInHand(1, "Page, Loose Leaf")   // the discard fodder ("another")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Library: two lands on top, then the lone instant, then more cards.
                builder = builder.withCardInLibrary(1, "Forest")
                builder = builder.withCardInLibrary(1, "Mountain")
                builder = builder.withCardInLibrary(1, "Lightning Bolt") // the instant to find
                builder = builder.withCardInLibrary(1, "Plains")
                builder = builder.withCardInLibrary(1, "Island")
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val page = game.findPermanent("Page, Loose Leaf")!!
                val libraryBefore = game.state.getLibrary(game.player1Id).size
                val pageInHand = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Page, Loose Leaf"
                }

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = page,
                        abilityId = grandeurAbilityId,
                        costPayment = AdditionalCostPayment(discardedCards = listOf(pageInHand)),
                    )
                )
                withClue("Activating Grandeur should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("The discarded Page copy goes to the graveyard") {
                    game.isInGraveyard(1, "Page, Loose Leaf").shouldBeTrue()
                    game.isInHand(1, "Page, Loose Leaf").shouldBeFalse()
                }
                withClue("The revealed instant (Lightning Bolt) ends up in hand") {
                    game.isInHand(1, "Lightning Bolt").shouldBeTrue()
                }
                withClue("Lightning Bolt is no longer in the library") {
                    game.state.getLibrary(game.player1Id).none { id ->
                        game.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Lightning Bolt"
                    }.shouldBeTrue()
                }
                withClue("The library shrinks by exactly one card (only the instant left it)") {
                    game.state.getLibrary(game.player1Id).size shouldBe libraryBefore - 1
                }
                withClue("The two lands revealed before the instant are now at the bottom of the library") {
                    val lib = game.state.getLibrary(game.player1Id)
                    fun name(id: com.wingedsheep.sdk.model.EntityId) =
                        game.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name
                    val bottomTwo = lib.takeLast(2).mapNotNull { name(it) }.toSet()
                    bottomTwo shouldBe setOf("Forest", "Mountain")
                }
            }

            test("Grandeur is unaffordable without another Page in hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Page, Loose Leaf", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Lightning Bolt")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val page = game.findPermanent("Page, Loose Leaf")!!
                val grandeur = game.getLegalActions(1).firstOrNull { la ->
                    val a = la.action
                    a is ActivateAbility && a.sourceId == page && a.abilityId == grandeurAbilityId
                }
                withClue("With no other Page in hand, Grandeur must not be an affordable legal action") {
                    (grandeur?.isAffordable ?: false).shouldBeFalse()
                }
            }
        }
    }
}
