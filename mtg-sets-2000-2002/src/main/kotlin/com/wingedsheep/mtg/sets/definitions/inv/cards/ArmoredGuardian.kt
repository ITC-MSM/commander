package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Armored Guardian
 * {3}{W}{U}
 * Creature — Cat Soldier
 * 2/5
 *
 * {1}{W}{W}: Target creature you control gains protection from the color of your choice
 *            until end of turn.
 * {1}{U}{U}: This creature gains shroud until end of turn.
 */
val ArmoredGuardian = card("Armored Guardian") {
    manaCost = "{3}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Cat Soldier"
    power = 2
    toughness = 5
    oracleText = "{1}{W}{W}: Target creature you control gains protection from the color of your choice until end of turn.\n" +
        "{1}{U}{U}: This creature gains shroud until end of turn. (It can't be the target of spells or abilities.)"

    activatedAbility {
        cost = Costs.Mana("{1}{W}{W}")
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.ChooseColorThen(Effects.GrantProtectionFromChosenColor(t))
        description = "{1}{W}{W}: Target creature you control gains protection from the color of your choice until end of turn."
    }

    activatedAbility {
        cost = Costs.Mana("{1}{U}{U}")
        effect = Effects.GrantKeyword(Keyword.SHROUD, EffectTarget.Self)
        description = "{1}{U}{U}: This creature gains shroud until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "230"
        artist = "Arnie Swekel"
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6de5e1bd-1d31-4f9f-b18d-d6f49bc7ef10.jpg?1562917002"
    }
}
