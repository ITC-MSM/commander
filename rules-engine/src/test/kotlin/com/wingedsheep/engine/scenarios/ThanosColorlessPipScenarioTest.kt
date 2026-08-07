package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Thanos, the Mad Titan — "Power-up — {C}{W}{U}{B}{R}{G}".
 *
 * The `{C}` is a *colorless* mana symbol (CR 107.4c), not generic: it can only be paid with
 * colorless mana, and five colored sources plus a sixth colored source must not cover it. Reduced
 * by his own `{R}{W}{B}` on the turn he enters, the ability asks for `{C}{U}{G}` — so a board of
 * an Island and a Forest and any number of *colored* lands still cannot pay it.
 *
 * Both halves matter and are asserted separately, because they run through different code: the
 * enumerator's affordability check (what the player is offered) and the handler's payment (what
 * the engine accepts if the action is submitted directly).
 */
class ThanosColorlessPipScenarioTest : ScenarioTestBase() {

    private val abilityId
        get() = cardRegistry.getCard("Thanos, the Mad Titan")!!.script.activatedAbilities[0].id

    private fun TestGame.powerUp() =
        getLegalActions(1).firstOrNull { it.description.startsWith("Power-up —") }

    init {
        context("Thanos, the Mad Titan — the {C} pip") {

            test("colored sources alone can't pay the {C}, so the power-up isn't affordable") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Thanos, the Mad Titan", enteredThisTurn = true)
                    // Blue and green for the {U}{G}, plus plenty of other colored mana. No
                    // colorless source anywhere.
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val action = game.powerUp()
                withClue("the reduced cost is {C}{U}{G}") {
                    action shouldNotBe null
                    action!!.description.startsWith("Power-up — {C}{U}{G}:") shouldBe true
                }
                withClue("15 colored lands still cannot produce the one {C} this needs") {
                    action!!.isAffordable shouldBe false
                }

                val thanos = game.findPermanent("Thanos, the Mad Titan")!!
                val result = game.execute(ActivateAbility(game.player1Id, thanos, abilityId))
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                withClue("the handler must refuse to pay a colorless pip with colored mana") {
                    val paid = game.state.getEntity(thanos)
                        ?.get<com.wingedsheep.engine.state.components.battlefield.CountersComponent>()
                        ?.getCount(com.wingedsheep.sdk.core.CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                    (result.error != null || paid == 0) shouldBe true
                }
            }

            test("adding one colorless source makes it payable") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Thanos, the Mad Titan", enteredThisTurn = true)
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withLandsOnBattlefield(1, "Forest", 3)
                    // Gathering Place: "{T}: Add {C}."
                    .withLandsOnBattlefield(1, "Gathering Place", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("with a real colorless source the same {C}{U}{G} is payable") {
                    game.powerUp()!!.isAffordable shouldBe true
                }
            }
        }
    }
}
