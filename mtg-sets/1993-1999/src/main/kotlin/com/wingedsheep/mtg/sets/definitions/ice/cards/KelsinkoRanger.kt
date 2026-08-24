package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kelsinko Ranger
 * {W}
 * Creature — Human Ranger
 * 1/1
 *
 * {1}{W}: Target green creature gains first strike until end of turn.
 *
 * The "green" is a targeting predicate, so it rides [Targets.CreatureWithColor] rather than a
 * condition; the grant is [Effects.GrantKeyword], taking its default `Duration.EndOfTurn`.
 */
val KelsinkoRanger = card("Kelsinko Ranger") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Ranger"
    power = 1
    toughness = 1
    oracleText = "{1}{W}: Target green creature gains first strike until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        val t = target("target", Targets.CreatureWithColor(Color.GREEN))
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Mark Poole"
        flavorText = "\"Rangers not trained by the Elves just aren't the same.\"\n—Lucilde Fiksdotter, Leader of the Order of the White Shield"
        imageUri = "https://cards.scryfall.io/normal/front/8/4/8402543e-5406-404f-95c4-800a1dce35f1.jpg"
    }
}
