package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Darklight Phoenix — a graveyard begin-combat trigger (as on Flamewake Phoenix) gated as an
 * intervening-if (CR 603.4) on the game-wide count of creatures that died this turn; tokens count.
 */
val DarklightPhoenix = card("Darklight Phoenix") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phoenix"
    power = 3
    toughness = 2
    oracleText = "Flying, haste\nAt the beginning of combat on your turn, if two or more creatures died this turn, return this card from your graveyard to the battlefield."

    keywords(Keyword.FLYING, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.BeginCombat
        triggerZone = Zone.GRAVEYARD
        interveningIf = Conditions.CompareAmounts(
            DynamicAmounts.creaturesDiedThisTurn(Player.Each),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(2),
        )
        effect = Effects.Move(EffectTarget.Self, Zone.BATTLEFIELD)
        description = "At the beginning of combat on your turn, if two or more creatures died this turn, return this card from your graveyard to the battlefield."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "53"
        artist = "Wayne Reynolds"
        flavorText = "Some eclipses never exit totality."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec454979-3839-4be3-a34a-9d25482948ba.jpg?1789470810"
        inBooster = false
    }
}
