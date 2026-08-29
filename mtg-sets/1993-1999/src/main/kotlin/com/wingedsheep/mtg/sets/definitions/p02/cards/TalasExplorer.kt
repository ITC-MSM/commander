package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.LookAtTargetHandEffect

/**
 * Talas Explorer
 * {1}{U}
 * Creature — Human Pirate Scout
 * 1 / 1
 *
 * Flying
 * When this creature enters, look at target opponent's hand.
 *
 * "Look at target opponent's hand" targets a *player*, so the requirement is
 * [Targets.Opponent]; [LookAtTargetHandEffect] has no `Effects.*` wrapper and is
 * constructed directly (the Portal shape — see Ingenious Thief).
 */
val TalasExplorer = card("Talas Explorer") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate Scout"
    oracleText =
        "Flying\n" +
        "When this creature enters, look at target opponent's hand."
    power = 1
    toughness = 1

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target", Targets.Opponent)
        effect = LookAtTargetHandEffect(opponent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Douglas Shuler"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64f184c9-a716-4ba8-8efa-495358660de5.jpg"
    }
}
