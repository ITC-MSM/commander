package com.wingedsheep.gym.trainer.spi

import com.wingedsheep.engine.core.DecisionResponse
import com.wingedsheep.engine.core.PendingDecision
import com.wingedsheep.engine.state.GameState

/**
 * Resolves "complex" engine decisions — `ChooseTargetsDecision`,
 * `DistributeDecision`, `OrderObjectsDecision`, `SplitPilesDecision`,
 * `SearchLibraryDecision`, `ReorderLibraryDecision`, `AssignDamageDecision`,
 * `SelectManaSourcesDecision`, multi-select `SelectCardsDecision`, and
 * multi-mode `ChooseModeDecision` — that the gym can't fold into a single
 * numeric action space.
 *
 * This is a strategic policy seam: it chooses one response. It remains the
 * fallback when [StructuredDecisionExpander] reports a decision family as unsupported; that
 * resolver-selected edge is not claimed to be the complete legal response set.
 * Supplying a custom [ActionFeaturizer] cannot expose responses that were
 * already collapsed here; enumeration belongs in [StructuredDecisionExpander].
 */
fun interface StructuredDecisionResolver {
    fun resolve(state: GameState, decision: PendingDecision): DecisionResponse
}
