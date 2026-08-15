package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.constant
import com.wingedsheep.assay.syntax.oneOf
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate

/**
 * The noun phrase a spell acts on — "creature", "artifact or enchantment", "creature you control".
 *
 * The target of these rules is `mtg-sdk`'s [GameObjectFilter], which is a *bag of predicates*, and
 * that shape decides how the grammar has to be written. A predicate bag has no canonical spelling:
 * `Creature.youControl()` could in principle be printed by a rule for the type and a rule for the
 * controller in either order, and two rules that can each print part of one value are how a
 * bidirectional grammar goes underdetermined. So the rules here are **layered, not composed**: one
 * alternation spells the whole type phrase, and exactly one optional suffix spells the controller.
 * Each layer strips precisely the field it owns and delegates the rest, which keeps one printed form
 * per model without needing a normal form for the bag itself.
 *
 * ### Why the type list is enumerated
 *
 * "artifact or enchantment" is `Or([IsArtifact, IsEnchantment])` — an ordered list — while "artifact
 * creature" is two separate predicates. English does not distinguish them by shape, only by the
 * words, so deriving the surface from the predicates would need a theory of Magic's templating that
 * the SDK does not carry. Enumerating the printed forms keeps every rule provably invertible, and a
 * form nobody wrote down declines rather than being approximated — which is the point.
 */
object Filters {

    /**
     * The type phrase, without a controller clause.
     *
     * Ordered by nothing in particular: parsing tries every branch and the whole-span filter
     * resolves it, so "creature" and "creature or land" cannot both consume the same text.
     */
    private val typePhrase: Phrase<GameObjectFilter> = oneOf(
        "a permanent type",
        constant("creature", GameObjectFilter.Creature),
        constant("artifact", GameObjectFilter.Artifact),
        constant("enchantment", GameObjectFilter.Enchantment),
        constant("land", GameObjectFilter.Land),
        constant("planeswalker", GameObjectFilter.Planeswalker),
        constant("permanent", GameObjectFilter.Permanent),
        constant("nonland permanent", GameObjectFilter.NonlandPermanent),
        constant("noncreature permanent", GameObjectFilter.NoncreaturePermanent),
        constant("nonbasic land", GameObjectFilter.NonbasicLand),
        constant("artifact creature", GameObjectFilter.ArtifactCreature),
        constant("creature or planeswalker", GameObjectFilter.CreatureOrPlaneswalker),
        constant("creature or enchantment", GameObjectFilter.CreatureOrEnchantment),
        constant("creature or land", GameObjectFilter.CreatureOrLand),
        constant("artifact or enchantment", GameObjectFilter.ArtifactOrEnchantment),
        constant("artifact or land", GameObjectFilter.ArtifactOrLand),
        constant("attacking creature", GameObjectFilter.Creature.attacking()),
        constant("blocking creature", GameObjectFilter.Creature.blocking()),
        constant("tapped creature", GameObjectFilter.Creature.tapped()),
        constant("untapped creature", GameObjectFilter.Creature.untapped()),
    )

    /**
     * The controller clause, which is a suffix in English and a single field in the model — so it is
     * one rule per printed form, each stripping [GameObjectFilter.controllerPredicate] and handing
     * the rest back to [typePhrase].
     */
    private fun controlledBy(surface: String, predicate: ControllerPredicate): Phrase<GameObjectFilter> =
        phrase("{type} $surface", name = "a permanent $surface") {
            slot("type", typePhrase)
            build { it.value<GameObjectFilter>("type").copy(controllerPredicate = predicate) }
            match { filter ->
                if (filter.controllerPredicate != predicate) {
                    null
                } else {
                    bind("type" to filter.copy(controllerPredicate = null))
                }
            }
        }

    /**
     * A whole noun phrase: a type, optionally with a controller clause.
     *
     * A filter carrying no controller predicate can only be printed by [typePhrase], and one that
     * carries a predicate only by the matching suffix rule, so printing is determined by the model
     * and the alternation order is irrelevant — the property every `oneOf` in this grammar wants.
     */
    val filter: Phrase<GameObjectFilter> = oneOf(
        "a permanent",
        typePhrase,
        controlledBy("you control", ControllerPredicate.ControlledByYou),
        controlledBy("an opponent controls", ControllerPredicate.ControlledByOpponent),
    )
}
