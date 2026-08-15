package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tawnos's Coffin
 * {4}
 * Artifact
 * You may choose not to untap this artifact during your untap step.
 * {3}, {T}: Exile target creature and all Auras attached to it. Note the number and kind of
 * counters that were on that creature. When this artifact leaves the battlefield or becomes
 * untapped, return that exiled card to the battlefield under its owner's control tapped with the
 * noted number and kind of counters on it. If you do, return the other exiled cards to the
 * battlefield under their owner's control attached to that permanent.
 *
 * A state-preserving "blink" tied to the source untapping or leaving:
 *  - The optional "may choose not to untap" half is [AbilityFlag.MAY_NOT_UNTAP] (so the Coffin can
 *    hold a creature exiled indefinitely by leaving itself tapped).
 *  - The activated ability exiles the creature + its Auras (linked to the Coffin) and notes the
 *    creature's counters ([Effects.ExileWithAurasNotingCounters]).
 *  - Both the leaves-the-battlefield and the becomes-untapped triggers run
 *    [Effects.ReturnNotedExileTappedWithAuras], which returns the creature tapped under its owner's
 *    control with the noted counters, then re-attaches the Auras (Auras that can't legally
 *    re-attach go to their owners' graveyards via the unattached-Aura SBA). The return effect is a
 *    no-op when nothing is noted, so the two triggers are safe to share.
 */
val TawnossCoffin = card("Tawnos's Coffin") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "You may choose not to untap this artifact during your untap step.\n" +
        "{3}, {T}: Exile target creature and all Auras attached to it. Note the number and kind " +
        "of counters that were on that creature. When this artifact leaves the battlefield or " +
        "becomes untapped, return that exiled card to the battlefield under its owner's control " +
        "tapped with the noted number and kind of counters on it. If you do, return the other " +
        "exiled cards to the battlefield under their owner's control attached to that permanent."

    flags(AbilityFlag.MAY_NOT_UNTAP)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ExileWithAurasNotingCounters(creature)
        description = "{3}, {T}: Exile target creature and all Auras attached to it, noting its counters."
    }

    // "When this artifact leaves the battlefield or becomes untapped, return that exiled card …"
    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnNotedExileTappedWithAuras()
    }
    triggeredAbility {
        trigger = Triggers.BecomesUntapped
        effect = Effects.ReturnNotedExileTappedWithAuras()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "68"
        artist = "Christopher Rush"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c27bc1de-8246-4dc8-af51-ec21def9e226.jpg?1562936148"
    }
}
