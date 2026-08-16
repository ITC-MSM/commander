package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Thopter Architect
 * {3}{W}
 * Creature — Human Artificer
 * 2/3
 * Whenever an artifact you control enters, target creature gains flying until end of turn.
 *
 * Weldfast Wingsmith's trigger and effect, but the flying lands on a chosen creature rather than
 * on the source — so the ability declares a target slot and [Effects.GrantKeyword] points at it.
 */
val ThopterArchitect = card("Thopter Architect") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Artificer"
    power = 2
    toughness = 3
    oracleText = "Whenever an artifact you control enters, target creature gains flying until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY
        )
        val creature = target("creature", TargetCreature())
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
        description = "Target creature gains flying until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "29"
        artist = "Michal Ivan"
        flavorText = "\"She'll only fly once. Make it count!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61017325-cec0-46ac-aa32-5855e5904888.jpg?1783920121"
    }
}
