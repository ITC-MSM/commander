package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Tivadar's Crusade
 * {1}{W}{W}
 * Sorcery
 * Destroy all Goblins.
 *
 * Every Goblin on the battlefield, whoever controls it, and regeneration still applies — the
 * card says nothing about it.
 */
val TivadarsCrusade = card("Tivadar's Crusade") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy all Goblins."

    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "18"
        artist = "Dennis Detwiller"
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b6da540-6803-47e5-9af0-7ae8e2f84b6c.jpg?1783947945"
    }
}
