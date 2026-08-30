package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.LookAudience
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Curse of Hospitality
 * {2}{R}
 * Enchantment — Aura Curse
 * Enchant player
 * Creatures attacking enchanted player have trample.
 * Whenever a creature deals combat damage to enchanted player, that player exiles the top card of
 * their library. Until end of turn, that creature's controller may play that card and they may
 * spend mana as though it were mana of any color to cast that spell.
 *
 * A player-enchanting Aura ([Targets.Player], as Grievous Wound), and the first card whose payoffs
 * are scoped by *who the Aura enchants* rather than by who controls it. Both halves needed one new
 * attachment-scoped filter, because every existing "attacking / dealt damage to" scope in the SDK
 * resolves its player against the ability's **controller** — which for a curse is exactly the wrong
 * player, and would let a curse on one opponent fire off damage dealt to another.
 *
 *  - **"Creatures attacking enchanted player have trample"** is
 *    [com.wingedsheep.sdk.scripting.predicates.StatePredicate.IsAttackingEnchantedPlayer], the
 *    attachment-scoped sibling of `IsAttackingAnOpponent` (Oviya, Automech Artisan's "each creature
 *    that's attacking one of your opponents has trample" — the same [GrantKeyword] shape, one scope
 *    over). It is deliberately **not** "attacking creatures you control": the grant follows the
 *    *defender*, so a creature an opponent controls that's attacking the cursed player gets trample
 *    too, and a creature attacking a planeswalker the cursed player controls does not.
 *
 *  - **The damage trigger** is an ANY-bound observer with
 *    [RecipientFilter.EnchantedPlayer] and `sourceFilter = Creature`, the shape Gonti, Night
 *    Minister uses for the same "creature deals combat damage to a player, *its controller* may play
 *    the exiled card" text. The source filter is what makes the engine bind the *damaging creature*
 *    as the triggering entity and the *damaged player* as the triggering player — the exact pair the
 *    text needs, since the card comes off the cursed player's library while the permission goes to
 *    [EffectTarget.ControllerOfTriggeringEntity].
 *
 * Per its own ruling the trigger fires **once for each creature** that connected, so it is a plain
 * per-event observer, never a `batch = true` one.
 *
 * The exile is face up and public, so the gather takes [LookAudience.None]: a look overlay would
 * show the card to the Curse's controller, who is not necessarily the player who gets to play it.
 */
val CurseOfHospitality = card("Curse of Hospitality") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura Curse"
    oracleText = "Enchant player\n" +
        "Creatures attacking enchanted player have trample.\n" +
        "Whenever a creature deals combat damage to enchanted player, that player exiles the top " +
        "card of their library. Until end of turn, that creature's controller may play that card " +
        "and they may spend mana as though it were mana of any color to cast that spell."

    auraTarget = Targets.Player

    // Creatures attacking enchanted player have trample.
    staticAbility {
        ability = GrantKeyword(
            Keyword.TRAMPLE,
            GroupFilter(GameObjectFilter.Creature.attackingEnchantedPlayer())
        )
    }

    // Whenever a creature deals combat damage to enchanted player, …
    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.EnchantedPlayer,
            sourceFilter = GameObjectFilter.Creature,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(
                    count = DynamicAmount.Fixed(1),
                    player = Player.TriggeringPlayer,
                ),
                storeAs = "cursedCard",
                lookAudience = LookAudience.None,
            ),
            MoveCollectionEffect(
                from = "cursedCard",
                destination = CardDestination.ToZone(Zone.EXILE, Player.TriggeringPlayer),
            ),
            GrantMayPlayFromExileEffect(
                from = "cursedCard",
                expiry = MayPlayExpiry.EndOfTurn,
                withAnyManaType = true,
                recipient = EffectTarget.ControllerOfTriggeringEntity,
            ),
        )
        description = "Whenever a creature deals combat damage to enchanted player, that player " +
            "exiles the top card of their library. Until end of turn, that creature's controller " +
            "may play that card and they may spend mana as though it were mana of any color to " +
            "cast that spell."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "152"
        artist = "Dominik Mayer"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45bcb839-4cff-4349-9892-0c76ae81929c.jpg?1783924838"
        ruling(
            "2021-11-19",
            "Curse of Hospitality's triggered ability triggers once for each creature that dealt " +
                "combat damage to the enchanted player."
        )
    }
}
