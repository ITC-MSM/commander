package com.wingedsheep.engine.hidden

import com.wingedsheep.engine.core.CardEntityFactory
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.TargetsComponent
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.EntityId

/**
 * Swapping the card identity occupying a hidden zone slot, without disturbing anything else.
 *
 * Both hidden-information callers need the same three answers — is this slot safe to rewrite,
 * which slots are pinned by in-flight stack references, and how is the rewrite applied — while
 * differing entirely in *policy*: [com.wingedsheep.engine.hidden.HiddenWorldMaterializer] installs
 * a caller-supplied assignment and refuses anything it cannot install, whereas the AI's
 * `Determinizer` samples an assignment from a visibility model and silently pins whatever it
 * cannot rewrite. Keeping the mechanics here means those two policies cannot drift into two
 * different notions of "safe".
 */
object HiddenSlotRewrite {

    /**
     * The components on [container] that block rewriting the slot to a different card identity,
     * by simple name and sorted.
     *
     * The safe set is derived rather than listed: build what [CardEntityFactory] would produce for
     * the definition *currently* occupying the slot, and every component that doesn't match is
     * runtime state. That keeps the check in lockstep with new definition-derived components
     * instead of drifting from a hand-maintained allowlist. Two consequences worth naming:
     *
     * - [CardComponent] is excluded, because it is exactly the identity being replaced. It is also
     *   the one component that legitimately differs from the factory output on a well-formed slot:
     *   scenario builders and pinned printings stamp their own.
     * - `RevealedToComponent` is *not* excluded, so a slot someone has already been shown is
     *   blocked. Preserving a reveal across an identity swap would leave a player holding a
     *   pointer to a card they never saw, which is the incoherence this check exists to prevent.
     *
     * Components the factory would produce that are *missing* from [container] are not blockers:
     * the rewrite regenerates them, and a zone transition legitimately strips [ControllerComponent].
     */
    fun runtimeBlockers(
        container: ComponentContainer,
        currentDefinition: CardDefinition,
        ownerId: EntityId,
    ): List<String> {
        val expectedByType = CardEntityFactory.create(currentDefinition, ownerId)
            .all()
            .associateBy { it::class.java }
        return container.all()
            .filterNot { it is CardComponent }
            .filter { component -> expectedByType[component::class.java] != component }
            .map { component -> component::class.simpleName ?: component::class.java.name }
            .sorted()
    }

    /**
     * Every entity id a stack object's chosen targets point at.
     *
     * A stack object records the object it targets, so rewriting that object's identity underneath
     * it produces a spell aimed at a card that was never there. Targets are the one in-flight
     * reference shape with a uniform representation ([TargetsComponent]); continuation frames and
     * pending decisions carry entity references in per-effect shapes with no common visitor, so
     * callers gate on those wholesale rather than per slot.
     */
    fun stackReferencedEntities(state: GameState): Set<EntityId> =
        state.stack.flatMapTo(mutableSetOf()) { stackId ->
            state.getEntity(stackId)?.get<TargetsComponent>()?.targets.orEmpty().mapNotNull {
                when (it) {
                    is ChosenTarget.Card -> it.cardId
                    is ChosenTarget.Permanent -> it.entityId
                    is ChosenTarget.Spell -> it.spellEntityId
                    is ChosenTarget.Player -> null
                }
            }
        }

    /**
     * Rebuild [entityId] as [definition], keeping the slot itself intact.
     *
     * The entity id, its position in its zone, and every other part of [state] are untouched;
     * only the components [CardEntityFactory] derives from the printed card are replaced. The
     * source's [ControllerComponent] carries over as-is — including its absence, which is how a
     * card that has left the battlefield looks.
     *
     * Callers must have cleared [runtimeBlockers] for this slot first: this function applies the
     * rewrite, it does not re-check whether the rewrite is safe.
     */
    fun rewrite(
        state: GameState,
        entityId: EntityId,
        definition: CardDefinition,
        ownerId: EntityId,
    ): GameState {
        val source = state.getEntity(entityId) ?: return state
        val rebuilt = CardEntityFactory.create(definition, ownerId)
        val withController = source.get<ControllerComponent>()
            ?.let { rebuilt.with(it) }
            ?: rebuilt.without<ControllerComponent>()
        return state.withEntity(entityId, withController)
    }
}
