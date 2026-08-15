package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Stalked Researcher
 * {1}{U}
 * Creature — Human Wizard
 * 3/3
 * Defender
 * Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room,
 * this creature can attack this turn as though it didn't have defender.
 *
 * The "X and whenever Y" Eerie templating is two distinct triggered abilities sharing one
 * payoff (CR has no "or" trigger combiner), modelled like the other DSK Eerie creatures
 * (e.g. Optimistic Scavenger): one block fires when an enchantment you control enters
 * ([Triggers.entersBattlefield] filtered to enchantments you control), the other when you
 * fully unlock a Room ([Triggers.RoomFullyUnlocked]). Both grant this creature
 * [Effects.CanAttackDespiteDefenderThisTurn] (the turn-scoped counterpart to the static
 * CanAttackDespiteDefender), which lets it attack as though it didn't have defender for the turn.
 */
val StalkedResearcher = card("Stalked Researcher") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 3
    oracleText = "Defender\nEerie — Whenever an enchantment you control enters and whenever you " +
        "fully unlock a Room, this creature can attack this turn as though it didn't have defender."

    keywords(Keyword.DEFENDER, Keyword.EERIE)

    // Eerie trigger — part 1: whenever an enchantment you control enters
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.CanAttackDespiteDefenderThisTurn()
        description = "Eerie — Whenever an enchantment you control enters, this creature can " +
            "attack this turn as though it didn't have defender."
    }

    // Eerie trigger — part 2: whenever you fully unlock a Room
    triggeredAbility {
        trigger = Triggers.RoomFullyUnlocked
        effect = Effects.CanAttackDespiteDefenderThisTurn()
        description = "Eerie — Whenever you fully unlock a Room, this creature can attack this " +
            "turn as though it didn't have defender."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Marta Nael"
        flavorText = "\"Just a shadow. It's only a shadow. Please let it be a shadow.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8a26ccad-adfa-43cc-be4c-dc8dd584bca3.jpg?1726286124"
    }
}
