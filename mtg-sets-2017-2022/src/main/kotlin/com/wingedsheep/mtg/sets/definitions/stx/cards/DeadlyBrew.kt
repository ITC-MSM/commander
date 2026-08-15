package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Deadly Brew
 * {B}{G}
 * Sorcery
 *
 * Each player sacrifices a creature or planeswalker of their choice. If you sacrificed a
 * permanent this way, you may return another permanent card from your graveyard to your
 * hand.
 *
 * Same shape as Rise of the Witch-king, with two differences: the edict filter is
 * creature-or-planeswalker (not just creature), and the reanimation half returns the
 * chosen card to hand rather than the battlefield.
 *
 * The oracle text does not say "target" — the return is a *resolution-time* choice, not a
 * cast-time target, so the spell has no targets and always resolves.
 *
 * Composition:
 *  - `Effects.Sacrifice(CreatureOrPlaneswalker, count=1, target=Player.Each)` — each player
 *    auto-sacrifices a sole eligible permanent or chooses among multiples. The snapshots
 *    flow into `EffectContext.sacrificedPermanents` so the rider can read them.
 *  - The rider is a `ConditionalEffect` gated on `YouSacrificedThisWay`.
 *  - The recursion half is the standard Gather → Select(`ChooseUpTo(1)`) → Move pipeline
 *    against your graveyard. `excludeSacrificedThisWay = true` keeps the permanent you just
 *    sacrificed (now in your graveyard) out of the "another permanent card" choice.
 */
val DeadlyBrew = card("Deadly Brew") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Sorcery"
    oracleText = "Each player sacrifices a creature or planeswalker of their choice. " +
        "If you sacrificed a permanent this way, you may return another permanent card " +
        "from your graveyard to your hand."

    spell {
        effect = Effects.Sacrifice(
            GameObjectFilter.CreatureOrPlaneswalker,
            count = 1,
            target = EffectTarget.PlayerRef(Player.Each)
        ).then(
            ConditionalEffect(
                condition = Conditions.YouSacrificedThisWay,
                effect = Effects.Composite(
                    listOf(
                        GatherCardsEffect(
                            source = CardSource.FromZone(
                                Zone.GRAVEYARD,
                                Player.You,
                                GameObjectFilter.Permanent,
                                // "return ANOTHER permanent card" — the permanent you just
                                // sacrificed sits in your graveyard but is not a legal choice.
                                excludeSacrificedThisWay = true
                            ),
                            storeAs = "eligible"
                        ),
                        SelectFromCollectionEffect(
                            from = "eligible",
                            selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                            storeSelected = "chosen",
                            prompt = "Choose a permanent card to return to your hand"
                        ),
                        MoveCollectionEffect(
                            from = "chosen",
                            destination = CardDestination.ToZone(Zone.HAND)
                        )
                    )
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "176"
        artist = "Randy Vargas"
        flavorText = "\"No one ever asked what was in Dina's concoctions, so long as they worked.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87d33e48-90fc-4aac-b09a-68050bc053b5.jpg?1783927318"
    }
}
