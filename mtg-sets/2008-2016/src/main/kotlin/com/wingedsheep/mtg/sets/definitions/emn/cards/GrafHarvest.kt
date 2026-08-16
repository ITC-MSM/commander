package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Graf Harvest
 * {B}
 * Enchantment
 * Zombies you control have menace. (They can't be blocked except by two or more creatures.)
 * {3}{B}, Exile a creature card from your graveyard: Create a 2/2 black Zombie creature token.
 *
 * "Zombies you control" is a bare tribal noun — every Zombie *permanent* you control, not only
 * the creature ones.
 */
val GrafHarvest = card("Graf Harvest") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Zombies you control have menace. (They can't be blocked except by two or more creatures.)\n{3}{B}, Exile a creature card from your graveyard: Create a 2/2 black Zombie creature token."

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.MENACE,
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.ZOMBIE).youControl())
        )
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}{B}"),
            Costs.ExileFromGraveyard(1, GameObjectFilter.Creature)
        )
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie"),
            imageUri = "https://cards.scryfall.io/normal/front/b/5/b5bd6905-79be-4d2c-a343-f6e6a181b3e6.jpg?1783936411"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "90"
        artist = "Lake Hurwitz"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbc17697-9db9-41d4-aacf-b2f2e6ff80cf.jpg?1783937482"
    }
}
