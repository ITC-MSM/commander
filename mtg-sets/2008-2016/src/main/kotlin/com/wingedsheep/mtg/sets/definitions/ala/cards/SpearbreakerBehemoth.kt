package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Spearbreaker Behemoth
 * {5}{G}{G}
 * Creature — Beast
 * 5 / 5
 * Indestructible
 * {1}: Target creature with power 5 or greater gains indestructible until end of turn.
 *
 * The Rakeclaw Gargantuan shape with its own keyword to hand out: the power threshold is a filter
 * on the target itself — [TargetFilter.Creature]`.powerAtLeast(5)` — so legality is checked on
 * announcement and rechecked on resolution, and the grant is [Effects.GrantKeyword] on the bound
 * target with its default `Duration.EndOfTurn`. The Behemoth's own indestructibility is the
 * printed [Keyword.INDESTRUCTIBLE], separate from the ability.
 */
val SpearbreakerBehemoth = card("Spearbreaker Behemoth") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5
    oracleText = "Indestructible\n" +
        "{1}: Target creature with power 5 or greater gains indestructible until end of turn."

    keywords(Keyword.INDESTRUCTIBLE)

    activatedAbility {
        cost = Costs.Mana("{1}")
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.powerAtLeast(5)))
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "150"
        artist = "Christopher Moeller"
        flavorText = "Few Nayans dare hunt the gargantuans. They're regarded not as animals but as forces of nature, like landslides or typhoons."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/132367ee-22e9-48e2-82e0-62ad9aaa62f3.jpg"
    }
}
