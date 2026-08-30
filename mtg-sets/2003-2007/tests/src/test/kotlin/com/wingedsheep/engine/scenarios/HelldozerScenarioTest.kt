package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.Helldozer
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Helldozer — Ravnica: City of Guilds #88, {3}{B}{B}{B} Creature — Zombie Giant 6/5
 *
 * "{B}{B}{B}, {T}: Destroy target land. If that land was nonbasic, untap this creature."
 *
 * The land always dies; only the untap is conditional, and the untap is the whole card — it
 * refunds the {T} in the cost, so a Helldozer with enough black mana eats every nonbasic land on
 * the board. The failure mode this pins is the ordering: the conditional runs while the target is
 * still on the battlefield, because after the destroy the target no longer names a battlefield
 * permanent and the nonbasic test would silently answer "no". Both branches are asserted, since a
 * conditional picking the wrong side is invisible in the card snapshot.
 */
class HelldozerScenarioTest : ScenarioTestBase() {

    private val dozeAbility = Helldozer.activatedAbilities.single().id

    init {
        context("Helldozer") {

            test("destroying a nonbasic land untaps Helldozer, so the ability can run again") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Helldozer")
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withCardOnBattlefield(2, "Great Furnace") // nonbasic artifact land
                    .withCardOnBattlefield(2, "Vault of Whispers") // a second nonbasic land
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dozer = game.findPermanent("Helldozer")!!
                val furnace = game.findPermanent("Great Furnace")!!

                val first = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = dozer,
                        abilityId = dozeAbility,
                        targets = listOf(ChosenTarget.Permanent(furnace))
                    )
                )
                withClue("activation should succeed: ${first.error}") { first.error shouldBe null }
                game.resolveStack()

                withClue("the land is destroyed") {
                    game.isOnBattlefield("Great Furnace") shouldBe false
                    game.isInGraveyard(2, "Great Furnace") shouldBe true
                }
                withClue("nonbasic: Helldozer untapped itself") {
                    game.state.getEntity(dozer)?.has<TappedComponent>() shouldBe false
                }

                // The untap is what makes it repeatable — the second activation is legal in the
                // same turn because the {T} was refunded.
                val vault = game.findPermanent("Vault of Whispers")!!
                val second = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = dozer,
                        abilityId = dozeAbility,
                        targets = listOf(ChosenTarget.Permanent(vault))
                    )
                )
                withClue("second activation in the same turn should succeed: ${second.error}") {
                    second.error shouldBe null
                }
                game.resolveStack()
                withClue("the second nonbasic land is destroyed too") {
                    game.isInGraveyard(2, "Vault of Whispers") shouldBe true
                }
            }

            test("destroying a basic land leaves Helldozer tapped") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Helldozer")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dozer = game.findPermanent("Helldozer")!!
                val forest = game.findPermanent("Forest")!!

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = dozer,
                        abilityId = dozeAbility,
                        targets = listOf(ChosenTarget.Permanent(forest))
                    )
                )
                withClue("activation should succeed: ${activation.error}") { activation.error shouldBe null }
                game.resolveStack()

                withClue("a basic land dies just the same") {
                    game.isInGraveyard(2, "Forest") shouldBe true
                }
                withClue("basic: no untap, so Helldozer stays tapped") {
                    game.state.getEntity(dozer)?.has<TappedComponent>() shouldBe true
                }
            }
        }
    }
}
