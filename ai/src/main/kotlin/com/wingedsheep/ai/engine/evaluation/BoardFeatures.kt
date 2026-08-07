package com.wingedsheep.ai.engine.evaluation

import com.wingedsheep.engine.mechanics.layers.ActiveFloatingEffect
import com.wingedsheep.engine.mechanics.layers.ProjectedState
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.ai.engine.OpponentAggregate
import com.wingedsheep.ai.engine.knowledge.CardIntent
import com.wingedsheep.ai.engine.knowledge.IntentCatalog
import com.wingedsheep.ai.engine.lifePoolsOf
import com.wingedsheep.ai.engine.sidesFor
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.battlefield.SummoningSicknessComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.LandDropsComponent
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId

// ═════════════════════════════════════════════════════════════════════════════
// Life Differential
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Non-linear life differential. Being at 3 life is exponentially worse
 * than being at 13 — each point near death is worth much more.
 *
 * Life is a resource, not a threat — an opponent sitting on 40 does not attack you — so the fold
 * over a pod is [OpponentAggregate.FIELD]: how the AI is doing against the table, not against its
 * scariest neighbour. Sides are valued per life *pool*, so a Two-Headed Giant team's shared 30
 * counts once (CR 810.4).
 */
object LifeDifferential : BoardFeature {
    override fun score(state: GameState, projected: ProjectedState, playerId: EntityId): Double {
        val sides = state.sidesFor(playerId) ?: return 0.0
        val mine = sideLifeValue(state, sides.mine)
        return sides.against(OpponentAggregate.FIELD) { opponent ->
            mine - sideLifeValue(state, opponent)
        }
    }

    private fun sideLifeValue(state: GameState, side: List<EntityId>): Double =
        state.lifePoolsOf(side).sumOf { lifeValue(it) }

    /**
     * Public so a consumer that wants to price *anticipated* life loss can charge it at exactly
     * the rate the evaluator charges life already, instead of inventing a second constant.
     * [com.wingedsheep.ai.engine.CombatAdvisor]'s crack-back estimate is the caller.
     */
    fun lifeValue(life: Int): Double = when {
        life <= 0 -> -100.0
        life <= 3 -> life * 3.0
        life <= 7 -> 9.0 + (life - 3) * 2.0
        life <= 15 -> 17.0 + (life - 7) * 1.0
        else -> 25.0 + (life - 15) * 0.5
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Board Presence
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Total effective board value. Creatures are scored by combat stats + keywords.
 * Non-creature permanents get type-appropriate values. Enchantments and artifacts
 * that aren't auras get a flat bonus (they're doing something even without P/T).
 *
 * A board is a threat, so a pod folds with [OpponentAggregate.THREAT] — the runaway leader
 * dominates. A team's board is the sum of its members' (CR 810: teammates attack and block as one
 * side, so their permanents defend each other).
 */
object BoardPresence : BoardFeature {
    /**
     * Score with no card knowledge — the pre-Phase-6 behaviour, and what every caller that has no
     * [IntentCatalog] to hand still gets.
     */
    override fun score(state: GameState, projected: ProjectedState, playerId: EntityId): Double =
        score(state, projected, playerId, IntentCatalog.NONE)

    /**
     * Score with [intents] supplying a per-card prior for non-creature permanents.
     *
     * `EvaluationWeights.toEvaluator(intents)` binds this into the evaluator; on
     * [IntentCatalog.NONE] it is exactly [score].
     */
    fun score(
        state: GameState,
        projected: ProjectedState,
        playerId: EntityId,
        intents: IntentCatalog,
    ): Double {
        val sides = state.sidesFor(playerId) ?: return 0.0
        val mine = boardValue(state, projected, sides.mine, intents)
        return sides.against(OpponentAggregate.THREAT) { opponent ->
            mine - boardValue(state, projected, opponent, intents)
        }
    }

    private fun boardValue(
        state: GameState,
        projected: ProjectedState,
        side: List<EntityId>,
        intents: IntentCatalog,
    ): Double {
        var total = 0.0
        for (playerId in side) {
            for (entityId in projected.getBattlefieldControlledBy(playerId)) {
                val card = state.getEntity(entityId)?.get<CardComponent>() ?: continue
                total += permanentValue(state, projected, entityId, card, intents)
            }
        }
        return total
    }

