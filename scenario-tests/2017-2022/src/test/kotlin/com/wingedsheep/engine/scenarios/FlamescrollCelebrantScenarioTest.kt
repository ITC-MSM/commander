package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.player.CantActivateLoyaltyAbilitiesComponent
import com.wingedsheep.engine.state.components.player.CantCastSpellsComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

/**
 * Scenario tests for Flamescroll Celebrant // Revel in Silence — a modal double-faced card.
 *
 * Front — Flamescroll Celebrant ({1}{R}, 2/1 Human Shaman):
 *   - "Whenever an opponent activates an ability that isn't a mana ability, this creature deals
 *     1 damage to that player."
 *   - "{1}{R}: This creature gets +2/+0 until end of turn."
 * Back — Revel in Silence ({W}{W} Instant):
 *   - "Your opponents can't cast spells or activate planeswalkers' loyalty abilities this turn.
 *     Exile Revel in Silence."
 *
 * Exercises the new MODAL_DFC layout, the OpponentActivatesAbility trigger, and the
 * CantActivateLoyaltyAbilities restriction.
 */
class FlamescrollCelebrantScenarioTest : ScenarioTestBase() {

    // A creature with a cheap, non-mana activated ability the opponent can activate.
    private val testActivator = card("Test Activator") {
        manaCost = "{1}"
        typeLine = "Creature — Construct"
        power = 1
        toughness = 1
        activatedAbility {
            cost = Costs.Mana("{0}")
            effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        }
    }

    // A minimal planeswalker with one loyalty ability, for the loyalty-restriction tests.
    private val testWalker = card("Test Walker") {
        manaCost = "{2}"
        typeLine = "Legendary Planeswalker — Tester"
        startingLoyalty = 3
        loyaltyAbility(1) {
            effect = Effects.GainLife(1)
        }
    }

    init {
        cardRegistry.register(testActivator)
        cardRegistry.register(testWalker)

        context("Front face — Flamescroll Celebrant (creature)") {
            test("casting the primary face resolves a 2/1 creature") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Flamescroll Celebrant")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Flamescroll Celebrant")
                withClue("Casting the creature face should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val flamescroll = game.findPermanent("Flamescroll Celebrant")
                withClue("Flamescroll Celebrant should be on the battlefield") {
                    flamescroll shouldNotBe null
                }
                val info = game.getClientState(1).cards[flamescroll]
                withClue("Flamescroll Celebrant is a 2/1") {
                    info!!.power shouldBe 2
                    info.toughness shouldBe 1
                }
            }

            test("the {1}{R} ability gives +2/+0 until end of turn") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Flamescroll Celebrant")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val flamescroll = game.findPermanent("Flamescroll Celebrant")!!
                val pump = cardRegistry.getCard("Flamescroll Celebrant")!!.script.activatedAbilities[0]

                val result = game.execute(ActivateAbility(game.player1Id, flamescroll, pump.id))
                withClue("Activating the pump should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                val info = game.getClientState(1).cards[flamescroll]
                withClue("Flamescroll Celebrant should be 4/1 after +2/+0") {
                    info!!.power shouldBe 4
                    info.toughness shouldBe 1
                }
            }
        }

        context("Trigger — opponent activates a non-mana ability") {
            test("deals 1 damage to the opponent who activated the ability") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Flamescroll Celebrant")
                    .withCardOnBattlefield(2, "Test Activator")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val activator = game.findPermanent("Test Activator")!!
                val ability = cardRegistry.getCard("Test Activator")!!.script.activatedAbilities[0]

                val result = game.execute(ActivateAbility(game.player2Id, activator, ability.id))
                withClue("Opponent activating their ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Flamescroll should have dealt 1 damage to the activating opponent (20 -> 19)") {
                    game.getLifeTotal(2) shouldBe 19
                }
                withClue("Flamescroll's controller takes no damage") {
                    game.getLifeTotal(1) shouldBe 20
                }
            }

            test("does NOT trigger off the controller's own ability activation") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Flamescroll Celebrant")
                    .withCardOnBattlefield(1, "Test Activator")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val activator = game.findPermanent("Test Activator")!!
                val ability = cardRegistry.getCard("Test Activator")!!.script.activatedAbilities[0]

                game.execute(ActivateAbility(game.player1Id, activator, ability.id))
                game.resolveStack()

                withClue("No self-ping when the controller activates their own ability") {
                    game.getLifeTotal(1) shouldBe 20
                }
            }
        }

        context("Back face — Revel in Silence (instant)") {
            test("casting the back face restricts opponents and exiles itself") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Flamescroll Celebrant")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = game.findCardsInHand(1, "Flamescroll Celebrant").first()

                // faceIndex = 0 casts the back face (Revel in Silence).
                val cast = game.execute(CastSpell(game.player1Id, cardId, faceIndex = 0))
                withClue("Casting Revel in Silence should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Revel in Silence exiles itself, not into the graveyard") {
                    game.state.getExile(game.player1Id).contains(cardId) shouldBe true
                    game.state.getGraveyard(game.player1Id).contains(cardId) shouldBe false
                }
                withClue("The opponent can't cast spells this turn") {
                    game.state.getEntity(game.player2Id)?.has<CantCastSpellsComponent>() shouldBe true
                }
                withClue("The opponent can't activate loyalty abilities this turn") {
                    game.state.getEntity(game.player2Id)?.has<CantActivateLoyaltyAbilitiesComponent>() shouldBe true
                }
            }
        }

        context("Loyalty restriction enforcement") {
            test("a loyalty ability is activatable without the restriction") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(2, "Test Walker")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val walker = game.findPermanent("Test Walker")!!
                val loyalty = cardRegistry.getCard("Test Walker")!!.script.activatedAbilities[0]

                val result = game.execute(ActivateAbility(game.player2Id, walker, loyalty.id))
                withClue("Loyalty ability should activate normally with no restriction: ${result.error}") {
                    result.error shouldBe null
                }
            }

            test("the restriction blocks loyalty-ability activation") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(2, "Test Walker")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Apply the restriction the way Revel in Silence would.
                game.state = game.state.updateEntity(game.player2Id) { c ->
                    c.with(CantActivateLoyaltyAbilitiesComponent())
                }

                val walker = game.findPermanent("Test Walker")!!
                val loyalty = cardRegistry.getCard("Test Walker")!!.script.activatedAbilities[0]

                val result = game.execute(ActivateAbility(game.player2Id, walker, loyalty.id))
                withClue("Loyalty activation should be rejected while restricted") {
                    result.error shouldNotBe null
                    result.error!! shouldContain "loyalty"
                }
            }
        }
    }
}
