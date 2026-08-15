package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dismantling Blow
 * {2}{W}
 * Instant
 * Kicker {2}{U}
 * Destroy target artifact or enchantment. If this spell was kicked, draw two cards.
 */
val DismantlingBlow = card("Dismantling Blow") {
    manaCost = "{2}{W}"
    colorIdentity = "WU"
    typeLine = "Instant"
    oracleText = "Kicker {2}{U} (You may pay an additional {2}{U} as you cast this spell.)\n" +
        "Destroy target artifact or enchantment. If this spell was kicked, draw two cards."

    keywordAbility(KeywordAbility.kicker("{2}{U}"))

    spell {
        target = Targets.ArtifactOrEnchantment
        effect = Effects.Destroy(EffectTarget.ContextTarget(0)) then ConditionalEffect(
            condition = WasKicked,
            effect = Effects.DrawCards(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Mark Tedin"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39514d54-cb6c-4b3b-a3be-46db991be4d4.jpg?1562906533"
    }
}
