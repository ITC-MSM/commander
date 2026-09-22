package com.wingedsheep.ai.jev

import com.wingedsheep.ai.ActionResponse
import com.wingedsheep.ai.engine.EngineAiPlayerController
import com.wingedsheep.engine.core.*
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.view.*
import com.wingedsheep.sdk.core.*
import com.wingedsheep.sdk.model.EntityId
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain

class JevGameplayTest : ScenarioTestBase() {
    init {
        test("Jev chooses a targeted spell and its target and submits a legal action without leaking the other hand") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Lightning Bolt").withCardInHand(2, "Counterspell")
                .withLandsOnBattlefield(1, "Mountain", 1).withCardOnBattlefield(2, "Grizzly Bears")
                .withCardInLibrary(1, "Craw Wurm").withCardInLibrary(2, "Hill Giant")
                .build()
            game.state = game.state.copy(phase = Phase.PRECOMBAT_MAIN, step = Step.PRECOMBAT_MAIN)
            val target = game.findPermanent("Grizzly Bears")!!
            val client = JevChoiceClient { state, _, choices, _ ->
                state shouldNotContain "Counterspell"
                // Own deck composition is known, but the library card identity/location is not.
                state shouldNotContain "\"name\":\"Craw Wurm\""
                state shouldNotContain "Hill Giant"
                choices.entries.firstOrNull { it.value.contains("Lightning Bolt") || it.value.contains(target.value) }?.key
                    ?: choices.keys.first()
            }
            var validations = 0
            val controller = JevAiPlayerController(game.player1Id, client,
                EngineAiPlayerController(cardRegistry, game.player1Id, { error("fallback must not run") }),
                validate = { response ->
                    validations++
                    actionProcessor.process(game.state, (response as ActionResponse.SubmitAction).action).result.error
                }, maskedStateProvider = { stateTransformer.transform(game.state, game.player1Id) })
            val debugView = ClientStateTransformer(cardRegistry, debugMode = true).transform(game.state, game.player1Id)
            check(debugView.cards.values.any { it.name == "Counterspell" })
            val result = controller.chooseAction(debugView, game.getLegalActions(1), null,
                recentGameLog = listOf("Opponent drew Counterspell"))
            val action = (result as ActionResponse.SubmitAction).action as CastSpell
            action.targets shouldBe listOf(ChosenTarget.Permanent(target))
            validations shouldBe 1
            game.execute(action).error shouldBe null
        }

        test("Jev chooses attackers and defending players rather than submitting empty combat templates") {
            val game = scenario().withPlayers().withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false).build()
            game.state = game.state.copy(phase = Phase.COMBAT, step = Step.DECLARE_ATTACKERS)
            val attacker = game.findPermanent("Grizzly Bears")!!
            val state = stateTransformer.transform(game.state, game.player1Id)
            val info = LegalActionInfo("DeclareAttackers", "Attack", DeclareAttackers(game.player1Id, emptyMap()),
                validAttackers = listOf(attacker), validAttackTargets = listOf(game.player2Id))
            val q = JevChoices(JevChoiceClient { _, _, choices, _ -> choices.keys.first() }, "state", 1000)
            val action = JevActions(q, state) { it.value }.complete(info) as DeclareAttackers
            action.attackers shouldBe mapOf(attacker to game.player2Id)
            game.execute(action).error shouldBe null
        }

        test("Jev selects blockers and the engine accepts the resulting assignment") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false).build()
            val attacker = game.findPermanent("Grizzly Bears")!!
            val blocker = game.findPermanent("Hill Giant")!!
            game.state = game.state.copy(phase = Phase.COMBAT, step = Step.DECLARE_ATTACKERS)
            game.execute(DeclareAttackers(game.player1Id, mapOf(attacker to game.player2Id))).error shouldBe null
            game.state = game.state.copy(step = Step.DECLARE_BLOCKERS, priorityPlayerId = game.player2Id)
            val state = stateTransformer.transform(game.state, game.player2Id)
            val info = LegalActionInfo("DeclareBlockers", "Block", DeclareBlockers(game.player2Id, emptyMap()), validBlockers = listOf(blocker))
            val q = JevChoices(JevChoiceClient { _, _, choices, _ -> choices.keys.first() }, "state", 1000)
            val action = JevActions(q, state) { it.value }.complete(info) as DeclareBlockers
            action.blockers shouldBe mapOf(blocker to listOf(attacker))
            game.execute(action).error shouldBe null
        }

        test("Jev chooses X and sacrifice payment, rather than the engine AI filling them") {
            val game = scenario().withPlayers().withCardInHand(1, "Lightning Bolt")
                .withCardOnBattlefield(1, "Grizzly Bears").build()
            val spell = game.findCardsInHand(1, "Lightning Bolt").single()
            val bear = game.findPermanent("Grizzly Bears")!!
            val info = LegalActionInfo("CastSpell", "Test parameter assembly", CastSpell(game.player1Id, spell),
                hasXCost = true, maxAffordableX = 5,
                additionalCostInfo = AdditionalCostInfo("Sacrifice a creature", "SacrificePermanent", validSacrificeTargets = listOf(bear)))
            val q = JevChoices(JevChoiceClient { _, prompt, choices, _ ->
                if (prompt.startsWith("Choose X")) choices.entries.first { it.value == "4" }.key else choices.keys.first()
            }, "state", 1000)
            val action = JevActions(q, stateTransformer.transform(game.state, game.player1Id)) { it.value }.complete(info) as CastSpell
            action.xValue shouldBe 4
            action.additionalCostPayment!!.sacrificedPermanents shouldBe listOf(bear)
        }

        test("invalid proposal is corrected with engine feedback before submission") {
            val game = scenario().withPlayers().build()
            val view = stateTransformer.transform(game.state, game.player1Id)
            var calls = 0
            var validations = 0
            val controller = JevAiPlayerController(game.player1Id, JevChoiceClient { state, _, choices, _ ->
                if (++calls == 2) check("test rejection" in state)
                choices.keys.first()
            }, EngineAiPlayerController(cardRegistry, game.player1Id, { error("fallback must not run") }),
                validate = { if (++validations == 1) "test rejection" else null })
            val decision = YesNoDecision("decision", game.player1Id, "Do it?", DecisionContext())
            val result = controller.chooseAction(view, emptyList(), decision) as ActionResponse.SubmitDecision
            result.response shouldBe YesNoResponse("decision", true)
            calls shouldBe 2
        }

        test("transport failure falls back to a legal engine action") {
            val game = scenario().withPlayers().build()
            val controller = JevAiPlayerController(game.player1Id, JevChoiceClient { _, _, _, _ -> error("offline") },
                EngineAiPlayerController(cardRegistry, game.player1Id, { game.state }), validate = { null })
            val decision = YesNoDecision("decision", game.player1Id, "Do it?", DecisionContext())
            val result = controller.chooseAction(stateTransformer.transform(game.state, game.player1Id), emptyList(), decision)
            (result is ActionResponse.SubmitDecision) shouldBe true
        }
    }
}
