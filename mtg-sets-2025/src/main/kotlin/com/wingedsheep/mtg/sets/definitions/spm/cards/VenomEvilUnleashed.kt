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
 * Venom, Evil Unleashed
 * {4}{B}
 * Legendary Creature — Symbiote Villain
 * 4/5
 * Deathtouch
 * {2}{B}, Exile this card from your graveyard: Put two +1/+1 counters on target creature. It gains deathtouch until end of turn. Activate only as a sorcery.
 */
val VenomEvilUnleashed = card("Venom, Evil Unleashed") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Symbiote Villain"
    oracleText = "Deathtouch\n{2}{B}, Exile this card from your graveyard: Put two +1/+1 counters on target creature. It gains deathtouch until end of turn. Activate only as a sorcery."
    power = 4
    toughness = 5
    keywords(Keyword.DEATHTOUCH)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.ExileSelf)
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 2, target = t),
            Effects.GrantKeyword(Keyword.DEATHTOUCH, t)
        )
        timing = TimingRule.SorcerySpeed
        activateFromZone = Zone.GRAVEYARD
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Jesper Ejsing"
        flavorText = "\"We like seeing fear in our victim's eyes.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab3d51a4-40f0-4606-b5f9-2686c12fd54b.jpg?1757377205"
    }
}
