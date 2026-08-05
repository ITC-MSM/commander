package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.LookAtTopOfLibrary
import com.wingedsheep.sdk.scripting.PlayLandsAndCastFilteredFromTopOfLibrary
import com.wingedsheep.sdk.scripting.effects.CreatePredefinedTokenEffect

/**
 * Ka-Zar of the Savage Land (MSH #174) — {4}{G} Legendary Creature — Human Barbarian Hero · 3/2
 *
 * You may look at the top card of your library any time.
 * You may play lands from the top of your library.
 * When Ka-Zar enters, create Zabu, a legendary 2/2 green Cat creature token with
 * "Landfall — Whenever a land you control enters, put a +1/+1 counter on Zabu."
 *
 * The two top-of-library statics are the Glarb, Calamity's Augur pair: [LookAtTopOfLibrary] for
 * the private peek, and [PlayLandsAndCastFilteredFromTopOfLibrary] for the play permission.
 * Ka-Zar grants *lands only*, so the spell filter is [GameObjectFilter.Land] — a filter no
 * castable card can ever match (the cast enumerator only considers the top card when it is a
 * nonland), leaving exactly the land-play half of the permission active. Only the ability's
 * auto-generated `description` reads as if spells were included; the card's printed text is
 * carried by `oracleText`.
 *
 * Zabu carries its own landfall trigger, so it is a registered `PredefinedTokens` definition
 * minted via [CreatePredefinedTokenEffect] — the trigger detector resolves the token's ability
 * from that definition by name.
 */
val KaZarOfTheSavageLand = card("Ka-Zar of the Savage Land") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Human Barbarian Hero"
    power = 3
    toughness = 2
    oracleText = "You may look at the top card of your library any time.\n" +
        "You may play lands from the top of your library.\n" +
        "When Ka-Zar enters, create Zabu, a legendary 2/2 green Cat creature token with " +
        "\"Landfall — Whenever a land you control enters, put a +1/+1 counter on Zabu.\""

    staticAbility { ability = LookAtTopOfLibrary }

    staticAbility {
        ability = PlayLandsAndCastFilteredFromTopOfLibrary(spellFilter = GameObjectFilter.Land)
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreatePredefinedTokenEffect("Zabu")
        description = "When Ka-Zar enters, create Zabu, a legendary 2/2 green Cat creature token " +
            "with \"Landfall — Whenever a land you control enters, put a +1/+1 counter on Zabu.\""
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/c/a/caf70fc5-87a0-4b4d-bedd-74dd09569bed.jpg?1783902916"
    }
}
