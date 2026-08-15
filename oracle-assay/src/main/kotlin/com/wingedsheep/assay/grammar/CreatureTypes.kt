package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.alternate
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.effects.BecomeCreatureTypeEffect
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.ForEachEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Clauses that name a creature type the player picks — "becomes the creature type of your choice",
 * "creatures of the creature type of your choice get +2/+2".
 *
 * A family because the *choice* is what they share: the type is not in the text, it is chosen when
 * the effect resolves, and the SDK carries it as a pipeline slot rather than as a value. That is the
 * opposite of [Filters]' subtype layer, where the word is printed and the model holds it — which is
 * why these are their own sentences and not that layer with a different noun.
 */
object CreatureTypes {

    /**
     * "~ becomes the creature type of your choice until end of turn." — the Mistform cycle.
     *
     * Legions prints the same effect two ways: Mistform Seaswift's plain form and Mistform Sliver's
     * "…in addition to its other types…", which spells out what `BecomeCreatureTypeEffect` already
     * means. One model, two real English spellings, so the shorter one is canonical and the longer
     * is an [alternate] — a `VARIANT` rather than a decline, which says the reading was right and
     * only the spelling moved.
     */
    private fun becomesChosenType(suffix: String, canonicalForm: Boolean): Phrase<CardScript> {
        val script = CardScript(spellEffect = BecomeCreatureTypeEffect(target = EffectTarget.Self))
        val rule = phrase<CardScript>(
            "{self} becomes the creature type of your choice$suffix until end of turn",
            name = "the source becomes a chosen creature type",
        ) {
            slot("self", Primitives.self)
            build { script }
            match { if (it == script) bind("self" to Unit) else null }
            canonical = canonicalForm
        }
        return if (canonicalForm) rule else alternate(rule)
    }

    /**
     * "Choose a creature type. Each creature you control becomes that type until end of turn." —
     * Mistform Wakecaster's second ability.
     *
     * Two printed sentences and one recipe: the choice is the pipeline's first step and the
     * iteration reads the slot it stored, so neither sentence denotes anything alone. Same shape as
     * [Library.lookAtOpponentTopAndBury], for the same reason.
     */
    private val eachCreatureYouControlBecomesChosenType: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = Patterns.CreatureType.becomeChosenTypeAllCreatures(controllerOnly = true)
        )
        phrase(
            "choose a creature type. each creature you control becomes that type until end of turn",
            name = "each creature you control becomes a chosen type",
        ) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Creatures of the creature type of your choice get +2/+2 and gain trample until end of turn."
     * — Tribal Forcemage.
     *
     * `Effects.ChooseCreatureTypeModifyStats` takes the keyword as a nullable parameter, so the
     * "and gain …" half is one field rather than a second effect — which is why this is one rule
     * spanning the whole sentence and not a composition of a pump and a grant.
     */
    private val chosenTypeGetsAndGains: Phrase<CardScript> = run {
        fun scriptFor(modifiers: Pair<Int, Int>, keyword: Keyword) = CardScript(
            spellEffect = Effects.ChooseCreatureTypeModifyStats(
                powerModifier = modifiers.first,
                toughnessModifier = modifiers.second,
                grantKeyword = keyword,
            )
        )
        phrase(
            "creatures of the creature type of your choice get {mod} and gain {kw} until end of turn",
            name = "creatures of a chosen type get and gain",
        ) {
            slot("mod", Primitives.statModifiers)
            slot("kw", Keywords.keyword)
            build { scriptFor(it.value("mod"), it.value("kw")) }
            match { script ->
                // The recipe is a two-step pipeline — choose, then iterate — whose inner effect is
                // the pump and the grant. Only the three printed values are read back out of it;
                // the equality against `scriptFor` is what checks everything else, exactly as every
                // other `Patterns` rule in the grammar does.
                val steps = (script.spellEffect as? CompositeEffect)?.effects ?: return@match null
                val body = (steps.getOrNull(1) as? ForEachEffect)?.body as? CompositeEffect ?: return@match null
                val stats = body.effects.firstOrNull() as? ModifyStatsEffect ?: return@match null
                val grant = body.effects.getOrNull(1) as? GrantKeywordEffect ?: return@match null
                val power = (stats.powerModifier as? DynamicAmount.Fixed)?.amount ?: return@match null
                val toughness = (stats.toughnessModifier as? DynamicAmount.Fixed)?.amount ?: return@match null
                val keyword = Keyword.entries.firstOrNull { it.name == grant.keyword } ?: return@match null
                if (script != scriptFor(power to toughness, keyword)) return@match null
                bind("mod" to (power to toughness), "kw" to keyword)
            }
        }
    }

    val clauses: List<Phrase<CardScript>> = listOf(
        becomesChosenType("", canonicalForm = true),
        becomesChosenType(" in addition to its other types", canonicalForm = false),
        eachCreatureYouControlBecomesChosenType,
        chosenTypeGetsAndGains,
    )
}
