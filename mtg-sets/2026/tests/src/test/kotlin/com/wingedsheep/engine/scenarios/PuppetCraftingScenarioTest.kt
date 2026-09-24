package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fra.cards.PuppetCrafting
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Puppet Crafting (Reality Fracture).
 *
 * Enchant artifact or non-Aura enchantment; the enchanted permanent becomes a 5/5 Construct creature
 * in addition to its other types; {4}{G} returns the card from the graveyard to hand.
 */
class PuppetCraftingScenarioTest : ScenarioTestBase() {

    private fun builder(): ScenarioBuilder {
        var b = scenario()
            .withPlayers("Player", "Opponent")
            .withCardInHand(1, "Puppet Crafting")
            .withLandsOnBattlefield(1, "Forest", 5)
            .withCardOnBattlefield(1, "Mind Stone")
            .withCardOnBattlefield(1, "Seal of Fire")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardAttachedTo(1, "Holy Strength", "Grizzly Bears")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        repeat(5) { b = b.withCardInLibrary(1, "Forest") }
        repeat(5) { b = b.withCardInLibrary(2, "Forest") }
        return b
    }

    init {
        context("Puppet Crafting") {

            test("an enchanted artifact is a 5/5 Construct artifact creature") {
                val game = builder().build()
                val stone = game.findPermanent("Mind Stone")!!

                game.castSpell(1, "Puppet Crafting", stone).error shouldBe null
                game.resolveStack()

                val projected = game.state.projectedState
                withClue("Mind Stone is a 5/5 Construct creature and still an artifact") {
                    projected.isCreature(stone) shouldBe true
                    projected.hasType(stone, "ARTIFACT") shouldBe true
                    projected.hasSubtype(stone, "Construct") shouldBe true
                    projected.getPower(stone) shouldBe 5
                    projected.getToughness(stone) shouldBe 5
                }
            }

            test("a non-Aura enchantment is a legal host and becomes a 5/5 Construct enchantment creature") {
                val game = builder().build()
                val seal = game.findPermanent("Seal of Fire")!!

                game.castSpell(1, "Puppet Crafting", seal).error shouldBe null
                game.resolveStack()

                val projected = game.state.projectedState
                projected.isCreature(seal) shouldBe true
                projected.hasType(seal, "ENCHANTMENT") shouldBe true
                projected.getPower(seal) shouldBe 5
                projected.getToughness(seal) shouldBe 5
            }

            test("an Aura and a plain creature are not legal hosts") {
                val game = builder().build()
                val holyStrength = game.findPermanent("Holy Strength")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("Holy Strength is an Aura") {
                    game.castSpell(1, "Puppet Crafting", holyStrength).error shouldNotBe null
                }
                withClue("Grizzly Bears is neither an artifact nor an enchantment") {
                    game.castSpell(1, "Puppet Crafting", bears).error shouldNotBe null
                }
            }

            test("{4}{G}: return it from the graveyard to hand") {
                var b = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Puppet Crafting")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { b = b.withCardInLibrary(1, "Forest") }
                repeat(5) { b = b.withCardInLibrary(2, "Forest") }
                val game = b.build()

                val card = game.findCardsInGraveyard(1, "Puppet Crafting").single()
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = card,
                        abilityId = PuppetCrafting.activatedAbilities.first().id,
                    )
                ).error shouldBe null
                game.resolveStack()

                game.isInHand(1, "Puppet Crafting") shouldBe true
                game.isInGraveyard(1, "Puppet Crafting") shouldBe false
            }
        }
    }
}
