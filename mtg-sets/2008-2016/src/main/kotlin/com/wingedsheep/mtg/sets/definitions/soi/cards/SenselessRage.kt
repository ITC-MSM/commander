package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Senseless Rage (Shadows over Innistrad #180)
 * {1}{R}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +2/+2.
 * Madness {1}{R}
 *
 * Madness (CR 702.35) on an Aura still puts the card on the stack as a normal Aura spell — it just
 * costs {1}{R} and is cast while the madness trigger resolves, so the enchant restriction is chosen
 * then like any other target.
 */
val SenselessRage = card("Senseless Rage") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+2.\n" +
        "Madness {1}{R} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"
    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    madness("{1}{R}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "180"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c779cb6-3454-4d5a-85a1-bea5cf8005b1.jpg?1783937744"
    }
}
