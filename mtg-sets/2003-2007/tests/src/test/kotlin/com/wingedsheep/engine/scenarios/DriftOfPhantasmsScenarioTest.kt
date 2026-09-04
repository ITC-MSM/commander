package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsRevealedEvent
import com.wingedsheep.engine.core.LibraryShuffledEvent
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class DriftOfPhantasmsScenarioTest : ScenarioTestBase() {
    init {
        test("transmute discards as a cost and selects exactly the matching mana value") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Drift of Phantasms")
                .withCardInLibrary(1, "Divination")
                .withCardInLibrary(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findCardsInHand(1, "Drift of Phantasms").single()
            val ability = cardRegistry.getCard("Drift of Phantasms")!!.activatedAbilities.single()
            game.getLegalActions(1).any { (it.action as? ActivateAbility)?.abilityId == ability.id } shouldBe true
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Drift of Phantasms") shouldBe true
            game.isInHand(1, "Divination") shouldBe false
            game.resolveStack()
            val decision = game.state.pendingDecision as SelectCardsDecision
            val match = game.findCardsInLibrary(1, "Divination").single()
            decision.options shouldBe listOf(match)
            val selected = game.selectCards(listOf(match))
            selected.error shouldBe null
            selected.events.filterIsInstance<CardsRevealedEvent>().single().cardIds shouldBe listOf(match)
            selected.events.filterIsInstance<LibraryShuffledEvent>().size shouldBe 1
            game.resolveStack()
            game.isInHand(1, "Divination") shouldBe true
            game.isInHand(1, "Grizzly Bears") shouldBe false
        }

        test("transmute may fail to find even with a matching card") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Drift of Phantasms")
                .withCardInLibrary(1, "Divination")
                .withLandsOnBattlefield(1, "Island", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findCardsInHand(1, "Drift of Phantasms").single()
            val ability = cardRegistry.getCard("Drift of Phantasms")!!.activatedAbilities.single()
            game.getLegalActions(1).any { (it.action as? ActivateAbility)?.abilityId == ability.id } shouldBe true
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.resolveStack()
            game.skipSelection().error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Divination") shouldBe false
            game.isInGraveyard(1, "Drift of Phantasms") shouldBe true
        }

        test("transmute cannot be activated in combat") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Drift of Phantasms")
                .withLandsOnBattlefield(1, "Island", 3)
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT).build()
            val source = game.findCardsInHand(1, "Drift of Phantasms").single()
            val ability = cardRegistry.getCard("Drift of Phantasms")!!.activatedAbilities.single()
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error.shouldNotBeNull()
            game.isInHand(1, "Drift of Phantasms") shouldBe true
        }

        test("transmute is unavailable from the battlefield") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Drift of Phantasms")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findPermanent("Drift of Phantasms")!!
            val ability = cardRegistry.getCard("Drift of Phantasms")!!.activatedAbilities.single()
            game.getLegalActions(1).any { (it.action as? ActivateAbility)?.abilityId == ability.id } shouldBe false
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error.shouldNotBeNull()
        }

        test("transmute cannot discard without enough mana") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Drift of Phantasms")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findCardsInHand(1, "Drift of Phantasms").single()
            val ability = cardRegistry.getCard("Drift of Phantasms")!!.activatedAbilities.single()
            game.getLegalActions(1).any { (it.action as? ActivateAbility)?.abilityId == ability.id } shouldBe false
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error.shouldNotBeNull()
            game.isInHand(1, "Drift of Phantasms") shouldBe true
        }

        test("transmute cannot be activated while a spell is on the stack") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Drift of Phantasms")
                .withCardInHand(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withLandsOnBattlefield(1, "Forest", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Grizzly Bears").error shouldBe null
            val source = game.findCardsInHand(1, "Drift of Phantasms").single()
            val ability = cardRegistry.getCard("Drift of Phantasms")!!.activatedAbilities.single()
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error.shouldNotBeNull()
            game.isInHand(1, "Drift of Phantasms") shouldBe true
        }

        test("transmute finishes with no matching library cards") {
            val game = scenario().withPlayers("P1", "P2")
                .withCardInHand(1, "Drift of Phantasms")
                .withCardInLibrary(1, "Island")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val source = game.findCardsInHand(1, "Drift of Phantasms").single()
            val ability = cardRegistry.getCard("Drift of Phantasms")!!.activatedAbilities.single()
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.resolveStack()
            if (game.state.pendingDecision != null) game.skipSelection().error shouldBe null
            game.resolveStack()
            game.state.pendingDecision shouldBe null
            game.isInGraveyard(1, "Drift of Phantasms") shouldBe true
            game.findCardsInLibrary(1, "Island").size shouldBe 1
        }
    }
}