    internal fun permanentValue(
        state: GameState,
        projected: ProjectedState,
        entityId: EntityId,
        card: CardComponent,
        intents: IntentCatalog = IntentCatalog.NONE,
    ): Double {
        val container = state.getEntity(entityId) ?: return 0.0

        if (projected.isCreature(entityId)) {
            return creatureValue(state, projected, entityId, container)
        }

        // Non-creature permanents
        if (card.isLand) {
            return if (container.has<TappedComponent>()) 0.3 else 0.6
        }

        // What the structural analyzer makes of the card, if this agent has card knowledge on.
        // Phase 6: `intent` is a *floor*, never a ceiling. The shape-based values below are read
        // off live game state (this Aura is attached to something, this O-Ring is holding a card)
        // and know things a static prior cannot; the prior knows what the card *does*. Taking the
        // max keeps both, and guarantees no permanent is ever valued lower than it was before
        // Phase 6 — so a position the AI played correctly on the old numbers still scores at least
        // as well.
        val prior = priorValue(projected, entityId, container, card, intents)

        // Planeswalkers: the flat 4.0 priced a fresh Jace and a Jace at 1 loyalty identically.
        // Loyalty is both the walker's life total and its remaining activations, so it is the one
        // number that has to be in here; the intent prior carries what its abilities *do*.
        if (card.isPlaneswalker) {
            val loyalty = container.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0
            return maxOf(4.0, prior + loyalty * LOYALTY_VALUE)
        }

        // Auras/equipment attached to something are valuable
        if (container.has<com.wingedsheep.engine.state.components.battlefield.AttachedToComponent>()) {
            return maxOf(1.5, prior)
        }

        // Enchantments/artifacts exiling something (O-Ring effects) are valuable
        if (container.has<com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent>()) {
            return maxOf(2.5, prior)
        }

        // General non-creature permanents. Without card knowledge this is a flat 0.5 regardless of
        // text — a signet, an Oblivion Ring and a Bitterblossom scoring the same number is the
        // blindness Phase 6 exists to remove.
        return maxOf(0.5, prior)
    }

    /**
     * The prior for the permanent [entityId] *as it currently stands* — which is not the same
     * question as what its card can do.
     *
     * [IntentCatalog.forPermanent] answers with one [CardIntent] per reading in force: one for an
     * ordinary permanent, one per **unlocked** door for a Room (CR 709.5). Reading a Room as a
     * whole card would price it the same however many doors are open, which is exactly why the AI
     * would cast a half and then never pay to unlock the other — a special action that moves no
     * evaluated number can never beat passing.
     *
     * The readings combine as a baseline plus what each one adds *over* it, rather than a plain
     * sum. `UNKNOWN.staticPriorValue` is the value of a permanent the analyzer cannot read, and a
     * Room with two blank halves is still one unreadable permanent, not two — summing raw would
     * make it outrank every other unreadable permanent for no reason at all.
     */
    private fun priorValue(
        projected: ProjectedState,
        entityId: EntityId,
        container: ComponentContainer,
        card: CardComponent,
        intents: IntentCatalog,
    ): Double {
        val inForce = intents.forPermanent(container, card.name)
        if (inForce.isEmpty()) return 0.0
        val unreadable = CardIntent.UNKNOWN.staticPriorValue
        return unreadable + inForce.sumOf {
            (intentValue(projected, entityId, it) - unreadable).coerceAtLeast(0.0)
        }
    }

    /**
     * A permanent's [CardIntent] prior, plus the one board-dependent term that is cheap to read.
     *
     * An anthem is the case where a static prior is genuinely not enough: "creatures you control
     * get +1/+1" behind an empty board and the same card behind five creatures are different cards.
     * The creatures' own [creatureValue] already contains the pumped stats, so this counts only
     * what killing the anthem would *take back* — a quarter point per point of P+T handed out,
     * which is a little over half the rate [creatureValue] itself pays for toughness.
     */
    private fun intentValue(
        projected: ProjectedState,
        entityId: EntityId,
        intent: CardIntent,
    ): Double {
        if (intent.anthemBonus <= 0) return intent.staticPriorValue
        val controller = projected.getController(entityId) ?: return intent.staticPriorValue
        val pumped = projected.getBattlefieldControlledBy(controller).count { projected.isCreature(it) }
        return intent.staticPriorValue + ANTHEM_VALUE_PER_STAT * intent.anthemBonus * pumped
    }

