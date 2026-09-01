package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Gleancrawler — Ravnica: City of Guilds #247
 * {3}{B/G}{B/G}{B/G} · Creature — Insect Horror · 6/6 · Rare
 *
 * Trample
 * At the beginning of your end step, return to your hand all creature cards in your graveyard
 * that were put there from the battlefield this turn.
 *
 * One Gather → Move pipeline over your own graveyard, the Second Sunrise shape narrowed to
 * creature cards. `putIntoGraveyardFromBattlefieldThisTurn()` is the whole clause: it reads the
 * per-card marker `ZoneTransitionService` stamps on every graveyard arrival from the battlefield
 * and wipes at the untap step, so a creature milled this turn or one that died last turn stays
 * put, and one that died before Gleancrawler arrived still comes back (the 2005 ruling).
 *
 * The move sends each card to its owner's hand: the zone transition routes non-battlefield
 * destinations to the card's owner, so a stolen creature that died under your control goes home
 * even though it sat in *your* graveyard — CR 400.3 puts it there, and "return to your hand" for
 * an opponent's card is impossible, so the engine's owner routing is the correct reading.
 */
val Gleancrawler = card("Gleancrawler") {
    manaCost = "{3}{B/G}{B/G}{B/G}"
    colorIdentity = "BG"
    typeLine = "Creature — Insect Horror"
    power = 6
    toughness = 6
    oracleText = "({B/G} can be paid with either {B} or {G}.)\n" +
        "Trample\n" +
        "At the beginning of your end step, return to your hand all creature cards in your " +
        "graveyard that were put there from the battlefield this turn."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.Pipeline {
            val fallen = gather(
                CardSource.FromZone(
                    zone = Zone.GRAVEYARD,
                    player = Player.You,
                    filter = GameObjectFilter.Creature.putIntoGraveyardFromBattlefieldThisTurn(),
                ),
                name = "fallen",
            )
            toHand(fallen)
        }
        description = "At the beginning of your end step, return to your hand all creature cards " +
            "in your graveyard that were put there from the battlefield this turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "247"
        artist = "Dave Allsop"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69ebb44b-bf3e-4b9e-b568-c20970bb969d.jpg?1783943604"
        ruling(
            "2005-10-01",
            "When this ability resolves, it returns all creature cards currently in your graveyard " +
                "that were put there directly from the battlefield sometime during the turn. This " +
                "includes cards that were put into your graveyard before Gleancrawler entered the " +
                "battlefield."
        )
    }
}
