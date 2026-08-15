package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Summon: Brynhildr
 * {1}{R}
 * Enchantment Creature — Saga Knight
 * 2/1
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — Chain — Exile the top card of your library. During any turn you put a lore counter on this
 *     Saga, you may play that card.
 * II, III — Gestalt Mode — When you next cast a creature spell this turn, it gains haste until end
 *     of turn.
 *
 * Chapter I ("Chain") is the impulse-exile shape: Gather top → Move to exile → grant a lasting "may
 * play from exile" permission ([MayPlayExpiry.Permanent], re-evaluated on every legal-action query
 * like Possibility Technician / Lightning, Security Sergeant). The oracle's window — "during any
 * turn you put a lore counter on this Saga" — is modeled as the gate "it's your turn AND you control
 * a Summon: Brynhildr": lore counters only land at enter and at your precombat main, so the turns a
 * counter is put on the Saga are exactly your turns while it's on the battlefield. The card isn't
 * legendary, so a name-filter [Exists] (rather than source identity) is the accepted corpus
 * approximation. The only divergences from the literal window are the theoretical proliferate-on-an-
 * opponent's-turn case and the sliver of turn III after the Saga is sacrificed (you can still play
 * the card during that same main phase before letting chapter III resolve).
 *
 * Chapters II and III ("Gestalt Mode") each install a one-shot end-of-turn delayed trigger on your
 * next creature spell cast — the same [CreateDelayedTriggerEffect] shape as Summon: Fenrir's chapter
 * II — whose effect grants haste to the triggering creature spell. The spell keeps its entity id as
 * it resolves onto the battlefield, so the haste grant (a floating keyword effect created while the
 * spell is on the stack) applies the moment it becomes a creature.
 */
val SummonBrynhildr = card("Summon: Brynhildr") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment Creature — Saga Knight"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Chain — Exile the top card of your library. During any turn you put a lore counter on " +
        "this Saga, you may play that card.\n" +
        "II, III — Gestalt Mode — When you next cast a creature spell this turn, it gains haste until " +
        "end of turn."
    power = 2
    toughness = 1

    // I — Chain — Exile the top card of your library; playable during turns you put a lore counter
    // on this Saga (modeled as your turns while you control it).
    sagaChapter(1) {
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                storeAs = "chainedCard"
            ),
            MoveCollectionEffect(
                from = "chainedCard",
                destination = CardDestination.ToZone(Zone.EXILE)
            ),
            Effects.GrantMayPlayFromExile(
                from = "chainedCard",
                expiry = MayPlayExpiry.Permanent,
                condition = Conditions.All(
                    Conditions.IsYourTurn,
                    Exists(
                        player = Player.You,
                        zone = Zone.BATTLEFIELD,
                        filter = GameObjectFilter.Enchantment.named("Summon: Brynhildr")
                    )
                )
            )
        )
    }

    // II, III — Gestalt Mode — When you next cast a creature spell this turn, it gains haste.
    sagaChapter(2) {
        effect = gestaltMode()
    }
    sagaChapter(3) {
        effect = gestaltMode()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "160"
        artist = "Kevin Glint"
        flavorText = "A name in blood, a pact of flame."
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8ab5429a-1075-49aa-9608-0610080fbf7a.jpg?1782686481"
    }
}

/**
 * Gestalt Mode — install a one-shot delayed trigger that grants haste to your next creature spell
 * cast this turn. Shared by chapters II and III.
 */
private fun gestaltMode() = CreateDelayedTriggerEffect(
    trigger = TriggerSpec(
        event = EventPattern.SpellCastEvent(
            spellFilter = GameObjectFilter.Creature,
            player = Player.You,
        ),
    ),
    fireOnce = true,
    expiry = DelayedTriggerExpiry.EndOfTurn,
    effect = Effects.GrantKeyword(Keyword.HASTE, EffectTarget.TriggeringEntity, Duration.EndOfTurn),
)
