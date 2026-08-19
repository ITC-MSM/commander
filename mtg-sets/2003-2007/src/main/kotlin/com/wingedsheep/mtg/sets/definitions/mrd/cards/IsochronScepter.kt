package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Isochron Scepter
 * {2}
 * Artifact
 *
 * Imprint — When this artifact enters, you may exile an instant card with mana value 2 or less
 * from your hand.
 * {2}, {T}: You may copy the exiled card. If you do, you may cast the copy without paying its
 * mana cost.
 *
 * The imprint is the Chrome Mox shape: a linked exile (`linkToSource = true`) from your own hand,
 * with no reveal — only the exiled card becomes public. Because the pile is linked to the Scepter,
 * the activated ability re-reads it live: if the imprinted card leaves exile the gather comes back
 * empty and nothing is copied, matching the ruling that the copy can't be made once the card has
 * left the exile zone.
 *
 * The printed text carries two "may"s — copy, then cast. They collapse into the single prompt here
 * because declining the second one is indistinguishable from declining the first: a copy that is
 * never cast is a card-shaped object in exile that the next state-based-action check removes, and
 * nothing in the rules triggers on a card in exile being copied. The prompt therefore covers the
 * whole copy-and-cast, which is the only branch with an observable outcome.
 */
val IsochronScepter = card("Isochron Scepter") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Imprint — When this artifact enters, you may exile an instant card with mana " +
        "value 2 or less from your hand.\n" +
        "{2}, {T}: You may copy the exiled card. If you do, you may cast the copy without paying " +
        "its mana cost."

    // Imprint — When this artifact enters, you may exile an instant card with mana value 2 or
    // less from your hand.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            Patterns.Hand.revealHandAndExileChosen(
                target = EffectTarget.Controller,
                filter = GameObjectFilter.Instant.manaValueAtMost(2),
                prompt = "Choose an instant card with mana value 2 or less to exile",
                storeChosenAs = "scepterImprint",
                revealHand = false,
                linkToSource = true
            ),
            descriptionOverride = "You may exile an instant card with mana value 2 or less from your hand."
        )
    }

    // {2}, {T}: You may copy the exiled card. If you do, you may cast the copy without paying its
    // mana cost.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = MayEffect(
            Effects.Composite(
                GatherCardsEffect(
                    source = CardSource.FromLinkedExile(),
                    storeAs = "scepterImprinted"
                ),
                Effects.CopyCollectionIntoCollection(
                    from = "scepterImprinted",
                    storeAs = "scepterCopy"
                ),
                Effects.CastFromCollectionWithoutPayingCost("scepterCopy")
            ),
            descriptionOverride = "You may copy the exiled card and cast the copy without paying its mana cost."
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "188"
        artist = "Mark Harrison"
        imageUri = "https://cards.scryfall.io/normal/front/8/7/878b0159-6917-45d3-b9ea-562ac49f0b8f.jpg?1783944517"

        ruling(
            "2020-08-07",
            "If Isochron Scepter leaves the battlefield while the activated ability is on the " +
                "stack, the ability can still make a copy. On the other hand, if the imprinted " +
                "card leaves the exile zone while the activated ability is on the stack, the copy " +
                "can't be made."
        )
        ruling(
            "2020-08-07",
            "You cast the copy while the ability is resolving and still on the stack. You can't " +
                "wait to cast it later in the turn."
        )
        ruling(
            "2020-08-07",
            "If you don't want to cast the copy, you can choose not to; the copy ceases to exist " +
                "the next time state-based actions are checked."
        )
        ruling(
            "2020-08-07",
            "If you cast a spell \"without paying its mana cost,\" you can't choose to cast it for " +
                "any alternative costs. You can, however, pay additional costs. If the card has any " +
                "mandatory additional costs, those must be paid to cast the spell."
        )
        ruling(
            "2020-08-07",
            "If a spell has {X} in its mana cost, you must choose 0 as the value of X when casting " +
                "it without paying its mana cost."
        )
    }
}
