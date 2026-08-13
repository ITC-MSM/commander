package com.wingedsheep.ai.llm.decision.handlers

import com.wingedsheep.ai.llm.AiResponseParser
import com.wingedsheep.ai.llm.GameStateFormatter
import com.wingedsheep.ai.llm.decision.AiDecisionHandler
import com.wingedsheep.engine.core.DecisionResponse
import com.wingedsheep.engine.core.OrderTriggeredAbilitiesDecision
import com.wingedsheep.engine.core.TriggeredAbilitiesOrderedResponse
import com.wingedsheep.engine.view.ClientGameState
import com.wingedsheep.sdk.model.EntityId
import kotlin.reflect.KClass

class OrderTriggeredAbilitiesHandler : AiDecisionHandler<OrderTriggeredAbilitiesDecision> {
    override val decisionType: KClass<OrderTriggeredAbilitiesDecision> = OrderTriggeredAbilitiesDecision::class

    override fun autoResolve(decision: OrderTriggeredAbilitiesDecision): DecisionResponse =
        TriggeredAbilitiesOrderedResponse(decision.id, decision.abilities.map { it.id })

    override fun format(
        sb: StringBuilder,
        decision: OrderTriggeredAbilitiesDecision,
        state: ClientGameState,
        labels: Map<EntityId, String>,
    ) {
        sb.appendLine("Order these triggered abilities; first is put on the stack first and resolves last:")
        decision.abilities.forEachIndexed { index, ability ->
            sb.appendLine("  [${GameStateFormatter.actionLetter(index)}] ${ability.sourceName}: ${ability.description}")
        }
    }

    override fun parse(
        response: String,
        decision: OrderTriggeredAbilitiesDecision,
        state: ClientGameState,
        parser: AiResponseParser,
    ): DecisionResponse? = parser.parseOrdering(response, decision.abilities.size)?.let { ordering ->
        TriggeredAbilitiesOrderedResponse(decision.id, ordering.map { decision.abilities[it].id })
    }
}
