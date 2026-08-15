package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Up the Beanstalk
 * {1}{G}
 * Enchantment
 *
 * When this enchantment enters and whenever you cast a spell with mana value 5 or greater,
 * draw a card.
 *
 * Printed as a single sentence, but it is two separate triggered abilities (CR 603.1) — an
 * enters trigger and a cast trigger — so it follows the repo's "enters or attacks" idiom of one
 * `triggeredAbility` block per condition. The cast trigger fires on *cast*, not on resolution,
 * so it still draws if the spell is countered.
 */
val UpTheBeanstalk = card("Up the Beanstalk") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters and whenever you cast a spell with mana value 5 " +
        "or greater, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    triggeredAbility {
        trigger = Triggers.youCastSpell(GameObjectFilter.Any.manaValueAtLeast(5))
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "195"
        artist = "Lucas Graciano"
        flavorText = "\"It's not so bad. All you have to do is keep from looking down.\"\n" +
            "—Troyan, to Kellan and Ruby"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d5e991f-23b2-4db0-a452-7755125b1fd2.jpg?1783915075"
    }
}
