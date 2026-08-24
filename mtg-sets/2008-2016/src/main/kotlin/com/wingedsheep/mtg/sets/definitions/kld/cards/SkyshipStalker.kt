package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skyship Stalker
 * {2}{R}{R}
 * Creature — Cat Dragon
 * 3/3
 *
 * Flying
 * {R}: This creature gets +1/+0 until end of turn.
 * {R}: This creature gains first strike until end of turn.
 * {R}: This creature gains haste until end of turn.
 *
 * Three separate one-mana pumps, not one modal ability: each printed line is its own activated
 * ability, so they can be activated independently and any number of times. All three point at
 * [EffectTarget.Self] — none of them targets, so none of them can be fizzled.
 */
val SkyshipStalker = card("Skyship Stalker") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cat Dragon"
    oracleText = "Flying\n" +
        "{R}: This creature gets +1/+0 until end of turn.\n" +
        "{R}: This creature gains first strike until end of turn.\n" +
        "{R}: This creature gains haste until end of turn."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "130"
        artist = "Chris Rahn"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a35be7b-d693-4433-9b13-8e019adc594e.jpg?1783937189"
    }
}
