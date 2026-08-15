package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DividedDamageEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.GrantAttackBlockTaxPerCreatureTypeEffect
import com.wingedsheep.sdk.scripting.effects.GrantCantBeBlockedExceptByColorEffect
import com.wingedsheep.sdk.scripting.effects.MustBeBlockedEffect
import com.wingedsheep.sdk.scripting.effects.ReflectCombatDamageEffect
import com.wingedsheep.sdk.scripting.effects.SkipCombatPhasesEffect
import com.wingedsheep.sdk.scripting.effects.SkipUntapEffect
import com.wingedsheep.sdk.scripting.effects.TauntEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Clauses that change how a combat goes — the spell-side siblings of the combat statics in
 * [Statics].
 *
 * A static says what a permanent *is* for as long as it is on the battlefield; these say what
 * happens for a turn, which the SDK models as ordinary resolution-time effects. Both vocabularies
 * name the same combat concepts and neither can be spelled in terms of the other, so they sit in
 * separate files by which slot they reach rather than by what they talk about.
 */
object Combat {

    /**
     * "During target player's next turn, creatures that player controls attack you if able." —
     * Taunt.
     *
     * The whole sentence is one SDK effect, and the "that player" in the second clause is the same
     * player the first clause targeted — a link the model carries as a single `target` field rather
     * than as two references, which is why this is one rule and not a [Continuations] pair.
     */
    private val taunt: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = TauntEffect(Targets.bound()),
            targetRequirements = listOf(Targets.player()),
        )
        phrase(
            "during target player's next turn, creatures that player controls attack you if able",
            name = "taunt",
        ) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Black creatures you control can't be blocked this turn except by black creatures." — Dread
     * Charge.
     *
     * Two colours in one sentence and two fields in the model: the group that gains the evasion, and
     * the colour that is still allowed to block it. The second is a bare `Color` rather than a
     * filter because that is the shape the SDK gives it, so the "creatures" after it is a literal
     * here rather than a slot — writing it as one would let the rule print a filter the effect
     * cannot hold.
     */
    private val cantBeBlockedExceptByColor: Phrase<CardScript> = run {
        fun scriptFor(filter: GameObjectFilter, colour: com.wingedsheep.sdk.core.Color) = CardScript(
            spellEffect = GrantCantBeBlockedExceptByColorEffect(
                filter = GroupFilter(filter),
                canOnlyBeBlockedByColor = colour,
            )
        )
        phrase(
            "{filter} can't be blocked this turn except by {color} creatures",
            name = "a group can't be blocked except by a colour",
        ) {
            slot("filter", Filters.plural)
            slot("color", Primitives.color)
            build { scriptFor(it.value("filter"), it.value("color")) }
            match { script ->
                val effect = script.spellEffect as? GrantCantBeBlockedExceptByColorEffect ?: return@match null
                val filter = effect.filter.baseFilter
                if (script != scriptFor(filter, effect.canOnlyBeBlockedByColor)) return@match null
                bind("filter" to filter, "color" to effect.canOnlyBeBlockedByColor)
            }
        }
    }

    /**
     * "Return one or two target attacking creatures to their owner's hand." — Command of
     * Unsummoning.
     *
     * The one rule in the grammar whose effect refers to its target **positionally**, and it has to:
     * `ForEachTargetEffect` rebinds `ContextTarget(0)` to the current target on each iteration, so a
     * named [EffectTarget.BoundVariable] would name the *whole* declaration rather than the member
     * being processed and would mean a different card. The differential normalizes named and
     * positional references to a slot's position, so this reads the same as every other rule there;
     * only the model differs, and it differs because the iteration requires it.
     *
     * The count pair is spelled by the template rather than by two number slots. "One or two" is a
     * `minCount`/`count` pair in the model and there is exactly one member of the shape so far, so
     * it is written inline — factor it when the second ("up to three target …") appears.
     */
    private val returnOneOrTwoTargets: Phrase<CardScript> = run {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = ForEachTargetEffect(listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.HAND))),
            targetRequirements = listOf(
                TargetCreature(count = 2, minCount = 1, filter = TargetFilter(filter), id = Targets.SLOT)
            ),
        )
        phrase(
            "return one or two target {filter} to their owner's hand",
            name = "return one or two targets to hand",
        ) {
            slot("filter", Filters.plural)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = (requirement as? com.wingedsheep.sdk.scripting.targets.TargetObject)
                    ?.filter?.baseFilter ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    /**
     * "All creatures able to block target creature this turn do so." — the lure.
     *
     * `MustBeBlockedEffect`'s `allCreatures` default is exactly this sentence; the narrower form
     * ("target creature blocks this turn if able") is a different sentence and refuses to print
     * here, which the reconstruct-and-compare enforces.
     */
    private val mustBeBlocked: Phrase<CardScript> = run {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = MustBeBlockedEffect(Targets.bound()),
            targetRequirements = listOf(Targets.permanent(filter)),
        )
        phrase("all creatures able to block target {filter} this turn do so", name = "lure") {
            slot("filter", Filters.filter)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = Targets.permanentFilter(requirement) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    /**
     * The turn-shaping effects a player is the object of — skipping a combat, skipping an untap.
     *
     * Each is one effect with one target and no variable, so each is a constant rule paired with the
     * requirement its sentence declares. Note that the *filter* in Exhaustion's printed noun phrase
     * ("Creatures and lands target opponent controls") is not a slot: `SkipUntapEffect` carries
     * `affectsCreatures` and `affectsLands` as booleans rather than a filter, so the phrase is a
     * literal here and a card naming any other combination declines.
     */
    private fun playerEffect(
        template: String,
        name: String,
        requirement: com.wingedsheep.sdk.scripting.targets.TargetRequirement,
        effect: (com.wingedsheep.sdk.scripting.targets.EffectTarget) -> Effect,
    ): Phrase<CardScript> {
        val script = CardScript(
            spellEffect = effect(Targets.bound()),
            targetRequirements = listOf(requirement),
        )
        return phrase(template, name = name) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /** The whole-turn combat effects with no target at all — Deep Wood and Harsh Justice. */
    private fun turnEffect(template: String, name: String, effect: Effect): Phrase<CardScript> {
        val script = CardScript(spellEffect = effect)
        return phrase(template, name = name) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "~ deals 4 damage divided as you choose among one, two, or three target creatures." — Forked
     * Lightning.
     *
     * The target-count range appears in the *effect* as well as on the requirement — `minTargets`
     * and `maxTargets` on `DividedDamageEffect`, `minCount` and `count` on the requirement — so the
     * two numbers the phrase spells land in four model fields. They are written as one range in the
     * template rather than as two slots because Oracle enumerates the run ("one, two, or three")
     * rather than stating bounds, and an enumeration is a printed form rather than a number.
     */
    private val dividedDamage: Phrase<CardScript> = run {
        fun scriptFor(total: Int, filter: GameObjectFilter) = CardScript(
            spellEffect = DividedDamageEffect(totalDamage = total, minTargets = 1, maxTargets = 3),
            targetRequirements = listOf(
                TargetCreature(count = 3, minCount = 1, filter = TargetFilter(filter), id = Targets.SLOT)
            ),
        )
        phrase(
            "{self} deals {n} damage divided as you choose among one, two, or three target {filter}",
            name = "divided damage among up to three targets",
        ) {
            slot("self", Primitives.self)
            slot("n", Primitives.cardinal)
            slot("filter", Filters.plural)
            build { scriptFor(it.int("n"), it.value("filter")) }
            match { script ->
                val effect = script.spellEffect as? DividedDamageEffect ?: return@match null
                val requirement = script.targetRequirements.singleOrNull()
                    as? com.wingedsheep.sdk.scripting.targets.TargetObject ?: return@match null
                val filter = requirement.filter.baseFilter
                if (script != scriptFor(effect.totalDamage, filter)) return@match null
                bind("self" to Unit, "n" to effect.totalDamage, "filter" to filter)
            }
        }
    }

    /**
     * "Until end of turn, target creature gains "This creature can't attack or block unless its
     * controller pays {1} for each Cleric on the battlefield."" — Whipgrass Entangler.
     *
     * A whole *ability* granted for a turn, and the SDK names the granted ability as one effect
     * type rather than as a `GrantActivatedAbility` over a constructed static — because the thing
     * granted is a combat *restriction* with a per-creature-type tax, which has no printed form
     * outside this sentence. So the quoted text is spelled here rather than slotted through
     * [Activated.quoted]: what is inside the quotes is not an ability the grammar can otherwise
     * read, and the two variables in it — the type and the tax — are the effect's two fields.
     *
     * "Until end of turn" is at the *front* of this sentence and at the back of every other
     * durational one, which is Oracle's own inconsistency and why the duration is a literal here.
     */
    private val grantAttackBlockTax: Phrase<CardScript> = run {
        fun scriptFor(subtype: Subtype, tax: ManaCost) = CardScript(
            spellEffect = Effects.GrantAttackBlockTaxPerCreatureType(
                target = Targets.bound(),
                creatureType = subtype.value,
                manaCostPer = tax.toString(),
            ),
            targetRequirements = listOf(Targets.permanent(GameObjectFilter.Creature)),
        )
        phrase(
            "until end of turn, target creature gains \"{self} can't attack or block unless its " +
                "controller pays {tax} for each {subtype} on the battlefield.\"",
            name = "grant an attack and block tax",
        ) {
            slot("self", Primitives.self)
            slot("tax", Primitives.manaCost)
            slot("subtype", Primitives.subtype)
            build { scriptFor(it.value("subtype"), it.value("tax")) }
            match { script ->
                val effect = script.spellEffect as? GrantAttackBlockTaxPerCreatureTypeEffect
                    ?: return@match null
                val tax = runCatching { ManaCost.parse(effect.manaCostPer) }.getOrNull() ?: return@match null
                val subtype = Subtype(effect.creatureType)
                if (script != scriptFor(subtype, tax)) return@match null
                bind("self" to Unit, "tax" to tax, "subtype" to subtype)
            }
        }
    }

    /**
     * The clauses that carry their own full stop, because it falls **inside** a quotation.
     *
     * "…gains "This creature can't attack or block …"" ends on a quote mark, not on a stop, so
     * [Steps.sentence] — which spells the stop itself — cannot end a line with one. They are
     * therefore offered beside a sentence rather than inside it; see [Steps.step].
     */
    val selfTerminatingClauses: List<Phrase<CardScript>> = listOf(grantAttackBlockTax)

    val clauses: List<Phrase<CardScript>> = listOf(
        taunt,
        cantBeBlockedExceptByColor,
        returnOneOrTwoTargets,
        mustBeBlocked,
        dividedDamage,
        playerEffect(
            "target player skips all combat phases of their next turn",
            "skip combat phases",
            Targets.player(),
        ) { SkipCombatPhasesEffect(it) },
        playerEffect(
            "creatures and lands target opponent controls don't untap during their next untap step",
            "skip untap",
            Targets.opponent(),
        ) { SkipUntapEffect(it) },
        turnEffect(
            "prevent all damage that would be dealt to you this turn by attacking creatures",
            "prevent damage from attackers",
            Effects.PreventDamageFromAttackingCreatures(),
        ),
        turnEffect(
            "this turn, whenever an attacking creature deals combat damage to you, it deals that " +
                "much damage to its controller",
            "reflect combat damage",
            ReflectCombatDamageEffect(),
        ),
    )
}
