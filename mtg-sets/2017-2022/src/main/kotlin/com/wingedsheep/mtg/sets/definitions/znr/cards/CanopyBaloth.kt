package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Canopy Baloth
 * {3}{G}
 * Creature — Beast
 * 4/3
 * Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn.
 *
 * Plain landfall pump: [Triggers.LandYouControlEnters] (ANY binding — no landfall ability prints
 * "another") into [Effects.ModifyStats] on [EffectTarget.Self] with the default end-of-turn duration.
 */
val CanopyBaloth = card("Canopy Baloth") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 3
    oracleText = "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "182"
        artist = "Filip Burburan"
        flavorText = "\"You can almost see it calculating—are we a tasty enough treat to risk the jump?\" —Samila, Murasa Expeditionary House"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b04160c-89a7-4dcd-b05d-5dc846824d64.jpg?1783929341"
        ruling("2024-11-08", "A landfall ability triggers whenever a land you control enters for any reason. It triggers whenever you play a land, as well as whenever a spell or ability puts a land onto the battlefield under your control.")
        ruling("2024-11-08", "A landfall ability doesn't trigger if a permanent already on the battlefield becomes a land.")
        ruling("2024-11-08", "Whenever a land you control enters, each landfall ability of the permanents you control will trigger. You can put them on the stack in any order. The last ability you put on the stack will be the first one to resolve (As a result, you can have those abilities resolve in the order of your choosing.).")
    }
}
