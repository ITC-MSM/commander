package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.CantBeBlockedExceptBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Legend of Kuruk // Avatar Kuruk
 * {2}{U}{U} — Enchantment — Saga
 * //  — Legendary Creature — Avatar 4/3
 *
 * Front — The Legend of Kuruk:
 *   (As this Saga enters and after your draw step, add a lore counter.)
 *   I, II — Scry 2, then draw a card.
 *   III — Exile this Saga, then return it to the battlefield transformed under your control.
 *
 * Back — Avatar Kuruk:
 *   Whenever you cast a spell, create a 1/1 colorless Spirit creature token with "This token can't
 *     block or be blocked by non-Spirit creatures."
 *   Exhaust — Waterbend {20}: Take an extra turn after this one.
 *
 * Both saga chapters I and II share the same Scry 2 + draw effect. Chapter III is the standard
 * transforming-Saga final chapter ([Effects.ExileAndReturnTransformed]). The back face's exhaust
 * ability (isExhaust = true) carries a waterbend cost (hasWaterbend = true) over [Effects.TakeExtraTurn].
 * The Spirit token mirrors the one minted by Foggy Swamp Spirit Keeper.
 */
private val AvatarKuruk = card("Avatar Kuruk") {
    manaCost = ""
    colorIdentity = "U"
    typeLine = "Legendary Creature — Avatar"
    power = 4
    toughness = 3
    oracleText = "Whenever you cast a spell, create a 1/1 colorless Spirit creature token with " +
        "\"This token can't block or be blocked by non-Spirit creatures.\"\n" +
        "Exhaust — Waterbend {20}: Take an extra turn after this one. (While paying a waterbend cost, " +
        "you can tap your artifacts and creatures to help. Each one pays for {1}. Activate each exhaust " +
        "ability only once.)"

    triggeredAbility {
        trigger = Triggers.youCastSpell()
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(1),
            power = 1,
            toughness = 1,
            colors = emptySet(),
            creatureTypes = setOf("Spirit"),
            name = "Spirit",
            imageUri = "https://cards.scryfall.io/normal/front/f/5/f59eba51-458a-40e0-b754-999f91d5d839.jpg?1764117653",
            staticAbilities = listOf(
                CantBeBlockedExceptBy(blockerFilter = GameObjectFilter.Creature.withSubtype("Spirit")),
                CanOnlyBlockCreaturesWith(blockerFilter = GameObjectFilter.Creature.withSubtype("Spirit")),
            )
        )
        description = "Whenever you cast a spell, create a 1/1 colorless Spirit creature token with " +
            "\"This token can't block or be blocked by non-Spirit creatures.\""
    }

    activatedAbility {
        isExhaust = true
        hasWaterbend = true
        cost = Costs.Mana("{20}")
        effect = Effects.TakeExtraTurn()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "61"
        artist = "Takayama Toshiaki"
        imageUri = "https://cards.scryfall.io/normal/back/5/e/5e9a53d3-7f2f-4a9c-9516-0713da740478.jpg?1770463831"
    }
}

private val TheLegendOfKurukFront = card("The Legend of Kuruk") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter.)\n" +
        "I, II — Scry 2, then draw a card.\n" +
        "III — Exile this Saga, then return it to the battlefield transformed under your control."

    // I — Scry 2, then draw a card.
    sagaChapter(1) {
        effect = Effects.Composite(
            Effects.Scry(2),
            Effects.DrawCards(1),
        )
    }

    // II — Scry 2, then draw a card.
    sagaChapter(2) {
        effect = Effects.Composite(
            Effects.Scry(2),
            Effects.DrawCards(1),
        )
    }

    // III — Exile this Saga, then return it to the battlefield transformed under your control.
    sagaChapter(3) {
        effect = Effects.ExileAndReturnTransformed()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "61"
        artist = "Takayama Toshiaki"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e9a53d3-7f2f-4a9c-9516-0713da740478.jpg?1770463831"
    }
}

val TheLegendOfKuruk: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = TheLegendOfKurukFront,
    backFace = AvatarKuruk,
)
