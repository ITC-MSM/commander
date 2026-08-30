package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Xathrid Necromancer
 * {2}{B}
 * Creature — Human Wizard
 * 2 / 2
 * Whenever this creature or another Human creature you control dies, create a tapped 2/2 black Zombie creature token.
 */
val XathridNecromancer = card("Xathrid Necromancer") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature or another Human creature you control dies, create a tapped 2/2 black Zombie creature token."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.HUMAN).youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie"),
            tapped = true
        )
        description = "Whenever this creature or another Human creature you control dies, " +
            "create a tapped 2/2 black Zombie creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "123"
        artist = "Maciej Kuciara"
        flavorText = "\"My commands shall echo forever in their dusty skulls.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26494f96-1d97-4435-a116-3ade1becaab4.jpg"
    }
}
