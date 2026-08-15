package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.StaticAbility

/**
 * Continuous abilities a permanent has just by being on the battlefield — `staticAbilities`, the
 * largest `CardScript` slot the grammar had never reached.
 *
 * The family opens on **auras**, because an aura is the card class where the whole card is two of
 * these sentences and nothing else: "Enchant creature" ([Targets.enchant]) plus one line saying what
 * the enchanted creature gets. Every later static family — lords, evasion grants, cost reduction,
 * "can't block" — lands in the same slot, so what this file buys is mostly the slot rather than the
 * thirty cards it completes today.
 *
 * ### The noun is in the text and not in the model
 *
 * `GroupFilter.attachedCreature()` is `GameObjectFilter.Permanent` scoped to `AttachedTo`: it says
 * *the thing this is attached to*, and nothing at all about that thing being a creature. So
 * "Enchanted creature has flying." and "Enchanted land has flying." denote the identical value, and
 * registering both would be genuine ambiguity — two printed forms, one model, nothing for the
 * printer to choose. Exactly one noun is spelled here, the one that is nearly all of the corpus, and
 * "Enchanted land" declines rather than being printed back as "Enchanted creature". That is the
 * fail-closed reading: a decline names the gap, a re-spelling would quietly change what the card
 * says.
 *
 * ### Why there is no "Equipped creature …" rule
 *
 * Same reason, one step further out. An Equipment prints "Equipped creature gets +1/+1." for a value
 * that is *byte-identical* to the Aura's, because which word a card uses is a function of its type
 * line — the same class of printed-shape information as the self-reference noun ("this creature" vs
 * "this Equipment"), which [com.wingedsheep.assay.normalize.Normalizer] owns and abstracts away. If
 * the equipment forms are ever read, they belong in that pass as a recorded-and-restored surface
 * form, not as a second rule here; a second rule would leave printing underdetermined between them.
 *
 * ### No facade to build through
 *
 * The module's rule is that `build` goes through an SDK companion facade. Static abilities have
 * none — `dsl` publishes `Effects`, `Triggers`, `Costs` and `Conditions`, and hand-written cards
 * construct the static directly (`staticAbility { ability = ModifyStats(1, 2) }` on Holy Strength,
 * `GrantKeyword(Keyword.FLYING)` on Flight). The constructor *is* the curated surface here, exactly
 * as it is for [Replacements], and the missing `Statics` facade is a small SDK finding rather than
 * something this file should route around.
 *
 * One thing to know before reading a golden: **two SDK types share `@SerialName("ModifyStats")`** —
 * the [ModifyStats] static below and `ModifyStatsEffect`, the *effect* [Steps.pumpTargetPermanent]
 * prints as "Target creature gets +3/+3 until end of turn." They are in different polymorphic
 * hierarchies so nothing clashes, but a card's JSON shows both as `"type": "ModifyStats"`.
 */
object Statics {

    /**
     * "Enchanted creature gets +1/+2." — Holy Strength, and 37 more lines on cards already written.
     *
     * The `filter` argument is absent from the constructed value on purpose: the aura form **is**
     * [ModifyStats]'s default, which is why Holy Strength's golden carries no `filter` key either.
     * The equality against that reconstruction is what makes the omission safe — a lord's
     * "Creatures you control get +1/+1." is the same type with a real `GroupFilter`, and it refuses
     * to print here rather than printing a sentence about an aura.
     */
    private val attachedPump: Phrase<StaticAbility> =
        phrase("enchanted creature gets {mod}.", name = "enchanted creature gets") {
            slot("mod", Primitives.statModifiers)
            build {
                val (power, toughness) = it.value<Pair<Int, Int>>("mod")
                ModifyStats(power, toughness)
            }
            match { ability ->
                val stats = ability as? ModifyStats ?: return@match null
                if (stats != ModifyStats(stats.powerBonus, stats.toughnessBonus)) return@match null
                bind("mod" to (stats.powerBonus to stats.toughnessBonus))
            }
        }

