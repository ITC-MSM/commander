package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player

/**
 * The Spirit Oasis
 * {2}{U}
 * Legendary Enchantment — Shrine
 * When The Spirit Oasis enters, draw a card for each Shrine you control.
 * Whenever another Shrine you control enters, draw a card.
 *
 * Parallels the Shrine cycle (cf. Northern Air Temple): the ETB draws one card per Shrine
 * you control (this permanent counts itself, as it is already on the battlefield when the
 * trigger resolves), and a second trigger draws on every *other* Shrine you control entering
 * ([TriggerBinding.OTHER] so The Spirit Oasis's own entry doesn't fire the second ability).
 */
val TheSpiritOasis = card("The Spirit Oasis") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Enchantment — Shrine"
    oracleText = "When The Spirit Oasis enters, draw a card for each Shrine you control.\n" +
        "Whenever another Shrine you control enters, draw a card."

    // The number of Shrines you control (this permanent already counts itself on its own ETB).
    val shrinesYouControl = DynamicAmounts
        .battlefield(Player.You, GameObjectFilter.Any.withSubtype("Shrine"))
        .count()

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(shrinesYouControl)
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Any.withSubtype("Shrine").youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "72"
        artist = "Slawek Fedorczuk"
        flavorText = "\"It's the center of all spiritual energy in our land.\"\n—Princess Yue"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a1b1329-531c-47ed-9802-c505501f8fd9.jpg?1764120455"
    }
}
