package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.mana.ManaPool
import com.wingedsheep.engine.mechanics.mana.SpellPaymentContext
import com.wingedsheep.engine.mechanics.mana.isSatisfiedBy
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.player.RestrictedManaEntry
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.msh.cards.IronManArmor
import com.wingedsheep.mtg.sets.definitions.msh.cards.RoninShadowStalker
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Engine-level tests for [ManaRestriction.EquipAbilityActivationOnly] — "Spend this mana only to
 * activate equip abilities" (CR 702.6).
 *
 * The restriction reads [SpellPaymentContext.isEquipAbilityActivation], a fact about the *ability*
 * rather than its source, which is why it can't be spelled with anything that already existed:
 *
 *  - `SubtypeSpellsOrAbilitiesOnly("Equipment")` admits **every** activated ability of an Equipment
 *    source, so it would also pay Iron Man Armor's "{2}: … it becomes a 0/0 Construct Hero artifact
 *    creature" (the shape usually cited as Batterskull's "{3}: Return this Equipment to its owner's
 *    hand").
 *  - `CardTypeSpellsOrAbilitiesOnly(ARTIFACT, allowAbilities = true)` has the same defect: the
 *    equip ability and the animate ability share one source and one card type.
 *
 * Coverage: the restriction's truth table over every spend context the engine builds; the invariant
 * that the equip flag implies an ability activation; the end-to-end payment of a real equip cost
 * from restricted mana; the refusal of the same Equipment's non-equip ability (through **legal-action
 * enumeration**, not just a rejected submission); and the mana emptying as the step ends.
 *
 * Ronin, Shadow Stalker is the only current producer; its card-level behaviour lives in
 * `RoninShadowStalkerScenarioTest`.
 */
class EquipAbilityManaRestrictionTest : ScenarioTestBase() {

    private val roninManaAbilityId = RoninShadowStalker.activatedAbilities.first { it.isManaAbility }.id
    private val armorEquipAbilityId = IronManArmor.activatedAbilities.single { it.isEquipAbility }.id
    private val armorAnimateAbilityId = IronManArmor.activatedAbilities.single { !it.isEquipAbility }.id

    /** A pool holding [amount] blue mana carrying [restriction] and nothing else. */
    private fun restrictedPool(restriction: ManaRestriction, amount: Int = 2): ManaPool =
        ManaPool(restrictedMana = List(amount) { RestrictedManaEntry(Color.BLUE, restriction) })

    private fun floatRoninMana(game: TestGame, ronin: com.wingedsheep.sdk.model.EntityId) =
        game.execute(
            ActivateAbility(
                playerId = game.player1Id,
                sourceId = ronin,
                abilityId = roninManaAbilityId,
                manaColorChoice = Color.BLUE,
            )
        )

