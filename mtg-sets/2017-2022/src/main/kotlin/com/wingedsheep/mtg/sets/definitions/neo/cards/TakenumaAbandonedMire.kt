package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Takenuma, Abandoned Mire — Kamigawa: Neon Dynasty #278 (canonical printing)
 * Legendary Land · Rare
 *
 * {T}: Add {B}.
 * Channel — {3}{B}, Discard this card: Mill three cards, then return a creature or planeswalker
 * card from your graveyard to your hand. This ability costs {1} less to activate for each
 * legendary creature you control.
 *
 * One of the five NEO channel lands; see [OtawaraSoaringCity] for the shape they share.
 *
 * The return is **not targeted** — it is a choice made on resolution, and it is mandatory rather
 * than a "may", so it is the Gather → Select → Move pipeline with
 * [SelectionMode.ChooseExactly]. That mode clamps to what is actually there, which is the right
 * reading here: with no creature or planeswalker card in the graveyard after the mill, nothing
 * is returned and the ability simply finishes.
 *
 * Printed order matters and is preserved: the mill happens *first*, so a creature card milled by
 * this very ability is already in the graveyard and is a legal choice to return.
 */
val TakenumaAbandonedMire = card("Takenuma, Abandoned Mire") {
    typeLine = "Legendary Land"
    colorIdentity = "B"
    oracleText = "{T}: Add {B}.\n" +
        "Channel — {3}{B}, Discard this card: Mill three cards, then return a creature or " +
        "planeswalker card from your graveyard to your hand. This ability costs {1} less to " +
        "activate for each legendary creature you control."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
    }

    // Channel — {3}{B}, Discard this card (from hand): mill 3, then return a creature/PW card.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{B}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        genericCostReduction = DynamicAmounts.legendaryCreaturesYouControl()
        effect = Effects.Composite(
            listOf(
                Patterns.Library.mill(3),
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.GRAVEYARD,
                        player = Player.You,
                        filter = GameObjectFilter.CreatureOrPlaneswalker
                    ),
                    storeAs = "takenumaGraveyard"
                ),
                SelectFromCollectionEffect(
                    from = "takenumaGraveyard",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                    chooser = Chooser.Controller,
                    storeSelected = "takenumaReturned",
                    prompt = "Choose a creature or planeswalker card to return to your hand",
                    showAllCards = true
                ),
                MoveCollectionEffect(
                    from = "takenumaReturned",
                    destination = CardDestination.ToZone(Zone.HAND, Player.You),
                    moveType = MoveType.Default
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "278"
        artist = "Sam Burley"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/499037cc-a577-41cb-8ca2-5e117945634f.jpg?1783923812"
    }
}
