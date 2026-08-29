package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Omen of the Sea
 * {1}{U}
 * Enchantment
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 * When this enchantment enters, scry 2, then draw a card.
 * {2}{U}, Sacrifice this enchantment: Scry 2. (Look at the top two cards of your library, then put
 * any number of them on the bottom and the rest on top in any order.)
 *
 * The blue member of the Omen cycle. The entry trigger's printed "scry 2, then draw a card" is a
 * single ordered sequence, so it is one `then`-composed pair rather than two abilities — the scry
 * must finish before the draw sees the top of the library.
 */
val OmenOfTheSea = card("Omen of the Sea") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "When this enchantment enters, scry 2, then draw a card.\n" +
        "{2}{U}, Sacrifice this enchantment: Scry 2. (Look at the top two cards of your library, " +
        "then put any number of them on the bottom and the rest on top in any order.)"

    keywords(Keyword.FLASH)

    // When this enchantment enters, scry 2, then draw a card.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Scry(2).then(Effects.DrawCards(1))
    }

    // {2}{U}, Sacrifice this enchantment: Scry 2.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{U}"),
            Costs.SacrificeSelf
        )
        effect = Effects.Scry(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "Piotr Dura"
        flavorText = "\"My time will come, when the rising tide will surge above the tallest mountain.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5f30ecd-d009-4d44-aef4-c926ed55a521.jpg"
    }
}
