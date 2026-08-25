package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Chronosavant
 * {5}{W}
 * Creature — Giant
 * 5/5
 * {1}{W}: Return this card from your graveyard to the battlefield tapped. You skip your next turn.
 *
 * A graveyard-activated ability (`activateFromZone = Zone.GRAVEYARD`) that moves the card back
 * itself: the self-return is a plain [Effects.Move] with [ZonePlacement.Tapped] and an explicit
 * `fromZone`, and the skipped turn is the price, paid on resolution rather than as a cost.
 */
val Chronosavant = card("Chronosavant") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant"
    power = 5
    toughness = 5
    oracleText = "{1}{W}: Return this card from your graveyard to the battlefield tapped. You skip your next turn."

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        activateFromZone = Zone.GRAVEYARD
        effect = Effects.Composite(
            Effects.Move(
                EffectTarget.Self,
                Zone.BATTLEFIELD,
                placement = ZonePlacement.Tapped,
                fromZone = Zone.GRAVEYARD,
            ),
            Effects.SkipNextTurn(),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "9"
        artist = "Pete Venters"
        flavorText = "\"In my dreams, I hear the voices of my future selves who have died in times yet to come. I use that knowledge to avoid those dark futures and continue my search for peace.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6264d4f-adf1-4d7b-b17a-fd7122e9b2cd.jpg"
    }
}
