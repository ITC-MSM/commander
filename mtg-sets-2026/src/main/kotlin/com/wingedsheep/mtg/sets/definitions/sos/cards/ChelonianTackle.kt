package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Chelonian Tackle
 * {2}{G}
 * Sorcery
 *
 * Target creature you control gets +0/+10 until end of turn. Then it fights up to one
 * target creature an opponent controls. (Each deals damage equal to its power to the other.)
 *
 * Two targets, the second "up to one" (optional). The pump (`Effects.ModifyStats(0, 10,
 * EndOfTurn)`) applies to the creature you control regardless of whether a fight target was
 * chosen; the fight then reuses `Effects.Fight`, whose opponent target is optional — so if the
 * second target is declined or becomes illegal, the creature is still pumped but no fight
 * occurs. Mirrors Dragonclaw Strike's "pump then fights up to one" shape.
 */
val ChelonianTackle = card("Chelonian Tackle") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Target creature you control gets +0/+10 until end of turn. Then it fights up to " +
        "one target creature an opponent controls. (Each deals damage equal to its power to the other.)"

    spell {
        val yourCreature = target(
            "creature you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl)
        )
        val opponentCreature = target(
            "creature an opponent controls",
            TargetCreature(optional = true, filter = TargetFilter.CreatureOpponentControls)
        )
        effect = Effects.ModifyStats(0, 10, yourCreature)
            .then(Effects.Fight(yourCreature, opponentCreature))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "142"
        artist = "Lorenzo Mastroianni"
        flavorText = "\"Scurrids to climb the tower, cervins to cross the stadium, and turtles to " +
            "win the game.\"\n—Quandrix Mage Tower tactics"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a82a4d8c-4105-4923-85a2-ef58241f725c.jpg?1775937964"
    }
}
