package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import com.wingedsheep.sdk.dsl.mobilize

/**
 * Scenario test for Avenger of the Fallen (TDM #73) — {2}{B} Human Warrior, 2/4.
 *
 * Deathtouch
 * Mobilize X, where X is the number of creature cards in your graveyard.
 *
 * Exercises the dynamic `mobilize(amount, ...)` DSL overload: the number of tapped/attacking
 * Warrior tokens created when Avenger attacks must equal the number of creature cards in the
 * controller's graveyard, counted (per Scryfall ruling) when the mobilize ability resolves.
 * Noncreature cards in the graveyard are ignored.
 */
class AvengerOfTheFallenScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature(
                name = "Dead Bear A",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )
        cardRegistry.register(
            CardDefinition.creature(
                name = "Dead Bear B",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )
        cardRegistry.register(
            CardDefinition.creature(
                name = "Dead Bear C",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )
        cardRegistry.register(
            CardDefinition.instant(
                name = "Test Bolt",
                manaCost = ManaCost.parse("{R}"),
                oracleText = "Test Bolt deals 3 damage to any target."
            )
        )

        context("Avenger of the Fallen") {

            test("has deathtouch") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Avenger of the Fallen", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val avenger = game.findPermanent("Avenger of the Fallen")!!
                withClue("Avenger of the Fallen has deathtouch") {
                    game.state.projectedState.hasKeyword(avenger, Keyword.DEATHTOUCH) shouldBe true
                }
            }

            test("Mobilize X creates one Warrior token per creature card in the graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Avenger of the Fallen", tapped = false, summoningSickness = false)
                    .withCardInGraveyard(1, "Dead Bear A")
                    .withCardInGraveyard(1, "Dead Bear B")
                    .withCardInGraveyard(1, "Dead Bear C")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val attack = game.declareAttackers(mapOf("Avenger of the Fallen" to 2))
                withClue("Declaring Avenger of the Fallen as attacker should succeed: ${attack.error}") {
                    attack.error shouldBe null
                }
                game.resolveStack()

                val warriors = game.findPermanents("Warrior Token")
                withClue("Three creature cards in the graveyard → Mobilize 3 → three Warrior tokens") {
                    warriors.size shouldBe 3
                }
                withClue("Each Warrior token enters tapped and attacking") {
                    warriors.forEach { token ->
                        game.state.getEntity(token)?.has<TappedComponent>() shouldBe true
                        game.state.getEntity(token)?.has<AttackingComponent>() shouldBe true
                    }
                }
            }

            test("noncreature cards in the graveyard do not count toward Mobilize X") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Avenger of the Fallen", tapped = false, summoningSickness = false)
                    .withCardInGraveyard(1, "Dead Bear A")
                    .withCardInGraveyard(1, "Test Bolt")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val attack = game.declareAttackers(mapOf("Avenger of the Fallen" to 2))
                withClue("Declaring Avenger of the Fallen as attacker should succeed: ${attack.error}") {
                    attack.error shouldBe null
                }
                game.resolveStack()

                val warriors = game.findPermanents("Warrior Token")
                withClue("Only the single creature card counts; the instant is ignored → one token") {
                    warriors.size shouldBe 1
                }
            }
        }
    }
}
