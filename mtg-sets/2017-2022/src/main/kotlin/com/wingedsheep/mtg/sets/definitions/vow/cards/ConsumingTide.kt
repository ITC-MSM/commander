package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Consuming Tide (Innistrad: Crimson Vow #53)
 * {2}{U}{U} · Sorcery
 *
 * Each player chooses a nonland permanent they control. Return all nonland permanents not chosen
 * this way to their owners' hands. Then you draw a card for each opponent who has more cards in
 * their hand than you.
 *
 * Implementation: the Liliana, Dreadhorde General −9 shape with the fate swapped — gather every
 * nonland permanent (the pool's controllers are the choosers, asked in APNAP order per the card's
 * ruling), `chooseOnePerCategory` over the single category "nonland permanent", then bounce
 * `exclude(pool, kept)`. A battlefield → hand move always routes to each card's *owner*, so
 * "their owners' hands" needs no per-owner loop. The draw runs after the bounce, so hand sizes
 * are read post-return, which is what "Then" means; the per-opponent count is the Wojek
 * Investigator idiom ([DynamicAmount.CountPlayersWith] rebinding `Player.You` to the tested
 * opponent, [Player.ControllerOfSource] falling back to the caster for a resolving spell).
 */
val ConsumingTide = card("Consuming Tide") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Each player chooses a nonland permanent they control. Return all nonland " +
        "permanents not chosen this way to their owners' hands. Then you draw a card for each " +
        "opponent who has more cards in their hand than you."

    spell {
        effect = Effects.Pipeline {
            val pool = gather(Filters.NonlandPermanent)
            val kept = chooseOnePerCategory(pool, listOf(Filters.NonlandPermanent))
            toHand(exclude(pool, kept))
            run(
                Effects.DrawCards(
                    DynamicAmount.CountPlayersWith(
                        scope = Player.EachOpponent,
                        condition = Conditions.CompareAmounts(
                            left = DynamicAmount.Count(Player.You, Zone.HAND),
                            operator = ComparisonOperator.GT,
                            right = DynamicAmount.Count(Player.ControllerOfSource, Zone.HAND),
                        ),
                    )
                )
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "53"
        artist = "Viko Menezes"
        flavorText = "\"Nephalia's tides obey an unforgiving moon.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/8/5865f0f1-28a6-49ac-b61e-135845075d1f.jpg?1783924899"
        ruling(
            "2021-11-19",
            "Starting with the player whose turn it is, each player chooses a nonland permanent they " +
                "control. Players get to know what permanents have been chose by players that chose " +
                "before them as they make their choice. Then all nonland permanents that weren't " +
                "chosen by any player are returned to their owners' hands at the same time."
        )
    }
}
