package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Death Begets Life
 * {5}{B}{G}{U}
 * Sorcery
 *
 * Destroy all creatures and enchantments. Draw a card for each permanent destroyed this way.
 *
 * Uses the [Effects.DestroyAll] pipeline with the shared [GameObjectFilter.CreatureOrEnchantment]
 * filter (a single Or card-predicate, so an enchantment creature is matched and counted once),
 * storing the destroyed count and drawing that many cards via [DynamicAmount.VariableReference].
 */
val DeathBegetsLife = card("Death Begets Life") {
    manaCost = "{5}{B}{G}{U}"
    colorIdentity = "BGU"
    typeLine = "Sorcery"
    oracleText = "Destroy all creatures and enchantments. Draw a card for each permanent destroyed this way."

    spell {
        effect = Effects.DestroyAll(
            filter = GameObjectFilter.CreatureOrEnchantment,
            storeDestroyedAs = "destroyed"
        ).then(
            Effects.DrawCards(DynamicAmount.VariableReference("destroyed_count"))
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "176"
        artist = "Justin Hernandez & Alexis Hernandez"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1faab43d-587d-44f6-9516-c8e3965bbc20.jpg?1743204680"
    }
}
