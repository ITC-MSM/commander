package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Northampton Farm
 * Land
 *
 * {T}: Add {C}.
 * {1}, {T}: Exile target creature you own.
 * {2}, {T}, Sacrifice this land: Return a creature card exiled with this land to
 * the battlefield under your control. Return each other card exiled with this land
 * to its owner's hand.
 */
val NorthamptonFarm = card("Northampton Farm") {
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n{1}, {T}: Exile target creature you own.\n{2}, {T}, Sacrifice this land: Return a creature card exiled with this land to the battlefield under your control. Return each other card exiled with this land to its owner's hand."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    // Exile a creature you own, linked to this land so the sacrifice ability can find it later.
    activatedAbility {
        val creature = target(
            "target creature you own",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.ownedByYou()))
        )
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.Move(creature, Zone.EXILE, linkToSource = true)
    }

    // Sacrifice: return one creature card from this land's linked exile to the battlefield under
    // your control; everything else exiled with it goes to its owner's hand.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(source = CardSource.FromLinkedExile(), storeAs = "exiled"),
                SelectFromCollectionEffect(
                    from = "exiled",
                    // "Return a creature card" is mandatory when one is available (ChooseExactly clamps
                    // to the eligible cards: auto-selects the lone creature, no-ops on an empty pile).
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                    filter = GameObjectFilter.Creature,
                    showAllCards = true,
                    storeSelected = "toBattlefield",
                    storeRemainder = "toHand",
                    prompt = "Return a creature card to the battlefield under your control",
                    selectedLabel = "Return to the battlefield",
                    remainderLabel = "Return to owner's hand"
                ),
                MoveCollectionEffect(
                    from = "toBattlefield",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD, Player.You)
                ),
                MoveCollectionEffect(
                    from = "toHand",
                    destination = CardDestination.ToZone(Zone.HAND)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "188"
        artist = "Marina Ortega Lorente"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/dbca168e-095f-4fbc-88f8-3048d83caf94.jpg?1769006600"
    }
}
