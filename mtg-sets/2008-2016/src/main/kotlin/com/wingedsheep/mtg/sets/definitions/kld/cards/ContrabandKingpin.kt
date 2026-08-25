package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Contraband Kingpin
 * {U}{B}
 * Creature — Aetherborn Rogue
 * 1/4
 * Lifelink
 * Whenever an artifact you control enters, scry 1.
 *
 * The same shape as Weldfast Wingsmith — [Triggers.entersBattlefield] over
 * `Artifact.youControl()` with [TriggerBinding.ANY], which widens the trigger past the source to
 * every artifact you control.
 */
val ContrabandKingpin = card("Contraband Kingpin") {
    manaCost = "{U}{B}"
    colorIdentity = "BU"
    typeLine = "Creature — Aetherborn Rogue"
    oracleText = "Lifelink\n" +
        "Whenever an artifact you control enters, scry 1."
    power = 1
    toughness = 4

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.Scry(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "177"
        artist = "Anna Steinbauer"
        flavorText = "\"Don't waste my time with trifles. Bring me works of art, and I will make you rich.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69beafcd-6c90-40c2-afff-0bd82377febf.jpg?1783937170"
    }
}
