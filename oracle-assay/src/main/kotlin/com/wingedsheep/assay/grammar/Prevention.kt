package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.PreventDamageEffect
import com.wingedsheep.sdk.scripting.effects.PreventionDirection
import com.wingedsheep.sdk.scripting.effects.PreventionScope
import com.wingedsheep.sdk.scripting.effects.PreventionSourceFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetRequirement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Damage prevention — "Prevent all combat damage that would be dealt this turn.", "Prevent the next
 * 2 damage that would be dealt to target creature this turn.", "Prevent all damage that would be
 * dealt to you and creatures you control this turn by creatures."
 *
 * ## Why this is a family and was three rules
 *
 * `PreventDamageEffect`'s own KDoc calls itself "a single parametrized type that can express any
 * combination of: amount-based vs prevent-all, combat-only vs all-damage, directional prevention,
 * source filtering". The grammar had it the other way round: [Steps] carried
 * `prevent the next {n} damage that would be dealt to any target this turn` and
 * `prevent all combat damage that would be dealt to and dealt by {self} this turn` as two
 * whole-sentence rules, and said of them that they "share nothing but the verb, so they are two
 * rules rather than a shape with a slot".
 *
 * They share every word except the ones the type's fields name. This is the top-of-library band's
 * lesson applied to an effect rather than to a `Patterns` recipe: **before adding a rule beside an
 * existing one, check whether the two differ only in a field the SDK type already carries.** Here
 * they differ in four — `amount`, `scope`, `direction`, and what `target` denotes — and the printed
 * sentence has a variable part for each, so the grammar is their product.
 *
 * ## The axes, and where each one lives
 *
 * - **`scope`** is one word, so it is [kind], a slot in every sentence below.
 * - **`amount`** changes the sentence's *quantifier* ("all" versus "the next 2"), and `null` is one
 *   of its values — which a slot cannot carry, because a `build` returning null means the surface
 *   form denotes nothing. So it is a [Quantity], a rule parameter with three instantiations, the
 *   shape [Replacements.entersWithCounters] takes for the same reason.
 * - **`direction`** changes the *clause frame* ("that would be dealt to X" / "that would be dealt by
 *   X" / "that would be dealt to and dealt by X"), so it is three frames rather than a slot.
 * - **`target`** is the [Recipient] vocabulary: the noun phrase, the [EffectTarget] behind it, and
 *   the requirement the script has to declare for it. Written once and instantiated per position,
 *   the treatment [SelfSteps.retargetable] gets — [Continuations] takes the anaphoric member and
 *   nothing else, which is what stops "that creature" being readable as a clause that can open a
 *   line.
 * - **`sourceFilter`** is a **layer**, never a member of the frames: exactly one optional suffix
 *   owns that field and strips precisely it, the discipline [Filters] states. A frame that already
 *   spells a direction other than `ToTarget` is refused by the layer, so a group-sourced shield has
 *   one printed form ([groupClauses]' "…that would be dealt by creatures this turn") instead of two
 *   rules that can each print it.
 *
 * ## Constructed rather than built through a facade
 *
 * Every other family here goes through an SDK companion factory, per the module's rule. `Effects`
 * publishes *fourteen* prevention facades and each one freezes a different subset of the same six
 * fields — `PreventAllCombatDamage()`, `PreventCombatDamageToAndBy(target)`,
 * `PreventAllDamageDealtBy(target)`, `PreventNextDamage(amount, target)` and so on. They are points
 * on the product this file spans, so picking one per combination would be a `when` over the model
 * reproducing a mapping nobody wrote down, and every combination the corpus prints and the facades
 * do not have would need a fifteenth. The type itself is the curated surface here, exactly as
 * [Replacements] argues for the replacement-effect constructors; that the facade list is a frozen
 * enumeration of a parametrized type is the same finding this file is an answer to, reported rather
 * than routed around.
 *
 * ## Two SDK findings this file declines rather than approximates
 *
 * - **`PreventionScope` has no `Noncombat` case.** "Prevent all noncombat damage that would be
 *   dealt to other creatures you control." (Crystal Barricade, Tajic, Blessed Sanctuary — 7 corpus
 *   lines) therefore declines. Reading it as `AllDamage` would be the reversible-but-wrong class in
 *   one word.
 * - **A combat-only shield on the controller is spelled by two English sentences and stored as one
 *   model.** `PreventDamageEffect(scope = CombatOnly)` leaves `target` at its `EffectTarget.Controller`
 *   default, and `PreventDamageExecutor` reads exactly that configuration as the *global* Fog rather
 *   than as a shield over the controller — so "prevent all combat damage that would be dealt to you
 *   this turn" (Inkshield, Take the Bait) denotes the same value as "prevent all combat damage that
 *   would be dealt this turn" and cannot be told apart from it. [fog] owns the value and every other
 *   rule refuses it, which keeps one printed form per model; the two cards that mean the narrower
 *   thing decline and are counted.
 *
 * ## What is deliberately not here
 *
 * The **duration-less** lines — "Prevent all damage that would be dealt to ~.", "Prevent all damage
 * that would be dealt by enchanted creature." — are 40-odd corpus lines and they are not this type
 * at all: a permanent's standing prevention is `ReplacementEffect.PreventDamage`, whose
 * `appliesTo` is an `EventPattern`, and the grammar has no event-pattern vocabulary yet. That is a
 * dependency with an expiry date, in the sense the conditional-tapped-entry band records: the day
 * some band builds `EventPattern`, this paragraph is what says the prevention statics are sitting
 * there waiting for it.
 */
object Prevention {

    /**
     * "damage" / "combat damage" — the [PreventionScope] slot every sentence here carries.
     *
     * A slot rather than two copies of each frame, and the reason it can be one is that the word
     * sits in the same place in all of them. `PreventionScope` has two cases and Oracle prints a
     * third quantity of damage ("noncombat"), which is the finding recorded on this object.
     */
    private val kind: Phrase<PreventionScope> = oneOf(
        "a kind of damage",
        constant("damage", PreventionScope.AllDamage),
        constant("combat damage", PreventionScope.CombatOnly),
    )

    /**
     * How a prevention sentence names the object its shield attaches to.
     *
     * Three things move together and nothing else does: the printed noun phrase, the
     * [EffectTarget] the model stores, and the [TargetRequirement]s the script has to declare. That
     * is why this is a vocabulary rather than four copies of every frame — and why a fifth member
     * (an anaphor in some position that does not exist yet) is a row here rather than a rule.
     *
     * [takesFilter] marks the one member whose noun phrase varies. It is what tells the shape to
     * register a `{filter}` slot and to read the filter back off the declared requirement; every
     * other member's surface is a literal and its requirement list a constant.
     */
    class Recipient internal constructor(
        internal val surface: String,
        internal val name: String,
        internal val target: EffectTarget,
        internal val requirements: List<TargetRequirement> = emptyList(),
        internal val takesFilter: Boolean = false,
    )

    /**
     * "~" — the source's own shield.
     *
     * The **name** and not [Primitives.self], which would also read "it". In this sentence the
     * pronoun is nearly always the target an earlier clause chose — "Put a +1/+1 counter on target
     * creature. It gains flying until end of turn. Prevent all combat damage that would be dealt to
     * **it** this turn." (Fleeting Flight) — and reading it as the source would round-trip
     * byte-perfectly while shielding a different permanent, the reversible-but-wrong class the
     * differential exists to catch. The pronoun is [Continuations]' to read, through [thatCreature].
     */
    val source: Recipient = Recipient("{self}", "the source", EffectTarget.Self)

    /** "you" — the shield every Circle of Protection and every damage-prevention artifact carries. */
    val you: Recipient = Recipient("you", "you", EffectTarget.Controller)

    /** "any target" — the healer's requirement, covering any creature, player or planeswalker. */
    val anyTarget: Recipient =
        Recipient("any target", "any target", Targets.bound(), listOf(Targets.any()))

    /** "target creature", "target legendary creature", "target artifact creature" — one noun phrase. */
    val targeted: Recipient =
        Recipient("target {filter}", "a targeted permanent", Targets.bound(), takesFilter = true)

    /**
     * "that creature" / "it" — the target an earlier sentence in the same line already chose.
     *
     * Declares **no** requirement of its own, which is what makes it a continuation: the sentence
     * only means something after the clause that introduced the target, and a script whose whole
     * content was a dangling reference is a reading no printed card supports. Reachable only from
     * [Continuations].
     */
    val thatCreature: Recipient = Recipient("that creature", "the target", Targets.bound())

    /** …and the same anaphor in its other spelling. Two surfaces, one model, so one of them prints. */
    private val itPronoun: Recipient = Recipient("it", "the target", Targets.bound())

    /**
     * The quantifier, and the one axis that cannot be a slot.
     *
     * `amount == null` is a legitimate value of the field ("prevent **all** damage") and a
     * `build` returning null means the surface denotes nothing, so a `Phrase<DynamicAmount?>`
     * would make "all" unparseable. Three instantiations of the shape instead, over disjoint halves
     * of `DynamicAmount` so printing stays determined by the model rather than by alternation order:
     * `null` is "all", a `Fixed` is the numeral, and `XValue` is the literal X.
     */
    private class Quantity(
        val surface: String,
        val counted: Boolean,
        val fixed: DynamicAmount?,
    ) {
        /** True when [amount] is this quantity's to spell. The three domains partition the field. */
        fun owns(amount: DynamicAmount?): Boolean = when {
            counted -> amount is DynamicAmount.Fixed
            fixed != null -> amount == fixed
            else -> amount == null
        }
    }

    private val all = Quantity("all", counted = false, fixed = null)
    private val nextN = Quantity("the next {n}", counted = true, fixed = null)
    private val nextX = Quantity("the next X", counted = false, fixed = DynamicAmount.XValue)

    private val quantities = listOf(all, nextN, nextX)

    private val bothScopes = setOf(PreventionScope.AllDamage, PreventionScope.CombatOnly)

    /**
     * The value [fog] owns, and the one every other rule has to refuse.
     *
     * See the SDK finding on this object: the executor reads a combat-only shield left at the
     * `EffectTarget.Controller` default as global prevention, so this model has two English
     * sentences and the grammar may print exactly one of them.
     */
    private val fogEffect = PreventDamageEffect(scope = PreventionScope.CombatOnly)

    /**
     * The script a frame denotes, or null when the combination is one the grammar may not spell.
     *
     * The single guard is the Fog collision. Everything else that could be wrong about a value —
     * a duration, a `gainLifeFromColors`, an `onPrevented` reaction, a `recipientGroup` — is caught
     * by the reconstruct-and-compare in each rule's `match`, which is what makes the check
     * exhaustive by construction rather than by a list of fields to remember.
     */
    private fun shield(
        recipient: Recipient,
        filter: GameObjectFilter?,
        scope: PreventionScope,
        amount: DynamicAmount?,
        direction: PreventionDirection,
    ): CardScript? {
        val effect = PreventDamageEffect(
            target = recipient.target,
            amount = amount,
            scope = scope,
            direction = direction,
        )
        if (effect == fogEffect) return null
        val requirements =
            if (recipient.takesFilter) listOf(Targets.permanent(filter ?: return null))
            else recipient.requirements
        return CardScript(spellEffect = effect, targetRequirements = requirements)
    }

    /**
     * One frame over one recipient at one quantity — the whole shape, and every rule below is a row.
     *
     * @param frame the clause after the kind word, with `{r}` standing where the recipient's own
     *   noun phrase goes. Substituted rather than slotted because a recipient is a *surface* plus a
     *   model, and half of the members spell a literal: a slot would need a phrase per member and
     *   would lose the requirement list, which is the half the script needs.
     * @param scopes which `PreventionScope` values this rule reads and prints. Two rules with
     *   disjoint scope domains are how a frame whose canonical word order flips with the scope is
     *   written — see [directed].
     */
    private fun shieldRule(
        frame: String,
        name: String,
        recipient: Recipient,
        quantity: Quantity,
        direction: PreventionDirection,
        scopes: Set<PreventionScope> = bothScopes,
        canonicalRule: Boolean = true,
    ): Phrase<CardScript> {
        val surface = "prevent ${quantity.surface} {kind} " + frame.replace("{r}", recipient.surface)
        val inner = phrase<CardScript>(surface, name = name) {
            canonical = canonicalRule
            slot("kind", kind)
            if (quantity.counted) slot("n", Primitives.cardinal)
            if (recipient.surface.contains("{self}")) slot("self", Primitives.selfNamed)
            if (recipient.takesFilter) slot("filter", Filters.filter)
            build { bindings ->
                val scope = bindings.value<PreventionScope>("kind")
                if (scope !in scopes) return@build null
                val amount = when {
                    quantity.counted -> DynamicAmount.Fixed(bindings.int("n"))
                    else -> quantity.fixed
                }
                shield(
                    recipient,
                    if (recipient.takesFilter) bindings.value("filter") else null,
                    scope,
                    amount,
                    direction,
                )
            }
            match { script ->
                val effect = script.spellEffect as? PreventDamageEffect ?: return@match null
                if (effect.scope !in scopes) return@match null
                if (effect.direction != direction) return@match null
                if (!quantity.owns(effect.amount)) return@match null
                val filter = if (recipient.takesFilter) {
                    val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                    Targets.permanentFilter(requirement) ?: return@match null
                } else {
                    null
                }
                if (script != shield(recipient, filter, effect.scope, effect.amount, direction)) {
                    return@match null
                }
                bind(
                    "kind" to effect.scope,
                    "n" to (effect.amount as? DynamicAmount.Fixed)?.amount,
                    "self" to Unit,
                    "filter" to filter,
                )
            }
        }
        return if (canonicalRule) inner else alternate(inner)
    }

    /**
     * The "silencing" frame, whose canonical word order is decided by the **scope**.
     *
     * Oracle spells `direction = FromTarget` two ways — "prevent all combat damage that would be
     * dealt **by** target attacking creature this turn" and "prevent all damage target creature
     * **would deal** this turn" — and which one is the majority flips with the kind of damage:
     * the passive leads 11 lines to 3 for combat damage, and the active is the only spelling the
     * corpus uses for damage in general. Two rules that could each print the whole field would leave
     * printing to alternation order, so each takes the scope it wins and refuses the other, and the
     * minority spelling for each half is an [alternate]. That is the top-of-library band's duration
     * finding one field over: the canonical order is determined by the model, just not by the field
     * you would guess.
     */
    private fun directed(recipient: Recipient): List<Phrase<CardScript>> {
        fun rule(frame: String, form: String, scope: PreventionScope, canonicalRule: Boolean) =
            shieldRule(
                frame = frame,
                name = "prevent damage dealt by ${recipient.name} ($form)",
                recipient = recipient,
                quantity = all,
                direction = PreventionDirection.FromTarget,
                scopes = setOf(scope),
                canonicalRule = canonicalRule,
            )
        return listOf(
            rule("that would be dealt by {r} this turn", "passive", PreventionScope.CombatOnly, true),
            rule("{r} would deal this turn", "active", PreventionScope.AllDamage, true),
            rule("that would be dealt by {r} this turn", "passive", PreventionScope.AllDamage, false),
            rule("{r} would deal this turn", "active", PreventionScope.CombatOnly, false),
        )
    }

    /**
     * "Prevent all combat damage that would be dealt this turn." — Fog, Holy Day, Tangle, Lull,
     * Moment's Peace, Spore Frog, and the 32 corpus lines that print exactly this.
     *
     * A `constant` on the whole value rather than a member of a frame, because the sentence names
     * nothing at all: there is no recipient, no source and no direction in it, and the model is the
     * bare `scope = CombatOnly`. It is also the rule that resolves the SDK collision recorded on this
     * object — it owns that value, so no recipient frame may build it.
     */
    private val fog: Phrase<CardScript> = run {
        val script = CardScript(spellEffect = fogEffect)
        phrase<CardScript>(
            "prevent all combat damage that would be dealt this turn",
            name = "prevent all combat damage",
        ) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * The shields whose recipient or source is a **group** rather than one object.
     *
     * `recipientGroup` and `PreventionSourceFilter.FromGroup` are two fields holding the same
     * `GroupFilter` on opposite sides of the damage, and [Filters.plural] spells both — so every
     * noun phrase the grammar can read ("creatures you control", "nongreen creatures", "creatures
     * your opponents control") arrives here as a row in a filter list rather than as a rule.
     *
     * "you and creatures you control" is its own frame rather than a filter, for the reason
     * `PreventDamageEffect.recipientGroupIncludesController` exists: a player is not a permanent, so
     * the "you and" can never come out of the `GroupFilter` itself.
     */
    private val groupClauses: List<Phrase<CardScript>> = run {
        fun rule(
            frame: String,
            name: String,
            canonicalRule: Boolean = true,
            effectFor: (GroupFilter, PreventionScope) -> PreventDamageEffect,
        ): Phrase<CardScript> {
            fun scriptFor(group: GroupFilter, scope: PreventionScope) =
                CardScript(spellEffect = effectFor(group, scope))
            val inner = phrase<CardScript>("prevent all {kind} $frame", name = name) {
                canonical = canonicalRule
                slot("kind", kind)
                slot("group", Filters.plural)
                build { scriptFor(GroupFilter(it.value("group")), it.value("kind")) }
                match { script ->
                    val effect = script.spellEffect as? PreventDamageEffect ?: return@match null
                    val group = effect.recipientGroup
                        ?: (effect.sourceFilter as? PreventionSourceFilter.FromGroup)?.filter
                        ?: return@match null
                    if (group != GroupFilter(group.baseFilter)) return@match null
                    if (script != scriptFor(group, effect.scope)) return@match null
                    bind("kind" to effect.scope, "group" to group.baseFilter)
                }
            }
            return if (canonicalRule) inner else alternate(inner)
        }

        fun dealtBy(group: GroupFilter, scope: PreventionScope) = PreventDamageEffect(
            scope = scope,
            direction = PreventionDirection.FromTarget,
            sourceFilter = PreventionSourceFilter.FromGroup(group),
        )

        fun toGroup(group: GroupFilter, scope: PreventionScope) =
            PreventDamageEffect(recipientGroup = group, scope = scope)

        listOf(
            rule(
                "that would be dealt to {group} this turn",
                "prevent damage dealt to a group",
                effectFor = ::toGroup,
            ),
            // "Prevent all damage that would be dealt **this turn to** creatures you control." —
            // Divine Light, Sivvi's Ruse. The duration and the recipient swap places and the model
            // has no room for the order, so the majority spelling above is canonical and this one
            // comes back as a variant. Only the recipient frames print both ways; a single-object
            // recipient is one line in the corpus and gets no second template.
            rule(
                "that would be dealt this turn to {group}",
                "prevent damage dealt to a group (duration first)",
                canonicalRule = false,
                effectFor = ::toGroup,
            ),
            rule(
                "that would be dealt to you and {group} this turn",
                "prevent damage dealt to you and a group",
            ) { group, scope ->
                PreventDamageEffect(
                    recipientGroup = group,
                    recipientGroupIncludesController = true,
                    scope = scope,
                )
            },
            rule(
                "that would be dealt by {group} this turn",
                "prevent damage dealt by a group",
                effectFor = ::dealtBy,
            ),
            // The active spelling — "prevent all combat damage non-Soldier creatures would deal this
            // turn" (Frontline Strategist). One model, so one of the two prints; the passive is
            // canonical here because it is what the corpus prints for both kinds of damage, which is
            // the opposite of how the single-object frame splits. See [directed].
            rule(
                "{group} would deal this turn",
                "prevent damage a group would deal",
                canonicalRule = false,
                effectFor = ::dealtBy,
            ),
            // "Prevent all damage that would be dealt **this turn by** creatures your opponents
            // control." — Thwart the Enemy, Vine Snare, Tanglesap. The same swap as above, on the
            // source side.
            rule(
                "that would be dealt this turn by {group}",
                "prevent damage dealt by a group (duration first)",
                canonicalRule = false,
                effectFor = ::dealtBy,
            ),
        )
    }

    /**
     * The **source layer** — "… this turn by attacking creatures", "… this turn by a source of your
     * choice", "… this turn by creatures".
     *
     * One layer, one field, and it strips precisely that field before delegating: the discipline
     * [Filters] states for a predicate bag, applied to a record. A combinator that could also spell
     * the recipient would leave printing underdetermined the moment two of them could express one
     * value, which is exactly what would happen against [groupClauses]' "that would be dealt by
     * creatures this turn" — so the layer refuses any inner clause that is not `ToTarget`, and the
     * two shapes take disjoint halves of the model.
     *
     * @param guard the extra restriction a particular source imposes on the shield it modifies. Only
     *   `AttackingCreatures` has one, and it is the executor's: that filter attaches the shield to
     *   the ability's controller and ignores `target` entirely, so the layer may only wear a
     *   recipient clause that already said "you".
     */
    private fun sourceLayer(
        inner: Phrase<CardScript>,
        suffix: String,
        name: String,
        source: PreventionSourceFilter,
        guard: (PreventDamageEffect) -> Boolean = { true },
    ): Phrase<CardScript> = phrase("{inner}$suffix", name = name) {
        slot("inner", inner)
        build { bindings ->
            val script = bindings.value<CardScript>("inner")
            val effect = script.spellEffect as? PreventDamageEffect ?: return@build null
            if (effect.direction != PreventionDirection.ToTarget) return@build null
            if (effect.sourceFilter != PreventionSourceFilter.AnySource) return@build null
            if (!guard(effect)) return@build null
            CardScript(
                spellEffect = effect.copy(sourceFilter = source),
                targetRequirements = script.targetRequirements,
            )
        }
        match { script ->
            val effect = script.spellEffect as? PreventDamageEffect ?: return@match null
            if (effect.direction != PreventionDirection.ToTarget) return@match null
            if (effect.sourceFilter != source) return@match null
            if (!guard(effect)) return@match null
            val bare = CardScript(
                spellEffect = effect.copy(sourceFilter = PreventionSourceFilter.AnySource),
                targetRequirements = script.targetRequirements,
            )
            bind("inner" to bare)
        }
    }

    /**
     * The noun phrase `PreventionSourceFilter.AttackingCreatures` already owns.
     *
     * "…by attacking creatures" is spellable twice — as the dedicated case and as a `FromGroup` over
     * the filter [Filters.plural] reads out of the same three words — and two rules reading one text
     * into two models is the hard ambiguity the design says never to resolve by ordering an
     * alternation. The dedicated case wins because it is what the hand-written cards carry (Deep
     * Wood), so [groupSourceLayer] refuses this filter outright and a shield holding the `FromGroup`
     * spelling has no printed form. That omission is the SDK finding: two spellings of one thing.
     */
    private val attackingCreatures = GameObjectFilter.Creature.attacking()

    /** The same layer over a source the sentence names with a noun phrase — "… by creatures". */
    private fun groupSourceLayer(inner: Phrase<CardScript>): Phrase<CardScript> =
        phrase("{inner} by {group}", name = "prevent damage from a group of sources") {
            slot("inner", inner)
            slot("group", Filters.plural)
            build { bindings ->
                val script = bindings.value<CardScript>("inner")
                val group = bindings.value<GameObjectFilter>("group")
                if (group == attackingCreatures) return@build null
                val effect = script.spellEffect as? PreventDamageEffect ?: return@build null
                if (effect.direction != PreventionDirection.ToTarget) return@build null
                if (effect.sourceFilter != PreventionSourceFilter.AnySource) return@build null
                CardScript(
                    spellEffect = effect.copy(
                        sourceFilter = PreventionSourceFilter.FromGroup(GroupFilter(group))
                    ),
                    targetRequirements = script.targetRequirements,
                )
            }
            match { script ->
                val effect = script.spellEffect as? PreventDamageEffect ?: return@match null
                if (effect.direction != PreventionDirection.ToTarget) return@match null
                val group = (effect.sourceFilter as? PreventionSourceFilter.FromGroup)?.filter
                    ?: return@match null
                if (group.baseFilter == attackingCreatures) return@match null
                if (group != GroupFilter(group.baseFilter)) return@match null
                val bare = CardScript(
                    spellEffect = effect.copy(sourceFilter = PreventionSourceFilter.AnySource),
                    targetRequirements = script.targetRequirements,
                )
                bind("inner" to bare, "group" to group.baseFilter)
            }
        }

    /**
     * The whole family over one set of [Recipient]s.
     *
     * Public because the recipient vocabulary is instantiated per anaphor position for the reason
     * [SelfSteps.retargetable] is: "that creature" denotes the target an earlier clause chose and is
     * therefore only readable from a later position in a sequence, while every other member can open
     * a line. One shape, two instantiations, and no rule written twice.
     */
    fun clausesFor(recipients: List<Recipient>): List<Phrase<CardScript>> {
        val toRecipient = recipients.flatMap { recipient ->
            quantities.map { quantity ->
                shieldRule(
                    frame = "that would be dealt to {r} this turn",
                    name = "prevent damage dealt to ${recipient.name}",
                    recipient = recipient,
                    quantity = quantity,
                    direction = PreventionDirection.ToTarget,
                )
            }
        }
        val fromRecipient = recipients.flatMap(::directed)
        val bothWays = recipients.map { recipient ->
            shieldRule(
                frame = "that would be dealt to and dealt by {r} this turn",
                name = "prevent damage dealt to and by ${recipient.name}",
                recipient = recipient,
                quantity = all,
                direction = PreventionDirection.Both,
            )
        }
        val recipientClause = oneOf("a prevented recipient", toRecipient)
        val layered = listOf(
            sourceLayer(
                recipientClause,
                " by attacking creatures",
                "prevent damage from attacking creatures",
                PreventionSourceFilter.AttackingCreatures,
            ) { it.target == EffectTarget.Controller && it.amount == null },
            sourceLayer(
                recipientClause,
                " by a source of your choice",
                "prevent damage from a chosen source",
                PreventionSourceFilter.ChosenSource,
            ),
            groupSourceLayer(recipientClause),
        )
        return toRecipient + fromRecipient + bothWays + layered
    }

    /** Everything readable from an ordinary clause position — what [Steps] slots. */
    val clauses: List<Phrase<CardScript>> =
        clausesFor(listOf(source, you, anyTarget, targeted)) +
            groupClauses +
            groupSourceLayer(oneOf("a group-recipient shield", groupClauses)) +
            fog

    /**
     * The same family over the anaphor — what [Continuations] slots.
     *
     * "That creature" is canonical and "it" is an [alternate] of it, the split
     * [com.wingedsheep.assay.grammar.Primitives.self] makes one level down: both spellings denote
     * the target the line already chose, and the model has no room for which one was printed.
     */
    val continuationClauses: List<Phrase<CardScript>> =
        clausesFor(listOf(thatCreature)) + clausesFor(listOf(itPronoun)).map(::alternate)
}
