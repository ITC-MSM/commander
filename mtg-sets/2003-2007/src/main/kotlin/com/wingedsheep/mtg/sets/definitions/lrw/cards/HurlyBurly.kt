package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hurly-Burly
 * {1}{R}
 * Sorcery
 * Choose one —
 * • Hurly-Burly deals 1 damage to each creature without flying.
 * • Hurly-Burly deals 1 damage to each creature with flying.
 */
val HurlyBurly = card("Hurly-Burly") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Hurly-Burly deals 1 damage to each creature without flying.\n" +
        "• Hurly-Burly deals 1 damage to each creature with flying."

    spell {
        modal(chooseCount = 1) {
            mode("Hurly-Burly deals 1 damage to each creature without flying") {
                effect = Effects.ForEachInGroup(
                    GroupFilter(GameObjectFilter.Creature.withoutKeyword(Keyword.FLYING)),
                    DealDamageEffect(1, EffectTarget.Self)
                )
            }
            mode("Hurly-Burly deals 1 damage to each creature with flying") {
                effect = Effects.ForEachInGroup(
                    GroupFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING)),
                    DealDamageEffect(1, EffectTarget.Self)
                )
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Steve Prescott"
        flavorText = "\"Things were popping like corn in a skillet. Olly landed in the pig pen with his prize sow on top of him, both squealin' like boggarts.\"\n—Deagan, cenn of Burrenton"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a6e0b97-c2a9-4cd6-957e-87e9b22f7b48.jpg?1783942872"
    }
}
