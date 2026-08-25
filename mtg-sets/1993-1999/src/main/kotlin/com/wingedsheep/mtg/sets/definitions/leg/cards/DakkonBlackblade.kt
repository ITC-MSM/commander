package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Dakkon Blackblade
 * {2}{W}{U}{U}{B}
 * Legendary Creature — Human Warrior
 *
 * Dakkon Blackblade's power and toughness are each equal to the number of lands you control.
 *
 * A characteristic-defining ability (CR 604.3): the `*`/`*` stat box is the P/T slot
 * itself, not an ability list entry, so it lives in [dynamicStats] and applies in every zone.
 */
val DakkonBlackblade = card("Dakkon Blackblade") {
    manaCost = "{2}{W}{U}{U}{B}"
    colorIdentity = "BUW"
    typeLine = "Legendary Creature — Human Warrior"
    oracleText = "Dakkon Blackblade's power and toughness are each equal to the number of lands you control."

    dynamicStats(DynamicAmounts.battlefield(Player.You, GameObjectFilter.Land).count())

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "225"
        artist = "Richard Kane Ferguson"
        flavorText = "\"My power is as vast as the plains, my strength is that of mountains. Each wave that " +
            "crashes upon the shore thunders like blood in my veins.\" —*Memoirs*"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbfd1278-1486-4516-8846-007ce1985ee9.jpg?1783948040"
    }
}
