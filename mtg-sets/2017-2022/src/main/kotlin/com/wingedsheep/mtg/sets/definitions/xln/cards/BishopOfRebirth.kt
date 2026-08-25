package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Bishop of Rebirth
 * {3}{W}{W}
 * Creature — Vampire Cleric
 * 3/4
 *
 * Vigilance
 * Whenever this creature attacks, you may return target creature card with mana value 3 or less
 * from your graveyard to the battlefield.
 */
val BishopOfRebirth = card("Bishop of Rebirth") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Vampire Cleric"
    oracleText = "Vigilance\n" +
        "Whenever this creature attacks, you may return target creature card with mana value 3 " +
        "or less from your graveyard to the battlefield."
    power = 3
    toughness = 4

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.Attacks
        val card = target(
            "target",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Creature.manaValueAtMost(3).ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        optional = true
        effect = Effects.Move(card, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "5"
        artist = "Tommy Arnold"
        flavorText = "\"In the death of the foe lies the resurrection of the faithful.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d654bec-4eb2-4df6-b71e-ce59a718f903.jpg"
    }
}
