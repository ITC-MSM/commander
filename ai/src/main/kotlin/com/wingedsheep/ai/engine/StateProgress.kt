package com.wingedsheep.ai.engine

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.AbilityActivatedEverComponent
import com.wingedsheep.engine.state.components.battlefield.AbilityActivatedThisTurnComponent
import com.wingedsheep.engine.state.components.battlefield.TargetedByControllerThisTurnComponent
import com.wingedsheep.engine.state.components.battlefield.TimestampComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId

/**
 * "Is this position one I have already been in?" — how the AI tells progress from a treadmill.
 *
 * Two shapes of the same bug live here. The first is an **inert** action, one whose resolved
 * position is the position it started from: Aphetto Alchemist's `{T}: Untap target artifact or
 * creature` aimed at itself taps for its own cost and then untaps itself again. The second is a
 * **cycle** — two Alchemists untapping each other — where every single step does change the board
 * and the sequence still goes nowhere.
 *
 * A leaf-scoring search has no defence against either on its own. It compares "take this action"
 * against "pass" by scoring the two positions they lead to, and those two positions are not
 * measured at the same point in the game: passing carries the game forward into whatever was about
 * to happen, while a free ability that resolves back onto the same board stops right where it is.
 * When what is about to happen is bad, doing nothing at all can therefore *outscore* passing — and
 * once it does, it does so again from the identical position it just produced, forever. That is the
 * bug this exists to prevent: the AI activated Aphetto Alchemist until the game had to be
 * abandoned.
 *
 * So this is not a heuristic about value — the evaluator owns that question. It is the structural
 * claim underneath it, and the rules make the same one: CR 732.3 requires a player whose actions
 * have "resulted in the same game state being reached multiple times" to make a *different* game
 * choice, and its example turns on the very thing [IGNORED_COMPONENTS] does — the loop repeats a
 * position when "nothing in the game cares how many times an ability has been activated."
 *
 * [Strategist] is the consumer: it drops any candidate whose leaf repeats a position it has already
 * acted from.
 */
object StateProgress {

    /**
     * Whether [after] is the same game position as [before], so the action between them
     * accomplished nothing at all.
     *
     * Compares a digest rather than the states themselves. `GameState` equality is unusable here —
     * a resolved ability leaves behind an orphaned stack entity, a bumped `nextEntityId` and an
     * advanced `rng`, none of which is a game fact — so [digest] reads the position the way a
     * player would: zone contents, and everything true of the objects and players in them.
     */
    fun isInert(before: GameState, after: GameState): Boolean = digest(before) == digest(after)

    /**
     * A 64-bit summary of everything about [state] that a player could point at.
     *
     * Deliberately blind to bookkeeping that changes on *every* activation regardless of what the
     * ability did — see [IGNORED_COMPONENTS] — and to whose priority it is, which is what makes an
     * action's own resolution comparable with the position it started from.
     *
     * Turn number and step *are* part of it, which is what keeps the repetition memory honest: the
     * same board in a later step is a different position, so a digest can only recur inside the
     * window where recurring means going in circles.
     *
     * Per-collection hashes are summed rather than folded, so a `Map` that rehashed between the two
     * states can't read as a change; order *within* a zone still counts, because library and stack
     * order are game facts.
     */
    fun digest(state: GameState): Long {
        var h = SEED

        h = h.mix(state.turnNumber)
        h = h.mix(state.phase.hashCode())
        h = h.mix(state.step.hashCode())
        h = h.mix(state.activePlayerId.hashCode())
        h = h.mix(state.gameOver.hashCode())
        h = h.mix(state.winnerId.hashCode())
        h = h.mix(state.stack.hashCode())
        h = h.mix(state.continuationStack.size)
        h = h.mix(state.floatingEffects.hashCode())
        h = h.mix(state.delayedTriggers.hashCode())
        h = h.mix(state.grantedTriggeredAbilities.hashCode())
        h = h.mix(state.grantedActivatedAbilities.hashCode())
        h = h.mix(state.grantedStaticAbilities.hashCode())
        h = h.mix(state.grantedReplacementEffects.hashCode())
        h = h.mix(state.grantedKeywordAbilities.hashCode())
        h = h.mix(state.globalGrantedTriggeredAbilities.hashCode())
        h = h.mix(state.mayPlayPermissions.hashCode())
        h = h.mix(state.commanderDamage.hashCode())
        h = h.mix(state.spellsCastThisTurn)
        h = h.mix(state.permanentsSacrificedThisTurn)

        var zones = 0L
        for ((key, contents) in state.zones) {
            var zone = key.hashCode().toLong()
            for (entityId in contents) {
                zone = zone.mix(entityId.hashCode())
                // A library is hashed by its order alone: 60 cards whose components no game action
                // touches without also moving them somewhere this digest reads in full.
                if (key.zoneType != Zone.LIBRARY) zone = zone.mix(objectHash(state, entityId))
            }
            zones += zone
        }
        h = h.mix(zones)

        var objects = 0L
        for (entityId in state.stack) objects += objectHash(state, entityId)
        for (playerId in state.turnOrder) objects += objectHash(state, playerId)
        return h.mix(objects)
    }

    /** Everything the ECS records about one object, minus the ignored bookkeeping. */
    private fun objectHash(state: GameState, entityId: EntityId): Long {
        val container = state.getEntity(entityId) ?: return 0L
        var h = 0L
        for (component in container.all()) {
            val type = component::class.java
            if (type in IGNORED_COMPONENTS) continue
            h += type.name.hashCode().toLong().mix(component.hashCode())
        }
        return h
    }

    private fun Long.mix(value: Int): Long = this * 0x100000001B3L xor value.toLong()

    private fun Long.mix(value: Long): Long = this * 0x100000001B3L xor value

    /**
     * "It happened" memories, as opposed to game position.
     *
     * Each of these records *that* an action was taken — the once-per-turn and `MaxPerTurn`
     * activation counts, Valiant's "has this been targeted by its controller yet this turn", and the
     * timestamp bumped when a continuous effect is re-applied. An activation changes them even when
     * it changed nothing else, so reading them would make every inert action look like progress —
     * exactly the reading this object exists to avoid. Nothing is lost by the omission: whatever
     * these memories gate (a Valiant trigger, a second activation being legal at all) shows up in
     * the position the moment it actually does something.
     *
     * The list is a floor, not a ceiling: a memory component not named here makes an inert action
     * read as progress, so the AI takes it once more than it should. Which is why it fails in that
     * direction — a *missing* entry costs a wasted activation, whereas wrongly ignoring something
     * real would cost the AI an ability it should have used.
     */
    private val IGNORED_COMPONENTS = setOf<Class<*>>(
        AbilityActivatedThisTurnComponent::class.java,
        AbilityActivatedEverComponent::class.java,
        TargetedByControllerThisTurnComponent::class.java,
        TimestampComponent::class.java,
    )

    private const val SEED = -0x340d631b7bdddcdbL
}
