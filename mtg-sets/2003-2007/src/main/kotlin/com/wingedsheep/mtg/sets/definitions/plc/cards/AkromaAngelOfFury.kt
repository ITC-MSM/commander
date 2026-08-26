package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Akroma, Angel of Fury
 * {5}{R}{R}{R}
 * Legendary Creature — Angel
 * 6/6
 * This spell can't be countered.
 * Flying, trample, protection from white and from blue
 * {R}: Akroma gets +1/+0 until end of turn.
 * Morph {3}{R}{R}{R}
 *
 * "Protection from white **and from blue**" is two separate protection abilities, not one
 * two-colour scope — the printed "from" repeats, so each colour is its own ability.
 */
val AkromaAngelOfFury = card("Akroma, Angel of Fury") {
    manaCost = "{5}{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Angel"
    power = 6
    toughness = 6
    oracleText = "This spell can't be countered.\n" +
        "Flying, trample, protection from white and from blue\n" +
        "{R}: Akroma gets +1/+0 until end of turn.\n" +
        "Morph {3}{R}{R}{R} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)"

    cantBeCountered = true

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    keywordAbility(KeywordAbility.protectionFrom(Color.WHITE))
    keywordAbility(KeywordAbility.protectionFrom(Color.BLUE))

    morph = "{3}{R}{R}{R}"

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "{R}: Akroma gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "94"
        artist = "Daren Bader"
        imageUri = "https://cards.scryfall.io/normal/front/3/8/3815de82-5ba9-4fb6-9259-d5e50e046890.jpg"
    }
}
