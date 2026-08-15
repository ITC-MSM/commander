package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreatureOrPlaneswalker
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetOpponent
import com.wingedsheep.sdk.scripting.targets.TargetOpponentOrPlaneswalker
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import com.wingedsheep.sdk.scripting.targets.TargetPlayerOrPlaneswalker
import com.wingedsheep.sdk.scripting.targets.TargetRequirement

/**
 * Targeting, as the SDK splits it: a spell declares a [TargetRequirement] and its effect refers to
 * what was chosen through an [EffectTarget].
 *
 * The two halves are linked by a **name**, and the name is arbitrary — Ancestral Recall's golden
 * uses `"target"`, and any other string would behave identically. That makes it the one thing in a
 * parsed model that is not determined by the text, so the grammar always mints [SLOT] and the
 * differential renames both sides before comparing (see `Differential.normalizeSlotNames`). Two
 * models that differ only in what they called a slot are the same model, and neither the round trip
 * nor the differential should be able to see the difference.
 *
 * Almost nothing here is a [com.wingedsheep.assay.syntax.Phrase], and that is on purpose: a target
 * is not a line, and the phrasings that introduce one ("Target player draws…", "Destroy target
 * creature") are inseparable from the step that consumes it — English puts the verb and its object
 * in one clause, and so does the rule. What lives here is the vocabulary those rules share.
 *
 * [enchant] is the exception, and it is one because Magic makes it one: an Aura's attachment
 * restriction *is* a whole printed line with no verb in it, and the model it denotes is a bare
 * `TargetRequirement`. There is nowhere else for it to live.
 */
object Targets {

    /**
     * The canonical name linking a [TargetRequirement] to the [EffectTarget] that reads it.
     *
     * One name is enough while every rule takes at most one target. When a rule needs two, this
     * becomes a generator and the differential's renaming already handles the rest.
     */
    const val SLOT = "target"

    /**
     * The name of the [index]-th slot, for the rules that declare more than one.
     *
     * Slot 0 keeps the bare [SLOT] so every existing single-target rule is untouched, and the rest
     * are numbered. The names are as arbitrary as [SLOT] itself — the differential compares slots by
     * *position* — so all this has to do is be distinct, which is precisely what
     * [Steps.merge] refuses to invent when two clauses each declare one.
     */
    fun slot(index: Int): String = if (index == 0) SLOT else "$SLOT $index"

    /** …and the reference half, for an effect reading the [index]-th slot. */
    fun bound(index: Int): EffectTarget = EffectTarget.BoundVariable(slot(index))

    /** "target creature you control and target creature an opponent controls" — one of several. */
    fun permanent(filter: GameObjectFilter, index: Int): TargetRequirement =
        TargetPermanent(filter = TargetFilter(filter), id = slot(index))

    /**
     * "target player" — the requirement half.
     *
     * Constructed directly rather than through `dsl.Targets.Player`, which is the facade for this
     * shape but exposes no id: it is a `val` fixed at `TargetPlayer()`. A requirement that cannot be
     * named cannot be referred to, so an effect wanting `EffectTarget.BoundVariable` has to bypass
     * it. Worth an `id` parameter on the facade; noted rather than changed here.
     */
    fun player(): TargetRequirement = TargetPlayer(id = SLOT)

    /** "target opponent" — the requirement half, constructed directly for the reason [player] is. */
    fun opponent(): TargetRequirement = TargetOpponent(id = SLOT)

    /** "target opponent or planeswalker" — the modern damage-redirection wording (CR 115.7b). */
    fun opponentOrPlaneswalker(): TargetRequirement = TargetOpponentOrPlaneswalker(id = SLOT)

    /** …and its any-player sibling, which older burn spells print instead. */
    fun playerOrPlaneswalker(): TargetRequirement = TargetPlayerOrPlaneswalker(id = SLOT)

    /**
     * "any target" — the burn-spell requirement, covering any creature, player or planeswalker.
     *
     * Constructed directly for the same reason [player] is: `dsl.Targets.Any` is a `val` fixed at
     * `AnyTarget()`, and a requirement with no id cannot be referred to by the effect that reads it.
     */
    fun any(): TargetRequirement = AnyTarget(id = SLOT)

    /** …and the reference half, for the effect that acts on it. */
    fun bound(): EffectTarget = EffectTarget.BoundVariable(SLOT)

