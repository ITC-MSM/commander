package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Infinite Coursework (FRA #31) — {2}{U} Enchantment — Aura.
 * "Enchant creature. When this Aura enters, tap enchanted creature. It becomes unprepared.
 *  Enchanted creature loses all abilities and doesn't untap during its controller's untap step."
 */
class InfiniteCourseworkScenarioTest : ScenarioTestBase() {

    private fun TestGame.prepareCopies(): List<EntityId> =
        state.getExile(player1Id).filter { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == "Woodwork Prodigy" && e.get<PreparedSpellCopyComponent>() != null
        }

    init {
        context("Infinite Coursework") {

            test("taps and unprepares a prepared creature, strips its abilities, and keeps it tapped") {
                var b = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Woodwork Prodigy")
                    .withCardInHand(2, "Infinite Coursework")
                    .withLandsOnBattlefield(2, "Island", 3)
                    .withActivePlayer(2)
                    .inPhase(Phase.ENDING, Step.END)
                repeat(6) { b = b.withCardInLibrary(1, "Forest") }
                repeat(6) { b = b.withCardInLibrary(2, "Island") }
                val game = b.build()
                val prodigy = game.findPermanent("Woodwork Prodigy")!!

                // Prodigy prepares itself in our upkeep.
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                game.state.getEntity(prodigy)?.get<PreparedComponent>() shouldNotBe null
                game.prepareCopies().size shouldBe 1

                // Through to the opponent's main phase, where they enchant the Prodigy.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.state.activePlayerId shouldBe game.player2Id
                game.castSpell(2, "Infinite Coursework", prodigy).error shouldBe null
                game.resolveStack()

                withClue("the ETB tapped it and unprepared it") {
                    game.state.getEntity(prodigy)?.has<TappedComponent>() shouldBe true
                    game.state.getEntity(prodigy)?.get<PreparedComponent>() shouldBe null
                    game.prepareCopies().shouldBeEmpty()
                }

                withClue("its upkeep trigger is gone with its abilities, and it stays tapped") {
                    game.passUntilPhase(Phase.ENDING, Step.END)
                    game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                    game.state.activePlayerId shouldBe game.player1Id
                    game.state.stack.shouldBeEmpty()
                    game.state.getEntity(prodigy)?.get<PreparedComponent>() shouldBe null
                    game.state.getEntity(prodigy)?.has<TappedComponent>() shouldBe true
                }
            }

            test("the enchanted creature loses keyword abilities") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(2, "Serra Angel")
                    .withCardInHand(1, "Infinite Coursework")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
                val angel = game.findPermanent("Serra Angel")!!

                game.castSpell(1, "Infinite Coursework", angel).error shouldBe null
                game.resolveStack()

                game.state.getEntity(angel)?.has<TappedComponent>() shouldBe true
                game.state.projectedState.hasKeyword(angel, Keyword.FLYING) shouldBe false
                game.state.projectedState.hasKeyword(angel, Keyword.VIGILANCE) shouldBe false
            }
        }
    }
}
