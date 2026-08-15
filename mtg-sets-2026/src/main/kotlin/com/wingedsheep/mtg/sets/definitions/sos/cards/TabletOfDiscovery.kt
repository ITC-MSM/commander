package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Tablet of Discovery
 * {2}{R}
 * Artifact
 *
 * When this artifact enters, mill a card. You may play that card this turn.
 * {T}: Add {R}.
 * {T}: Add {R}{R}. Spend this mana only to cast instant and sorcery spells.
 *
 * "Mill a card" puts the top card into the graveyard; the may-play grant then lets the
 * controller play it from the graveyard until end of turn. The grant is keyed to the
 * milled card collection — the cast-from-zone enumerator already honors a
 * [MayPlayPermission] whose card sits in the graveyard, so no exile detour is needed.
 */
val TabletOfDiscovery = card("Tablet of Discovery") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, mill a card. You may play that card this turn.\n" +
        "{T}: Add {R}.\n" +
        "{T}: Add {R}{R}. Spend this mana only to cast instant and sorcery spells."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(listOf(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                storeAs = "milledThisWay"
            ),
            MoveCollectionEffect(
                from = "milledThisWay",
                destination = CardDestination.ToZone(Zone.GRAVEYARD)
            ),
            GrantMayPlayFromExileEffect("milledThisWay")
        ))
    }

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.RED, amount = 2, restriction = ManaRestriction.InstantOrSorceryOnly)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "132"
        artist = "Craig J Spearing"
        flavorText = "Leaving no stone unturned is just the beginning."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13059664-a940-4a66-8100-0c90b884bab4.jpg?1775937891"
    }
}
