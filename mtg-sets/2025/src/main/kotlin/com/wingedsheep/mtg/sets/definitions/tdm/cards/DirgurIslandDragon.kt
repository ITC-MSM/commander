package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost

/**
 * Dirgur Island Dragon // Skimming Strike — Tarkir: Dragonstorm #40
 * {5}{U} · Creature — Dragon · 4/4
 *
 * Flying
 * Ward {2}
 *
 * Omen: Skimming Strike — {1}{U}, Instant — Omen
 * Tap up to one target creature. Draw a card.
 *
 * (Omen, Tarkir: Dragonstorm: casting the Omen face shuffles this card into its owner's
 * library on resolution instead of putting it in the graveyard. From every zone other than
 * the stack the card is just the Dragon — see [com.wingedsheep.sdk.model.CardLayout.OMEN].)
 */
val DirgurIslandDragon = card("Dirgur Island Dragon") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\nWard {2} (Whenever this creature becomes the target of a spell or " +
        "ability an opponent controls, counter it unless that player pays {2}.)"

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{2}")))

    // Omen: Skimming Strike — Instant. Tap up to one target creature. Draw a card.
    omen("Skimming Strike") {
        manaCost = "{1}{U}"
        typeLine = "Instant — Omen"
        oracleText = "Tap up to one target creature. Draw a card. " +
            "(Then shuffle this card into its owner's library.)"
        spell {
            val creature = target("creature", Targets.UpToCreatures(1))
            effect = Effects.Tap(creature).then(Effects.DrawCards(1))
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Daniel Ljunggren"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1d21a9a-6b0c-4fbc-a427-81be885d326b.jpg?1743204119"
    }
}
