package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.LookAtTargetHandEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Cracked Skull
 * {2}{B}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, look at target player's hand. You may choose a nonland card from it.
 * That player discards that card.
 * When enchanted creature is dealt damage, destroy it.
 */
val CrackedSkull = card("Cracked Skull") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, look at target player's hand. You may choose a nonland card " +
        "from it. That player discards that card.\n" +
        "When enchanted creature is dealt damage, destroy it."

    auraTarget = Targets.Creature

    // When this Aura enters, look at target player's hand. You may choose a nonland card from it.
    // That player discards that card.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val player = target("target player", Targets.Player)
        effect = Effects.Composite(
            listOf(
                LookAtTargetHandEffect(player),
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.HAND, Player.ContextPlayer(0)),
                    storeAs = "targetHand",
                ),
                SelectFromCollectionEffect(
                    from = "targetHand",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    chooser = Chooser.Controller,
                    filter = GameObjectFilter.Nonland,
                    storeSelected = "toDiscard",
                    prompt = "You may choose a nonland card for that player to discard",
                    showAllCards = true,
                    alwaysPrompt = true,
                ),
                MoveCollectionEffect(
                    from = "toDiscard",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD, Player.ContextPlayer(0)),
                    moveType = MoveType.Discard,
                ),
            ),
        )
    }

    // When enchanted creature is dealt damage, destroy it.
    triggeredAbility {
        trigger = Triggers.takesDamage(binding = TriggerBinding.ATTACHED)
        effect = Effects.Destroy(EffectTarget.EnchantedCreature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "Mirko Failoni"
        flavorText = "Ears ringing and head throbbing, all Tarvin could do was wait to see what would finish the job."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/7616ad5e-ed30-4876-8743-6f0f9f143ea1.jpg?1726286178"
    }
}
