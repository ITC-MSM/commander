package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.MustBeBlockedEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Roar of Challenge
 * {2}{G}
 * Sorcery
 * All creatures able to block target creature this turn do so.
 * Ferocious — That creature gains indestructible until end of turn if you control a creature
 * with power 4 or greater.
 */
val RoarOfChallenge = card("Roar of Challenge") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "All creatures able to block target creature this turn do so.\nFerocious — That creature gains indestructible until end of turn if you control a creature with power 4 or greater."

    spell {
        val t = target("creature", TargetCreature())
        effect = MustBeBlockedEffect(t)
            .then(ConditionalEffect(
                condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature.powerAtLeast(4)),
                effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
            ))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "145"
        artist = "Viktor Titov"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83456f8f-8a1a-403c-816b-25e454ba1edf.jpg?1562789481"
    }
}
