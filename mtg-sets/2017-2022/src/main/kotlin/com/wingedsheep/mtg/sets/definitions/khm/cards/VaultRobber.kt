package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Vault Robber
 * {1}{R}
 * Creature — Dwarf Rogue
 * 1/3
 * {1}, {T}, Exile a creature card from your graveyard: Create a Treasure token. (It's an artifact with "{T}, Sacrifice this token: Add one mana of any color.")
 */
val VaultRobber = card("Vault Robber") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dwarf Rogue"
    power = 1
    toughness = 3
    oracleText = "{1}, {T}, Exile a creature card from your graveyard: Create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.ExileFromGraveyard(1, GameObjectFilter.Creature)
        )
        effect = Effects.CreateTreasure(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Slawomir Maniak"
        flavorText = "The dwarves believe works of art should be passed down the generations, not buried with the dead."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74f68014-489d-4f51-a959-0f335541cb4e.jpg?1783928220"
        ruling("2021-02-05", "Once you announce that you’re activating the activated ability, no player may take actions until the ability has been paid for. Notably, opponents can’t try to remove one of your creature cards to stop you from exiling it.")
    }
}
