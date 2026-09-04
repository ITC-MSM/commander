package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Champion of Dusk
 * {3}{B}{B}
 * Creature — Vampire Knight
 * 4/4
 * When this creature enters, you draw X cards and you lose X life, where X is the number of
 * Vampires you control.
 *
 * X is counted twice — once for the draw, once for the loss — because both legs read the
 * battlefield as the ability resolves; the printed line is a single count, and the two reads
 * happen back-to-back with nothing in between that could change it.
 */
val ChampionOfDusk = card("Champion of Dusk") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Knight"
    oracleText = "When this creature enters, you draw X cards and you lose X life, where X is " +
        "the number of Vampires you control."
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(
            count = DynamicAmount.AggregateBattlefield(
                Player.You,
                GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE)
            ),
            target = EffectTarget.Controller
        ) then Effects.LoseLife(
            amount = DynamicAmount.AggregateBattlefield(
                Player.You,
                GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE)
            ),
            target = EffectTarget.Controller
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "64"
        artist = "Josh Hass"
        flavorText = "\"Drench these golden streets in the blood of our enemies.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/0/507a4fb1-d27b-4393-9eed-48fe05b367d8.jpg?1783935315"
        ruling(
            "2018-01-19",
            "The number of Vampires you control is counted only as Champion of Dusk's ability " +
                "resolves. If Champion of Dusk is still on the battlefield, it'll count itself."
        )
    }
}
