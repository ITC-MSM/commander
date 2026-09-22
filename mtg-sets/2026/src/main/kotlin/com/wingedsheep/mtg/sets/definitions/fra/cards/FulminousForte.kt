package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val FulminousForte = card("Fulminous Forte") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Fulminous Forte deals 1 damage to each creature and planeswalker your opponents control.\n• Fulminous Forte deals 5 damage to target creature or planeswalker."

    spell {
        effect = ModalEffect.chooseOne(
            Mode(
                effect = Patterns.Group.dealDamageToAll(1,
                    GroupFilter(GameObjectFilter.CreatureOrPlaneswalker.opponentControls())),
                description = "Deal 1 damage to each creature and planeswalker your opponents control."
            ),
            Mode.withTarget(
                Effects.DealDamage(5, EffectTarget.ContextTarget(0)),
                Targets.CreatureOrPlaneswalker,
                "Deal 5 damage to target creature or planeswalker."
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "84"
        artist = "Andre Garcia"
        flavorText = "A solo so violent even the echoes can deafen the unprepared."
        imageUri = "https://cards.scryfall.io/normal/front/1/9/19acb2b5-3b3e-43f0-bd81-8426ed3d9c55.jpg?1789614832"
        inBooster = false
    }
}
