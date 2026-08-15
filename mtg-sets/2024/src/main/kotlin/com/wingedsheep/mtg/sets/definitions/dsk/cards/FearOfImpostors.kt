package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Fear of Impostors
 * {1}{U}{U}
 * Enchantment Creature — Nightmare
 * 3/2
 * Flash
 * When this creature enters, counter target spell. Its controller manifests dread. (That player
 * looks at the top two cards of their library, then puts one onto the battlefield face down as a
 * 2/2 creature and the other into their graveyard. If it's a creature card, it can be turned face
 * up any time for its mana cost.)
 *
 * Implementation notes:
 * - The ETB targets a spell, counters it, then the countered spell's controller manifests dread.
 *   "Its controller" is [Player.ControllerOf] over the spell target; after the counter the spell
 *   card sits in its owner's graveyard, where `controllerOf` resolves to that owner (= the spell's
 *   caster for a normal spell), so the right player manifests.
 * - "That player manifests dread" reuses the shared [Patterns.Library.manifestDread] recipe, but
 *   run under the spell controller via [Effects.ForEachPlayer] over a single player. The iteration
 *   rebinds the body's controller to that player (CR — they look at their own library, choose, and
 *   the face-down 2/2 enters under their control), so no manifest-dread-for-another-player special
 *   case is needed.
 */
val FearOfImpostors = card("Fear of Impostors") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Nightmare"
    power = 3
    toughness = 2
    oracleText = "Flash\nWhen this creature enters, counter target spell. Its controller manifests " +
        "dread. (That player looks at the top two cards of their library, then puts one onto the " +
        "battlefield face down as a 2/2 creature and the other into their graveyard. If it's a " +
        "creature card, it can be turned face up any time for its mana cost.)"

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("target spell", TargetSpell())
        effect = Effects.Composite(
            Effects.CounterSpell(),
            // Its controller manifests dread — run the shared recipe under the spell's controller.
            Effects.ForEachPlayer(
                players = Player.ControllerOf("target spell"),
                effects = Patterns.Library.manifestDread().effects,
            ),
        )
        description = "When this creature enters, counter target spell. Its controller manifests dread."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "57"
        artist = "David Szabo"
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bdee441e-14ab-42d1-b447-5a6488fd713a.jpg?1726286066"
    }
}
