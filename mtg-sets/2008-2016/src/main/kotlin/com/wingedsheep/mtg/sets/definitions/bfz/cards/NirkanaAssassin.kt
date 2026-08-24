package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nirkana Assassin
 * {2}{B}
 * Creature — Vampire Assassin Ally
 * 2/3
 * Whenever you gain life, this creature gains deathtouch until end of turn. (Any amount of damage it deals to a creature is enough to destroy it.)
 */
val NirkanaAssassin = card("Nirkana Assassin") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Assassin Ally"
    power = 2
    toughness = 3
    oracleText = "Whenever you gain life, this creature gains deathtouch until end of turn. (Any amount of damage " +
        "it deals to a creature is enough to destroy it.)"

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Viktor Titov"
        flavorText = "Nirkana assassins craft incurable concoctions by mixing basilisk marrow and deathwillow sap " +
            "with the vital fluids of a dozen other poisonous species."
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2ca43ea1-ba7b-4dc7-b6f6-a0c92321ebe1.jpg?1783938200"
    }
}
