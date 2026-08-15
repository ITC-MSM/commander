package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Inspiration from Beyond
 * {2}{U}
 * Sorcery
 *
 * Mill three cards, then return an instant or sorcery card from your graveyard
 * to your hand.
 * Flashback {5}{U}{U}
 *
 * The returned card is chosen at resolution (not a cast-time target), so a spell
 * put into the graveyard by the mill is a legal choice. Modeled as
 * mill → gather instant/sorcery cards from your graveyard → choose one → to hand.
 */
val InspirationFromBeyond = card("Inspiration from Beyond") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Mill three cards, then return an instant or sorcery card from your graveyard to your hand.\n" +
        "Flashback {5}{U}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        effect = Patterns.Library.mill(3) then Effects.Pipeline {
            val spells = gather(
                CardSource.FromZone(Zone.GRAVEYARD, Player.You, GameObjectFilter.InstantOrSorcery),
                name = "graveyardSpells",
            )
            val returned = chooseExactly(
                1,
                from = spells,
                prompt = "Return an instant or sorcery card to your hand",
                name = "returned",
            )
            toHand(returned)
        }
    }

    keywordAbility(KeywordAbility.flashback("{5}{U}{U}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "Xavier Ribeiro"
        flavorText = "When seeking spiritual enlightenment, best to go directly to the source."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b636fe95-664f-4fb1-aab9-28856edeccd6.jpg?1782689228"
    }
}
