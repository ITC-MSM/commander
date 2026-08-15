package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Boiling Rock Rioter
 * {2}{B}
 * Creature — Human Rogue Ally
 * 3/3
 *
 * Firebending 1 (Whenever this creature attacks, add {R}. This mana lasts until end of combat.)
 * Tap an untapped Ally you control: Exile target card from a graveyard.
 * Whenever this creature attacks, you may cast an Ally spell from among cards you own exiled with
 * this creature.
 *
 * The exile ability links each exiled card to this permanent via `LinkedExileComponent`
 * (`Effects.ExileLinkedToSource`), building the "cards exiled with this creature" pile. The attack
 * trigger gathers that pile (`CardSource.FromLinkedExile`), lets the controller pick up to one Ally
 * card they own (`SelectionMode.ChooseUpTo(1)` over an `Ally you own` filter), then casts it for its
 * normal mana cost during resolution (`Effects.CastFromCollection`). The cost taps an untapped Ally
 * you control — including this creature itself, which is an Ally — via `Costs.TapPermanents`.
 */
val BoilingRockRioter = card("Boiling Rock Rioter") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue Ally"
    power = 3
    toughness = 3
    oracleText = "Firebending 1 (Whenever this creature attacks, add {R}. This mana lasts until end of combat.)\n" +
        "Tap an untapped Ally you control: Exile target card from a graveyard.\n" +
        "Whenever this creature attacks, you may cast an Ally spell from among cards you own exiled with this creature."

    firebending(1)

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 1,
            filter = GameObjectFilter.Creature.withSubtype(Subtype.ALLY).youControl(),
            excludeSelf = false,
        )
        val t = target("target", Targets.CardInGraveyard)
        effect = Effects.ExileLinkedToSource(t)
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromLinkedExile(),
                storeAs = "exiledWithThis",
            ),
            SelectFromCollectionEffect(
                from = "exiledWithThis",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                filter = GameObjectFilter.Creature.withSubtype(Subtype.ALLY).ownedByYou(),
                storeSelected = "chosenAlly",
                prompt = "Choose an Ally spell to cast",
            ),
            Effects.CastFromCollection(from = "chosenAlly"),
        )
        description = "Whenever this creature attacks, you may cast an Ally spell from among cards " +
            "you own exiled with this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "87"
        artist = "Airi Yoshihisa"
        imageUri = "https://cards.scryfall.io/normal/front/7/3/739653cc-35c8-4a66-95d8-3e80fa6114f0.jpg?1764120601"
    }
}
