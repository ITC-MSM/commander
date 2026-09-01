package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersAsCopy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Copy Enchantment
 * {2}{U}
 * Enchantment
 *
 * You may have this enchantment enter as a copy of any enchantment on the battlefield.
 *
 * The enchantment counterpart of [com.wingedsheep.mtg.sets.definitions.lea.cards.Clone] —
 * same [EntersAsCopy] replacement, with the copy filter widened from creature to enchantment.
 */
val CopyEnchantment = card("Copy Enchantment") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "You may have this enchantment enter as a copy of any enchantment on the battlefield."

    replacementEffect(EntersAsCopy(optional = true, copyFilter = GameObjectFilter.Enchantment))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "42"
        artist = "Joel Thomas"
        flavorText = "Simic mages create redundant backups of their experiments to reduce the " +
            "consequences of catastrophe."
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac22117d-bd58-439f-b199-da72bc7160b2.jpg"
    }
}
