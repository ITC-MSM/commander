package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rubblebelt Boar
 * {3}{R}
 * Creature — Boar
 * 3/3
 * When this creature enters, target creature gets +2/+0 until end of turn.
 */
val RubblebeltBoar = card("Rubblebelt Boar") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Boar"
    oracleText = "When this creature enters, target creature gets +2/+0 until end of turn."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target", Targets.Creature)
        effect = Effects.ModifyStats(2, 0, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Tomasz Jedruszek"
        flavorText = "Some Gruul druids believe that boars are spawn of the great Ilharg, the mighty Raze-Boar who will one day rise and level the city."
        imageUri = "https://cards.scryfall.io/normal/front/1/9/196bf114-e135-43f3-83d5-cb08ca766881.jpg?1783934159"
    }
}
