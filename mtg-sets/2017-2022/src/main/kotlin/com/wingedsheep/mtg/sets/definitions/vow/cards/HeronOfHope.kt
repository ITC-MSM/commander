package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.ModifyLifeGain
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Heron of Hope
 * {3}{W}
 * Creature — Bird
 * 2/3
 *
 * Flying
 * If you would gain life, you gain that much life plus 1 instead.
 * {1}{W}: This creature gains lifelink until end of turn.
 *
 * The replacement applies once per life-gaining *event* (CR 614.1), so two lifelink creatures
 * dealing combat damage at the same time are modified twice while one creature dealing trample
 * damage to several recipients is modified once.
 */
val HeronOfHope = card("Heron of Hope") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird"
    oracleText = "Flying\n" +
        "If you would gain life, you gain that much life plus 1 instead.\n" +
        "{1}{W}: This creature gains lifelink until end of turn."
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    replacementEffect(
        ModifyLifeGain(
            multiplier = 1,
            modifier = 1,
            appliesTo = EventPattern.LifeGainEvent(player = Player.You)
        )
    )

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        effect = Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self, Duration.EndOfTurn)
        description = "{1}{W}: This creature gains lifelink until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa7e6933-5c64-4fcd-bb7f-740767a149cb.jpg?1783924918"
        ruling(
            "2021-11-19",
            "If you control two Heron of Hopes and you would gain life, you gain that much life plus 2. " +
                "A third Heron of Hope has you gain that much life plus 3, and so on."
        )
        ruling(
            "2021-11-19",
            "The middle ability applies just once to each life-gaining event. If you gain an amount of life " +
                "\"for each\" of something or \"equal to the number\" of something, that life is gained as one " +
                "event and the ability applies only once."
        )
    }
}
