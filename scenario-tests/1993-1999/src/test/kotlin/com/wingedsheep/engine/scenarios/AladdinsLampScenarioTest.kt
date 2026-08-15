package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Aladdin's Lamp (ARN #56) — {10} Artifact.
 *
 * "{X}, {T}: The next time you would draw a card this turn, instead look at the top X cards of your
 *  library, put all but one of them on the bottom of your library in a random order, then draw a
 *  card. X can't be 0."
 *
 * Exercises the activation-time {X} captured onto the one-shot draw-replacement shield (so the
 * replacement's `DynamicAmount.XValue` resolves at draw time) and the look-at-top-X / keep-one /
 * rest-to-bottom dig.
 */
class AladdinsLampScenarioTest : ScenarioTestBase() {

    private val lampAbilityId =
        cardRegistry.getCard("Aladdin's Lamp")!!.activatedAbilities.first().id

    // A free draw spell to provide the "you would draw a card" the shield replaces.
    private val drawOne = card("Draw One Test") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        spell { effect = Effects.DrawCards(1) }
    }

    init {
        cardRegistry.register(drawOne)

        context("Aladdin's Lamp") {

            test("digs the top X, keeps the chosen card, puts the rest on the bottom") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Aladdin's Lamp", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 2)   // pay {2} for X=2
                    .withCardInHand(1, "Draw One Test")
                    .withCardInLibrary(1, "Plains")    // top
                    .withCardInLibrary(1, "Swamp")     // 2nd
                    .withCardInLibrary(1, "Mountain")  // 3rd — beyond X=2, untouched
                    .withCardInLibrary(1, "Forest")    // 4th
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                fun libraryNames(): List<String> =
                    game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY))
                        .mapNotNull { game.state.getEntity(it)?.get<CardComponent>()?.name }

                fun libraryIdOf(name: String): EntityId =
                    game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY))
                        .first { game.state.getEntity(it)?.get<CardComponent>()?.name == name }

                val plains = libraryIdOf("Plains")

                // {X=2}, {T}: install the replacement shield.
                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = game.findPermanent("Aladdin's Lamp")!!,
                        abilityId = lampAbilityId,
                        xValue = 2,
                    )
                )
                withClue("Activating Aladdin's Lamp should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                // Trigger the replaced draw; the shield fires and asks which of the top 2 to keep.
                game.castSpell(1, "Draw One Test")
                game.resolveStack()
                game.selectCards(listOf(plains))
                game.resolveStack()

                withClue("The kept card (Plains) is drawn into hand") {
                    game.isInHand(1, "Plains") shouldBe true
                }
                withClue("The other looked-at card (Swamp) is not drawn") {
                    game.isInHand(1, "Swamp") shouldBe false
                }
                withClue("With X=2, the 3rd card (Mountain) was never looked at and stays in the library") {
                    libraryNames() shouldBe listOf("Mountain", "Forest", "Swamp")
                }
                withClue("The rejected card (Swamp) is on the bottom of the library") {
                    libraryNames().last() shouldBe "Swamp"
                }
            }

            test("the {X} cost can be paid from mana already floating in the pool") {
                // Regression: activating an {X} ability whose X is covered by floating mana used to
                // fail with "Not enough mana", because the X portion was solved purely by tapping
                // sources and never credited the pool (autoTapForManaCost).
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Aladdin's Lamp", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 1)   // one untapped source — not enough for X=4 alone
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Four mana already floating in the pool; X=4 should be paid entirely from it.
                game.state = game.state.updateEntity(game.player1Id) { c ->
                    c.with(ManaPoolComponent(blue = 4))
                }

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = game.findPermanent("Aladdin's Lamp")!!,
                        abilityId = lampAbilityId,
                        xValue = 4,
                    )
                )
                withClue("Activating with X=4 paid from 4 floating mana should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                withClue("The {X} cost should be drained from the floating pool") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.blue shouldBe 0
                }
                withClue("The lone Island was not needed and stays untapped") {
                    game.state.getEntity(game.findPermanent("Island")!!)?.get<TappedComponent>() shouldBe null
                }
            }

            test("X=0 degenerates to an ordinary draw (no prompt, library order preserved)") {
                // "X can't be 0" isn't separately enforced; with X=0 the dig looks at zero cards,
                // so the replacement is just the inner DrawCards(1) — a plain top-of-library draw.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Aladdin's Lamp", summoningSickness = false)
                    .withCardInHand(1, "Draw One Test")
                    .withCardInLibrary(1, "Plains")    // top — drawn by the ordinary draw
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                fun libraryNames(): List<String> =
                    game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY))
                        .mapNotNull { game.state.getEntity(it)?.get<CardComponent>()?.name }

                // {X=0}, {T}: install the shield. No mana needed — X=0 is {0}.
                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = game.findPermanent("Aladdin's Lamp")!!,
                        abilityId = lampAbilityId,
                        xValue = 0,
                    )
                )
                withClue("Activating Aladdin's Lamp with X=0 should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                // Trigger the replaced draw; with X=0 the shield fires but asks for no decision.
                game.castSpell(1, "Draw One Test")
                game.resolveStack()

                withClue("The top card (Plains) is drawn as an ordinary draw") {
                    game.isInHand(1, "Plains") shouldBe true
                }
                withClue("No card was looked at or moved — the rest of the library keeps its order") {
                    libraryNames() shouldBe listOf("Swamp", "Mountain")
                }
            }
        }
    }
}
