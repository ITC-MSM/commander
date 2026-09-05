package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import io.kotest.matchers.shouldBe

class GolgariBrownscaleScenarioTest : ScenarioTestBase() {
    init {
        cardRegistry.register(card("Test Return Any Creature") {
            manaCost = "{B}"
            typeLine = "Instant"
            spell {
                val creature = target("creature", Targets.CreatureCardInGraveyard)
                effect = Effects.ReturnToHand(creature)
            }
        })
        cardRegistry.register(card("Test Return Then Discard") {
            manaCost = "{B}"
            typeLine = "Sorcery"
            spell {
                val creature = target("creature", Targets.CreatureCardInYourGraveyard)
                effect = Effects.ReturnToHand(creature) then Patterns.Hand.discardHand()
            }
        })

        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("dredge mills two and returning Brownscale triggers two life") {
            val game = base().withCardInGraveyard(1, "Golgari Brownscale")
                .withCardInHand(1, "Inspiration").withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Golgari Brownscale"
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Golgari Brownscale") shouldBe true
            game.findCardsInGraveyard(1, "Forest").size shouldBe 2
            game.getLifeTotal(1) shouldBe 22
        }

        test("ordinary graveyard return also gains life") {
            val game = base().withCardInGraveyard(1, "Golgari Brownscale")
                .withCardInHand(1, "Raise Dead").withLandsOnBattlefield(1, "Swamp", 1).build()
            game.castSpellTargetingGraveyardCard(1, "Raise Dead", 1, "Golgari Brownscale").error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Golgari Brownscale") shouldBe true
            game.getLifeTotal(1) shouldBe 22
        }

        test("returning from the battlefield does not gain life") {
            val game = base().withCardOnBattlefield(1, "Golgari Brownscale")
                .withCardInHand(1, "Unsummon").withLandsOnBattlefield(1, "Island", 1).build()
            game.castSpell(1, "Unsummon", game.findPermanent("Golgari Brownscale")!!).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Golgari Brownscale") shouldBe true
            game.getLifeTotal(1) shouldBe 20
        }

        test("leaving the graveyard for exile does not gain life") {
            val game = base().withCardInGraveyard(1, "Golgari Brownscale")
                .withCardInHand(1, "Cremate").withLandsOnBattlefield(1, "Swamp", 1)
                .withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingGraveyardCard(1, "Cremate", 1, "Golgari Brownscale").error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Golgari Brownscale") shouldBe false
            game.getLifeTotal(1) shouldBe 20
        }

        test("owner gains life when an opponent causes the return") {
            val game = base().withActivePlayer(2)
                .withCardInGraveyard(1, "Golgari Brownscale")
                .withCardInHand(2, "Test Return Any Creature")
                .withLandsOnBattlefield(2, "Swamp", 1).build()
            game.castSpellTargetingGraveyardCard(2, "Test Return Any Creature", 1, "Golgari Brownscale")
                .error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Golgari Brownscale") shouldBe true
            game.getLifeTotal(1) shouldBe 22
            game.getLifeTotal(2) shouldBe 20
        }

        test("return followed by discard in the same resolution triggers exactly once") {
            val game = base().withCardInGraveyard(1, "Golgari Brownscale")
                .withCardInHand(1, "Test Return Then Discard")
                .withLandsOnBattlefield(1, "Swamp", 1).build()
            game.castSpellTargetingGraveyardCard(1, "Test Return Then Discard", 1, "Golgari Brownscale")
                .error shouldBe null
            game.resolveStack()
            game.isInGraveyard(1, "Golgari Brownscale") shouldBe true
            game.getLifeTotal(1) shouldBe 22
        }
    }
}
