package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skyline Scout
 * {1}{W}
 * Creature — Human Scout
 * 2/1
 * Whenever this creature attacks, you may pay {1}{W}. If you do, it gains flying until end of turn.
 */
val SkylineScout = card("Skyline Scout") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Scout"
    oracleText = "Whenever this creature attacks, you may pay {1}{W}. If you do, it gains flying until end of turn."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = MayPayManaEffect(
            ManaCost.parse("{1}{W}"),
            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Paul Scott Canavan"
        flavorText = "\"Sometimes an angel passes by and gives me a little nod, like, 'You're a daring one!' That always makes my day.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b052b641-fce9-45c1-a15b-1e22f1a64e4d.jpg?1783934195"
    }
}
