package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mistwalker
 * {2}{U}
 * Creature — Shapeshifter
 * 1/4
 * Changeling (This card is every creature type.)
 * Flying
 * {1}{U}: This creature gets +1/-1 until end of turn.
 */
val Mistwalker = card("Mistwalker") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Shapeshifter"
    power = 1
    toughness = 4
    oracleText = "Changeling (This card is every creature type.)\nFlying\n{1}{U}: This creature gets +1/-1 until end of turn."

    keywords(Keyword.CHANGELING, Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        effect = Effects.ModifyStats(1, -1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Steve Prescott"
        flavorText = "\"To escape Littjara, follow a bird.\" —Tuskeri folklore"
        imageUri = "https://cards.scryfall.io/normal/front/9/2/92f64b49-327c-473b-a492-f020a322aed7.jpg?1783928259"
        ruling("2021-02-05", "Changeling is a characteristic-defining ability. It functions in all zones, not only while a card that has it is on the battlefield.")
        ruling("2021-02-05", "The subtype Shapeshifter that appears on the type line is mostly there to reinforce the flavor. A creature card with changeling is just as much an Elf, a Dwarf, a Sliver, a Goat, a Coward, and a Zombie as it is a Shapeshifter.")
        ruling("2021-02-05", "If an effect causes a creature with changeling to become a new creature type, it will be only that new creature type. It will still have changeling; the effect making it all creature types will simply be overwritten.")
        ruling("2021-02-05", "If an effect causes a creature with changeling to lose all abilities, it will remain all creature types, even though it will no longer have changeling. This is because changeling applies before the effect that removes it.")
    }
}
