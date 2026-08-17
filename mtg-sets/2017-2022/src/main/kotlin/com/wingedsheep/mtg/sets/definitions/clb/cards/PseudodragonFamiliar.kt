package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pseudodragon Familiar
 * {2}{U}
 * Creature — Dragon
 * 2/1
 * Flying
 * {2}{U}: Target creature gains flying until end of turn.
 *
 * A flyer that rents its wings out. The activated half is one [Effects.GrantKeyword] on a plain
 * [Targets.Creature] slot — end-of-turn is that facade's default duration, so the printed "until end
 * of turn" needs no argument.
 */
val PseudodragonFamiliar = card("Pseudodragon Familiar") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Dragon"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "{2}{U}: Target creature gains flying until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        val creature = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
        description = "{2}{U}: Target creature gains flying until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "Campbell White"
        flavorText = "Pseudodragons are favored companions of wizards, prized for their cunning and curiosity."
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50d69ee8-a616-4191-b111-1330dfb24f72.jpg?1783922780"
    }
}
