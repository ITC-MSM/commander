package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fin.cards.FreyaCrescent
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Freya Crescent (FIN #138) — {R} 1/1 Legendary Rat Knight.
 *
 * "Jump — During your turn, Freya Crescent has flying.
 *  {T}: Add {R}. Spend this mana only to cast an Equipment spell or activate an equip ability."
 *
 * The spend restriction is the point of this file. It used to be the one-liner
 * `SubtypeSpellsOrAbilitiesOnly("Equipment")`, which is over-broad in exactly one way: it admits
 * **every** activated ability of an Equipment source, not just the equip ability (CR 702.6). The
 * card's own KDoc used to record that as a known, accepted gap because the fix needed an
 * ability-level flag on the payment context that did not exist yet. It exists now
 * ([ManaRestriction.EquipAbilityActivationOnly]), so the card is the composed
 * `AnyOf(SubtypeSpellsOnly("Equipment"), EquipAbilityActivationOnly)` — the same shape as Ronin,
 * Shadow Stalker's identical printed clause.
 *
 * The discriminating case needs one Equipment with **two** activated abilities of the *same* mana
 * cost — equip, and something else — so that neither cost nor card type can be what separates them,
 * only the restriction. No printed Equipment happens to have that exact shape at {1}, so the test
 * defines one; the printed cards it stands in for are Iron Man Armor ("{2}: … becomes a 0/0
 * Construct Hero artifact creature") and Batterskull ("{3}: Return this Equipment to its owner's
 * hand"), which the old spelling would have paid for.
 *
 * Assertions run through **legal-action enumeration** rather than only a rejected submission, so a
 * regression shows up as the ability being offered as affordable, not merely as a lost error string.
 */
class FreyaCrescentScenarioTest : ScenarioTestBase() {

    private val freyaManaAbilityId = FreyaCrescent.activatedAbilities.single { it.isManaAbility }.id

    /**
     * An Equipment whose equip ability and whose other activated ability cost the same {1}. Both
     * are activated abilities of an Equipment source, so `SubtypeSpellsOrAbilitiesOnly("Equipment")`
     * would pay for both; only `EquipAbilityActivationOnly` tells them apart.
     */
    private val testBlade = card("Practice Blade") {
        manaCost = "{1}"
        typeLine = "Artifact — Equipment"
        oracleText = "{1}: Draw a card.\nEquip {1}"
        activatedAbility {
            cost = Costs.Mana("{1}")
            effect = Effects.DrawCards(1)
            description = "{1}: Draw a card."
        }
        equipAbility("{1}")
    }

    private val bladeEquipAbilityId = testBlade.activatedAbilities.single { it.isEquipAbility }.id
    private val bladeDrawAbilityId = testBlade.activatedAbilities.single { !it.isEquipAbility }.id

    init {
        cardRegistry.register(FreyaCrescent)
        cardRegistry.register(testBlade)

        context("Freya Crescent") {

            test("the mana ability carries the composed equip-spell-or-equip-ability restriction") {
                val effect = FreyaCrescent.activatedAbilities.single { it.isManaAbility }.effect
                val restriction = (effect as AddManaEffect).restriction

                withClue("the printed clause names two spend contexts, so the model is AnyOf of two atoms") {
                    restriction shouldBe ManaRestriction.AnyOf(
                        listOf(
                            ManaRestriction.SubtypeSpellsOnly(setOf("Equipment")),
                            ManaRestriction.EquipAbilityActivationOnly,
                        )
                    )
                }
            }

            test("its mana pays an equip ability but not the same Equipment's other ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Freya Crescent")
                    .withCardOnBattlefield(1, "Practice Blade")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val freya = game.findPermanent("Freya Crescent")!!
                val blade = game.findPermanent("Practice Blade")!!

                // No lands are in play, so every payment below can only come from Freya's mana.
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = freya,
                        abilityId = freyaManaAbilityId,
                    )
                ).error shouldBe null
                game.state.getEntity(game.player1Id)!!.get<ManaPoolComponent>()!!
                    .restrictedMana.size shouldBe 1

                val affordability = game.getLegalActions(1)
                    .mapNotNull { info -> (info.action as? ActivateAbility)?.let { it to info.isAffordable } }
                    .filter { (action, _) -> action.sourceId == blade }
                    .associate { (action, affordable) -> action.abilityId to affordable }
                withClue("Practice Blade's abilities and their affordability: $affordability") {
                    affordability[bladeEquipAbilityId] shouldBe true
                    affordability[bladeDrawAbilityId] shouldBe false
                }

                // And submitting the non-equip one anyway is refused.
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = blade,
                        abilityId = bladeDrawAbilityId,
                    )
                ).error shouldNotBe null
            }
        }
    }
}
