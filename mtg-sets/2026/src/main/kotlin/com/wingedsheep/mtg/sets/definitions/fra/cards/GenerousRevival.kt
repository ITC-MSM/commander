package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Generous Revival — Reality Fracture #8
 * {2}{W} · Sorcery
 *
 * Return target creature card with mana value 3 or less from your graveyard to the battlefield
 * with an additional +1/+1 counter on it.
 * Flashback {4}{W}
 *
 * "With an additional +1/+1 counter on it" is an entry rider, so the counter rides on the zone
 * change itself ([Effects.Move]'s `addCounterType`, as Scout for Survivors does) rather than
 * being put on afterwards.
 */
val GenerousRevival = card("Generous Revival") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Return target creature card with mana value 3 or less from your graveyard to the battlefield with an additional +1/+1 counter on it.\n" +
        "Flashback {4}{W} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        val creatureCard = target(
            "target creature card with mana value 3 or less from your graveyard",
            TargetObject(filter = TargetFilter.CreatureInYourGraveyard.manaValueAtMost(3))
        )
        effect = Effects.Move(
            creatureCard,
            Zone.BATTLEFIELD,
            fromZone = Zone.GRAVEYARD,
            addCounterType = CounterType.PLUS_ONE_PLUS_ONE
        )
    }
    keywordAbility(KeywordAbility.flashback("{4}{W}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "8"
        artist = "Alessandra Pisano"
        flavorText = "\"Right as rain!\" the saccharine double taunted."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f82f181b-a43b-4cec-b8f6-f6c1dd4c64fd.jpg?1788433351"
        inBooster = false
    }
}
