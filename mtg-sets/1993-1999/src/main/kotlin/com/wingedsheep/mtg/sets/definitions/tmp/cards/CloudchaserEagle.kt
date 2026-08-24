package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cloudchaser Eagle
 * {3}{W}
 * Creature — Bird
 * 2/2
 * Flying
 * When this creature enters, destroy target enchantment.
 */
val CloudchaserEagle = card("Cloudchaser Eagle") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature enters, destroy target enchantment."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target("target", Targets.Enchantment)
        effect = Effects.Destroy(victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Una Fricker"
        flavorText = "When the eagle catches a cloud, it tears it into strips that fall to earth.\n" +
            "—Vec myth of the rains"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a70a6da-dea3-49c0-8c49-6a2229c3ac91.jpg"
    }
}
