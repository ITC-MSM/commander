package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Flickerform
 * {1}{W}
 * Enchantment — Aura
 * Enchant creature
 * {2}{W}{W}: Exile enchanted creature and all Auras attached to it. At the beginning of the next
 * end step, return that card to the battlefield under its owner's control. If you do, return the
 * other cards exiled this way to the battlefield under their owners' control attached to that
 * creature.
 *
 * The host and its Auras (Flickerform among them) are gathered into two collections while still
 * attached, then exiled. The end-step trigger carries both collections
 * (`CreateDelayedTriggerEffect.carryCollections`), returns the host under its owner's control, then
 * returns the Auras with `MoveCollectionEffect.attachTo` naming that card. `attachTo` requires the
 * host to be on the battlefield, which is the "if you do": a token host ceased to exist in exile,
 * so its Auras stay exiled (2005-10-01 ruling), and an Aura that can't legally enchant the returned
 * card stays exiled too. A bestow card isn't an Aura card in exile, so it returns as a creature.
 */
val Flickerform = card("Flickerform") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "{2}{W}{W}: Exile enchanted creature and all Auras attached to it. At the beginning of the next " +
        "end step, return that card to the battlefield under its owner's control. If you do, return the " +
        "other cards exiled this way to the battlefield under their owners' control attached to that creature."

    auraTarget = Targets.Creature

    activatedAbility {
        cost = Costs.Mana("{2}{W}{W}")
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.BattlefieldMatching(GameObjectFilter.Creature.attachedToBySource()),
                storeAs = "flickerHost"
            ),
            GatherCardsEffect(
                source = CardSource.AttachedTo(
                    EffectTarget.EnchantedCreature,
                    GameObjectFilter.Enchantment.withSubtype("Aura")
                ),
                storeAs = "flickerAuras"
            ),
            MoveCollectionEffect(from = "flickerHost", destination = CardDestination.ToZone(Zone.EXILE)),
            MoveCollectionEffect(from = "flickerAuras", destination = CardDestination.ToZone(Zone.EXILE)),
            CreateDelayedTriggerEffect(
                step = Step.END,
                effect = Effects.Composite(
                    MoveCollectionEffect(
                        from = "flickerHost",
                        destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                        underOwnersControl = true
                    ),
                    MoveCollectionEffect(
                        from = "flickerAuras",
                        destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                        underOwnersControl = true,
                        attachTo = EffectTarget.PipelineTarget("flickerHost")
                    )
                ),
                carryCollections = listOf("flickerHost", "flickerAuras")
            )
        )
        description = "{2}{W}{W}: Exile enchanted creature and all Auras attached to it. At the beginning of " +
            "the next end step, return that card to the battlefield under its owner's control. If you do, " +
            "return the other cards exiled this way to the battlefield under their owners' control attached " +
            "to that creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "18"
        artist = "Ron Spears"
        imageUri = "https://cards.scryfall.io/normal/front/6/1/6165de5d-57f0-419b-95a1-02bd53e5a0c5.jpg?1783943701"
        ruling(
            "2005-10-01",
            "The card that was enchanted comes back onto the battlefield first, regardless of whether it's " +
                "still a creature. Then any Auras exiled that can legally enchant that card come back. Any " +
                "Auras that can't enchant that permanent remain exiled."
        )
        ruling(
            "2005-10-01",
            "If the enchanted creature was a token, it ceased to exist when it was exiled. Any Auras that " +
                "were attached to it (including Flickerform) remain exiled."
        )
        ruling(
            "2014-02-01",
            "If the enchanted creature was enchanted by any Auras with bestow or any Licids, those cards " +
                "will return to the battlefield unattached as creatures."
        )
    }
}
