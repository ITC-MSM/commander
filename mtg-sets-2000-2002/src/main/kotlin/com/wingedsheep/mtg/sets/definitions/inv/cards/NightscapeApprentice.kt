package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nightscape Apprentice
 * {B}
 * Creature — Zombie Wizard
 * 1/1
 * {U}, {T}: Put target creature you control on top of its owner's library.
 * {R}, {T}: Target creature gains first strike until end of turn.
 */
val NightscapeApprentice = card("Nightscape Apprentice") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Wizard"
    power = 1
    toughness = 1
    oracleText = "{U}, {T}: Put target creature you control on top of its owner's library.\n" +
        "{R}, {T}: Target creature gains first strike until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.PutOnTopOfLibrary(t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "112"
        artist = "Andrew Goldhawk"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/7498ca4c-614a-4776-8886-0a6ed58520f6.jpg?1562918357"
    }
}
