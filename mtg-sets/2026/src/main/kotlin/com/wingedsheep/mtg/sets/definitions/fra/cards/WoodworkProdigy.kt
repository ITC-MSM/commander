package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Woodwork Prodigy // Soul Tether — does not enter prepared. Its upkeep trigger is an
 * intervening-if (CR 603.4) on the creature not being prepared, so it neither triggers nor
 * resolves while the creature is already prepared.
 */
val WoodworkProdigy = card("Woodwork Prodigy") {
    manaCost = "{2}{R/G}"
    colorIdentity = "RG"
    typeLine = "Creature — Cat Druid"
    power = 3
    toughness = 3
    oracleText = "At the beginning of your upkeep, if this creature isn't prepared, it becomes prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        interveningIf = Conditions.Not(Conditions.SourceIsPrepared)
        effect = Effects.BecomePrepared(EffectTarget.Self)
        description = "At the beginning of your upkeep, if this creature isn't prepared, it becomes prepared."
    }

    prepare("Soul Tether") {
        manaCost = "{2}{R/G}"
        typeLine = "Sorcery"
        oracleText = "Create a Heartwood token. (It's a red and green artifact with \"{T}: Add {R} or {G}.\")"
        spell {
            effect = Effects.CreateHeartwood()
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "165"
        artist = "Lius Lasahido"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d17f7e3-7b63-4674-9024-4fd1827f40ec.jpg?1788329429"
        inBooster = false
    }
}