    /**
     * "Enchanted creature has flying." — the granted-keyword static, slotting [Keywords.keyword]
     * whole so every parameterless keyword the grammar can spell arrives here for free.
     *
     * [GrantKeyword] holds its keyword as a `String`, which is wider than [Keyword]: the SDK also
     * uses the field for synthesized markers like `PROTECTION_FROM_BLACK` and `TOXIC_2` that no
     * enum constant names. Reading it back therefore has to find the constant rather than assume
     * one, and a value that names none declines — as does one whose keyword has no surface form in
     * [Keywords.keyword], since the slot's own printer refuses it.
     */
    private val attachedKeyword: Phrase<StaticAbility> =
        phrase("enchanted creature has {kw}.", name = "enchanted creature has a keyword") {
            slot("kw", Keywords.keyword)
            build { GrantKeyword(it.value<Keyword>("kw")) }
            match { ability ->
                val grant = ability as? GrantKeyword ?: return@match null
                val keyword = Keyword.entries.firstOrNull { it.name == grant.keyword } ?: return@match null
                if (grant != GrantKeyword(keyword)) return@match null
                bind("kw" to keyword)
            }
        }

    val all: List<Phrase<StaticAbility>> = listOf(attachedPump, attachedKeyword)

    val static: Phrase<StaticAbility> = oneOf("a static ability", all)

    /** One static, lifted into the one-element list a line usually denotes. */
    private val single: Phrase<List<StaticAbility>> = phrase("{one}", name = "a static ability") {
        slot("one", static)
        build { listOf(it.value<StaticAbility>("one")) }
        match { it.singleOrNull()?.let { ability -> bind("one" to ability) } }
    }

    /**
     * "Enchanted creature gets +2/+2 and has flying." — **two** static abilities from one sentence,
     * the third time a printed phrase denotes several models rather than one.
     *
     * [Keywords.qualityRun] (CR 702.16g's joined protection) and [Mana.alternatives] ("{T}: Add {B}
     * or {G}." as two abilities sharing a cost) are the other two, and the answer is the same one
     * each time: the rule denotes a *list*, and the slot above lifts the ordinary single-ability
     * line into the same shape so nothing downstream has to know which it got. What is emphatically
     * not the answer is a compound SDK type meaning "pump and grant" — the model is already right,
     * and there are exactly two abilities in it.
     *
     * Written as one rule rather than as a family: it has one member. Twenty-seven hand-written
     * cards print this sentence and all twenty-seven order the statics as the text does, pump then
     * grant, which is what makes the reconstruction below a comparison and not a convention. A card
     * carrying them the other way round declines rather than being reordered into agreement.
     */
    private val pumpAndKeyword: Phrase<List<StaticAbility>> = run {
        fun abilitiesFor(modifiers: Pair<Int, Int>, keyword: Keyword) =
            listOf(ModifyStats(modifiers.first, modifiers.second), GrantKeyword(keyword))
        phrase("enchanted creature gets {mod} and has {kw}.", name = "enchanted creature gets and has") {
            slot("mod", Primitives.statModifiers)
            slot("kw", Keywords.keyword)
            build { abilitiesFor(it.value("mod"), it.value("kw")) }
            match { abilities ->
                val stats = abilities.firstOrNull() as? ModifyStats ?: return@match null
                val grant = abilities.getOrNull(1) as? GrantKeyword ?: return@match null
                val keyword = Keyword.entries.firstOrNull { it.name == grant.keyword } ?: return@match null
                val modifiers = stats.powerBonus to stats.toughnessBonus
                if (abilities != abilitiesFor(modifiers, keyword)) return@match null
                bind("mod" to modifiers, "kw" to keyword)
            }
        }
    }

    /**
     * What one static-ability line denotes: usually one ability, and for [pumpAndKeyword] two.
     *
     * The two alternatives take disjoint list sizes, so printing is decided by the model rather than
     * by the alternation's order — the property every `oneOf` in this grammar is written to have.
     */
    val line: Phrase<List<StaticAbility>> = oneOf("static abilities", pumpAndKeyword, single)
}
