package com.wingedsheep.mtg.sets.definitions.usg.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ControlEnchantedPermanent

/**
 * Confiscate
 * {4}{U}{U}
 * Enchantment — Aura
 * Enchant permanent
 * You control enchanted permanent.
 *
 * Broader sibling of [com.wingedsheep.mtg.sets.definitions.ons.cards.Annex]
 * (which enchants only lands) — Confiscate can steal any permanent.
 */
val Confiscate = card("Confiscate") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant permanent\nYou control enchanted permanent."

    auraTarget = Targets.Permanent

    staticAbility {
        ability = ControlEnchantedPermanent
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "66"
        artist = "Adam Rex"
        flavorText = "\"I don't understand why he works so hard on a device to duplicate a sound so " +
            "easily made with hand and armpit.\"\n—Barrin, progress report"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7cba6d4a-58d0-42d6-b49b-65c72b86007f.jpg?1782720734"
    }
}
