package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ghost Vacuum (DSK #248) — {1} Artifact.
 *
 * "{T}: Exile target card from a graveyard.
 *  {6}, {T}, Sacrifice this artifact: Put each creature card exiled with this artifact onto the
 *  battlefield under your control with a flying counter on it. Each of them is a 1/1 Spirit in
 *  addition to its other types. Activate only as a sorcery."
 *
 * Exercises:
 *  - the first ability exiling a graveyard card linked to the artifact;
 *  - the second ability returning the creature cards exiled this way under your control;
 *  - each returned creature being a 1/1 Spirit (base P/T set, Spirit type added) with a flying
 *    counter (and thus flying);
 *  - non-creature cards exiled this way staying exiled.
 */
class GhostVacuumScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Ghost Vacuum") {

            test("exiles a graveyard creature, then returns it as a 1/1 flying Spirit") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ghost Vacuum", tapped = false, summoningSickness = false)
                    .withCardInGraveyard(2, "Centaur Courser") // opponent's graveyard creature
                    .withLandsOnBattlefield(1, "Forest", 6)     // pay {6}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val vacuum = game.findPermanent("Ghost Vacuum")!!
                val cardDef = cardRegistry.getCard("Ghost Vacuum")!!.script
                val exileAbility = cardDef.activatedAbilities[0]
                val returnAbility = cardDef.activatedAbilities[1]

                val gyCard = game.findCardsInGraveyard(2, "Centaur Courser").single()

                // {T}: exile the graveyard creature (linked to Ghost Vacuum).
                val exile = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = vacuum,
                        abilityId = exileAbility.id,
                        targets = listOf(ChosenTarget.Card(gyCard, game.player2Id, Zone.GRAVEYARD)),
                    )
                )
                withClue("Exiling the graveyard card should succeed: ${exile.error}") {
                    exile.error shouldBe null
                }
                game.resolveStack()

                withClue("The card is no longer in the graveyard") {
                    game.isInGraveyard(2, "Centaur Courser") shouldBe false
                }

                // Untap the artifact (the second ability would normally be activated on a later
                // turn after the {T} of the first ability has worn off).
                game.state = game.state.updateEntity(vacuum) { it.without<TappedComponent>() }

                // {6}, {T}, Sacrifice: return creature cards as 1/1 flying Spirits.
                val ret = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = vacuum,
                        abilityId = returnAbility.id,
                    )
                )
                withClue("Activating the return ability should succeed: ${ret.error}") {
                    ret.error shouldBe null
                }
                game.resolveStack()

                withClue("Ghost Vacuum was sacrificed") {
                    game.isOnBattlefield("Ghost Vacuum") shouldBe false
                }

                val spirit = game.findPermanent("Centaur Courser")
                withClue("The creature card returned to the battlefield under your control") {
                    spirit shouldBe game.findPermanents("Centaur Courser").single()
                }
                val id = spirit!!
                val projected = stateProjector.project(game.state)
                withClue("It is a 1/1") {
                    projected.getPower(id) shouldBe 1
                    projected.getToughness(id) shouldBe 1
                }
                withClue("It has a flying counter and therefore flying") {
                    game.state.getEntity(id)?.get<CountersComponent>()
                        ?.getCount(CounterType.FLYING) shouldBe 1
                    projected.hasKeyword(id, Keyword.FLYING) shouldBe true
                }
                withClue("It is a Spirit in addition to its other types") {
                    projected.getSubtypes(id).contains("Spirit") shouldBe true
                }
            }

            test("non-creature cards exiled this way are not returned") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ghost Vacuum", tapped = false, summoningSickness = false)
                    .withCardInGraveyard(1, "Lightning Bolt") // an instant, not a creature
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val vacuum = game.findPermanent("Ghost Vacuum")!!
                val cardDef = cardRegistry.getCard("Ghost Vacuum")!!.script
                val gyCard = game.findCardsInGraveyard(1, "Lightning Bolt").single()

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = vacuum,
                        abilityId = cardDef.activatedAbilities[0].id,
                        targets = listOf(ChosenTarget.Card(gyCard, game.player1Id, Zone.GRAVEYARD)),
                    )
                ).error shouldBe null
                game.resolveStack()

                game.state = game.state.updateEntity(vacuum) { it.without<TappedComponent>() }

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = vacuum,
                        abilityId = cardDef.activatedAbilities[1].id,
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("The instant stays exiled (not a creature card)") {
                    game.isOnBattlefield("Lightning Bolt") shouldBe false
                    game.isInGraveyard(1, "Lightning Bolt") shouldBe false
                }
            }
        }
    }
}
