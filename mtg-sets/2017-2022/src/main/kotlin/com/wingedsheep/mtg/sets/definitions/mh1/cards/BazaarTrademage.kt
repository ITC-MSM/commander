package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bazaar Trademage
 * {2}{U}
 * Creature — Human Wizard
 * 3/4
 * Flying
 * When this creature enters, draw two cards, then discard three cards.
 *
 * "Draw two, then discard three" is Bazaar of Baghdad's shape on an enters trigger: the draw runs
 * first and the discard is the Gather → Select → Move hand pipeline behind [Effects.Discard], so the
 * three cards are chosen after the two are in hand. The discard is mandatory — with fewer than
 * three cards you discard as many as you can.
 */
val BazaarTrademage = card("Bazaar Trademage") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature enters, draw two cards, then discard three cards."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(2).then(Effects.Discard(3))
        description = "When this creature enters, draw two cards, then discard three cards."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "41"
        artist = "Christopher Moeller"
        flavorText = "He traded a lamp for a scepter, the scepter for a ruby, and the ruby for a simple rug."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d75faf7-fc27-4fc2-9e80-e35232c42542.jpg?1783933150"
    }
}
