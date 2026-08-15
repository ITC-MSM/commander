package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Théoden, King of Rohan
 * {1}{R}{W}
 * Legendary Creature — Human Noble
 * 2/3
 *
 * Whenever Théoden or another Human you control enters, target creature gains
 * double strike until end of turn.
 */
val TheodenKingOfRohan = card("Théoden, King of Rohan") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Human Noble"
    power = 2
    toughness = 3
    oracleText = "Whenever Théoden or another Human you control enters, target creature gains double strike until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl().withSubtype("Human"),
            binding = TriggerBinding.ANY
        )
        target = TargetCreature(
            filter = TargetFilter(GameObjectFilter.Creature)
        )
        effect = GrantKeywordEffect(Keyword.DOUBLE_STRIKE, EffectTarget.ContextTarget(0), Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Kieran Yanner"
        flavorText = "\"Dark have been my dreams of late, but I feel new-awakened. I only fear that already you have come too late, Gandalf.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6dcd1ca-4943-46e4-bb5d-c14949e21e23.jpg?1686970093"
    }
}
