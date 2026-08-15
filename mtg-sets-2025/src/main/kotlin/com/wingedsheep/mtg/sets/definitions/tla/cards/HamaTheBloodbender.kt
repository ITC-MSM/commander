package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.AllConditions
import com.wingedsheep.sdk.scripting.conditions.IsYourTurn
import com.wingedsheep.sdk.scripting.conditions.YouControlSource
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Hama, the Bloodbender
 * {2}{U/B}{U/B}{U/B}
 * Legendary Creature — Human Warlock
 * 3/3
 *
 * When Hama enters, target opponent mills three cards. Exile up to one noncreature, nonland card
 * from that player's graveyard. For as long as you control Hama, you may cast the exiled card during
 * your turn by waterbending {X} rather than paying its mana cost, where X is its mana value. (While
 * paying a waterbend cost, you can tap your artifacts and creatures to help. Each one pays for {1}.)
 *
 * The ETB triggered ability targets an opponent and, in one resolution, mills them three
 * ([Patterns.Library.mill]), then exiles up to one noncreature/nonland card from *that player's*
 * graveyard (a [CardSource.FromZone] over the target's graveyard filtered to
 * `Noncreature and Nonland`, a [SelectionMode.ChooseUpTo] `1` select, then a move to exile), and
 * finally grants the caster a waterbend-cast permission over the exiled card via
 * [Effects.WaterbendCastFromExile]. That grant stamps a
 * `PlayWithFixedAlternativeManaCostComponent(waterbend = true)` whose fixed cost is the exiled card's
 * mana value — the alternative cost is a waterbend {mana value} (CR 701.67): payable with mana and/or
 * by tapping untapped artifacts/creatures, each {1}. The permission persists while the card stays
 * exiled but is gated by `IsYourTurn AND YouControlSource` — re-checked on every legal-action query —
 * so the card is castable only on your turn and only while you still control Hama; once Hama leaves
 * the battlefield [YouControlSource] fails (its `ControllerComponent` is stripped) and the grant ends.
 */
val HamaTheBloodbender: CardDefinition = card("Hama, the Bloodbender") {
    manaCost = "{2}{U/B}{U/B}{U/B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Human Warlock"
    power = 3
    toughness = 3
    oracleText = "When Hama enters, target opponent mills three cards. Exile up to one noncreature, " +
        "nonland card from that player's graveyard. For as long as you control Hama, you may cast " +
        "the exiled card during your turn by waterbending {X} rather than paying its mana cost, " +
        "where X is its mana value. (While paying a waterbend cost, you can tap your artifacts and " +
        "creatures to help. Each one pays for {1}.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.Composite(
            listOf(
                // target opponent mills three cards
                Patterns.Library.mill(3, opponent),
                // Exile up to one noncreature, nonland card from that player's graveyard.
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.GRAVEYARD,
                        player = Player.ContextPlayer(0),
                        filter = GameObjectFilter.Noncreature and GameObjectFilter.Nonland,
                    ),
                    storeAs = "hamaGraveyard",
                ),
                SelectFromCollectionEffect(
                    from = "hamaGraveyard",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "hamaChosen",
                    prompt = "Exile up to one noncreature, nonland card from that player's graveyard",
                ),
                MoveCollectionEffect(
                    from = "hamaChosen",
                    // The exiled card is owned by the target opponent — keep it in that player's
                    // exile (a graveyard→exile move doesn't collapse to owner automatically the way a
                    // battlefield→exile move does).
                    destination = CardDestination.ToZone(Zone.EXILE, Player.ContextPlayer(0)),
                    storeMovedAs = "hamaExiled",
                ),
                // For as long as you control Hama, you may cast the exiled card during your turn by
                // waterbending {its mana value} rather than paying its mana cost.
                Effects.WaterbendCastFromExile(
                    from = "hamaExiled",
                    condition = AllConditions(listOf(IsYourTurn, YouControlSource)),
                ),
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "224"
        artist = "Le Vuong"
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6fa1197d-7b19-4d86-81e2-5c87de87757b.jpg?1764121634"
    }
}
