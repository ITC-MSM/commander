package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Aim for the Head
 * {2}{B}
 * Sorcery
 *
 * Choose one —
 * • Exile target Zombie.
 * • Target opponent exiles two cards from their hand.
 *
 * Mode 1 is a plain [Effects.Exile] over a subtype-scoped creature target (the Phoenix Down
 * shape). Mode 2 is the Perfect Intimidation pipeline: gather the targeted opponent's hand,
 * have *that player* ([Chooser.TargetPlayer]) pick two of their own cards, then move the
 * selection to exile. The ruling — "if the target opponent only has one card in hand, they
 * will exile that card" — falls out of `SelectFromCollection`, which takes everything eligible
 * rather than stalling when the collection is smaller than the requested count.
 */
val AimForTheHead = card("Aim for the Head") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Exile target Zombie.\n" +
        "• Target opponent exiles two cards from their hand."

    spell {
        modal(chooseCount = 1) {
            mode("Exile target Zombie") {
                target(
                    "target Zombie",
                    TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withSubtype("Zombie")))
                )
                effect = Effects.Exile(EffectTarget.ContextTarget(0))
            }
            mode("Target opponent exiles two cards from their hand") {
                target("opponent", Targets.Opponent)
                effect = Effects.Composite(
                    listOf(
                        GatherCardsEffect(
                            source = CardSource.FromZone(Zone.HAND, Player.ContextPlayer(0), GameObjectFilter.Any),
                            storeAs = "handCards"
                        ),
                        SelectFromCollectionEffect(
                            from = "handCards",
                            selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(2)),
                            chooser = Chooser.TargetPlayer,
                            storeSelected = "exiledCards",
                            prompt = "Exile two cards from your hand"
                        ),
                        MoveCollectionEffect(
                            from = "exiledCards",
                            destination = CardDestination.ToZone(Zone.EXILE, Player.ContextPlayer(0))
                        )
                    )
                )
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "92"
        artist = "Zoltan Boros"
        flavorText = "\"Works every time.\"\n—Halana, Kessig ranger"
        imageUri = "https://cards.scryfall.io/normal/front/1/1/1174e8e1-2e8e-4070-9871-7d5d93e0dd56.jpg?1783924875"
        ruling("2021-11-19", "If the target opponent only has one card in hand, they will exile that card.")
    }
}
