package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantAdditionalLandDrop
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Loot, Exuberant Explorer
 * {2}{G}
 * Legendary Creature — Beast Noble
 * 1/4
 *
 * You may play an additional land on each of your turns.
 * {4}{G}{G}, {T}: Look at the top six cards of your library. You may reveal a creature card with
 *   mana value less than or equal to the number of lands you control from among them and put it
 *   onto the battlefield. Put the rest on the bottom in a random order.
 *
 * - The extra land drop is a static [GrantAdditionalLandDrop] (cumulative with other such effects).
 * - The activated ability is an atomic Gather → Select → Move pipeline: look at the top six
 *   (GatherCards), the controller may keep at most one creature card whose mana value is at most
 *   the number of lands they control (SelectUpTo(1) filtered by [ManaValueAtMostDynamic] over
 *   [DynamicAmounts.landsYouControl] — evaluated live), that card is put onto the battlefield, and
 *   every remaining looked-at card goes to the bottom of the library in a random order. Declining /
 *   no eligible creature simply keeps nothing and bottoms all six.
 */
val LootExuberantExplorer = card("Loot, Exuberant Explorer") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Beast Noble"
    power = 1
    toughness = 4
    oracleText = "You may play an additional land on each of your turns.\n" +
        "{4}{G}{G}, {T}: Look at the top six cards of your library. You may reveal a creature card " +
        "with mana value less than or equal to the number of lands you control from among them and " +
        "put it onto the battlefield. Put the rest on the bottom in a random order."

    // You may play an additional land on each of your turns.
    staticAbility {
        ability = GrantAdditionalLandDrop(count = 1)
    }

    // {4}{G}{G}, {T}: Look at the top six ... put a creature (MV <= lands) onto the battlefield;
    // rest on the bottom in a random order.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{4}{G}{G}"),
            Costs.Tap,
        )
        effect = Effects.Composite(
            // Look at the top six cards of your library.
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(6)),
                storeAs = "lootLooked",
            ),
            // You may reveal a creature card with mana value <= the number of lands you control.
            SelectFromCollectionEffect(
                from = "lootLooked",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                filter = GameObjectFilter.Creature.manaValueAtMostDynamic(
                    DynamicAmounts.landsYouControl(),
                ),
                storeSelected = "lootKept",
                storeRemainder = "lootRest",
                prompt = "You may put a creature card with mana value ≤ lands you control " +
                    "onto the battlefield",
                showAllCards = true,
            ),
            // ... and put it onto the battlefield (revealed as it goes).
            MoveCollectionEffect(
                from = "lootKept",
                destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                revealed = true,
            ),
            // Put the rest on the bottom in a random order.
            MoveCollectionEffect(
                from = "lootRest",
                destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                order = CardOrder.Random,
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "106"
        artist = "Arif Wijaya"
        imageUri = "https://cards.scryfall.io/normal/front/0/9/09980ce6-425b-4e03-94d0-0f02043cb361.jpg?1783909097"
    }
}
