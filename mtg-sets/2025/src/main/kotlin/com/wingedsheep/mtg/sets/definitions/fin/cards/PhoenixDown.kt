package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Phoenix Down
 * {W}
 * Artifact
 * {1}{W}, {T}, Exile this artifact: Choose one —
 * • Return target creature card with mana value 4 or less from your graveyard to the battlefield tapped.
 * • Exile target Skeleton, Spirit, or Zombie.
 *
 * A modal activated ability whose cost includes exiling the artifact itself
 * ([Costs.ExileSelf]). Mode 1 reanimates a small creature from your graveyard (entering
 * tapped via [ZonePlacement.Tapped]); Mode 2 exiles a creature of one of the three named
 * undead/spirit types.
 */
val PhoenixDown = card("Phoenix Down") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "{1}{W}, {T}, Exile this artifact: Choose one —\n" +
        "• Return target creature card with mana value 4 or less from your graveyard to the battlefield tapped.\n" +
        "• Exile target Skeleton, Spirit, or Zombie."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap, Costs.ExileSelf)
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                Effects.Move(
                    EffectTarget.ContextTarget(0),
                    Zone.BATTLEFIELD,
                    placement = ZonePlacement.Tapped,
                    fromZone = Zone.GRAVEYARD
                ),
                TargetObject(filter = TargetFilter.CreatureInYourGraveyard.manaValueAtMost(4)),
                "Return target creature card with mana value 4 or less from your graveyard to the battlefield tapped"
            ),
            Mode.withTarget(
                Effects.Exile(EffectTarget.ContextTarget(0)),
                TargetCreature(
                    filter = TargetFilter(GameObjectFilter.Creature.withAnySubtype("Skeleton", "Spirit", "Zombie"))
                ),
                "Exile target Skeleton, Spirit, or Zombie"
            ),
            countsAsModalSpell = false
        )
        description = "{1}{W}, {T}, Exile this artifact: Choose one — Return target creature card with mana value 4 or less from your graveyard to the battlefield tapped; or Exile target Skeleton, Spirit, or Zombie."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "29"
        artist = "John Severin Brassell"
        flavorText = "A feather with the power to revive those who have exhausted their strength."
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62e299b0-9ef6-49d3-aa79-384325fed89e.jpg?1748705861"
    }
}
