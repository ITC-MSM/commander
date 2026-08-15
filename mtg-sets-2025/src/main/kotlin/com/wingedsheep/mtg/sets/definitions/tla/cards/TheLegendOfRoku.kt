package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Legend of Roku // Avatar Roku
 * {2}{R}{R} — Enchantment — Saga
 * //  — Legendary Creature — Avatar 4/4
 *
 * Front — The Legend of Roku:
 *   (As this Saga enters and after your draw step, add a lore counter.)
 *   I — Exile the top three cards of your library. Until the end of your next turn, you may play
 *       those cards.
 *   II — Add one mana of any color.
 *   III — Exile this Saga, then return it to the battlefield transformed under your control.
 *
 * Back — Avatar Roku:
 *   Firebending 4 (Whenever this creature attacks, add {R}{R}{R}{R}. This mana lasts until end of
 *   combat.)
 *   {8}: Create a 4/4 red Dragon creature token with flying and firebending 4.
 *
 * Chapter I is the named "impulse draw" mechanic via [Patterns.Exile.impulse], exiling the top three
 * cards and granting permission to play them with [MayPlayExpiry.UntilEndOfNextTurn] ("until the end
 * of your next turn"). Chapter II adds one mana of any single color ([Effects.AddAnyColorMana]).
 * Chapter III is the standard transforming-Saga final chapter — [Effects.ExileAndReturnTransformed]
 * re-enters the permanent as its back face. The back face carries firebending 4 (the [firebending]
 * DSL: a display-only keyword backed by an attack-triggered "add {R}{R}{R}{R} until end of combat"
 * ability) and an {8} activated ability minting a 4/4 red Dragon with flying and its own firebending 4
 * (the inline [dragonToken] supplies the firebending triggered ability copied onto the token).
 */
private val dragonToken = card("Dragon") {
    typeLine = "Token Creature — Dragon"
    colorIdentity = "R"
    power = 4
    toughness = 4
    keywords(Keyword.FLYING)
    firebending(4)
}

private val AvatarRoku = card("Avatar Roku") {
    manaCost = ""
    colorIdentity = "R"
    typeLine = "Legendary Creature — Avatar"
    oracleText = "Firebending 4 (Whenever this creature attacks, add {R}{R}{R}{R}. This mana lasts " +
        "until end of combat.)\n" +
        "{8}: Create a 4/4 red Dragon creature token with flying and firebending 4."
    power = 4
    toughness = 4

    firebending(4)

    // {8}: Create a 4/4 red Dragon creature token with flying and firebending 4.
    activatedAbility {
        cost = Costs.Mana("{8}")
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(1),
            power = 4,
            toughness = 4,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Dragon"),
            keywords = setOf(Keyword.FLYING, Keyword.FIREBENDING),
            triggeredAbilities = dragonToken.triggeredAbilities,
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "145"
        artist = "Song Qijin"
        flavorText = "\"You must be decisive.\""
        imageUri = "https://cards.scryfall.io/normal/back/9/5/95f2f5af-d405-4534-8683-5a9001f997b4.jpg?1782135808"
    }
}

private val TheLegendOfRokuFront = card("The Legend of Roku") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter.)\n" +
        "I — Exile the top three cards of your library. Until the end of your next turn, you may " +
        "play those cards.\n" +
        "II — Add one mana of any color.\n" +
        "III — Exile this Saga, then return it to the battlefield transformed under your control."

    // I — Exile the top three cards of your library. Until the end of your next turn, you may play
    // those cards.
    sagaChapter(1) {
        effect = Patterns.Exile.impulse(3, MayPlayExpiry.UntilEndOfNextTurn)
    }

    // II — Add one mana of any color.
    sagaChapter(2) {
        effect = Effects.AddAnyColorMana(1)
    }

    // III — Exile this Saga, then return it to the battlefield transformed under your control.
    sagaChapter(3) {
        effect = Effects.ExileAndReturnTransformed()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "145"
        artist = "Song Qijin"
        imageUri = "https://cards.scryfall.io/normal/front/9/5/95f2f5af-d405-4534-8683-5a9001f997b4.jpg?1782135808"
    }
}

val TheLegendOfRoku: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = TheLegendOfRokuFront,
    backFace = AvatarRoku,
)
