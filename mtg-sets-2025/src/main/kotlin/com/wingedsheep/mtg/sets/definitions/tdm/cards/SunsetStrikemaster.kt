package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Sunset Strikemaster — Tarkir: Dragonstorm #126
 * {1}{R} · Creature — Human Monk · 3/1
 *
 * {T}: Add {R}.
 * {2}{R}, {T}, Sacrifice this creature: It deals 6 damage to target creature with flying.
 *
 * The first ability is a vanilla red mana ability ({T}: Add {R}). The second is an activated
 * ability whose cost is the composite of {2}{R}, tapping, and sacrificing the source; on
 * resolution it deals 6 damage to a target creature with flying (via the `withKeyword(FLYING)`
 * target filter). Both abilities tap the source, so they compete for the same tap — exactly as
 * the printed card intends.
 */
val SunsetStrikemaster = card("Sunset Strikemaster") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Monk"
    power = 3
    toughness = 1
    oracleText = "{T}: Add {R}.\n" +
        "{2}{R}, {T}, Sacrifice this creature: It deals 6 damage to target creature with flying."

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{R}"),
            AbilityCost.Tap,
            AbilityCost.SacrificeSelf,
        )
        val t = target(
            "target creature with flying",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))),
        )
        effect = DealDamageEffect(6, t)
        description = "{2}{R}, {T}, Sacrifice this creature: It deals 6 damage to target creature with flying."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "Zara Alfonso"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8f1a2f2-526d-4b2c-985b-0acfdc21a2ee.jpg?1743204467"
    }
}
