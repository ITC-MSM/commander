package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kranioceros
 * {4}{R}
 * Creature — Beast
 * 5/2
 * {1}{W}: This creature gets +0/+3 until end of turn.
 *
 * A self-pump with an off-colour activation cost: [Effects.ModifyStats] over
 * [EffectTarget.Self], whose default duration is already `Duration.EndOfTurn`, so the printed
 * "until end of turn" needs no argument. The `{W}` in the cost is why the card's colour identity
 * is RW while its mana cost is mono-red.
 */
val Kranioceros = card("Kranioceros") {
    manaCost = "{4}{R}"
    colorIdentity = "RW"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 2
    oracleText = "{1}{W}: This creature gets +0/+3 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        effect = Effects.ModifyStats(0, 3, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Steve Argyle"
        flavorText = "\"A surly beast, the kranioceros will raise its defenses at the smallest threat. Stay out of sight and downwind, or you'll disrupt its natural migrations.\" —Ebrel, godtoucher mentor"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52aece74-cc1f-4f32-ad1f-00733eb79007.jpg"
    }
}
