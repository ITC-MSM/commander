package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Gothmog, Morgul Lieutenant
 * {3}{B}
 * Legendary Creature — Human Soldier
 * 3/3
 *
 * When Gothmog enters, amass Orcs 1.
 * Creature tokens you control have deathtouch.
 */
val GothmogMorgulLieutenant = card("Gothmog, Morgul Lieutenant") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Soldier"
    power = 3
    toughness = 3
    oracleText = "When Gothmog enters, amass Orcs 1. (Put a +1/+1 counter on an Army you control. " +
        "It's also an Orc. If you don't control an Army, create a 0/0 black Orc Army creature token first.)\n" +
        "Creature tokens you control have deathtouch."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Amass(1, "Orc")
    }

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.DEATHTOUCH,
            filter = GroupFilter((GameObjectFilter.Creature and GameObjectFilter.Token).youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "87"
        artist = "Ilker Yildiz"
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1c10e93-88eb-46b9-8adc-583661807990.jpg?1686968484"
    }
}
