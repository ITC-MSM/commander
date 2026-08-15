package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Crypt Feaster
 * {3}{B}
 * Creature — Zombie
 * 3/4
 *
 * Menace
 * Threshold — Whenever this creature attacks, if there are seven or more cards in your
 * graveyard, this creature gets +2/+0 until end of turn.
 *
 * The threshold clause is an intervening-"if" on the attack trigger ([triggerCondition] is
 * checked both when the trigger would go on the stack and again on resolution), pumping the
 * source via [Effects.ModifyStats] targeting [EffectTarget.Self].
 */
val CryptFeaster = card("Crypt Feaster") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 3
    toughness = 4
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Threshold — Whenever this creature attacks, if there are seven or more cards in your " +
        "graveyard, this creature gets +2/+0 until end of turn."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.CardsInGraveyardAtLeast(7)
        effect = Effects.ModifyStats(power = 2, toughness = 0, target = EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "John Di Giovanni"
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b072811-998a-4a71-b59c-6afecc0dc4b6.jpg?1782689215"
    }
}
