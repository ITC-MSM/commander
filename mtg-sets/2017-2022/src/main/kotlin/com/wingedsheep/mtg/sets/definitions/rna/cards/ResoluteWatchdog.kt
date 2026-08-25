package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Resolute Watchdog — Ravnica Allegiance #19
 * {W} · Creature — Dog · 1 / 3
 *
 * Sacrificing itself is [Costs.SacrificeSelf]; the indestructible grant is an ordinary
 * until-end-of-turn keyword grant onto the chosen creature.
 */
val ResoluteWatchdog = card("Resolute Watchdog") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dog"
    power = 1
    toughness = 3
    oracleText = "Defender\n" +
        "{1}, Sacrifice this creature: Target creature you control gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    keywords(Keyword.DEFENDER)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        val creature = target("target", Targets.CreatureYouControl)
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "19"
        artist = "Milivoj Ćeran"
        flavorText = "A friend in good times, a guardian in bad times, and a savior when all else fails."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56d86909-b7f3-4a46-9904-e173853b79f1.jpg"
    }
}
