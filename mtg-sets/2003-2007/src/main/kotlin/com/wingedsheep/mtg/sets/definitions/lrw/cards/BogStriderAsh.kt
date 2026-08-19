package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Bog-Strider Ash
 * {3}{G}
 * Creature — Treefolk Shaman
 * 2/4
 * Swampwalk
 * Whenever a player casts a Goblin spell, you may pay {G}. If you do, you gain 2 life.
 *
 * "A player" is every player, Bog-Strider Ash's controller included, so the trigger uses
 * [Triggers.anyPlayerCasts]. The optional payment is [MayPayManaEffect] — the yes/no and the mana
 * both happen on resolution, and declining does nothing.
 */
val BogStriderAsh = card("Bog-Strider Ash") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Shaman"
    power = 2
    toughness = 4
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a " +
        "Swamp.)\nWhenever a player casts a Goblin spell, you may pay {G}. If you do, you gain 2 life."

    keywords(Keyword.SWAMPWALK)

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withSubtype(Subtype.GOBLIN))
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{G}"),
            effect = Effects.GainLife(2)
        )
        description = "Whenever a player casts a Goblin spell, you may pay {G}. If you do, you gain 2 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "198"
        artist = "Steven Belledin"
        flavorText = "\"If you want to test wisdom, offer it to fools and watch how they tear it up.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/1/310a1b54-56d9-496a-b910-90cc55996ad4.jpg?1783942867"
    }
}
