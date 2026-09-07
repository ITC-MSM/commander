package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Grozoth
 * {6}{U}{U}{U}
 * Creature — Leviathan
 * 9/9
 * Defender
 * When this creature enters, you may search your library for any number of cards that have mana
 * value 9, reveal them, put them into your hand, then shuffle.
 * {4}: This creature loses defender until end of turn.
 * Transmute {1}{U}{U}
 *
 * The search is the ordinary library pipeline with an *unbounded* selection, so it is written
 * inline via `Effects.Pipeline` rather than `Patterns.Library.searchLibrary` (which only offers
 * `ChooseUpTo(count)`). The consent gate wraps the whole pipeline — declining skips the shuffle
 * too, the Goblin Matron rule — while `chooseAnyNumber` separately allows finding nothing.
 *
 * "Loses defender until end of turn" is [Effects.RemoveKeyword]'s default `Duration.EndOfTurn`, a
 * layer-6 removal; activating twice in a turn is redundant rather than cumulative.
 *
 * Grozoth's mana value is 9 and transmute searches for a card with the *discarded card's* mana
 * value, so both halves of this card fetch the same nine-drops — the printed joke.
 */
val Grozoth = card("Grozoth") {
    manaCost = "{6}{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Leviathan"
    power = 9
    toughness = 9
    oracleText = "Defender (This creature can't attack.)\n" +
        "When this creature enters, you may search your library for any number of cards that have mana value 9, reveal them, put them into your hand, then shuffle.\n" +
        "{4}: This creature loses defender until end of turn.\n" +
        "Transmute {1}{U}{U} ({1}{U}{U}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            Effects.Pipeline {
                val searchable = gather(
                    CardSource.FromZone(Zone.LIBRARY, Player.You, GameObjectFilter.Any.manaValue(9))
                )
                val found = chooseAnyNumber(
                    from = searchable,
                    prompt = "Search your library for any number of cards with mana value 9",
                )
                toHand(found, revealed = true)
                run(ShuffleLibraryEffect())
            }
        )
    }

    activatedAbility {
        cost = Costs.Mana("{4}")
        effect = Effects.RemoveKeyword(Keyword.DEFENDER, EffectTarget.Self)
    }

    transmute("{1}{U}{U}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "53"
        artist = "Cyril Van Der Haegen"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96d81c13-93de-4cf6-bb15-d387ed259c50.jpg?1783943685"
    }
}
