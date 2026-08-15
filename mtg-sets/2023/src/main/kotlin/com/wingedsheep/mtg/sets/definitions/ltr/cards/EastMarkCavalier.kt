package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * East-Mark Cavalier
 * {1}{W}
 * Creature — Human Knight
 * 2/2
 *
 * Vigilance
 * Whenever this creature deals damage to a Goblin or Orc, destroy that creature.
 */
val EastMarkCavalier = card("East-Mark Cavalier") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "Vigilance\nWhenever this creature deals damage to a Goblin or Orc, destroy that creature."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            recipient = RecipientFilter.Matching(
                GameObjectFilter.Creature.withAnySubtype("Goblin", "Orc")
            )
        )
        effect = Effects.Destroy(EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Viko Menezes"
        flavorText = "\"The Rohirrim have long been the friends of the people of Gondor, though they are not akin to them.\"\n—Aragorn"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9be412c0-45d4-4856-a8cf-63a5a822dc5c.jpg?1686967715"
    }
}
