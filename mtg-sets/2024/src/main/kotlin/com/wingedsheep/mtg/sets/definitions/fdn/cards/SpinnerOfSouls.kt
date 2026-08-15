package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Spinner of Souls
 * {2}{G}
 * Creature — Spider Spirit
 * 4/3
 * Reach
 * Whenever another nontoken creature you control dies, you may reveal cards from the top of
 * your library until you reveal a creature card. Put that card into your hand and the rest on
 * the bottom of your library in a random order.
 */
val SpinnerOfSouls = card("Spinner of Souls") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider Spirit"
    power = 4
    toughness = 3
    oracleText = "Reach\nWhenever another nontoken creature you control dies, you may reveal cards from the top of your library until you reveal a creature card. Put that card into your hand and the rest on the bottom of your library in a random order."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl().nontoken(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER
        )
        optional = true
        effect = Patterns.Library.revealUntilMatchToHand(
            filter = GameObjectFilter.Creature
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "112"
        artist = "Xavier Ribeiro"
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f50a8dec-b079-4192-9098-6cdc1026c693.jpg?1782689168"
    }
}
