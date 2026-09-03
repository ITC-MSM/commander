package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Wild Leotau
 * {2}{G}{G}
 * Creature — Cat
 * 5/4
 * At the beginning of your upkeep, sacrifice this creature unless you pay {G}.
 *
 * [Triggers.YourUpkeep] is `StepEvent(UPKEEP, You)` bound `ANY`. The "unless" is a **cost paid on
 * resolution**, not a may-gate: [PayOrSufferEffect] pairs [Costs.pay.Mana] with
 * [SacrificeSelfEffect], so declining — or being unable to pay — sacrifices the creature.
 */
val WildLeotau = card("Wild Leotau") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat"
    power = 5
    toughness = 4
    oracleText = "At the beginning of your upkeep, sacrifice this creature unless you pay {G}."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = PayOrSufferEffect(cost = Costs.pay.Mana("{G}"), suffer = SacrificeSelfEffect)
        description = "At the beginning of your upkeep, sacrifice this creature unless you pay {G}."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Michael Komarck"
        flavorText = "\"Leotau that were born wild make the best mounts. It's like riding a thunderstorm.\" —Rafiq of the Many"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a47bc387-ac1a-42b4-9427-fc944094b3a1.jpg"
    }
}
