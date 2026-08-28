package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wildfire
 * {4}{R}{R}
 * Sorcery
 * Each player sacrifices four lands of their choice. Wildfire deals 4 damage to each creature.
 *
 * "Each player sacrifices …" is [Effects.Sacrifice] naming [Player.Each] as the sacrificing player —
 * each player chooses their own lands, in APNAP order. The sweep is [Effects.ForEachInGroup] over
 * every creature with the damage aimed at [EffectTarget.Self], the current iteration entity.
 */
val Wildfire = card("Wildfire") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Each player sacrifices four lands of their choice. Wildfire deals 4 damage to each creature."

    spell {
        effect = Effects.Composite(
            Effects.Sacrifice(
                GameObjectFilter.Land,
                4,
                EffectTarget.PlayerRef(Player.Each)
            ),
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature),
                Effects.DealDamage(4, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "120"
        artist = "Rob Alexander"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b69cfcb0-db68-4494-a3e1-7c2ca279fcf5.jpg"
    }
}