    /** True when [target] is a reference to the single slot this grammar mints. */
    fun isBound(target: EffectTarget): Boolean =
        target is EffectTarget.BoundVariable && target.name == SLOT

    /**
     * "target creature" — one permanent on the battlefield matching [filter].
     *
     * `TargetPermanent` and `TargetCreature` are both thin factories over the same [TargetObject]
     * with the same defaults, so the choice between them is a naming one and the model is identical
     * either way; the filter is what carries the meaning. Cards written by hand reach for whichever
     * reads better at the call site, and this has to equal both.
     */
    fun permanent(filter: GameObjectFilter): TargetRequirement =
        if (filter == GameObjectFilter.CreatureOrPlaneswalker) TargetCreatureOrPlaneswalker(id = SLOT)
        else TargetPermanent(filter = TargetFilter(filter), id = SLOT)

    /**
     * **"Target creature or planeswalker" has a requirement type of its own, and it is the one the
     * corpus writes.**
     *
     * Every other noun phrase in [Filters] becomes a filter inside a `TargetObject`, and this one
     * could too — `GameObjectFilter.CreatureOrPlaneswalker` is a perfectly good `Or` of two
     * predicates. But the SDK also ships [TargetCreatureOrPlaneswalker], a requirement carrying no
     * filter at all, and 34 hand-written cards use it against none that spell the `Or`. So the two
     * spellings are real and the corpus has already chosen; a rule that printed the other would be
     * inventing a house style, exactly as the each-player damage rule in [Steps] was. Broadside
     * Barrage, Sear, Hero's Downfall and Defibrillating Current are what the differential reported.
     *
     * Only the *bare* phrase maps: "target creature or planeswalker you control" carries a
     * controller predicate that this filterless requirement cannot hold, so it stays a
     * `TargetObject` and [permanentFilter]'s reconstruct-and-compare is what keeps that honest.
     */
    private val creatureOrPlaneswalker = GameObjectFilter.CreatureOrPlaneswalker

    /**
     * The inverse: the filter [requirement] restricts to, or null when it is anything else.
     *
     * Fail-closed on every field the grammar does not spell — a requirement that also carries
     * `excludeSelf`, a non-battlefield zone or a cross-zone union says something the printed phrase
     * "target creature" does not, and confirming it would claim a reading nobody performed. The
     * final equality check is what makes that exhaustive rather than a list of fields to remember.
     */
    fun permanentFilter(requirement: TargetRequirement): GameObjectFilter? {
        if (requirement is TargetCreatureOrPlaneswalker) {
            return creatureOrPlaneswalker.takeIf { requirement == permanent(it) }
        }
        val base = (requirement as? TargetObject)?.filter?.baseFilter ?: return null
        return base.takeIf { requirement == permanent(it) }
    }

    /**
     * "Enchant creature" — an Aura's attachment restriction, and the whole of its printed line.
     *
     * ### Why this is not a keyword ability
     *
     * Enchant *is* a keyword ability in the Comprehensive Rules (702.5), and it is the largest
     * keyword-only decline family in the corpus at 1,289 cards — but `mtg-sdk` models it as
     * [com.wingedsheep.sdk.model.CardScript.auraTarget], a plain `TargetRequirement`, so there is no
     * `KeywordAbility` for [Keywords] to parse it into. That mismatch was Phase 1's first reported
     * finding; this rule is the answer to it, and it is a rule about a *target*, not about a keyword.
     *
     * Equip, the other half of that finding, is deliberately still absent. It looks like the same
     * shape and is not: `Equip {2}` lowers at authoring time into `CardDefinition.equipCost` *and* a
     * synthesized activated ability carrying its own timing, effect and target requirement — a
     * lowering to reproduce rather than a sentence to read, and one that reaches past `CardScript`
     * into a slot [CardFragment] does not model. Enchant needs none of that.
     *
     * The restriction is spelled through [permanent] like every other filtered target, so the whole
     * of [Filters] arrives with it: "Enchant creature you control" and "Enchant land" are already
     * rows in a list this rule slots rather than rules of their own.
     */
    val enchant: Phrase<TargetRequirement> = phrase("enchant {filter}", name = "enchant") {
        slot("filter", Filters.filter)
        build { permanent(it.value("filter")) }
        match { requirement -> permanentFilter(requirement)?.let { bind("filter" to it) } }
    }
}
