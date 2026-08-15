package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sunstar Expansionist
 * {1}{W}
 * Creature — Human Knight
 * 2/3
 *
 * When this creature enters, if an opponent controls more lands than you, create a Lander token. (It's an artifact with "{2}, {T}, Sacrifice this token: Search your library for a basic land card, put it onto the battlefield tapped, then shuffle.")
 * Landfall — Whenever a land you control enters, this creature gets +1/+0 until end of turn.
 */
val SunstarExpansionist = card("Sunstar Expansionist") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "When this creature enters, if an opponent controls more lands than you, create a Lander token. (It's an artifact with \"{2}, {T}, Sacrifice this token: Search your library for a basic land card, put it onto the battlefield tapped, then shuffle.\")\nLandfall — Whenever a land you control enters, this creature gets +1/+0 until end of turn."
    power = 2
    toughness = 3

    // ETB: Create Lander token if opponent controls more lands
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ConditionalEffect(
            condition = Conditions.OpponentControlsMoreLands,
            effect = Effects.CreateLander()
        )
    }

    // Landfall: +1/+0 until end of turn when land enters
    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "41"
        artist = "Mark Poole"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93b0e6e5-9fb6-4342-9322-1a4cc09a7a76.jpg?1752946712"
    }
}
