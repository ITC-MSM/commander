package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Butterbur, Bree Innkeeper
 * {2}{G}{W}
 * Legendary Creature — Human Peasant
 * 3/3
 * At the beginning of your end step, if you don't control a Food, create a Food token.
 */
val ButterburBreeInnkeeper = card("Butterbur, Bree Innkeeper") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Human Peasant"
    power = 3
    toughness = 3
    oracleText = "At the beginning of your end step, if you don't control a Food, create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.Not(
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Any.withSubtype("Food"))
        )
        effect = Effects.CreateFood()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "197"
        artist = "Ryan Yee"
        flavorText = "\"I hope you'll be comfortable. You'll be wanting supper, I don't doubt. As soon as may be. This way now!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3fd9ff1-278b-4e6a-b30b-90250d8b5762.jpg?1686969700"
    }
}
