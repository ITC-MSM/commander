package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wispmare
 * {2}{W}
 * Creature — Elemental
 * 1/3
 * Flying
 * When this creature enters, destroy target enchantment.
 * Evoke {W}
 */
val Wispmare = card("Wispmare") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elemental"
    power = 1
    toughness = 3
    oracleText = "Flying\nWhen this creature enters, destroy target enchantment.\n" +
        "Evoke {W} (You may cast this spell for its evoke cost. If you do, it's sacrificed when " +
        "it enters.)"

    keywords(Keyword.FLYING)

    evoke = "{W}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val enchantment = target("target enchantment", Targets.Enchantment)
        effect = Effects.Destroy(enchantment)
        description = "destroy target enchantment."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Eric Fortune"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56389c91-a470-4c43-9368-98df4d38f7fd.jpg?1783942907"
    }
}
