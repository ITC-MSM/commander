package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Pegasus Courser
 * {2}{W}
 * Creature — Pegasus
 * 1/3
 * Flying
 * Whenever Pegasus Courser attacks, another target attacking creature gains flying
 * until end of turn.
 */
val PegasusCourser = card("Pegasus Courser") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Pegasus"
    power = 1
    toughness = 3
    oracleText = "Flying\nWhenever Pegasus Courser attacks, another target attacking creature gains flying until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        target = TargetCreature(
            filter = TargetFilter(GameObjectFilter.Creature.attacking(), excludeSelf = true)
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Mathias Kollros"
        flavorText = "\"A pegasus chooses its rider, bearing the worthy into the clouds and tossing all others to the ground.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35263639-f09d-429f-bd99-09cfdee668e8.jpg?1562733919"
    }
}
