package com.wingedsheep.mtg.sets.definitions.mir.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Brushwagg
 * {1}{G}{G}
 * Creature — Brushwagg
 * 3/2
 * Whenever this creature blocks or becomes blocked, it gets -2/+2 until end of turn.
 */
val Brushwagg = card("Brushwagg") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Brushwagg"
    power = 3
    toughness = 2
    oracleText = "Whenever this creature blocks or becomes blocked, it gets -2/+2 until end of turn."

    triggeredAbility {
        trigger = Triggers.BlocksOrBecomesBlockedBy(GameObjectFilter.Creature)
        effect = Effects.ModifyStats(-2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "208"
        artist = "Ian Miller"
        flavorText = "\"Defiantly, the young cyclops popped the brushwagg into his mouth. His cheeks suddenly puffed, his eye bulged, and he was forced to agree with his elder.\"\n—Afari, Tales"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c20edc3-5ad0-42c1-a5ec-3e680fb03297.jpg?1587912656"
    }
}
