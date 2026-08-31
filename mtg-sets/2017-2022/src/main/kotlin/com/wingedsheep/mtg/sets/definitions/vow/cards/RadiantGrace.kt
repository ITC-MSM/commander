package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.PermanentsEnterTapped
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Radiant Grace // Radiant Restraints (Innistrad: Crimson Vow #31)
 * {W} · Enchantment — Aura // Enchantment — Aura Curse
 *
 * Front — Radiant Grace
 *   Enchant creature
 *   Enchanted creature gets +1/+0 and has vigilance.
 *   When enchanted creature dies, return this card to the battlefield transformed under your
 *   control attached to target opponent.
 *
 * Back — Radiant Restraints
 *   Enchant player
 *   Creatures enchanted player controls enter tapped.
 *
 * The corpus's first **transforming** Aura-front / Aura-back DFC — every other double-faced Aura
 * here is the disturb shape (Twinblade Geist), where the back is cast from the graveyard rather
 * than returned by a trigger. Two things had to be built, and each is a *scope* the SDK could name
 * but not yet resolve:
 *
 *  - **"return this card to the battlefield transformed … attached to target opponent"** is
 *    [Effects.ReturnSelfToBattlefieldAttached] with `transformed = true` and a **player** host.
 *    The Dragon-aura cycle's version of this effect only ever attached to a creature, and handed
 *    control of the returned Aura to *that creature's* controller — the right reading for "attached
 *    to that creature", and exactly wrong for a curse, which would arrive under the control of the
 *    player it curses. A player host now keeps the Aura under the ability's controller, which is
 *    what "under your control" says. The face swap happens while the card is still in the
 *    graveyard, so the battlefield entry registers Radiant Restraints' replacement effect and not
 *    Radiant Grace's statics.
 *
 *  - **"Creatures enchanted player controls"** is
 *    `GameObjectFilter.Creature.controlledByEnchantedPlayer()`, the controller-axis sibling of
 *    Curse of Hospitality's [com.wingedsheep.sdk.scripting.predicates.StatePredicate.IsAttackingEnchantedPlayer]
 *    and the same lesson: every other controller scope in the SDK resolves against the *ability's*
 *    controller, which for a Curse is the wrong player. It resolves the granting Aura's own
 *    attachment, so the enters-tapped replacement had to start passing the permanent that grants
 *    it — not the permanent that is entering — as the filter's source.
 *
 * The trigger is `leavesBattlefield(to = GRAVEYARD, binding = ATTACHED)`: "when enchanted creature
 * dies" fires while the Aura is still on the battlefield, and resolves once the Aura has itself
 * been put into the graveyard by CR 704.5m — which is the zone the return reads from. A dead
 * enchanted creature that was exiled or bounced rather than dying doesn't fire it, and a game with
 * no opponent left to target simply removes the trigger from the stack.
 */
private val RadiantGraceFront = card("Radiant Grace") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+0 and has vigilance.\n" +
        "When enchanted creature dies, return this card to the battlefield transformed under " +
        "your control attached to target opponent."

    auraTarget = Targets.Creature

    // Enchanted creature gets +1/+0 and has vigilance.
    staticAbility {
        ability = ModifyStats(1, 0, GroupFilter.attachedCreature())
    }
    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE)
    }

    // When enchanted creature dies, return this card to the battlefield transformed under your
    // control attached to target opponent.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ATTACHED,
        )
        val cursed = target("target opponent", Targets.Opponent)
        effect = Effects.ReturnSelfToBattlefieldAttached(target = cursed, transformed = true)
        description = "When enchanted creature dies, return this card to the battlefield " +
            "transformed under your control attached to target opponent."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "31"
        artist = "Campbell White"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a708243-42a1-4fa7-8b0b-9d5163da84bb.jpg?1783924919"
    }
}

private val RadiantRestraints = card("Radiant Restraints") {
    manaCost = ""
    colorIdentity = "W"
    colorIndicator = "W" // Transformed back face, no mana cost (CR 204).
    typeLine = "Enchantment — Aura Curse"
    oracleText = "Enchant player\n" +
        "Creatures enchanted player controls enter tapped."

    auraTarget = Targets.Player

    // Creatures enchanted player controls enter tapped.
    replacementEffect(
        PermanentsEnterTapped(
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Creature.controlledByEnchantedPlayer(),
                to = Zone.BATTLEFIELD,
            )
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "31"
        artist = "Campbell White"
        flavorText = "\"Even in times of brutal darkness, never mistake beauty and delicacy for " +
            "weakness.\"\n—Thalia, Guardian of Thraben"
        imageUri = "https://cards.scryfall.io/normal/back/4/a/4a708243-42a1-4fa7-8b0b-9d5163da84bb.jpg?1783924919"
    }
}

val RadiantGrace: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = RadiantGraceFront,
    backFace = RadiantRestraints,
)
