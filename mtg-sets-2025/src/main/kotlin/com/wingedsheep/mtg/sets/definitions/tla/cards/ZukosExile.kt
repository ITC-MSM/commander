package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SelectTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Zuko's Exile
 * {5}
 * Instant — Lesson
 *
 * Exile target artifact, creature, or enchantment. Its controller creates a Clue token.
 * (It's an artifact with "{2}, Sacrifice this token: Draw a card.")
 *
 * The single target is selected via [SelectTargetEffect] into a pipeline collection so the
 * follow-up [Effects.CreateClue] can resolve "Its controller" with
 * [EffectTarget.ControllerOfPipelineTarget] (the controller of the now-exiled permanent),
 * mirroring "Exile target nonland permanent. Its controller draws a card." (Season of the Burrow).
 */
val ZukosExile = card("Zuko's Exile") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Instant — Lesson"
    oracleText = "Exile target artifact, creature, or enchantment. Its controller creates a Clue token. " +
        "(It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")"

    spell {
        effect = SelectTargetEffect(
            requirement = TargetObject(
                filter = TargetFilter(GameObjectFilter.ArtifactCreatureOrEnchantment),
                id = "target artifact, creature, or enchantment"
            ),
            storeAs = "exileTarget"
        )
            .then(Effects.Exile(EffectTarget.PipelineTarget("exileTarget")))
            .then(Effects.CreateClue(controller = EffectTarget.ControllerOfPipelineTarget("exileTarget")))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Eiji Kaneda"
        flavorText = "As punishment for his disobedience, Zuko was banished and sent to capture the Avatar. " +
            "Only then could he return home with his honor."
        imageUri = "https://cards.scryfall.io/normal/front/9/0/9090b055-3406-4fed-a8c6-3f6353a9600e.jpg?1778833154"
    }
}
