package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.GatherUntilMatchEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Bre of Clan Stoutarm
 * {2}{R}{W}
 * Legendary Creature — Giant Warrior
 * 4/4
 *
 * {1}{W}, {T}: Another target creature you control gains flying and lifelink until end of turn.
 * At the beginning of your end step, if you gained life this turn, exile cards from the top of
 * your library until you exile a nonland card. You may cast that card without paying its mana
 * cost if the spell's mana value is less than or equal to the amount of life you gained this
 * turn. Otherwise, put it into your hand.
 */
val BreOfClanStoutarm = card("Bre of Clan Stoutarm") {
    manaCost = "{2}{R}{W}"
    colorIdentity = "WR"
    typeLine = "Legendary Creature — Giant Warrior"
    power = 4
    toughness = 4
    oracleText = "{1}{W}, {T}: Another target creature you control gains flying and lifelink until end of turn.\n" +
        "At the beginning of your end step, if you gained life this turn, exile cards from the top of your library until you exile a nonland card. You may cast that card without paying its mana cost if the spell's mana value is less than or equal to the amount of life you gained this turn. Otherwise, put it into your hand."

    // {1}{W}, {T}: Another target creature you control gains flying and lifelink until end of turn.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap)
        val creature = target("creature", TargetCreature(filter = TargetFilter.CreatureYouControl.other()))
        effect = Effects.Composite(listOf(
            Effects.GrantKeyword(Keyword.FLYING, creature),
            Effects.GrantKeyword(Keyword.LIFELINK, creature)
        ))
    }

    // At the beginning of your end step, if you gained life this turn,
    // exile cards from the top of your library until you exile a nonland card.
    // You may cast that card without paying its mana cost if its mana value is
    // less than or equal to the amount of life you gained this turn.
    // Otherwise, put it into your hand.
    triggeredAbility {
        // Every road out of the "may cast" ends in your hand — the mana value being too high, and
        // declining the offered cast — so both branches below move the same stored card the same way.
        val nonlandToHand = MoveCollectionEffect(
            from = "nonland",
            destination = CardDestination.ToZone(Zone.HAND)
        )
        trigger = Triggers.YourEndStep
        interveningIf = Conditions.YouGainedLifeThisTurn
        effect = Effects.Composite(listOf(
            // Exile from top until nonland — same pipeline as The Infamous Cruelclaw.
            GatherUntilMatchEffect(
                filter = GameObjectFilter.Nonland,
                storeMatch = "nonland",
                storeRevealed = "allRevealed"
            ),
            RevealCollectionEffect(from = "allRevealed"),
            MoveCollectionEffect(
                from = "allRevealed",
                destination = CardDestination.ToZone(Zone.EXILE)
            ),
            // Compare the exiled nonland's mana value to the life gained this turn.
            ConditionalEffect(
                condition = Compare(
                    left = DynamicAmount.StoredCardManaValue("nonland"),
                    operator = ComparisonOperator.LTE,
                    right = DynamicAmounts.lifeGainedThisTurn()
                ),
                // MV ≤ life gained: you may cast it for free *while this ability resolves* (the
                // printed ruling — you can't wait to cast it later), so cast inline from exile
                // rather than granting deferred may-play permission.
                effect = MayEffect(
                    Effects.CastFromCollectionWithoutPayingCost("nonland"),
                    otherwise = nonlandToHand
                ),
                // "Otherwise" covers every way you don't cast it — the mana value being too high,
                // and declining the offer above. Matter Reshaper prints the same shape ("You may put
                // that card onto the battlefield if it's a permanent card with mana value 3 or less.
                // Otherwise, put that card into your hand.") and is ruled "If you don't put the card
                // onto the battlefield for any reason, you put the card into your hand." Cf. Fecund
                // Greenshell and Aid from the Cowl, both ruled the same way. Nothing is ever left
                // stranded in exile.
                elseEffect = nonlandToHand
            )
        ))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "207"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/013cefb7-a059-45c2-81b0-187f35aac4a2.jpg?1767658514"
        ruling("2025-11-17", "Bre's last ability will check as your end step starts to see if you gained life this turn. If you didn't, the ability won't trigger at all. Gaining life during your end step won't cause the ability to trigger.")
        ruling("2025-11-17", "If you choose to cast the exiled card, you do so while Bre's last ability is resolving and still on the stack. You can't wait to cast it later in the turn. Timing restrictions based on the card's type are ignored.")
        ruling("2025-11-17", "If the spell you cast has {X} in its mana cost, you must choose 0 as the value of X when casting it without paying its mana cost.")
        ruling("2025-11-17", "Since you are using an alternative cost to cast the spell, you can't pay any other alternative costs. You can, however, pay additional costs, such as kicker costs. If the card has any mandatory additional costs, you must pay those.")
    }
}
