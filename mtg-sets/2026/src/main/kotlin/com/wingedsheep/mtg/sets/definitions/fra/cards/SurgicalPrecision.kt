package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val SurgicalPrecision = card("Surgical Precision") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n• Destroy target creature with toughness 4 or greater. You gain 1 life.\n• You draw a card and gain 2 life."

    spell {
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                Effects.Composite(Effects.Destroy(EffectTarget.ContextTarget(0)), Effects.GainLife(1)),
                TargetCreature(filter = TargetFilter.Creature.toughnessAtLeast(4)),
                "Destroy target creature with toughness 4 or greater. You gain 1 life."
            ),
            Mode(
                effect = Effects.Composite(Effects.DrawCards(1), Effects.GainLife(2)),
                description = "You draw a card and gain 2 life."
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Nathaniel Himawan"
        flavorText = "Vigorbloom battlemages love dueling. Opportunities to hurt sow opportunities to heal."
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d3acf176-ef02-4729-88c4-0f0dfbfdada4.jpg?1789385570"
        inBooster = false
    }
}
