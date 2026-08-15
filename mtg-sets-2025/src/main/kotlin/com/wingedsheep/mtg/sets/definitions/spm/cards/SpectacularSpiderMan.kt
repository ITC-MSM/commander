package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Spectacular Spider-Man
 * {1}{W}
 * Legendary Creature — Spider Human Hero, 3/2
 * Flash
 * {1}: Spectacular Spider-Man gains flying until end of turn.
 * {1}, Sacrifice Spectacular Spider-Man: Creatures you control gain hexproof and indestructible until end of turn.
 */
val SpectacularSpiderMan = card("Spectacular Spider-Man") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Spider Human Hero"
    oracleText = "Flash\n{1}: Spectacular Spider-Man gains flying until end of turn.\n{1}, Sacrifice Spectacular Spider-Man: Creatures you control gain hexproof and indestructible until end of turn."
    power = 3
    toughness = 2

    keywords(Keyword.FLASH)

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.Composite(
            Patterns.Group.grantKeywordToAll(Keyword.HEXPROOF, Filters.Group.creaturesYouControl),
            Patterns.Group.grantKeywordToAll(Keyword.INDESTRUCTIBLE, Filters.Group.creaturesYouControl)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "14"
        artist = "Roberta Ingranata"
        flavorText = "\"Have no fear. Spidey is here!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32bff506-efc0-42ef-8286-ed939bf853d7.jpg?1779339757"
    }
}