    init {
        cardRegistry.register(RoninShadowStalker)
        cardRegistry.register(IronManArmor)

        context("EquipAbilityActivationOnly — spend contexts") {

            test("only an equip activation satisfies it") {
                val equip = SpellPaymentContext(
                    isAbilityActivation = true,
                    isEquipAbilityActivation = true,
                    abilitySourceCardTypes = setOf(CardType.ARTIFACT),
                    subtypes = setOf("Equipment"),
                )
                // The lossy case: a non-equip activated ability of the *same* Equipment source.
                val equipmentsOtherAbility = SpellPaymentContext(
                    isAbilityActivation = true,
                    abilitySourceCardTypes = setOf(CardType.ARTIFACT),
                    subtypes = setOf("Equipment"),
                )
                val otherAbility = SpellPaymentContext(
                    isAbilityActivation = true,
                    abilitySourceCardTypes = setOf(CardType.CREATURE),
                )
                val equipmentSpell = SpellPaymentContext(
                    cardTypes = setOf(CardType.ARTIFACT),
                    subtypes = setOf("Equipment"),
                )
                val creatureSpell = SpellPaymentContext(
                    isCreature = true,
                    cardTypes = setOf(CardType.CREATURE),
                )
                val turnFaceUp = SpellPaymentContext(isTurnFaceUpAction = true)
                val unlockDoor = SpellPaymentContext(isUnlockDoorAction = true)

                val restriction = ManaRestriction.EquipAbilityActivationOnly
                restriction.isSatisfiedBy(equip) shouldBe true
                restriction.isSatisfiedBy(equipmentsOtherAbility) shouldBe false
                restriction.isSatisfiedBy(otherAbility) shouldBe false
                restriction.isSatisfiedBy(equipmentSpell) shouldBe false
                restriction.isSatisfiedBy(creatureSpell) shouldBe false
                restriction.isSatisfiedBy(turnFaceUp) shouldBe false
                restriction.isSatisfiedBy(unlockDoor) shouldBe false

                // The old, lossy spelling really does admit the Equipment's other ability — this is
                // what makes the new atom necessary rather than a rename.
                ManaRestriction.SubtypeSpellsOrAbilitiesOnly("Equipment")
                    .isSatisfiedBy(equipmentsOtherAbility) shouldBe true
            }

            test("Ronin's composed restriction allows Equipment spells and equip abilities only") {
                val restriction = ManaRestriction.AnyOf(
                    listOf(
                        ManaRestriction.SubtypeSpellsOnly(setOf("Equipment")),
                        ManaRestriction.EquipAbilityActivationOnly,
                    )
                )
                val cost = ManaCost.parse("{1}")
                val pool = restrictedPool(restriction)

                val equipmentSpell = SpellPaymentContext(
                    cardTypes = setOf(CardType.ARTIFACT),
                    subtypes = setOf("Equipment"),
                )
                val plainArtifactSpell = SpellPaymentContext(cardTypes = setOf(CardType.ARTIFACT))
                val equipActivation = SpellPaymentContext(
                    isAbilityActivation = true,
                    isEquipAbilityActivation = true,
                    abilitySourceCardTypes = setOf(CardType.ARTIFACT),
                    subtypes = setOf("Equipment"),
                )
                val equipmentsOtherAbility = SpellPaymentContext(
                    isAbilityActivation = true,
                    abilitySourceCardTypes = setOf(CardType.ARTIFACT),
                    subtypes = setOf("Equipment"),
                )

                pool.canPay(cost, equipmentSpell) shouldBe true
                pool.canPay(cost, equipActivation) shouldBe true
                pool.canPay(cost, plainArtifactSpell) shouldBe false
                pool.canPay(cost, equipmentsOtherAbility) shouldBe false
            }

            test("the equip flag may not be set without an ability activation") {
                shouldThrow<IllegalArgumentException> {
                    SpellPaymentContext(isEquipAbilityActivation = true)
                }
            }

            test("the restriction renders its printed clause") {
                ManaRestriction.EquipAbilityActivationOnly.description shouldBe
                    "Spend this mana only to activate equip abilities"
            }
        }

        context("EquipAbilityActivationOnly — end to end") {

            test("Ronin's mana pays a real equip cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withCardOnBattlefield(1, "Iron Man Armor")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                val armor = game.findPermanent("Iron Man Armor")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                floatRoninMana(game, ronin).error shouldBe null
                game.state.getEntity(game.player1Id)!!.get<ManaPoolComponent>()!!
                    .restrictedMana.size shouldBe 2

                // Equip {2} is the only cost the floating mana may pay. No lands are in play, so a
                // success here can only have come from the restricted mana.
                val equip = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = armor,
                        abilityId = armorEquipAbilityId,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                    )
                )
                withClue("equip should be payable from Ronin's mana: ${equip.error}") {
                    equip.error shouldBe null
                }
                game.resolveStack()

                game.state.getEntity(armor)?.get<AttachedToComponent>()?.targetId shouldBe bears
                game.state.getEntity(game.player1Id)!!.get<ManaPoolComponent>()!!
                    .restrictedMana.size shouldBe 0
            }

            test("the same Equipment's non-equip ability is neither offered nor payable") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withCardOnBattlefield(1, "Iron Man Armor")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                val armor = game.findPermanent("Iron Man Armor")!!

                floatRoninMana(game, ronin).error shouldBe null

                // Legal-action enumeration, not a hand-built submission: the enumerator lists an
                // unaffordable ability greyed out, so the assertion is on affordability. Both
                // abilities cost {2} and share a source; only the restriction separates them, so
                // this is the discriminating assertion for the whole feature.
                val affordability = game.getLegalActions(1)
                    .mapNotNull { info -> (info.action as? ActivateAbility)?.let { it to info.isAffordable } }
                    .filter { (action, _) -> action.sourceId == armor }
                    .associate { (action, affordable) -> action.abilityId to affordable }
                withClue("Iron Man Armor's abilities and their affordability: $affordability") {
                    affordability[armorEquipAbilityId] shouldBe true
                    affordability[armorAnimateAbilityId] shouldBe false
                }

                // And submitting it anyway is refused.
                val animate = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = armor,
                        abilityId = armorAnimateAbilityId,
                    )
                )
                animate.error shouldNotBe null
            }

            test("the restricted mana empties as the step ends") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                floatRoninMana(game, ronin).error shouldBe null
                game.state.getEntity(game.player1Id)!!.get<ManaPoolComponent>()!!
                    .restrictedMana.size shouldBe 2

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)

                game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                    ?.restrictedMana.orEmpty().size shouldBe 0
            }
        }
    }
}
