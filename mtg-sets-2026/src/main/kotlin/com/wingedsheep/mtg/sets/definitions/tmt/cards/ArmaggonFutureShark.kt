package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Armaggon, Future Shark
 * {6}{B}{B}
 * Legendary Creature — Shark Horror Mutant
 * 9/6
 *
 * Flash
 * When Armaggon enters, destroy up to three target creatures.
 */
val ArmaggonFutureShark = card("Armaggon, Future Shark") {
    manaCost = "{6}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Shark Horror Mutant"
    oracleText = "Flash\nWhen Armaggon enters, destroy up to three target creatures."
    power = 9
    toughness = 6

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("up to three target creatures", TargetCreature(count = 3, optional = true))
        effect = ForEachTargetEffect(
            listOf(Effects.Destroy(EffectTarget.ContextTarget(0)))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "58"
        artist = "Mathias Kollros"
        flavorText = "\"I am ancient efficiency. I am evolution's future. I am mutation come full, vicious circle!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/9/5989378b-0eac-43cf-bc83-8f7765536789.jpg?1769005763"
    }
}
