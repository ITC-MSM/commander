package com.wingedsheep.engine.core

import com.wingedsheep.engine.state.ComponentContainer
import kotlinx.serialization.PolymorphicSerializer

/** Internal seam for testing consumers' fail-closed response to projection errors. */
internal interface InFlightReferenceProjector {
    fun project(stackObject: ComponentContainer): TypedEntityReferences.Projection
    fun project(decision: PendingDecision): TypedEntityReferences.Projection
    fun project(frame: ContinuationFrame): TypedEntityReferences.Projection
}

/**
 * Typed entity-reference projection for persisted in-flight engine execution.
 *
 * Live stack objects, pending decisions, and continuation frames are serializable graphs, so they
 * use the same serializer-backed traversal as [TypedEntityReferences]. Hidden-world consumers only
 * need set membership and read it off
 * [TypedEntityReferences.Projection.Complete.entityIds]; any serialization failure stays explicit
 * so they fail closed rather than treating an incomplete graph as unreferenced.
 */
internal object InFlightEntityReferences : InFlightReferenceProjector {

    override fun project(stackObject: ComponentContainer): TypedEntityReferences.Projection =
        TypedEntityReferences.project(ComponentContainer.serializer(), stackObject)

    override fun project(decision: PendingDecision): TypedEntityReferences.Projection =
        TypedEntityReferences.project(PolymorphicSerializer(PendingDecision::class), decision)

    override fun project(frame: ContinuationFrame): TypedEntityReferences.Projection =
        TypedEntityReferences.project(PolymorphicSerializer(ContinuationFrame::class), frame)
}
