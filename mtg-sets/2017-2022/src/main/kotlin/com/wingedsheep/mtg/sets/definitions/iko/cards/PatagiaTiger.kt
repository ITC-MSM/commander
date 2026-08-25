package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Patagia Tiger
 * {4}{W}
 * Creature — Cat
 * 3/4
 * Flying
 * When this creature enters, target Human you control gets +2/+2 until end of turn.
 *
 * "Human you control" is a bare tribal noun, so the filter is [GameObjectFilter.Permanent] with the
 * subtype — not the creature filter. The pump is not "another", so the tiger itself stays legal.
 */
val PatagiaTiger = card("Patagia Tiger") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    power = 3
    toughness = 4
    oracleText = "Flying\nWhen this creature enters, target Human you control gets +2/+2 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = TargetObject(
            filter = TargetFilter(GameObjectFilter.Permanent.youControl().withSubtype("Human"))
        )
        effect = Effects.ModifyStats(2, 2, EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Micah Epstein"
        flavorText = "Lukka looked out from the parapet and saw not a monster to be put down, but a fierce kind of beauty."
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be398100-89e2-432b-8017-74fb7e4dbc26.jpg"
    }
}
