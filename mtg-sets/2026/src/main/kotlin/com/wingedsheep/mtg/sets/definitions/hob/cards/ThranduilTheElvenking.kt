package com.wingedsheep.mtg.sets.definitions.hob.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DonorCards
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.HasAllActivatedAbilitiesOfCards
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Thranduil, the Elvenking
 * {2}{B}{G}{U}
 * Legendary Creature — Elf Noble (The Hobbit, rare)
 *
 * "Thranduil has all activated abilities of all Elf cards in your graveyard.
 *  Whenever another legendary Elf you control enters, draw two cards, then discard a card."
 *
 * Implementation:
 *  - The ability grant is [HasAllActivatedAbilitiesOfCards] with `donors = YOUR_GRAVEYARD` — the
 *    graveyard arm of the same primitive that backs Territory Forge (linked exile) and Locus of
 *    Enlightenment (craft materials). `cardFilter` narrows the pool to Elf cards; `receivedBy`
 *    defaults to the source, so Thranduil grants to himself. The engine re-reads the graveyard on
 *    every legality query, so the granted set changes live as Elf cards arrive and leave, and each
 *    ability is granted with *Thranduil* as its source — `{T}` taps him and the donor card's
 *    self-references bind to him (CR 113.7, and the printed ruling that an ability referencing its
 *    own card by name is treated as referencing Thranduil).
 *  - The trigger filters on `Any` rather than `Creature`: the text says "legendary Elf", not
 *    "legendary Elf creature", so a noncreature Elf permanent counts. Same reason the graveyard
 *    filter is "Elf card", not "Elf creature card".
 *
 * Rulings (2026-08-14):
 *  - An activated ability of a donor Elf card that references its own card by name is treated as
 *    referencing Thranduil instead.
 *  - Linked activated abilities (one exiles a card, another refers to "the card exiled with it")
 *    stay linked only while Thranduil has them; losing and regaining the abilities breaks the link.
 *  - Only *activated* abilities are granted — those with a colon in their cost/effect separator,
 *    including keyword abilities whose reminder text contains one.
 */
val ThranduilTheElvenking = card("Thranduil, the Elvenking") {
    manaCost = "{2}{B}{G}{U}"
    colorIdentity = "BGU"
    typeLine = "Legendary Creature — Elf Noble"
    oracleText = "Thranduil has all activated abilities of all Elf cards in your graveyard.\n" +
        "Whenever another legendary Elf you control enters, draw two cards, then discard a card."
    power = 5
    toughness = 6

    // "Thranduil has all activated abilities of all Elf cards in your graveyard."
    staticAbility {
        ability = HasAllActivatedAbilitiesOfCards(
            donors = DonorCards.YOUR_GRAVEYARD,
            cardFilter = GameObjectFilter.Any.withSubtype(Subtype.ELF)
        )
    }

    // "Whenever another legendary Elf you control enters, draw two cards, then discard a card."
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Any.legendary().withSubtype(Subtype.ELF).youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.Composite(
            Effects.DrawCards(2),
            Patterns.Hand.discardCards(1)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "167"
        artist = "Magali Villeneuve"
        flavorText = "In a great hall with pillars hewn out of the living stone sat the Elvenking " +
            "on a chair of carven wood."
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe2fe8fa-3b99-44c1-bab9-922e5c864952.jpg?1784377043"
    }
}
