package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Falkenrath Noble
 * {3}{B}
 * Creature — Vampire Noble
 * 2/2
 * Flying
 * Whenever this creature or another creature dies, target player loses 1 life and you gain 1 life.
 */
val FalkenrathNoble = card("Falkenrath Noble") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Noble"
    oracleText =
        "Flying\nWhenever this creature or another creature dies, target player loses 1 life and you gain 1 life."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.AnyCreatureDies
        val player = target("target player", Targets.Player)
        effect = Effects.Composite(
            Effects.LoseLife(1, player),
            Effects.GainLife(1, EffectTarget.Controller),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "100"
        artist = "Slawomir Maniak"
        flavorText = "\"I often think on how excited they must feel to be chosen as my prey.\""
        imageUri =
            "https://cards.scryfall.io/normal/front/e/2/e2286f94-4cf9-4462-b5d7-cee7f6910018.jpg?1782714754"
    }
}