    /** Board value of one point of P/T an anthem hands to one creature. */
    private const val ANTHEM_VALUE_PER_STAT = 0.25

    /** Board value of one loyalty counter. Only reached with card knowledge on. */
    private const val LOYALTY_VALUE = 0.8

    private fun creatureValue(
        state: GameState,
        projected: ProjectedState,
        entityId: EntityId,
        container: ComponentContainer
    ): Double {
        val power = projected.getPower(entityId) ?: 0
        val toughness = projected.getToughness(entityId) ?: 0
        val keywords = projected.getKeywords(entityId)

        // Discount temporary P/T modifications. "Until end of turn" effects expire
        // at cleanup — evaluate using the permanent stats so killing a 2/2 with -2/-2
        // scores better than merely shrinking a 2/3 (which recovers next turn).
        val tempMod = temporaryPTModification(state, entityId)
        val settledPower = (power - tempMod.first).coerceAtLeast(0)
        val settledToughness = (toughness - tempMod.second).coerceAtLeast(0)

        // Base: power matters more than toughness for winning
        var value = settledPower * 1.0 + settledToughness * 0.4

        // ── Evasion (the most important combat keyword category) ──
        if (Keyword.FLYING.name in keywords) value += 1.5 + power * 0.3
        if (Keyword.MENACE.name in keywords) value += 0.8
        if (Keyword.FEAR.name in keywords) value += 0.8
        if (Keyword.INTIMIDATE.name in keywords) value += 0.8
        if (Keyword.SHADOW.name in keywords) value += 1.0
        // Landwalk — contextual but often evasion
        if (Keyword.SWAMPWALK.name in keywords) value += 0.6
        if (Keyword.FORESTWALK.name in keywords) value += 0.6
        if (Keyword.ISLANDWALK.name in keywords) value += 0.6
        if (Keyword.MOUNTAINWALK.name in keywords) value += 0.6

        // ── Combat modifiers ──
        if (Keyword.TRAMPLE.name in keywords) value += 0.5 + power * 0.2
        if (Keyword.DEATHTOUCH.name in keywords) value += 2.0  // trades with anything
        if (Keyword.FIRST_STRIKE.name in keywords) value += 1.0 + power * 0.2
        if (Keyword.DOUBLE_STRIKE.name in keywords) value += 2.0 + power * 0.5
        if (Keyword.LIFELINK.name in keywords) value += 0.5 + power * 0.3
        if (Keyword.VIGILANCE.name in keywords) value += 0.8  // attacks without cost
        if (Keyword.PROVOKE.name in keywords) value += 0.5

        // ── Survivability ──
        if (Keyword.INDESTRUCTIBLE.name in keywords) value += 3.0
        if (Keyword.HEXPROOF.name in keywords) value += 1.5
        if (Keyword.SHROUD.name in keywords) value += 1.2
        if (Keyword.PROTECTION.name in keywords) value += 1.0

        // ── Drawbacks ──
        if (Keyword.DEFENDER.name in keywords) value -= power * 0.8 // can't attack, power mostly wasted

        // ── Combat restrictions (Pacifism, "can't block this turn", etc.) ──
        // A creature that can't block has lost its defensive value; one that can't attack can't
        // pressure. Pricing these keeps the AI from stacking redundant "target creature can't
        // block / can't attack" effects on a creature already under that restriction: hitting a
        // fresh target strictly lowers the opponent's board value, re-hitting a restricted one
        // does not. (DEFENDER's can't-attack is already priced just above — don't double-count.)
        if (projected.cantBlock(entityId)) value *= 0.85
        if (projected.cantAttack(entityId) && Keyword.DEFENDER.name !in keywords) value *= 0.85

        // ── Speed ──
        if (Keyword.HASTE.name in keywords && container.has<SummoningSicknessComponent>()) {
            value += 0.5 // haste is most valuable the turn it enters
        }

        // +1/+1 counters represent permanent investment
        val counters = container.get<CountersComponent>()
        val plusCounters = counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        value += plusCounters * 0.5

        // Summoning sickness reduces immediate threat
        if (container.has<SummoningSicknessComponent>() && Keyword.HASTE.name !in keywords) {
            value *= 0.85
        }

        // Tapped creatures can't block, but tapping is temporary — light discount
        if (container.has<TappedComponent>()) {
            value *= 0.9
        }

        // Damaged creatures are closer to dying
        val damage = container.get<DamageComponent>()?.amount ?: 0
        if (damage > 0 && toughness > 0) {
            val healthFraction = (toughness - damage).toDouble() / toughness
            value *= (0.5 + 0.5 * healthFraction) // half value at 1 toughness remaining
        }

        return value.coerceAtLeast(0.1)
    }

