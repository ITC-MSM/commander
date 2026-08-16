package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Deathbloom Ritualist
 * {3}{B}{G}
 * Creature — Elf Warlock
 * 3/5
 * {T}: Add X mana of any one color, where X is the number of creature cards in your graveyard.
 *
 * "Any one color" is [Effects.AddAnyColorMana] — one color chosen for the whole batch, not one
 * per mana — taking the dynamic [DynamicAmounts.creatureCardsInYourGraveyard] count as X.
 * `manaAbility = true` is the only switch: the builder derives both `isManaAbility` and the
 * `ManaAbility` timing rule from it, so neither is hand-set.
 */
val DeathbloomRitualist = card("Deathbloom Ritualist") {
    manaCost = "{3}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Elf Warlock"
    power = 3
    toughness = 5
    oracleText = "{T}: Add X mana of any one color, where X is the number of creature cards in your graveyard."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(DynamicAmounts.creatureCardsInYourGraveyard())
        manaAbility = true
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "208"
        artist = "Taras Susak"
        flavorText = "Each fallen friend was a seed of regret, eager to blossom again in Gaea's name."
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45ff816d-1eb7-4985-90ec-f44802a696fc.jpg?1783920033"
    }
}
