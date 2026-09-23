package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Yuriko, Hope from the Shadows — X is counted as the mode resolves (CR 608.2h) and then locked in
 * for the turn. [DynamicAmount] has no negation, so −X is `Multiply(count, -1)`.
 */
val YurikoHopeFromTheShadows = card("Yuriko, Hope from the Shadows") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Ninja"
    power = 1
    toughness = 1
    oracleText = "Flash\n" +
        "When Yuriko enters, choose one —\n" +
        "• Target creature gets -X/-0 until end of turn, where X is the number of cards in your graveyard.\n" +
        "• Surveil 2. (Look at the top two cards of your library, then put any number of them into " +
        "your graveyard and the rest on top of your library in any order.)"

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                Effects.ModifyStats(
                    power = DynamicAmount.Multiply(DynamicAmounts.cardsInYourGraveyard(), -1),
                    toughness = DynamicAmount.Fixed(0),
                    target = EffectTarget.ContextTarget(0),
                ),
                Targets.Creature,
                "Target creature gets -X/-0 until end of turn, where X is the number of cards in your graveyard.",
            ),
            Mode(effect = Effects.Surveil(2), description = "Surveil 2."),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "226"
        artist = "Lie Setiawan"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45e81487-8b8c-480b-922a-eaa9edc7201d.jpg?1789127717"
        inBooster = false
    }
}
