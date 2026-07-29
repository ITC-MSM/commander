package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.MayCastSelfFromZones
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.StaticAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.conditions.AllConditions
import com.wingedsheep.sdk.scripting.conditions.Condition
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Add **Start your engines!** (Aetherdrift, CR 702.179a).
 *
 * "If a player controls a permanent with start your engines! and that player has no speed, their
 * speed becomes 1."
 *
 * Nothing but the keyword is wired here, and that is the whole design: raising a controller's speed
 * to 1 is a *state-based action* (CR 704.5z), not a triggered ability, so the engine's
 * `StartYourEnginesCheck` does it by scanning projected battlefield permanents for
 * [Keyword.START_YOUR_ENGINES]. Consequences that fall out for free:
 *
 * - Gaining control of an opponent's permanent with the keyword starts *your* speed.
 * - Granting the keyword to a permanent at runtime works, because the scan reads projected state.
 * - There is no trigger to miss, no window in which a permanent with the keyword is on the
 *   battlefield while its controller still has no speed, and no ordering interaction with the
 *   inherent speed trigger.
 *
 * Works on any card type — the corpus puts it on creatures, artifacts, enchantments and lands alike.
 * Pair with [maxSpeed] for the "Max speed — [Ability]" half of the mechanic.
 */
fun CardBuilder.startYourEngines() {
    keywords(Keyword.START_YOUR_ENGINES)
}

/**
 * Add one or more **Max speed** abilities (Aetherdrift, CR 702.178a).
 *
 * "Max speed — [Ability]" means "As long as your speed is 4, this object has '[Ability].'" Every
 * ability declared in the block is gated on [Conditions.YouHaveMaxSpeed]; the block itself adds the
 * display-only [Keyword.MAX_SPEED] tag so the client can badge the card.
 *
 * The gate is applied with the vocabulary each ability kind already has, so max speed adds no new
 * ability types:
 *
 * | Declared as | Gate |
 * |---|---|
 * | `staticAbility { }` | [ConditionalStaticAbility] — re-evaluated every projection, so the ability appears and disappears with your speed |
 * | `staticAbility { ability = ModifySpellCost(…) }` | [CostGating.OnlyIf] folded into the modifier itself — cost calculation never reads the layer system, so a wrapper would hide it (Racers' Scoreboard's "Max speed — Spells you cast cost {1} less to cast") |
 * | `activatedAbility { }` | [ActivationRestriction.OnlyIfCondition] — the ability isn't a legal action below max speed |
 * | `triggeredAbility { }` | `triggerCondition` (CR 603.4) — checked when the trigger would fire and again on resolution |
 * | `keywords(…)` | sugar for a gated [GrantKeyword] on the source, covering the common "Max speed — This creature has double strike" shape |
 *
 * One block may hold several abilities — Tsagan, Raider Warlord's "Max speed — Tsagan has
 * deathtouch. Other creatures you control have first strike." is two statics under one gate.
 *
 * Example (Burnout Bashtronaut):
 * ```kotlin
 * startYourEngines()
 * maxSpeed { keywords(Keyword.DOUBLE_STRIKE) }
 * ```
 *
 * Example (Endrider Catalyzer — "Max speed — {T}: Add {R}{R}"):
 * ```kotlin
 * maxSpeed {
 *     activatedAbility {
 *         cost = Costs.Tap
 *         effect = Effects.AddMana("{R}{R}")
 *         manaAbility = true
 *     }
 * }
 * ```
 *
 * **Not covered:** a max-speed-gated *replacement* effect (Vnwxt, Verbose Host's "If you would draw
 * a card, draw two cards instead"; Far Fortune, End Boss's damage rider). Replacement effects are
 * read straight off `ReplacementEffectSourceComponent` at ~20 independent interception sites, none of
 * which evaluates a condition, so gating one needs a shared conditional-replacement seam that
 * doesn't exist yet — deliberately left out rather than approximated per site.
 */
fun CardBuilder.maxSpeed(init: MaxSpeedBuilder.() -> Unit) {
    val builder = MaxSpeedBuilder()
    builder.init()
    keywords(Keyword.MAX_SPEED)
    staticAbilities.addAll(builder.gatedStaticAbilities())
    activatedAbilities.addAll(builder.gatedActivatedAbilities())
    triggeredAbilities.addAll(builder.gatedTriggeredAbilities())
}

/**
 * Collects the abilities inside a [maxSpeed] block and hands them back gated on "your speed is 4".
 *
 * Declaration mirrors [CardBuilder] exactly — the same `staticAbility { }` / `activatedAbility { }` /
 * `triggeredAbility { }` builders — so authoring a max-speed ability is authoring an ordinary
 * ability; only the gate and the "Max speed — " prefix are added.
 */
@CardDsl
class MaxSpeedBuilder {

