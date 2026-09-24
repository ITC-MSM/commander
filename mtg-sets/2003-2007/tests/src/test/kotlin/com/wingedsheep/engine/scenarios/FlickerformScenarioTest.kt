package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.Flickerform
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Flickerform (RAV #18) — "{2}{W}{W}: Exile enchanted creature and all Auras attached to it. At the
 * beginning of the next end step, return that card to the battlefield under its owner's control. If
 * you do, return the other cards exiled this way to the battlefield under their owners' control
 * attached to that creature."
 *
 * Exercises the two pieces the card needs: `CreateDelayedTriggerEffect.carryCollections` (the end-step
 * trigger remembers which card was the creature and which were the Auras) and
 * `MoveCollectionEffect.attachTo` (each Aura comes back attached to that card, under its owner's
 * control, with no enchant choice).
 */
class FlickerformScenarioTest : ScenarioTestBase() {

    private val abilityId = Flickerform.activatedAbilities.single().id

    init {
        context("Flickerform") {

            test("the creature and every Aura on it are exiled, then come back together at the end step") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Flickerform", "Grizzly Bears")
                    .withCardAttachedTo(1, "Holy Strength", "Grizzly Bears")
                    // Bob's Aura on Alice's creature returns under Bob's control.
                    .withCardAttachedTo(2, "Unholy Strength", "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val flickerform = game.findPermanent("Flickerform")!!
                game.execute(ActivateAbility(playerId = game.player1Id, sourceId = flickerform, abilityId = abilityId))
                    .error shouldBe null
                game.resolveStack()

                withClue("the creature and all three Auras are in exile") {
                    game.isInExile(1, "Grizzly Bears") shouldBe true
                    game.isInExile(1, "Flickerform") shouldBe true
                    game.isInExile(1, "Holy Strength") shouldBe true
                    game.isInExile(2, "Unholy Strength") shouldBe true
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()
                game.checkStateBasedActions()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("the creature returned under its owner's control") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }
                for (name in listOf("Flickerform", "Holy Strength", "Unholy Strength")) {
                    withClue("$name returned attached to that creature") {
                        val aura = game.findPermanent(name)!!
                        game.state.getEntity(aura)!!.get<AttachedToComponent>()!!.targetId shouldBe bears
                    }
                }
                withClue("each Aura returned under its owner's control") {
                    game.state.projectedState.getController(game.findPermanent("Unholy Strength")!!) shouldBe game.player2Id
                    game.state.projectedState.getController(game.findPermanent("Holy Strength")!!) shouldBe game.player1Id
                }
                withClue("both Auras apply again: 2/2 +1/+2 +2/+1") {
                    game.state.projectedState.getPower(bears) shouldBe 5
                    game.state.projectedState.getToughness(bears) shouldBe 5
                }
            }

            test("a token host ceases to exist in exile, so its Auras stay exiled") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Flickerform", "Grizzly Bears")
                    .withCardAttachedTo(1, "Holy Strength", "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state.updateEntity(bears) { it.with(TokenComponent) }

                val flickerform = game.findPermanent("Flickerform")!!
                game.execute(ActivateAbility(playerId = game.player1Id, sourceId = flickerform, abilityId = abilityId))
                    .error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("nothing returns: the token is gone and the Auras have no host") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInExile(1, "Flickerform") shouldBe true
                    game.isInExile(1, "Holy Strength") shouldBe true
                }
            }

            test("a card that left exile before the end step is a new object and isn't returned") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Flickerform", "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
                val bears = game.findPermanent("Grizzly Bears")!!
                val flickerform = game.findPermanent("Flickerform")!!
                game.execute(ActivateAbility(playerId = game.player1Id, sourceId = flickerform, abilityId = abilityId))
                    .error shouldBe null
                game.resolveStack()

                // The exiled creature card moves to its owner's graveyard before the end step (CR 603.7c).
                game.state = zones.moveToZone(game.state, bears, Zone.GRAVEYARD).state

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("the creature card stays in the graveyard, and Flickerform stays exiled") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInExile(1, "Flickerform") shouldBe true
                }
            }

            test("an Aura of a color the returned creature has protection from stays exiled") {
                // White Knight has protection from black. Its Flickerform (white) is legal; a black
                // Aura exiled "this way" can't be attached to it on the way back (CR 702.16c), so it
                // must stay in exile (CR 303.4g) rather than attach and then be graveyarded.
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "White Knight")
                    .withCardAttachedTo(1, "Flickerform", "White Knight")
                    .withCardInExile(1, "Unholy Strength")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
                val flickerform = game.findPermanent("Flickerform")!!
                val unholy = game.state.getZone(
                    com.wingedsheep.engine.state.ZoneKey(game.player1Id, com.wingedsheep.sdk.core.Zone.EXILE)
                ).single()
                game.execute(ActivateAbility(playerId = game.player1Id, sourceId = flickerform, abilityId = abilityId))
                    .error shouldBe null
                game.resolveStack()

                // A black Aura can't legally be on a pro-black creature on the battlefield, so stand
                // one in for "the other cards exiled this way" by adding it to the carried pile.
                game.state = game.state.copy(delayedTriggers = game.state.delayedTriggers.map { d ->
                    val auras = d.carriedCollections["flickerAuras"] ?: return@map d
                    d.copy(carriedCollections = d.carriedCollections + ("flickerAuras" to auras +
                        com.wingedsheep.engine.handlers.CapturedObjectBinding(unholy, game.state.objectRef(unholy))))
                })

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()
                game.checkStateBasedActions()

                val knight = game.findPermanent("White Knight")!!
                withClue("Flickerform (white) returns attached to the pro-black Knight") {
                    game.state.getEntity(game.findPermanent("Flickerform")!!)!!
                        .get<AttachedToComponent>()!!.targetId shouldBe knight
                }
                withClue("the black Aura stays in exile instead of attaching and being graveyarded") {
                    game.isInExile(1, "Unholy Strength") shouldBe true
                    game.isInGraveyard(1, "Unholy Strength") shouldBe false
                    game.isOnBattlefield("Unholy Strength") shouldBe false
                }
            }

            test("the creature doesn't come back until the end step") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Flickerform", "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val flickerform = game.findPermanent("Flickerform")!!
                game.execute(ActivateAbility(playerId = game.player1Id, sourceId = flickerform, abilityId = abilityId))
                    .error shouldBe null
                game.resolveStack()
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.isOnBattlefield("Grizzly Bears") shouldBe false

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()
                game.checkStateBasedActions()
                game.isOnBattlefield("Grizzly Bears") shouldBe true
                val aura = game.findPermanent("Flickerform")!!
                game.state.getEntity(aura)!!.get<AttachedToComponent>()!!.targetId shouldBe game.findPermanent("Grizzly Bears")
            }
        }
    }
}
