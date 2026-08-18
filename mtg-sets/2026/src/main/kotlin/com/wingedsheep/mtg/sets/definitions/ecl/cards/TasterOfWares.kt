package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.StoreNumberEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetOpponent
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.dsl.Effects

/**
 * Taster of Wares
 * {2}{B}
 * Creature — Goblin Warlock
 * 3/2
 *
 * When this creature enters, target opponent reveals X cards from their hand,
 * where X is the number of Goblins you control. You choose one of those cards.
 * That player exiles it. If an instant or sorcery card is exiled this way, you
 * may cast it for as long as you control this creature, and mana of any type
 * can be spent to cast that spell.
 *
 * Implementation notes:
 * - The cast-from-exile permission carries `MayPlayExpiry.WhileYouControlSource`,
 *   so it ends the moment this creature leaves the battlefield or someone else
 *   gains control of it. Per CR 611.2b the window is one-way: the grant is not
 *   created at all if the Taster is already gone when the trigger resolves, and
 *   getting it back later does not revive a permission that has ended. The card
 *   stays exiled either way — only the permission ends.
 * - "Mana of any type can be spent" is plumbed via `withAnyManaType = true` on
 *   the granted permission, which relaxes colored cost requirements at cast time.
 */
val TasterOfWares = card("Taster of Wares") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Warlock"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, target opponent reveals X cards from their hand, where X is the number of Goblins you control. You choose one of those cards. That player exiles it. If an instant or sorcery card is exiled this way, you may cast it for as long as you control this creature, and mana of any type can be spent to cast that spell."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("opponent", TargetOpponent())
        description = "When this creature enters, target opponent reveals X cards from their hand, " +
            "where X is the number of Goblins you control. You choose one of those cards. " +
            "That player exiles it. If an instant or sorcery card is exiled this way, you may cast it " +
            "for as long as you control this creature, and mana of any type can be spent to cast that spell."
        effect = Effects.Composite(
            listOf(
                // Snapshot the number of Goblins you control as X
                StoreNumberEffect(
                    name = "goblinCount",
                    amount = DynamicAmounts.battlefield(
                        Player.You,
                        GameObjectFilter.Creature.withSubtype(Subtype.GOBLIN)
                    ).count()
                ),
                // Gather opponent's hand
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.HAND, Player.ContextPlayer(0)),
                    storeAs = "hand"
                ),
                // Opponent chooses X cards to reveal
                SelectFromCollectionEffect(
                    from = "hand",
                    selection = SelectionMode.ChooseExactly(
                        DynamicAmount.VariableReference("goblinCount")
                    ),
                    chooser = Chooser.TargetPlayer,
                    storeSelected = "revealed",
                    prompt = "Reveal X cards from your hand (X = number of Goblins your opponent controls)"
                ),
                // You choose one of the revealed cards. Mandatory (ChooseExactly), not a "may":
                // ChooseUpTo would offer a min-0 decision that a player or the AI can decline,
                // and nothing would be exiled. ChooseExactly still resolves cleanly when X = 0 or
                // the hand is empty — an empty collection auto-selects nothing rather than pausing.
                SelectFromCollectionEffect(
                    from = "revealed",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                    chooser = Chooser.Controller,
                    storeSelected = "chosen",
                    prompt = "Choose one of the revealed cards to exile"
                ),
                // That player exiles it
                MoveCollectionEffect(
                    from = "chosen",
                    destination = CardDestination.ToZone(Zone.EXILE, Player.ContextPlayer(0))
                ),
                // If the exiled card is an instant or sorcery, grant cast-from-exile
                FilterCollectionEffect(
                    from = "chosen",
                    filter = CollectionFilter.MatchesFilter(GameObjectFilter.InstantOrSorcery),
                    storeMatching = "instantOrSorcery"
                ),
                GrantMayPlayFromExileEffect(
                    from = "instantOrSorcery",
                    expiry = MayPlayExpiry.WhileYouControlSource("this creature"),
                    withAnyManaType = true
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "121"
        artist = "Edgar Sánchez Hidalgo"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc0b64b6-8984-431c-8a2f-84402b429e2b.jpg?1767952030"
    }
}
