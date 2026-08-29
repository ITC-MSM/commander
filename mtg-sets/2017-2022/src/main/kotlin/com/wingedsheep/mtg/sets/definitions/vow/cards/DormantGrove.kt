package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dormant Grove // Gnarled Grovestrider (Innistrad: Crimson Vow)
 * {3}{G}
 * Enchantment // Creature — Treefolk
 *
 * Front — Dormant Grove
 *   At the beginning of combat on your turn, put a +1/+1 counter on target creature you control.
 *   Then if that creature has toughness 6 or greater, transform this enchantment.
 *
 * Back — Gnarled Grovestrider (3/6)
 *   Vigilance
 *   Other creatures you control have vigilance.
 *
 * The "Then if that creature …" clause is a resolution-time recheck of the *same* target, not a
 * second condition on the ability, so it is a [ConditionalEffect] gated on
 * [Conditions.TargetMatchesFilter] against target slot 0 — the handle returned by `target(…)`
 * cannot itself carry a condition (a `target()` handle inside a `Condition` silently evaluates
 * false). The toughness is read off projected state, so the +1/+1 counter this ability just placed
 * counts toward the 6, which is exactly how the card plays: a 3/5 you pump becomes a 4/6 and flips
 * the Grove that same combat.
 *
 * The back's "Other creatures you control have vigilance" is a plain [GrantKeyword] over
 * [GroupFilter.OtherCreaturesYouControl] — the Grovestrider's own vigilance is the
 * printed keyword above it, not a self-inclusion in the grant.
 */

private val DormantGroveFront = card("Dormant Grove") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "At the beginning of combat on your turn, put a +1/+1 counter on target creature " +
        "you control. Then if that creature has toughness 6 or greater, transform this enchantment."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature) then ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(
                GameObjectFilter.Creature.toughnessAtLeast(6),
                targetIndex = 0
            ),
            effect = TransformEffect(EffectTarget.Self)
        )
        description = "At the beginning of combat on your turn, put a +1/+1 counter on target " +
            "creature you control. Then if that creature has toughness 6 or greater, transform " +
            "this enchantment."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "198"
        artist = "Kekai Kotaki"
        flavorText = "As the natural cycle of day and night grew twisted, old powers stirred deep " +
            "in the Ulvenwald."
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7d3012e-1798-4f72-8d9a-b2d16f817502.jpg?1783924820"
    }
}

private val GnarledGrovestrider = card("Gnarled Grovestrider") {
    manaCost = ""
    colorIdentity = "G"
    colorIndicator = "G" // Transformed back face, no mana cost (CR 204).
    typeLine = "Creature — Treefolk"
    power = 3
    toughness = 6
    oracleText = "Vigilance\nOther creatures you control have vigilance."

    keywords(Keyword.VIGILANCE)

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, GroupFilter.OtherCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "198"
        artist = "Kekai Kotaki"
        flavorText = "Far from the bloodsoaked wedding, unseen by human eyes, a long-slumbering " +
            "force arose to protect its home."
        imageUri = "https://cards.scryfall.io/normal/back/e/7/e7d3012e-1798-4f72-8d9a-b2d16f817502.jpg?1783924820"
    }
}

val DormantGrove: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = DormantGroveFront,
    backFace = GnarledGrovestrider,
)
