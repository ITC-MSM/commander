package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vulturous Zombie — Ravnica: City of Guilds #238
 * {3}{B}{G} · Creature — Plant Zombie · Rare
 *
 * Flying
 * Whenever a card is put into an opponent's graveyard from anywhere, put a +1/+1 counter on this
 * creature.
 *
 * The Golgari read of a graveyard: every card an opponent loses, from any zone, is food. In a
 * two-player game it grows off their removal, their discards, and every creature it kills; in a pod
 * it grows off all of it at once.
 *
 * **"From anywhere" is a bare destination filter.** The trigger is an
 * [EventPattern.ZoneChangeEvent] with `to = GRAVEYARD` and **no** `from` — the first ruling
 * enumerates the stack, the battlefield, hands and libraries precisely because the card doesn't
 * care, so pinning a source zone would be wrong. [TriggerBinding.ANY] because the moving card is
 * some other object entirely, and [CardPredicate.IsNontoken] because the second ruling is explicit
 * that a token going to a graveyard is not a card and does not trigger it.
 *
 * The graveyard is keyed by **owner**, not controller — a card always goes to its owner's graveyard
 * (CR 404.3) — so the filter is `ownedByOpponent()`, which correctly ignores an opponent's creature
 * you had stolen when it dies (it goes to *their* graveyard, so it counts) and equally ignores your
 * own card that an opponent controlled (it goes to *yours*, so it doesn't).
 *
 * This is not a batching trigger: one +1/+1 counter per card, so a five-card mill grows it by five.
 */
val VulturousZombie = card("Vulturous Zombie") {
    manaCost = "{3}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Plant Zombie"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever a card is put into an opponent's graveyard from anywhere, put a +1/+1 counter " +
        "on this creature."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = TriggerSpec(
            event = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Any
                    .withCardPredicate(CardPredicate.IsNontoken)
                    .ownedByOpponent(),
                to = Zone.GRAVEYARD,
            ),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever a card is put into an opponent's graveyard from anywhere, put a " +
            "+1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "238"
        artist = "Greg Staples"
        flavorText = "\"When something dies, all things benefit. Well okay, just our things.\"\n" +
            "—Ezoc, Golgari rot farmer"
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7b0fb8e7-14aa-4f2d-aa05-c98b2c9c463c.jpg?1783943609"
        ruling(
            "2005-10-01",
            "The ability doesn't care why a card goes to your opponent's graveyard — only that it " +
                "does. The ability triggers when a card is put into your opponent's graveyard " +
                "from the stack (a spell resolves, fails to resolve, or is countered), from the " +
                "battlefield (a permanent is destroyed or sacrificed), from the player's hand (a " +
                "card is discarded), from the player's library (from a Millstone-like effect), or " +
                "from any other zone."
        )
        ruling(
            "2005-10-01",
            "This ability triggers only on cards, so it won't trigger when a token is put into " +
                "your opponent's graveyard."
        )
    }
}
