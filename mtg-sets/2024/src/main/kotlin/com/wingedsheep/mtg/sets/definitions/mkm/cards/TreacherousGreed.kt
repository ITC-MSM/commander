package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Treacherous Greed — Murders at Karlov Manor #237
 * {1}{W}{B} · Instant · Rare
 *
 * As an additional cost to cast this spell, sacrifice a creature that dealt damage this turn.
 * Draw three cards. Each opponent loses 3 life and you gain 3 life.
 *
 * The whole card is the additional cost. `Sacrifice a creature that dealt damage this turn` is
 * `GameObjectFilter.Creature.hasDealtDamageThisTurn()` — the **active**-voice predicate, the one
 * that asks what the creature dealt, not what was dealt to it. Its passive sibling
 * `wasDealtDamageThisTurn()` reads almost identically and means the opposite; picking the wrong one
 * would let a creature that merely *survived* a Shock pay this, and would refuse the attacker that
 * actually connected.
 *
 * The tracker behind that predicate records the fact of dealing, not the recipient, which is why
 * the 2024-02-02 ruling holds for free: a creature that killed a blocker still qualifies once the
 * blocker is gone, and a creature that hit a player who has since left the game still qualifies too.
 * There is nothing to look up at cast time beyond the flag itself.
 *
 * Because the sacrifice is an additional *cost*, it happens on announcement (CR 601.2h) — before
 * anyone can respond, and it happens whether or not the spell resolves. Note also that the creature
 * doesn't have to be the one that dealt the damage on your behalf: any creature you control that
 * dealt damage this turn is legal, including one that dealt damage while an opponent controlled it.
 *
 * The payoff is a flat three-part [Effects.Composite]. `Each opponent loses 3 life` is
 * [Player.EachOpponent] rather than three separate effects, and the 3 life you gain is a fixed
 * number — not "life equal to the life lost this way", which is a different card and a different
 * amount in multiplayer.
 */
val TreacherousGreed = card("Treacherous Greed") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature that dealt " +
        "damage this turn.\n" +
        "Draw three cards. Each opponent loses 3 life and you gain 3 life."

    additionalCost(
        Costs.additional.SacrificePermanent(GameObjectFilter.Creature.hasDealtDamageThisTurn())
    )

    spell {
        effect = Effects.Composite(
            Effects.DrawCards(3),
            Effects.LoseLife(3, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(3)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "237"
        artist = "Eli Minaya"
        flavorText = "Loot's easy to divide by one."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4b9260b-0993-42c5-9bcf-87ab394d51db.jpg?1783912838"

        ruling(
            "2024-02-02",
            "You can sacrifice any creature you control that dealt damage this turn to pay " +
                "Treacherous Greed's additional cost, even if the permanent it dealt damage to is " +
                "no longer on the battlefield or the player it dealt damage to is no longer in the " +
                "game."
        )
    }
}
