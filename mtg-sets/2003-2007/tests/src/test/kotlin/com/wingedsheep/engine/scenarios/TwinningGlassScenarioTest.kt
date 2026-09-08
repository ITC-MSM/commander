package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.CastSpellRecord
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lrw.cards.TwinningGlass
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import io.kotest.matchers.shouldBe

class TwinningGlassScenarioTest : ScenarioTestBase() {
    init {
        fun setup(): TestGame = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Twinning Glass")
            .withLandsOnBattlefield(1, "Mountain", 2)
            .withCardInHand(1, "Lightning Bolt")
            .withCardInHand(1, "Lightning Bolt")
            .withCardInHand(1, "Grizzly Bears")
            .withCardInHand(1, "Island")
            .withCardInHand(1, "Jennifer Walters")
            .withCardInHand(1, "Disenchant")
            .withCardInHand(2, "Disenchant")
            .withCardOnBattlefield(2, "Sol Ring")
            .withLandsOnBattlefield(2, "Plains", 2)
            .withCardInHand(1, "Mosswood Dreadknight")
            .withCardInHand(1, "Pain // Suffering")
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(2, "Island")
            .withCardInLibrary(2, "Island")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        fun activate(game: TestGame) {
            game.execute(ActivateAbility(
                playerId = game.player1Id,
                sourceId = game.findPermanent("Twinning Glass")!!,
                abilityId = TwinningGlass.script.activatedAbilities.single().id
            )).error shouldBe null
            game.resolveStack()
        }

        test("casts exactly one matching spell for free and asks for its targets") {
            val game = setup()
            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.resolveStack()
            activate(game)
            val bolt = game.findCardsInHand(1, "Lightning Bolt").single()
            (game.state.pendingDecision is SelectCardsDecision) shouldBe true
            game.selectCards(listOf(bolt)).error shouldBe null
            game.selectTargets(listOf(game.player2Id)).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(2) shouldBe 14
            game.isInHand(1, "Lightning Bolt") shouldBe false
            game.isInHand(1, "Grizzly Bears") shouldBe true
            game.state.getEntity(game.findPermanent("Twinning Glass")!!)!!.has<com.wingedsheep.engine.state.components.battlefield.TappedComponent>() shouldBe true
        }

        test("opponent's cast qualifies and a creature can be cast during combat") {
            val game = setup()
            game.state = game.state.copy(spellsCastThisTurnByPlayer = mapOf(
                game.player2Id to listOf(CastSpellRecord(
                    TypeLine.parse("Creature — Bear"), 2, emptySet(), false, name = "Grizzly Bears"
                ))
            ))
            game.advanceToPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
            activate(game)
            game.selectCards(listOf(game.findCardsInHand(1, "Grizzly Bears").single())).error shouldBe null
            game.resolveStack()
            game.findPermanent("Grizzly Bears")?.let { game.state.projectedState.getController(it) } shouldBe game.player1Id
            game.isInHand(1, "Lightning Bolt") shouldBe true
        }

        test("unmatched spells and lands cannot be selected") {
            val game = setup()
            game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
            game.resolveStack()
            activate(game)
            game.selectCards(listOf(game.findCardsInHand(1, "Grizzly Bears").single())).isSuccess shouldBe false
            game.selectCards(listOf(game.findCardsInHand(1, "Island").single())).isSuccess shouldBe false
            game.selectCards(emptyList()).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Lightning Bolt") shouldBe true
        }

        test("may activate without a matching spell and decline without gaining later permission") {
            val game = setup()
            activate(game)
            if (game.state.pendingDecision is SelectCardsDecision) game.selectCards(emptyList()).error shouldBe null
            game.resolveStack()
            game.state.pendingDecision shouldBe null
            game.findCardsInHand(1, "Lightning Bolt").size shouldBe 2
            game.isInHand(1, "Grizzly Bears") shouldBe true
        }

        test("matches an Adventure spell name and casts that face rather than the creature") {
            val game = setup()
            game.state = game.state.copy(spellsCastThisTurnByPlayer = mapOf(
                game.player2Id to listOf(CastSpellRecord(
                    TypeLine.parse("Sorcery"), 2, emptySet(), false, name = "Dread Whispers"
                ))
            ))
            activate(game)
            val knight = game.findCardsInHand(1, "Mosswood Dreadknight").single()
            game.selectCards(listOf(knight)).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 19
            game.findPermanent("Mosswood Dreadknight") shouldBe null
            game.state.spellsCastThisTurnByPlayer[game.player1Id]!!.last().name shouldBe "Dread Whispers"
        }

        test("a split card casts only the matching half with that half's targets") {
            val game = setup()
            game.state = game.state.copy(spellsCastThisTurnByPlayer = mapOf(
                game.player2Id to listOf(CastSpellRecord(
                    TypeLine.parse("Sorcery"), 4, emptySet(), false, name = "Suffering"
                ))
            ))
            val mountain = game.findPermanent("Mountain")!!
            activate(game)
            game.selectCards(listOf(game.findCardsInHand(1, "Pain // Suffering").single())).error shouldBe null
            game.selectTargets(listOf(mountain)).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(1, "Mountain") shouldBe true
            game.state.spellsCastThisTurnByPlayer[game.player1Id]!!.last().name shouldBe "Suffering"
        }

        test("offers a face choice only when multiple spell faces match") {
            val game = setup()
            game.state = game.state.copy(spellsCastThisTurnByPlayer = mapOf(
                game.player2Id to listOf("Mosswood Dreadknight", "Dread Whispers").map { name ->
                    CastSpellRecord(TypeLine.parse("Sorcery"), 2, emptySet(), false, name = name)
                }
            ))
            activate(game)
            game.selectCards(listOf(game.findCardsInHand(1, "Mosswood Dreadknight").single())).error shouldBe null
            val decision = game.state.pendingDecision as com.wingedsheep.engine.core.ChooseOptionDecision
            decision.options shouldBe listOf("Mosswood Dreadknight", "Dread Whispers")
            game.execute(com.wingedsheep.engine.core.SubmitDecision(
                game.player1Id, com.wingedsheep.engine.core.OptionChosenResponse(decision.id, 0)
            )).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 20
            (game.findPermanent("Mosswood Dreadknight") != null) shouldBe true
        }

        test("a modal permanent back face qualifies independently of its front") {
            val game = setup()
            game.state = game.state.copy(spellsCastThisTurnByPlayer = mapOf(
                game.player2Id to listOf(CastSpellRecord(
                    TypeLine.parse("Creature"), 6, emptySet(), false, name = "The Sensational She-Hulk"
                ))
            ))
            activate(game)
            game.selectCards(listOf(game.findCardsInHand(1, "Jennifer Walters").single())).error shouldBe null
            game.resolveStack()
            game.state.spellsCastThisTurnByPlayer[game.player1Id]!!.last().name shouldBe "The Sensational She-Hulk"
        }

        test("resolves after the Glass leaves and counts a spell cast in response") {
            val game = setup()
            val glass = game.findPermanent("Twinning Glass")!!
            game.execute(ActivateAbility(game.player1Id, glass, TwinningGlass.script.activatedAbilities.single().id)).error shouldBe null
            game.passPriority()
            game.castSpell(2, "Disenchant", glass).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(1, "Twinning Glass") shouldBe true
            game.selectCards(listOf(game.findCardsInHand(1, "Disenchant").single())).error shouldBe null
            game.selectTargets(listOf(game.findPermanent("Sol Ring")!!)).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Sol Ring") shouldBe true
            game.state.pendingDecision shouldBe null
            game.state.spellsCastThisTurnByPlayer[game.player2Id]!!.last().name shouldBe "Disenchant"
        }
    }
}
