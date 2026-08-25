package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Otawara, Soaring City (NEO #271) — Legendary Land.
 *
 *   {T}: Add {U}.
 *   Channel — {3}{U}, Discard this card: Return target artifact, creature, enchantment, or
 *   planeswalker to its owner's hand. This ability costs {1} less to activate for each legendary
 *   creature you control.
 *
 * The reference test for the five NEO channel lands: it covers the from-hand activation, the
 * per-legendary cost reduction, and the target restriction. The other four lands' tests lean on
 * this one for the shared shape and focus on their own effects.
 *
 * The reduction is also covered from the enumeration side by
 * ZoneAbilityCostReductionEnumerationTest, which is where the "was the ability even *offered*"
 * regression lives; here we prove the handler charges the reduced cost and the effect works.
 */
class OtawaraSoaringCityScenarioTest : ScenarioTestBase() {

    private fun channelAbilityId() = cardRegistry.getCard("Otawara, Soaring City")!!
        .activatedAbilities.first { it.activateFromZone == Zone.HAND }.id

    init {
        context("Otawara, Soaring City") {

            test("{T}: Add {U} — it is still just a land") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Otawara, Soaring City")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val otawara = game.findPermanent("Otawara, Soaring City")!!
                val manaAbility = cardRegistry.getCard("Otawara, Soaring City")!!
                    .activatedAbilities.first { it.isManaAbility }

                val result = game.execute(
                    ActivateAbility(game.player1Id, otawara, manaAbility.id)
                )
                withClue("tapping for {U} should succeed: ${result.error}") {
                    result.error shouldBe null
                }
            }

            test("Channel from hand at full price bounces a creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Otawara, Soaring City")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val handCard = game.findCardsInHand(1, "Otawara, Soaring City").first()

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = handCard,
                        abilityId = channelAbilityId(),
                        targets = listOf(ChosenTarget.Permanent(bears))
                    )
                )
                withClue("{3}{U} with four Islands should be payable: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Grizzly Bears is bounced") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInHand(2, "Grizzly Bears") shouldBe true
                }
                withClue("The land paid for itself by being discarded") {
                    game.isInGraveyard(1, "Otawara, Soaring City") shouldBe true
                }
            }

            test("two legendary creatures make it cost {1}{U} — two lands are enough") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Otawara, Soaring City")
                    .withLandsOnBattlefield(1, "Island", 2)
                    // Two *differently named* legendary creatures, so the legend rule keeps both.
                    .withCardOnBattlefield(1, "Ghalta, Primal Hunger")
                    .withCardOnBattlefield(1, "Squee, Goblin Nabob")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val handCard = game.findCardsInHand(1, "Otawara, Soaring City").first()

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = handCard,
                        abilityId = channelAbilityId(),
                        targets = listOf(ChosenTarget.Permanent(bears))
                    )
                )
                withClue("{3}{U} reduced by two legends is {1}{U}: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                game.isInHand(2, "Grizzly Bears") shouldBe true
            }

            test("a land is not a legal target — artifact/creature/enchantment/planeswalker only") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Otawara, Soaring City")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!
                val handCard = game.findCardsInHand(1, "Otawara, Soaring City").first()

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = handCard,
                        abilityId = channelAbilityId(),
                        targets = listOf(ChosenTarget.Permanent(forest))
                    )
                )
                withClue("Otawara can't bounce a land — Boseiju is the one that touches lands") {
                    result.error shouldNotBe null
                }
            }
        }
    }
}
