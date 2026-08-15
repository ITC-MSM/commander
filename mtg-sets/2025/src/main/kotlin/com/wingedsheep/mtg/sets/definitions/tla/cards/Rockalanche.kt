package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Rockalanche — {2}{G} Sorcery — Lesson
 *
 * Earthbend X, where X is the number of Forests you control. (Target land you
 * control becomes a 0/0 creature with haste that's still a land. Put X +1/+1
 * counters on it. When it dies or is exiled, return it to the battlefield tapped.)
 * Flashback {5}{G} (You may cast this card from your graveyard for its flashback
 * cost. Then exile it.)
 *
 * Earthbend is a keyword *action* composed from existing primitives (animate land +
 * haste + counters + return-tapped self-trigger), not a keyword ability. Here X is
 * dynamic — the number of Forests you control, counted on resolution via
 * [DynamicAmounts.landsWithSubtype] — so the spell uses the dynamic-amount
 * [Effects.Earthbend] overload. Flashback is the card's own keyword ability.
 */
val Rockalanche = card("Rockalanche") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery — Lesson"
    oracleText = "Earthbend X, where X is the number of Forests you control. " +
        "(Target land you control becomes a 0/0 creature with haste that's still a land. " +
        "Put X +1/+1 counters on it. When it dies or is exiled, return it to the battlefield tapped.)\n" +
        "Flashback {5}{G} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        val land = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl()))
        effect = Effects.Earthbend(DynamicAmounts.landsWithSubtype(Subtype.FOREST), land)
    }

    keywordAbility(KeywordAbility.flashback("{5}{G}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "192"
        artist = "Yuu Fujiki"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52b213d3-6f68-43e6-91e9-435d8fe1f34c.jpg?1764121307"
    }
}