    /**
     * Sum temporary P/T modifications from "until end of turn" floating effects.
     * Returns (powerMod, toughnessMod) that will expire at cleanup.
     */
    private fun temporaryPTModification(state: GameState, entityId: EntityId): Pair<Int, Int> {
        var powerMod = 0
        var toughnessMod = 0
        for (effect in state.floatingEffects) {
            if (effect.duration != Duration.EndOfTurn) continue
            if (entityId !in effect.effect.affectedEntities) continue
            val mod = effect.effect.modification
            if (mod is SerializableModification.ModifyPowerToughness) {
                powerMod += mod.powerMod
                toughnessMod += mod.toughnessMod
            }
        }
        return powerMod to toughnessMod
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Card Advantage
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Cards in hand with non-linear scaling. Empty hand (topdeck mode) is heavily
 * penalized. Excess cards have diminishing returns since you'll discard at cleanup.
 *
 * A resource, so a pod folds with [OpponentAggregate.FIELD]. Hands are per player even in a team
 * format (CR 810 pools life and poison, not cards), so a side's value is the sum over its members
 * — two teammates in topdeck mode really is twice the disaster.
 */
object CardAdvantage : BoardFeature {
    /** The historical empty-hand value. See [EvaluationWeights.topdeckPenalty] for why it moves. */
    const val LEGACY_TOPDECK_PENALTY = -3.0

    override fun score(state: GameState, projected: ProjectedState, playerId: EntityId): Double =
        score(state, projected, playerId, LEGACY_TOPDECK_PENALTY)

    fun score(
        state: GameState,
        @Suppress("UNUSED_PARAMETER") projected: ProjectedState,
        playerId: EntityId,
        topdeckPenalty: Double,
        landDropIsNotCardLoss: Boolean = false,
    ): Double {
        val sides = state.sidesFor(playerId) ?: return 0.0
        val mine = sideHandValue(state, sides.mine, topdeckPenalty, landDropIsNotCardLoss)
        return sides.against(OpponentAggregate.FIELD) { opponent ->
            mine - sideHandValue(state, opponent, topdeckPenalty, landDropIsNotCardLoss)
        }
    }

    private fun sideHandValue(
        state: GameState,
        side: List<EntityId>,
        topdeckPenalty: Double,
        landDropIsNotCardLoss: Boolean,
    ): Double =
        side.sumOf { cardValue(heldCardCount(state, it, landDropIsNotCardLoss), topdeckPenalty) }

    /**
     * How many cards in [playerId]'s hand are *cards* rather than mana waiting to be tapped.
     *
     * With [landDropIsNotCardLoss] off this is the hand size, which is what every published number
     * before 2026-08-07 was measured on.
     *
     * On, it holds back one land per unused land drop. Playing a land does not spend a card — it
     * relocates one, from a zone this feature prices to the battlefield that [Tempo] and
     * [BoardPresence] price. Charging the move as card loss made it a strict debit: the land drop
     * paid 1.5–3.0 here to buy 2.1 there, and at the empty-hand cliff it did not cover the
     * difference, which is why the AI would sit on its last land for the rest of the game
     * (`sequencing-02`). Holding the drop back on *both* sides of the count makes the move exactly
     * card-neutral, so [Tempo] and [BoardPresence] decide it alone.
     *
     * `LandDropsComponent.remaining` rather than the enumerator's `canPlayLand`, which also demands
     * main phase / empty stack / your turn. Land drops reset for every player at cleanup, so
     * `remaining` reads 1 for whoever is not the active player too — the earmark is symmetric and
     * survives a turn boundary instead of flickering on and off within one. It also misses the
     * `GrantAdditionalLandDrop` statics that `LandDropUtils` adds on top, which needs a
     * `CardRegistry` this feature does not have: under an Exploration the second drop is still
     * charged as card loss, which is the old behaviour rather than a new error.
     */
    private fun heldCardCount(state: GameState, playerId: EntityId, landDropIsNotCardLoss: Boolean): Int {
        val hand = state.getZone(playerId, Zone.HAND)
        if (!landDropIsNotCardLoss) return hand.size
        val drops = state.getEntity(playerId)?.get<LandDropsComponent>()?.remaining ?: 0
        if (drops <= 0) return hand.size
        val lands = hand.count { state.getEntity(it)?.get<CardComponent>()?.isLand == true }
        return hand.size - minOf(lands, drops)
    }

    private fun cardValue(count: Int, topdeckPenalty: Double): Double = when {
        count <= 0 -> topdeckPenalty
        count == 1 -> 1.0
        count <= 3 -> 1.0 + (count - 1) * 1.5
        count <= 7 -> 4.0 + (count - 3) * 0.8
        else -> 7.2 + (count - 7) * 0.2 // past 7 cards, you're discarding anyway
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Threat Assessment
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Measures how close the opponent is to killing you with creatures on board.
 * A negative score means they have enough power to threaten lethal.
 *
 * This is distinct from LifeDifferential — it measures *potential* damage, not
 * actual life totals. An opponent at 5 life with 15 power on board is more
 * dangerous than one at 5 life with 1 power.
 *
 * The whole race calculation runs once per opposing side and folds with
 * [OpponentAggregate.THREAT]: this is the feature that is *about* who is going to kill whom, so the
 * matchup the AI is losing has to dominate. A side's clock is the sum of its members' power
 * (CR 805.10 — teammates attack together) and its life is the *lowest* pool on that side, since a
 * team dies when any of its pools does.
 */
object ThreatAssessment : BoardFeature {
    override fun score(state: GameState, projected: ProjectedState, playerId: EntityId): Double {
        val sides = state.sidesFor(playerId) ?: return 0.0

        val myLife = sideLife(state, sides.mine)

        // Calculate attack potential (power of untapped, non-sick creatures)
        val myAttackPower = attackPotential(state, projected, sides.mine)

        // Calculate defense capability (total toughness of untapped creatures that can block)
        val myDefense = defensePotential(state, projected, sides.mine)

        return sides.against(OpponentAggregate.THREAT) { opponent ->
            val theirLife = sideLife(state, opponent)
            val theirAttackPower = attackPotential(state, projected, opponent)
            val theirDefense = defensePotential(state, projected, opponent)

            // How many turns until opponent kills us (if we can't block)
            val turnsUntilDead = if (theirAttackPower > 0) myLife.toDouble() / theirAttackPower else 99.0
            val turnsUntilWeKill = if (myAttackPower > 0) theirLife.toDouble() / myAttackPower else 99.0

            // Score: positive if we're the faster clock
            var score = 0.0

            // Being closer to killing them is good
            if (turnsUntilWeKill < turnsUntilDead) {
                score += (turnsUntilDead - turnsUntilWeKill) * 2.0
            } else {
                score -= (turnsUntilWeKill - turnsUntilDead) * 1.5
            }

            // Lethal on board next turn is very valuable
            if (myAttackPower >= theirLife && myAttackPower > theirDefense) score += 8.0
            if (theirAttackPower >= myLife && theirAttackPower > myDefense) score -= 10.0

            // Evasive damage (flying power they can't block)
            val theirEvasivePower = evasivePower(state, projected, opponent, sides.mine)
            val myEvasivePower = evasivePower(state, projected, sides.mine, opponent)
            score += (myEvasivePower - theirEvasivePower) * 0.5

            score
        }
    }

    /** A side dies when its weakest life pool does, so the clock runs against the minimum. */
    private fun sideLife(state: GameState, side: List<EntityId>): Int =
        state.lifePoolsOf(side).minOrNull() ?: 20

    private fun attackPotential(state: GameState, projected: ProjectedState, side: List<EntityId>): Int {
        // Don't filter by TappedComponent — tapped creatures untap on the next
        // turn and can attack again. Filtering them out massively penalizes the
        // post-combat state (where our creatures are tapped from attacking),
        // making the AI think attacking reduced its clock to zero.
        return side.sumOf { playerId ->
            projected.getBattlefieldControlledBy(playerId)
                .filter { entityId ->
                    projected.isCreature(entityId) &&
                        !projected.cantAttack(entityId) &&
                        state.getEntity(entityId)?.has<SummoningSicknessComponent>() != true
                }
                .sumOf { (projected.getPower(it) ?: 0).coerceAtLeast(0) }
        }
    }

    private fun defensePotential(state: GameState, projected: ProjectedState, side: List<EntityId>): Int {
        return side.sumOf { playerId ->
            projected.getBattlefieldControlledBy(playerId)
                .filter { entityId ->
                    projected.isCreature(entityId) &&
                        !projected.cantBlock(entityId) &&
                        state.getEntity(entityId)?.has<TappedComponent>() != true
                }
                .sumOf { (projected.getToughness(it) ?: 0).coerceAtLeast(0) }
        }
    }

    /** Power of creatures with flying/evasion that the defending side can't block. */
    private fun evasivePower(
        state: GameState,
        projected: ProjectedState,
        attackers: List<EntityId>,
        defenders: List<EntityId>
    ): Int {
        val defenderHasFlyers = defenders.any { defenderId ->
            projected.getBattlefieldControlledBy(defenderId).any { entityId ->
                projected.isCreature(entityId) &&
                    state.getEntity(entityId)?.has<TappedComponent>() != true &&
                    (Keyword.FLYING.name in projected.getKeywords(entityId) ||
                        Keyword.REACH.name in projected.getKeywords(entityId))
            }
        }

        if (defenderHasFlyers) return 0 // they can block flyers

        return attackers.sumOf { attackerId ->
            projected.getBattlefieldControlledBy(attackerId)
                .filter { entityId ->
                    projected.isCreature(entityId) &&
                        Keyword.FLYING.name in projected.getKeywords(entityId) &&
                        state.getEntity(entityId)?.has<TappedComponent>() != true &&
                        state.getEntity(entityId)?.has<SummoningSicknessComponent>() != true
                }
                .sumOf { (projected.getPower(it) ?: 0).coerceAtLeast(0) }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Tempo
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Measures mana development and efficiency. Having more available mana means
 * casting bigger spells and holding up responses. Counts lands (not mana pool)
 * since land count is the durable measure of mana development.
 *
 * A resource, so a pod folds with [OpponentAggregate.FIELD]. Mana is *not* pooled by team — CR 810
 * shares life and poison, never mana — so a side's value is the sum of its members' own land
 * curves, evaluated one player at a time.
 */
object Tempo : BoardFeature {
    override fun score(state: GameState, projected: ProjectedState, playerId: EntityId): Double {
        val sides = state.sidesFor(playerId) ?: return 0.0

        // Each land ahead is worth about 1.5 points (mana advantage = tempo)
        // But the first few lands matter more than later ones
        val mine = sideLandValue(state, projected, sides.mine)
        return sides.against(OpponentAggregate.FIELD) { opponent ->
            mine - sideLandValue(state, projected, opponent)
        }
    }

    private fun sideLandValue(state: GameState, projected: ProjectedState, side: List<EntityId>): Double =
        side.sumOf { landValue(countLands(state, projected, it)) }

    private fun countLands(state: GameState, projected: ProjectedState, playerId: EntityId): Int {
        return projected.getBattlefieldControlledBy(playerId).count { entityId ->
            val card = state.getEntity(entityId)?.get<CardComponent>()
            card?.isLand == true
        }
    }

    private fun landValue(count: Int): Double = when {
        count <= 0 -> -5.0  // no mana is terrible
        count <= 3 -> count * 2.0  // early mana is critical
        count <= 6 -> 6.0 + (count - 3) * 1.2  // mid-game mana
        else -> 9.6 + (count - 6) * 0.4  // diminishing returns for excess mana
    }
}
