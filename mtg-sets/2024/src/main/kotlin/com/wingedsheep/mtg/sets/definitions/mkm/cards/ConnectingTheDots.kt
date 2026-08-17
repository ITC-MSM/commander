package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Connecting the Dots — Murders at Karlov Manor #118
 * {1}{R} · Enchantment
 *
 * Whenever a creature you control attacks, exile the top card of your library face down.
 * {1}{R}, Discard your hand, Sacrifice this enchantment: Put all cards exiled with this
 * enchantment into their owners' hands.
 *
 * The two halves are one mechanism: the exile side stamps `linkToSource`, and the payoff reads that
 * same linked pile back with [Effects.ReturnLinkedExileToHand]. That link is what makes the first
 * ruling true without any extra wiring — each copy of this enchantment owns its own pile, so a
 * second Connecting the Dots can't cash in the first one's cards. It's also why the second ruling
 * holds: sacrificing or bouncing the enchantment destroys the link, and the cards stay in exile
 * face down forever.
 *
 * `FaceDownMode.HIDDEN` is the "face down in exile" mode (the hideaway shape), so the cards are
 * genuinely unlookable rather than merely unrevealed — the parenthetical "(You can't look at it.)"
 * is load-bearing, and hidden exile is what masks them from their own controller.
 *
 * The trigger is per-attacker (`binding = ANY` over creatures you control), not a
 * once-per-declaration `YouAttack`: attacking with three creatures exiles three cards.
 *
 * Discarding your hand is [Costs.DiscardHand], which is payable with an empty hand — the third
 * ruling — because it discards whatever is there rather than requiring a card.
 */
val ConnectingTheDots = card("Connecting the Dots") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control attacks, exile the top card of your library " +
        "face down. (You can't look at it.)\n" +
        "{1}{R}, Discard your hand, Sacrifice this enchantment: Put all cards exiled with this " +
        "enchantment into their owners' hands."

    triggeredAbility {
        trigger = Triggers.attacks(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(count = DynamicAmount.Fixed(1), player = Player.You),
                    storeAs = "clue"
                ),
                MoveCollectionEffect(
                    from = "clue",
                    destination = CardDestination.ToZone(Zone.EXILE),
                    faceDown = FaceDownMode.HIDDEN,
                    linkToSource = true
                )
            )
        )
        description = "Whenever a creature you control attacks, exile the top card of your " +
            "library face down."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.DiscardHand, Costs.SacrificeSelf)
        effect = Effects.ReturnLinkedExileToHand()
        description = "Put all cards exiled with this enchantment into their owners' hands."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "118"
        artist = "Aaron J. Riley"
        flavorText = "\"How could I have missed it? The answer was right in front of me all along!\""
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e02731a-8698-4b41-99c3-f0a19fc31430.jpg?1783912884"
        ruling(
            "2024-02-02",
            "Each Connecting the Dots you control has its own set of face-down exiled cards. " +
                "Connecting the Dots's last ability puts only those cards into your hand, not those of " +
                "any other Connecting the Dots."
        )
        ruling(
            "2024-02-02",
            "If Connecting the Dots leaves the battlefield before you activate its last ability, any " +
                "cards exiled by its triggered ability remain exiled face down for the rest of the game."
        )
        ruling("2024-02-02", "You can pay the cost of \"discard your hand\" even if your hand contains zero cards.")
    }
}
