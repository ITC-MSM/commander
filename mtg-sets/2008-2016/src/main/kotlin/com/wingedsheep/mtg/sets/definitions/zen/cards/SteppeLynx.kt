package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Steppe Lynx
 * {W}
 * Creature — Cat
 * 0/1
 * Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn.
 */
val SteppeLynx = card("Steppe Lynx") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    power = 0
    toughness = 1
    oracleText = "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."

    // Landfall — +2/+2 until end of turn whenever a land you control enters.
    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Nic Klein"
        flavorText = "Nothing quickens the predator's blood like the unfamiliar scents of new hunting grounds and the mewling cries of new prey."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0aaec1c-8084-4468-82e7-2e4cc5ebe244.jpg?1783942167"
        ruling(
            "2024-11-08",
            "A landfall ability triggers whenever a land you control enters for any reason. It triggers whenever you play a land, as well as whenever a spell or ability puts a land onto the battlefield under your control.",
        )
        ruling(
            "2024-11-08",
            "A landfall ability doesn't trigger if a permanent already on the battlefield becomes a land.",
        )
        ruling(
            "2024-11-08",
            "Whenever a land you control enters, each landfall ability of the permanents you control will trigger. You can put them on the stack in any order. The last ability you put on the stack will be the first one to resolve (As a result, you can have those abilities resolve in the order of your choosing.).",
        )
    }
}
