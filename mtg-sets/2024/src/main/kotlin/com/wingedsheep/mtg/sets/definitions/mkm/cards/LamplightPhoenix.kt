package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lamplight Phoenix — Murders at Karlov Manor #137
 * {1}{R}{R} · Creature — Phoenix · Rare
 * 3/3
 *
 * The dies trigger functions from the graveyard so its composite optional payment can exile the
 * Phoenix itself before collecting evidence. That ordering matters: the Phoenix is no longer in
 * the graveyard when the evidence selection is made, so it cannot help pay its own evidence cost.
 * The payment composite stops on failure, and the return happens only after both parts succeed.
 */
val LamplightPhoenix = card("Lamplight Phoenix") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Phoenix"
    oracleText = "Flying\n" +
        "When this creature dies, you may exile it and collect evidence 4. If you do, return this " +
        "card to the battlefield tapped. (To collect evidence 4, exile cards with total mana " +
        "value 4 or greater from your graveyard.)"
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        triggerZone = Zone.GRAVEYARD
        effect = OptionalCostEffect(
            cost = Effects.Composite(
                Effects.Exile(EffectTarget.Self, fromZone = Zone.GRAVEYARD),
                Effects.CollectEvidence(4),
            ),
            ifPaid = Effects.PutOntoBattlefield(EffectTarget.Self, tapped = true),
        )
        description = "When this creature dies, you may exile it and collect evidence 4. If you " +
            "do, return this card to the battlefield tapped."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "137"
        artist = "Ryan Pancoast"
        flavorText = "The only warning was the faint smell of burning plasma."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2faa0e56-527c-4be5-b8c9-b10ccde275f5.jpg?1783912876"

        ruling(
            "2024-02-02",
            "You can't exile Lamplight Phoenix from your graveyard to pay the collect evidence " +
                "cost of its triggered ability.",
        )
        ruling(
            "2024-02-02",
            "If you can't exile enough cards to meet or exceed the required mana value, you " +
                "can't choose to collect evidence at all.",
        )
    }
}