    private val statics: MutableList<StaticAbility> = mutableListOf()
    private val activated: MutableList<ActivatedAbility> = mutableListOf()
    private val triggered: MutableList<TriggeredAbility> = mutableListOf()

    /**
     * "Max speed — This creature has [keywords]." Sugar for a [GrantKeyword] on the source per
     * keyword — the single most common max-speed shape (double strike, deathtouch, lifelink,
     * vigilance, menace, haste).
     */
    fun keywords(vararg keywords: Keyword) {
        keywords.forEach { statics.add(GrantKeyword(it, GroupFilter.source())) }
    }

    /** A max-speed static ability, e.g. "Max speed — This creature gets +1/+2." */
    fun staticAbility(init: StaticAbilityBuilder.() -> Unit) {
        val builder = StaticAbilityBuilder()
        builder.init()
        statics.add(builder.build())
    }

    /** A max-speed activated ability, e.g. "Max speed — {T}: Add {R}{R}." */
    fun activatedAbility(init: ActivatedAbilityBuilder.() -> Unit) {
        val builder = ActivatedAbilityBuilder()
        builder.init()
        activated.add(builder.build())
    }

    /** A max-speed triggered ability, e.g. "Max speed — At the beginning of your end step, …" */
    fun triggeredAbility(init: TriggeredAbilityBuilder.() -> Unit) {
        val builder = TriggeredAbilityBuilder()
        builder.init()
        triggered.add(builder.build())
    }

    internal fun gatedStaticAbilities(): List<StaticAbility> = statics.map { ability ->
        when (ability) {
            // A cost modifier never reaches the layer system: `CostCalculator` scans the raw
            // static-ability list for `is ModifySpellCost`, so a ConditionalStaticAbility wrapper
            // would hide it and the reduction would silently never apply. ModifySpellCost carries
            // its own condition slot for exactly this — fold the gate into it.
            is ModifySpellCost -> ability.copy(
                gating = when (val existing = ability.gating) {
                    CostGating.None -> CostGating.OnlyIf(MAX_SPEED_GATE)
                    is CostGating.OnlyIf -> CostGating.OnlyIf(existing.condition and MAX_SPEED_GATE)
                    // NthOfTypePerTurn holds a count, not a condition, so the two gates can't be
                    // merged into one slot. No card needs both; refuse rather than drop one.
                    else -> throw IllegalArgumentException(
                        "Max speed cannot gate a ModifySpellCost that already uses ${existing::class.simpleName} gating"
                    )
                }
            )
            // Same trap as ModifySpellCost, one seam further out: the graveyard/exile cast paths
            // (`CastFromZoneEnumerator.enumerateIntrinsicZoneCast`, `CastZoneResolver
            // .findMayCastSelfFromZoneAbility`) scan the raw static list with
            // `filterIsInstance<MayCastSelfFromZones>`, so a ConditionalStaticAbility wrapper would
            // hide the permission and the card would simply never be castable. It carries its own
            // condition slot — evaluated in the *casting player's* context at both read sites, which
            // is what makes "Max speed — You may cast this card from your graveyard" (Lightwheel
            // Enhancements) work from a zone where the card is not a permanent at all.
            is MayCastSelfFromZones -> ability.copy(
                condition = ability.condition?.let { it and MAX_SPEED_GATE } ?: MAX_SPEED_GATE
            )
            // A `staticAbility { condition = … }` block already wrapped itself; fold the max-speed
            // gate into that wrapper's condition instead of nesting two ConditionalStaticAbility
            // layers, so the projected description and the layer system see one conditional ability.
            is ConditionalStaticAbility -> ability.copy(condition = ability.condition and MAX_SPEED_GATE)
            else -> ConditionalStaticAbility(ability, MAX_SPEED_GATE)
        }
    }

    internal fun gatedActivatedAbilities(): List<ActivatedAbility> = activated.map { ability ->
        ability.copy(
            restrictions = ability.restrictions + ActivationRestriction.OnlyIfCondition(MAX_SPEED_GATE),
            descriptionOverride = MAX_SPEED_PREFIX + (ability.descriptionOverride ?: ability.description)
        )
    }

    internal fun gatedTriggeredAbilities(): List<TriggeredAbility> = triggered.map { ability ->
        val gated = ability.triggerCondition?.let { it and MAX_SPEED_GATE } ?: MAX_SPEED_GATE
        ability.copy(
            triggerCondition = gated,
            descriptionOverride = MAX_SPEED_PREFIX + (ability.descriptionOverride ?: ability.description)
        )
    }

    private companion object {
        const val MAX_SPEED_PREFIX = "Max speed — "
        val MAX_SPEED_GATE: Condition = Conditions.YouHaveMaxSpeed

        /** Conjoin two conditions, flattening an existing [AllConditions] rather than nesting. */
        infix fun Condition.and(other: Condition): Condition = when (this) {
            is AllConditions -> AllConditions(conditions + other)
            else -> AllConditions(listOf(this, other))
        }
    }
}
