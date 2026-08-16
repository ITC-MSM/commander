package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Snapping Gnarlid
 * {1}{G}
 * Creature — Beast
 * 2/2
 * Landfall — Whenever a land you control enters, this creature gets +1/+1 until end of turn.
 */
val SnappingGnarlid = card("Snapping Gnarlid") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 2
    oracleText = "Landfall — Whenever a land you control enters, this creature gets +1/+1 until end of turn."

    // Landfall — +1/+1 until end of turn whenever a land you control enters.
    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "Landfall — Whenever a land you control enters, this creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "190"
        artist = "Kev Walker"
        flavorText = "All of Zendikar's beings sense the upheaval that accompanies the Eldrazi."
        imageUri = "https://cards.scryfall.io/normal/front/8/3/834409e3-134e-4a34-89cb-53e2a039e980.jpg?1783938185"
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
