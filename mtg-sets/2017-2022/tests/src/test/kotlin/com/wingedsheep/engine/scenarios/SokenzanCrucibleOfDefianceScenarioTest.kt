package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Sokenzan, Crucible of Defiance (NEO #276) — Legendary Land.
 *
 *   {T}: Add {R}.
 *   Channel — {3}{R}, Discard this card: Create two 1/1 colorless Spirit creature tokens. They
 *   gain haste until end of turn. This ability costs {1} less to activate for each legendary
 *   creature you control.
 *
 * See OtawaraSoaringCityScenarioTest for the shared channel shape. What matters here is that
 * "they" means *these two tokens* — the grant iterates the created-tokens collection rather than
 * baking haste onto the token, which would leave printed haste behind on later turns.
 */
class SokenzanCrucibleOfDefianceScenarioTest : ScenarioTestBase() {

    private fun channelAbilityId() = cardRegistry.getCard("Sokenzan, Crucible of Defiance")!!
        .activatedAbilities.first { it.activateFromZone == Zone.HAND }.id

    init {
        context("Sokenzan, Crucible of Defiance") {

            test("creates two 1/1 Spirits, and both have haste") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Sokenzan, Crucible of Defiance")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handCard = game.findCardsInHand(1, "Sokenzan, Crucible of Defiance").first()
                val result = game.execute(
                    ActivateAbility(game.player1Id, handCard, channelAbilityId())
                )
                withClue("{3}{R} from four Mountains: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                val spirits = game.findPermanents("Spirit Token")

                withClue("Exactly two Spirit tokens") {
                    spirits.size shouldBe 2
                }
                withClue("Both gained haste — 'they' is the pair this activation made") {
                    spirits.all {
                        game.state.projectedState.hasKeyword(it, Keyword.HASTE)
                    } shouldBe true
                }
                withClue("The land discarded itself to pay") {
                    game.isInGraveyard(1, "Sokenzan, Crucible of Defiance") shouldBe true
                }
            }

            test("the hasty Spirits can attack the turn they are made") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Sokenzan, Crucible of Defiance")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handCard = game.findCardsInHand(1, "Sokenzan, Crucible of Defiance").first()
                game.execute(ActivateAbility(game.player1Id, handCard, channelAbilityId()))
                game.resolveStack()

                val spirits = game.findPermanents("Spirit Token")
                spirits.size shouldBe 2

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Spirit Token" to 2)).error shouldBe null

                // `declareAttackers` drops names it can't resolve, so a green call proves nothing
                // on its own — assert the token is actually marked as attacking.
                withClue("Haste is the whole point of the token half") {
                    game.state.getEntity(spirits.first())?.has<AttackingComponent>() shouldBe true
                }
            }
        }
    }
}
