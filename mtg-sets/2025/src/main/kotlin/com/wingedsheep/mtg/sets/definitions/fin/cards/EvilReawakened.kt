package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject


/**
 * Evil Reawakened
 * {4}{B}
 * Sorcery
 * Return target creature card from your graveyard to the battlefield with two additional +1/+1 counters on it.
 */
val EvilReawakened = card("Evil Reawakened") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return target creature card from your graveyard to the battlefield with two additional +1/+1 counters on it."
    spell {
        val t = target("target", TargetObject(filter = TargetFilter.CreatureInYourGraveyard))
        // Return it to the battlefield, then put two additional +1/+1 counters on it.
        effect = Effects.Move(t, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
            .then(AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 2, t))
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "98"
        artist = "Nino Is"
        flavorText = "\"Galuf. It's good to see you again . . . Mwa-hahahahaha!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb98cbc3-749c-44f4-974c-00be1286d69e.jpg"
    }
}
