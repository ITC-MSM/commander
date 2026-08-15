package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Beetle, Legacy Criminal
 * {3}{U}
 * Legendary Creature — Human Rogue Villain
 * 3/3
 * Flying
 * {1}{U}, Exile this card from your graveyard: Put a +1/+1 counter on target creature. It gains flying until end of turn. Activate only as a sorcery.
 */
val BeetleLegacyCriminal = card("Beetle, Legacy Criminal") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Rogue Villain"
    oracleText = "Flying\n{1}{U}, Exile this card from your graveyard: Put a +1/+1 counter on target creature. It gains flying until end of turn. Activate only as a sorcery."
    power = 3
    toughness = 3
    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.ExileSelf)
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 1, target = t),
            Effects.GrantKeyword(Keyword.FLYING, t)
        )
        timing = TimingRule.SorcerySpeed
        activateFromZone = Zone.GRAVEYARD
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Carlos Dattoli"
        flavorText = "Bored with her humdrum existence, Janice Lincoln sought the thrill of her father's life of crime."
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a194f930-c99f-4915-8a62-e20ab2b4ad1f.jpg?1757376901"
    }
}
