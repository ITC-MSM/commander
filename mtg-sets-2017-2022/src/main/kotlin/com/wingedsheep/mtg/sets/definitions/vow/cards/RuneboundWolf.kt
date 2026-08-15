package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Runebound Wolf
 * {1}{R}
 * Creature — Wolf
 * 2/2
 *
 * {3}{R}, {T}: This creature deals damage equal to the number of Wolves and Werewolves you
 * control to target opponent.
 */
val RuneboundWolf = card("Runebound Wolf") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wolf"
    power = 2
    toughness = 2
    oracleText = "{3}{R}, {T}: This creature deals damage equal to the number of Wolves and " +
        "Werewolves you control to target opponent."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{R}"), Costs.Tap)
        val t = target("target opponent", Targets.Opponent)
        effect = Effects.DealDamage(
            DynamicAmount.Count(
                Player.You,
                Zone.BATTLEFIELD,
                GameObjectFilter.Creature.withSubtype(Subtype.WOLF) or
                    GameObjectFilter.Creature.withSubtype(Subtype.WEREWOLF)
            ),
            t
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "176"
        artist = "Tomas Duchek"
        flavorText = "The wolf's captor hid his fear, but he knew someday the runes would fail, " +
            "the collar would break, and the wolf would remember his face."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f9615f0-376f-4ac0-b269-5f497f2b5223.jpg?1782703065"
    }
}
